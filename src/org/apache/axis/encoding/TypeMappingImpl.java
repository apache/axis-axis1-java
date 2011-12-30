/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.AxisProperties;
import org.apache.axis.MessageContext;
import org.apache.axis.AxisEngine;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.utils.ArrayUtil;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.toJava.Utils;
import org.apache.axis.wsdl.fromJava.Namespaces;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.Serializable;

/**
 * <p>
 * This is the implementation of the axis TypeMapping interface (which extends
 * the JAX-RPC TypeMapping interface).
 * </p>
 * <p>
 * A TypeMapping is obtained from the singleton TypeMappingRegistry using
 * the namespace of the webservice.  The TypeMapping contains the tuples
 * {Java type, SerializerFactory, DeserializerFactory, Type QName)
 * </p>
 * <p>
 * So if you have a Web Service with the namespace "XYZ", you call
 * the TypeMappingRegistry.getTypeMapping("XYZ").
 * </p>
 * <p>
 * The wsdl in your web service will use a number of types.  The tuple
 * information for each of these will be accessed via the TypeMapping.
 * </p>
 * <p>
 * Because every web service uses the soap, schema, wsdl primitives, we could
 * pre-populate the TypeMapping with these standard tuples.  Instead,
 * if the namespace/class matches is not found in the TypeMapping
 * the request is delegated to the
 * Default TypeMapping or another TypeMapping
 * </p>
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class TypeMappingImpl implements Serializable
{
    protected static Log log =
        LogFactory.getLog(TypeMappingImpl.class.getName());

    /**
     * Work around a .NET bug with soap encoded types.
     * This is a static property of the type mapping that will
     * cause the class to ignore SOAPENC types when looking up
     * QNames of java types.  See getTypeQNameExact().
     */
    public static boolean dotnet_soapenc_bugfix = false;

    public static class Pair implements Serializable {
        public Class javaType;
        public QName xmlType;
        public Pair(Class javaType, QName xmlType) {
            this.javaType = javaType;
            this.xmlType = xmlType;
        }
        public boolean equals(Object o) {
            if (o == null) return false;
            Pair p = (Pair) o;
            // Test straight equality
            if (p.xmlType == this.xmlType &&
                p.javaType == this.javaType) {
                return true;
            }
            return (p.xmlType.equals(this.xmlType) &&
                    p.javaType.equals(this.javaType));
        }
        public int hashCode() {
            int hashcode = 0;
            if (javaType != null) {
                hashcode ^= javaType.hashCode();
            }
            if (xmlType != null) {
                hashcode ^= xmlType.hashCode();
            }
            return hashcode;
        }
    }

    private HashMap qName2Pair;     // QName to Pair Mapping
    private HashMap class2Pair;     // Class Name to Pair Mapping
    private HashMap pair2SF;        // Pair to Serialization Factory
    private HashMap pair2DF;        // Pair to Deserialization Factory
    private ArrayList namespaces;   // Supported namespaces

    protected Boolean doAutoTypes = null;

    /**
     * Construct TypeMapping
     */
    public TypeMappingImpl() {
        qName2Pair  = new HashMap();
        class2Pair  = new HashMap();
        pair2SF     = new HashMap();
        pair2DF     = new HashMap();
        namespaces  = new ArrayList();
    }

    private static boolean isArray(Class clazz)
    {
        return clazz.isArray() || java.util.Collection.class.isAssignableFrom(clazz);
    }


    /********* JAX-RPC Compliant Method Definitions *****************/

    /**
     * Gets the list of encoding styles supported by this TypeMapping object.
     *
     * @return  String[] of namespace URIs for the supported encoding
     * styles and XML schema namespaces.
     */
    public String[] getSupportedEncodings() {
        String[] stringArray = new String[namespaces.size()];
        return (String[]) namespaces.toArray(stringArray);
    }

    /**
     * Sets the list of encoding styles supported by this TypeMapping object.
     * (Not sure why this is useful...this information is automatically updated
     * during registration.
     *
     * @param namespaceURIs String[] of namespace URI's
     */
    public void setSupportedEncodings(String[] namespaceURIs) {
        namespaces.clear();
        for (int i =0; i< namespaceURIs.length; i++) {
            if (!namespaces.contains(namespaceURIs[i])) {
                namespaces.add(namespaceURIs[i]);
            }
        }
    }

    /**
     * isRegistered returns true if the [javaType, xmlType]
     * pair is registered.
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     * @return true if there is a mapping for the given pair, or
     * false if the pair is not specifically registered.
     *
     * For example if called with (java.lang.String[], soapenc:Array)
     * this routine will return false because this pair is
     * probably not specifically registered.
     * However if getSerializer is called with the same pair,
     * the default TypeMapping will use extra logic to find
     * a serializer (i.e. array serializer)
     */
    public boolean isRegistered(Class javaType, QName xmlType) {
        if (javaType == null || xmlType == null) {
            // REMOVED_FOR_TCK
            // return false;
            throw new JAXRPCException(
                    Messages.getMessage(javaType == null ?
                                         "badJavaType" : "badXmlType"));
        }
        if (pair2SF.keySet().contains(new Pair(javaType, xmlType))) {
            return true;
        }
        return false;
    }

    /**
     * Registers SerializerFactory and DeserializerFactory for a
     * specific type mapping between an XML type and Java type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     * @param sf - SerializerFactory
     * @param dsf - DeserializerFactory
     *
     * @throws JAXRPCException - If any error during the registration
     */
    public void register(Class javaType, QName xmlType,
                         javax.xml.rpc.encoding.SerializerFactory sf,
                         javax.xml.rpc.encoding.DeserializerFactory dsf)
        throws JAXRPCException {
        // At least a serializer or deserializer factory must be specified.
        if (sf == null && dsf == null) {
            throw new JAXRPCException(Messages.getMessage("badSerFac"));
        }

        internalRegister(javaType, xmlType, sf, dsf);
    }

    /**
     * Internal version of register(), which allows null factories.
     *
     * @param javaType
     * @param xmlType
     * @param sf
     * @param dsf
     * @throws JAXRPCException
     */
    protected void internalRegister(Class javaType, QName xmlType,
                         javax.xml.rpc.encoding.SerializerFactory sf,
                         javax.xml.rpc.encoding.DeserializerFactory dsf)
            throws JAXRPCException {
        // Both javaType and xmlType must be specified.
        if (javaType == null || xmlType == null) {
            throw new JAXRPCException(
                    Messages.getMessage(javaType == null ?
                                         "badJavaType" : "badXmlType"));
        }

        //REMOVED_FOR_TCK
        //if (sf != null &&
        //    !(sf instanceof javax.xml.rpc.encoding.SerializerFactory)) {
        //    throw new JAXRPCException(message text);
        //}
        //if (dsf != null &&
        //    !(dsf instanceof javax.xml.rpc.encoding.DeserializerFactory)) {
        //    throw new JAXRPCException(message text);
        //}

        Pair pair = new Pair(javaType, xmlType);

        // This code used to not put the xmlType and the JavaType
        // in the maps if it already existed:
        //    if ((dsf != null) || (qName2Pair.get(xmlType) == null))
        // This goes against the philosphy that "last one registered wins".
        // In particular, the mapping for java.lang.Object --> anyType
        // was coming out in WSDL generation under the 1999 XML Schema
        // namespace, which .NET doesn't understand (and is not great anyway).
        qName2Pair.put(xmlType, pair);
        class2Pair.put(javaType, pair);

        if (sf != null)
            pair2SF.put(pair, sf);
        if (dsf != null)
            pair2DF.put(pair, dsf);
    }

    /**
     * Gets the SerializerFactory registered for the specified pair
     * of Java type and XML data type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     *
     * @return Registered SerializerFactory
     *
     * @throws JAXRPCException - If there is no registered SerializerFactory
     * for this pair of Java type and XML data type
     * java.lang.IllegalArgumentException -
     * If invalid or unsupported XML/Java type is specified
     */
    public javax.xml.rpc.encoding.SerializerFactory
        getSerializer(Class javaType, QName xmlType)
        throws JAXRPCException {

        javax.xml.rpc.encoding.SerializerFactory sf = null;

        // If the xmlType was not provided, get one
        if (xmlType == null) {
            xmlType = getTypeQName(javaType, null);
            // If we couldn't find one, we're hosed, since getTypeQName()
            // already asked all of our delegates.
            if (xmlType == null) {
                return null;
            }
        }

        // Try to get the serializer associated with this pair
        Pair pair = new Pair(javaType, xmlType);

        // Now get the serializer with the pair
        sf = (javax.xml.rpc.encoding.SerializerFactory) pair2SF.get(pair);

        // Need to look into hierarchy of component type.
        // ex) java.util.GregorianCalendar[]
        //     -> java.util.Calendar[]
        if (sf == null && javaType.isArray()) {
            int dimension = 1;
            Class componentType = javaType.getComponentType();
            while (componentType.isArray()) {
                dimension += 1;
                componentType = componentType.getComponentType();
            }
            int[] dimensions = new int[dimension];
            componentType = componentType.getSuperclass();
            Class superJavaType = null;
            while (componentType != null) {
    			superJavaType = Array.newInstance(componentType, dimensions).getClass();
                pair = new Pair(superJavaType, xmlType);
                sf = (javax.xml.rpc.encoding.SerializerFactory) pair2SF.get(pair);
                if (sf != null) {
                    break;
                }
                componentType = componentType.getSuperclass();
            }
        }
        
        // check if ArrayOfT(xml)->T[](java) conversion is possible
        if (sf == null && javaType.isArray() && xmlType != null) {
            Pair pair2 = (Pair) qName2Pair.get(xmlType);
            if (pair2 != null 
                    && pair2.javaType != null
                    && !pair2.javaType.isPrimitive() 
                    && ArrayUtil.isConvertable(pair2.javaType, javaType)) {
                sf = (javax.xml.rpc.encoding.SerializerFactory) pair2SF.get(pair2);
            }
        }
        
        // find serializer with xmlType
        if (sf == null && !javaType.isArray() 
                && !Constants.isSchemaXSD(xmlType.getNamespaceURI()) 
                && !Constants.isSOAP_ENC(xmlType.getNamespaceURI())) {
            Pair pair2 = (Pair) qName2Pair.get(xmlType);
            if (pair2 != null && pair2.javaType != null 
                    && !pair2.javaType.isArray()                         // for array
                    && (javaType.isAssignableFrom(pair2.javaType) || 
                       (pair2.javaType.isPrimitive() && javaType == JavaUtils.getWrapperClass(pair2.javaType))))       // for derived type (xsd:restriction) 
            {
                sf = (javax.xml.rpc.encoding.SerializerFactory) pair2SF.get(pair2);
            }
        }
        
        return sf;
    }
    
    public SerializerFactory finalGetSerializer(Class javaType) {
        Pair pair;
        if (isArray(javaType)) {
            pair = (Pair) qName2Pair.get(Constants.SOAP_ARRAY);
        } else {
            pair = (Pair) class2Pair.get(javaType);
        }
        if (pair != null) {
            return (SerializerFactory)pair2SF.get(pair);
        }

        return null;
    }

    /**
     * Get the exact XML type QName which will be used when serializing a
     * given Class to a given type QName.  In other words, if we have:
     *
     * Class        TypeQName
     * ----------------------
     * Base         myNS:Base
     * Child        myNS:Child
     *
     * and call getXMLType(Child.class, BASE_QNAME), we should get
     * CHILD_QNAME.
     *
     * @param javaType
     * @param xmlType
     * @return the type's QName
     * @throws JAXRPCException
     */
    public QName getXMLType(Class javaType, QName xmlType, boolean encoded)
        throws JAXRPCException
    {
        javax.xml.rpc.encoding.SerializerFactory sf = null;

        // If the xmlType was not provided, get one
        if (xmlType == null) {
            xmlType = getTypeQNameRecursive(javaType);

            // If we couldn't find one, we're hosed, since getTypeQName()
            // already asked all of our delegates.
            if (xmlType == null) {
                return null;
            }
        }

        // Try to get the serializer associated with this pair
        Pair pair = new Pair(javaType, xmlType);

        // Now get the serializer with the pair
        sf = (javax.xml.rpc.encoding.SerializerFactory) pair2SF.get(pair);
        if (sf != null)
            return xmlType;

        // If not successful, use the xmlType to get
        // another pair.  For some xmlTypes (like SOAP_ARRAY)
        // all of the possible javaTypes are not registered.
        if (isArray(javaType)) {
            if (encoded) {
                return Constants.SOAP_ARRAY;
            } else {
                pair = (Pair) qName2Pair.get(xmlType);
            }
        }

        if (pair == null) {
            pair = (Pair) class2Pair.get(javaType);
        }

        if (pair != null) {
            xmlType = pair.xmlType;
        }
        return xmlType;
    }

    /**
     * Gets the DeserializerFactory registered for the specified pair
     * of Java type and XML data type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     *
     * @return Registered DeserializerFactory
     *
     * @throws JAXRPCException - If there is no registered DeserializerFactory
     * for this pair of Java type and  XML data type
     * java.lang.IllegalArgumentException -
     * If invalid or unsupported XML/Java type is specified
     */
    public javax.xml.rpc.encoding.DeserializerFactory
        getDeserializer(Class javaType, QName xmlType, TypeMappingDelegate start)
        throws JAXRPCException {
        if (javaType == null) {
            javaType = start.getClassForQName(xmlType);
            // If we don't have a mapping, we're hosed since getClassForQName()
            // has already asked all our delegates.
            if (javaType == null) {
                return null;
            }
        }

        Pair pair = new Pair(javaType, xmlType);

        return (javax.xml.rpc.encoding.DeserializerFactory) pair2DF.get(pair);
    }
    
    public DeserializerFactory finalGetDeserializer(Class javaType,
                                                    QName xmlType,
                                                    TypeMappingDelegate start) {
        DeserializerFactory df = null;
        if (javaType != null && javaType.isArray()) {
            Class componentType = javaType.getComponentType();

            // HACK ALERT - Don't return the ArrayDeserializer IF
            // the xmlType matches the component type of the array
	    // or if the componentType is the wrappertype of the
	    // xmlType, because that means we're using maxOccurs 
	    // and/or nillable and we'll want the higher layers to 
	    // get the component type deserializer... (sigh)
            if (xmlType != null) {
                Class actualClass = start.getClassForQName(xmlType);
                if (actualClass == componentType
                    || (actualClass != null && (componentType.isAssignableFrom(actualClass)
                        || Utils.getWrapperType(actualClass.getName()).equals(componentType.getName())))) {
			return null;
                }
            }
            Pair pair = (Pair) qName2Pair.get(Constants.SOAP_ARRAY);
            df = (DeserializerFactory) pair2DF.get(pair);
            if (df instanceof ArrayDeserializerFactory && javaType.isArray()) {
                QName componentXmlType = start.getTypeQName(componentType);
                if (componentXmlType != null) {
                    df = new ArrayDeserializerFactory(componentXmlType);
                }
            }
        }
        return df;
    }

    /**
     * Removes the SerializerFactory registered for the specified
     * pair of Java type and XML data type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     *
     * @throws JAXRPCException - If there is error in
     * removing the registered SerializerFactory
     */
    public void removeSerializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        if (javaType == null || xmlType == null) {
            throw new JAXRPCException(
                    Messages.getMessage(javaType == null ?
                                         "badJavaType" : "badXmlType"));
        }

        Pair pair = new Pair(javaType, xmlType);
        pair2SF.remove(pair);
    }

    /**
     * Removes the DeserializerFactory registered for the specified
     * pair of Java type and XML data type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     *
     * @throws JAXRPCException - If there is error in
     * removing the registered DeserializerFactory
     */
    public void removeDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        if (javaType == null || xmlType == null) {
            throw new JAXRPCException(
                    Messages.getMessage(javaType == null ?
                                         "badJavaType" : "badXmlType"));
        }
        Pair pair = new Pair(javaType, xmlType);
        pair2DF.remove(pair);
    }


     /********* End JAX-RPC Compliant Method Definitions *****************/

    /**
     * Gets the QName for the type mapped to Class.
     * @param javaType class or type
     * @return xmlType qname or null
     */
    public QName getTypeQNameRecursive(Class javaType) {
        QName ret = null;
        while (javaType != null) {
            ret = getTypeQName(javaType, null);
            if (ret != null)
                return ret;

            // Walk my interfaces...
            Class [] interfaces = javaType.getInterfaces();
            if (interfaces != null) {
                for (int i = 0; i < interfaces.length; i++) {
                    Class iface = interfaces[i];
                    ret = getTypeQName(iface, null);
                    if (ret != null)
                        return ret;
                }
            }

            javaType = javaType.getSuperclass();
        }
        return null;
    }

    /**
     * Get the QName for this Java class, but only return a specific
     * mapping if there is one.  In other words, don't do special array
     * processing, etc.
     * 
     * @param javaType
     * @return
     */
    public QName getTypeQNameExact(Class javaType, TypeMappingDelegate next) {
        if (javaType == null)
            return null;
       
        QName xmlType = null;
        Pair pair = (Pair) class2Pair.get(javaType);

        if (isDotNetSoapEncFixNeeded() && pair != null ) {
            // Hack alert!
            // If we are in .NET bug compensation mode, skip over any
            // SOAP Encoded types we my find and prefer XML Schema types
            xmlType = pair.xmlType;
            if (Constants.isSOAP_ENC(xmlType.getNamespaceURI()) &&
                    !xmlType.getLocalPart().equals("Array")) {
                pair = null;
            }
        }

        if (pair == null && next != null) {
            // Keep checking up the stack...
            xmlType = next.delegate.getTypeQNameExact(javaType,
                                                      next.next);
        }

        if (pair != null) {
            xmlType = pair.xmlType;
        }

        return xmlType;
    }

    /**
     * isDotNetSoapEncFixNeeded - Do we need to compensate for the dotnet bug.
     * check the service specific flag before using the global flag
     * @return
     */
    private boolean isDotNetSoapEncFixNeeded() {
        MessageContext msgContext = MessageContext.getCurrentContext();
        if (msgContext != null) {
            SOAPService service = msgContext.getService();
            if (service != null) {
                String dotNetSoapEncFix = (String) service.getOption(AxisEngine.PROP_DOTNET_SOAPENC_FIX);
                if (dotNetSoapEncFix != null) {
                    return JavaUtils.isTrue(dotNetSoapEncFix);
                }
            }
        }
        return TypeMappingImpl.dotnet_soapenc_bugfix;
    }

    public QName getTypeQName(Class javaType, TypeMappingDelegate next) {
        QName xmlType = getTypeQNameExact(javaType, next);

        /* If auto-typing is on and the array has the default SOAP_ARRAY QName,
         * then generate a namespace for this array intelligently.   Also
         * register it's javaType and xmlType. List classes and derivitives
         * can't be used because they should be serialized as an anyType array.
         */
        if ( shouldDoAutoTypes() &&
             javaType != List.class &&
             !List.class.isAssignableFrom(javaType) &&
             xmlType != null &&
             xmlType.equals(Constants.SOAP_ARRAY) )
        {
            xmlType = new QName(
                Namespaces.makeNamespace( javaType.getName() ),
                Types.getLocalNameFromFullName( javaType.getName() ) );

            internalRegister( javaType,
                              xmlType,
                              new ArraySerializerFactory(),
                              new ArrayDeserializerFactory() );
        }

        // Can only detect arrays via code
        if (xmlType == null && isArray(javaType)) {

            // get the registered array if any
            Pair pair = (Pair) class2Pair.get(Object[].class);
            // TODO: it always returns the last registered one,
            //  so that's why the soap 1.2 typemappings have to 
            //  move to an other registry to differentiate them
            if (pair != null) {
                xmlType = pair.xmlType;
            } else {
                xmlType = Constants.SOAP_ARRAY;
            }
        }

        /* If the class isn't an array or List and auto-typing is turned on,
        * register the class and it's type as beans.
        */
        if (xmlType == null && shouldDoAutoTypes())
        {
            xmlType = new QName(
                Namespaces.makeNamespace( javaType.getName() ),
                Types.getLocalNameFromFullName( javaType.getName() ) );

            /* If doAutoTypes is set, register a new type mapping for the
            * java class with the above QName.  This way, when getSerializer()
            * and getDeserializer() are called, this QName is returned and
            * these methods do not need to worry about creating a serializer.
            */
            internalRegister( javaType,
                              xmlType,
                              new BeanSerializerFactory(javaType, xmlType),
                              new BeanDeserializerFactory(javaType, xmlType) );
        }

        //log.debug("getTypeQName xmlType =" + xmlType);
        return xmlType;
    }

    public Class getClassForQName(QName xmlType, Class javaType,
                                  TypeMappingDelegate next) {
        if (xmlType == null) {
            return null;
        }

        //log.debug("getClassForQName xmlType =" + xmlType);

        if (javaType != null) {
            // Looking for an exact match first
            Pair pair = new Pair(javaType, xmlType);
            if (pair2DF.get(pair) == null) {
                if (next != null) {
                    javaType = next.getClassForQName(xmlType, javaType);
                }
            }
        }

        if (javaType == null) {
            //look for it in our map
            Pair pair = (Pair) qName2Pair.get(xmlType);
            if (pair == null && next != null) {
                //on no match, delegate
                javaType = next.getClassForQName(xmlType);
            } else if (pair != null) {
                javaType = pair.javaType;
            }
        }
        
        //log.debug("getClassForQName javaType =" + javaType);
        if(javaType == null && shouldDoAutoTypes()) {
            String pkg = Namespaces.getPackage(xmlType.getNamespaceURI());
            if (pkg != null) {
                String className = xmlType.getLocalPart();
                if (pkg.length() > 0) {
                    className = pkg + "." + className;
                }
                try {
                    javaType = ClassUtils.forName(className);
                    internalRegister(javaType,
                                     xmlType,
                                     new BeanSerializerFactory(javaType, xmlType),
                                     new BeanDeserializerFactory(javaType, xmlType));
                } catch (ClassNotFoundException e) {
                }
            }
        }
        return javaType;
    }

    public void setDoAutoTypes(boolean doAutoTypes) {
        this.doAutoTypes = doAutoTypes ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public boolean shouldDoAutoTypes() {
        if(doAutoTypes != null) {
            return doAutoTypes.booleanValue();
        }
        MessageContext msgContext = MessageContext.getCurrentContext();
        if(msgContext != null) {
            if (msgContext.isPropertyTrue("axis.doAutoTypes") ||
                    (msgContext.getAxisEngine() != null && JavaUtils.isTrue(msgContext.getAxisEngine().getOption("axis.doAutoTypes")))) {
                doAutoTypes = Boolean.TRUE;
            }
        }
        if(doAutoTypes == null){
            doAutoTypes = AxisProperties.getProperty("axis.doAutoTypes",
                    "false")
                    .equals("true") ?
                    Boolean.TRUE : Boolean.FALSE;
        }
        return doAutoTypes.booleanValue();
    }

    /**
     * Returns an array of all the classes contained within this mapping
     */
    public Class [] getAllClasses(TypeMappingDelegate next)
    {
        java.util.HashSet temp = new java.util.HashSet();
        if (next != null)
        {
            temp.addAll(java.util.Arrays.asList(next.getAllClasses()));
        }
        temp.addAll(class2Pair.keySet());
        return (Class[])temp.toArray(new Class[temp.size()]);
    }
}