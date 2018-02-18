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

package org.apache.axis.encoding.ser;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.FieldPropertyDescriptor;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * General purpose serializer/deserializerFactory for an arbitrary java bean.
 *
 * @author Sam Ruby (rubys@us.ibm.com)
 * @author Rich Scheuerle (scheu@us.ibm.com)
 * @author Tom Jordahl (tomj@macromedia.com)
 */
public class BeanSerializer implements Serializer, Serializable {

    protected static Log log =
        LogFactory.getLog(BeanSerializer.class.getName());

    private static final QName MUST_UNDERSTAND_QNAME = 
        new QName(Constants.URI_SOAP11_ENV, Constants.ATTR_MUST_UNDERSTAND);
    private static final Object[] ZERO_ARGS =
        new Object [] { "0" };

    QName xmlType;
    Class javaType;

    protected BeanPropertyDescriptor[] propertyDescriptor = null;
    protected TypeDesc typeDesc = null;

    // Construct BeanSerializer for the indicated class/qname
    public BeanSerializer(Class javaType, QName xmlType) {
        this(javaType, xmlType, TypeDesc.getTypeDescForClass(javaType));
    }

    // Construct BeanSerializer for the indicated class/qname
    public BeanSerializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
        this(javaType, xmlType, typeDesc, null);

        if (typeDesc != null) {
            propertyDescriptor = typeDesc.getPropertyDescriptors();
        } else {
            propertyDescriptor = BeanUtils.getPd(javaType, null);
        }
    }

    // Construct BeanSerializer for the indicated class/qname/propertyDesc
    public BeanSerializer(Class javaType, QName xmlType, TypeDesc typeDesc,
                          BeanPropertyDescriptor[] propertyDescriptor) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
        this.propertyDescriptor = propertyDescriptor;
    }

    /**
     * Serialize a bean.  Done simply by serializing each bean property.
     * @param name is the element name
     * @param attributes are the attributes...serialize is free to add more.
     * @param value is the value
     * @param context is the SerializationContext
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        // Check for meta-data in the bean that will tell us if any of the
        // properties are actually attributes, add those to the element
        // attribute list
        Attributes beanAttrs = getObjectAttributes(value, attributes, context);

        // Get the encoding style
        boolean isEncoded = context.isEncoded();

        if (log.isDebugEnabled()) {
            log.debug("Start serializing bean; xmlType=" + xmlType + "; javaType=" + javaType
                    + "; name=" + name + "; isEncoded=" + isEncoded);
        }
        
        // check whether we have and xsd:any namespace="##any" type
        boolean suppressElement = !isEncoded &&
                                  name.getNamespaceURI().equals("") &&
                                  name.getLocalPart().equals("any");

        if (!suppressElement)
            context.startElement(name, beanAttrs);

        // check whether the array is converted to ArrayOfT shema type    
        if (value != null && value.getClass().isArray()) {
           Object newVal = JavaUtils.convert(value, javaType); 
           if (newVal != null && javaType.isAssignableFrom(newVal.getClass())) {
               value = newVal; 
           }
        }
        try {
            // Serialize each property
            for (int i=0; i<propertyDescriptor.length; i++) {
                String propName = propertyDescriptor[i].getName();
                if (propName.equals("class"))
                    continue;
                QName qname = null;
                QName xmlType = null;
                Class javaType = propertyDescriptor[i].getType();

                boolean isOmittable = false;
                // isNillable default value depends on the field type
                boolean isNillable = Types.isNullable(javaType);
                // isArray
                boolean isArray = false;
                QName itemQName = null;

                // If we have type metadata, check to see what we're doing
                // with this field.  If it's an attribute, skip it.  If it's
                // an element, use whatever qname is in there.  If we can't
                // find any of this info, use the default.
                if (typeDesc != null) {
                    FieldDesc field = typeDesc.getFieldByName(propName);
                    if (field != null) {
                        if (!field.isElement()) {
                            continue;
                        }

                        ElementDesc element = (ElementDesc)field;

                        // If we're SOAP encoded, just use the local part,
                        // not the namespace.  Otherwise use the whole
                        // QName.
                        if (isEncoded) {
                            qname = new QName(element.getXmlName().getLocalPart());
                        } else {
                            qname = element.getXmlName();
                        }
                        isOmittable = element.isMinOccursZero();
                        isNillable = element.isNillable();
                        isArray = element.isMaxOccursUnbounded();
                        xmlType = element.getXmlType();
                        itemQName = element.getItemQName();
                        context.setItemQName(itemQName);
                    }
                }

                if (qname == null) {
                    qname = new QName(isEncoded ? "" : name.getNamespaceURI(),
                                      propName);
                }

                if (xmlType == null) {
                    // look up the type QName using the class
                    xmlType = context.getQNameForClass(javaType);
                }

                // Read the value from the property
                if (propertyDescriptor[i].isReadable()) {
                    if (itemQName != null ||
                            (!propertyDescriptor[i].isIndexed() && !isArray)) {
                        // Normal case: serialize the value
                        Object propValue =
                            propertyDescriptor[i].get(value);


                        if (propValue == null) {
                            // an element cannot be null if nillable property is set to
                            // "false" and the element cannot be omitted
                            if (!isNillable && !isOmittable) {
                                if (Number.class.isAssignableFrom(javaType)) {
                                    // If we have a null and it's a number, though,
                                    // we might turn it into the appropriate kind of 0.
                                    // TODO : Should be caching these constructors?
                                    try {
                                        Constructor constructor =
                                                javaType.getConstructor(
                                                        SimpleDeserializer.STRING_CLASS);
                                        propValue = constructor.newInstance(ZERO_ARGS);
                                    } catch (Exception e) {
                                        // If anything goes wrong here, oh well we tried.
                                    }
                                }

                                if (propValue == null) {
                                    throw new IOException(
                                            Messages.getMessage(
                                                    "nullNonNillableElement",
                                                    propName));
                                }
                            }

                            // if meta data says minOccurs=0, then we can skip
                            // it if its value is null and we aren't doing SOAP
                            // encoding.
                            if (isOmittable && !isEncoded) {
                                continue;
                            }
                        }

                        context.serialize(qname,
                                          null,
                                          propValue,
                                          xmlType, javaType);
                    } else {
                        // Collection of properties: serialize each one
                        int j=0;
                        while(j >= 0) {
                            Object propValue = null;
                            try {
                                propValue =
                                    propertyDescriptor[i].get(value, j);
                                j++;
                            } catch (Exception e) {
                                j = -1;
                            }
                            if (j >= 0) {
                                context.serialize(qname, null,
                                                  propValue, xmlType, propertyDescriptor[i].getType());
                            }
                        }
                    }
                }
            }

            BeanPropertyDescriptor anyDesc = typeDesc == null ? null :
                    typeDesc.getAnyDesc();
            if (anyDesc != null) {
                // If we have "extra" content here, it'll be an array
                // of MessageElements.  Serialize each one.
                Object anyVal = anyDesc.get(value);
                if (anyVal != null && anyVal instanceof MessageElement[]) {
                    MessageElement [] anyContent = (MessageElement[])anyVal;
                    for (int i = 0; i < anyContent.length; i++) {
                        MessageElement element = anyContent[i];
                        element.output(context);
                    }
                }
            }
        } catch (InvocationTargetException ite) {
            Throwable target = ite.getTargetException();
            log.error(Messages.getMessage("exception00"), target);
            throw new IOException(target.toString());
        } catch (Exception e) {
            log.error(Messages.getMessage("exception00"), e);
            throw new IOException(e.toString());
        }

        if (!suppressElement)
            context.endElement();
    }



    public String getMechanismType() { return Constants.AXIS_SAX; }

    /**
     * Return XML schema for the specified type, suitable for insertion into
     * the &lt;types&gt; element of a WSDL document, or underneath an
     * &lt;element&gt; or &lt;attribute&gt; declaration.
     *
     * @param javaType the Java Class we're writing out schema for
     * @param types the Java2WSDL Types object which holds the context
     *              for the WSDL being generated.
     * @return a type element containing a schema simpleType/complexType
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public Element writeSchema(Class javaType, Types types) throws Exception {

        // ComplexType representation of bean class
        Element complexType = types.createElement("complexType");

        // See if there is a super class, stop if we hit a stop class
        Element e = null;
        Class superClass = javaType.getSuperclass();
        BeanPropertyDescriptor[] superPd = null;
        List stopClasses = types.getStopClasses();
        if (superClass != null &&
                superClass != java.lang.Object.class &&
                superClass != java.lang.Exception.class &&
                superClass != java.lang.Throwable.class &&
                superClass != java.lang.RuntimeException.class &&
                superClass != java.rmi.RemoteException.class &&
                superClass != org.apache.axis.AxisFault.class &&
                (stopClasses == null ||
                !(stopClasses.contains(superClass.getName()))) ) {
            // Write out the super class
            String base = types.writeType(superClass);
            Element complexContent = types.createElement("complexContent");
            complexType.appendChild(complexContent);
            Element extension = types.createElement("extension");
            complexContent.appendChild(extension);
            extension.setAttribute("base", base);
            e = extension;
            // Get the property descriptors for the super class
            TypeDesc superTypeDesc = TypeDesc.getTypeDescForClass(superClass);
            if (superTypeDesc != null) {
                superPd = superTypeDesc.getPropertyDescriptors();
            } else {
                superPd = BeanUtils.getPd(superClass, null);
            }
        } else {
            e = complexType;
        }

        // Add fields under sequence element.
        // Note: In most situations it would be okay
        // to put the fields under an all element.
        // However it is illegal schema to put an
        // element with minOccurs=0 or maxOccurs>1 underneath
        // an all element.  This is the reason why a sequence
        // element is used.
        Element all = types.createElement("sequence");
        e.appendChild(all);

        if (Modifier.isAbstract(javaType.getModifiers())) {
            complexType.setAttribute("abstract", "true");
        }

        // Serialize each property
        for (int i=0; i<propertyDescriptor.length; i++) {
            String propName = propertyDescriptor[i].getName();

            // Don't serializer properties named class
            boolean writeProperty = true;
            if (propName.equals("class")) {
                writeProperty = false;
            }

            // Don't serialize the property if it is present
            // in the super class property list
            if (superPd != null && writeProperty) {
                for (int j=0; j<superPd.length && writeProperty; j++) {
                    if (propName.equals(superPd[j].getName())) {
                        writeProperty = false;
                    }
                }
            }
            if (!writeProperty) {
                continue;
            }

            // If we have type metadata, check to see what we're doing
            // with this field.  If it's an attribute, skip it.  If it's
            // an element, use whatever qname is in there.  If we can't
            // find any of this info, use the default.

            if (typeDesc != null) {
                Class fieldType = propertyDescriptor[i].getType();
                FieldDesc field = typeDesc.getFieldByName(propName);

                if (field != null) {
                    QName qname = field.getXmlName();
                    QName fieldXmlType = field.getXmlType();
                    boolean isAnonymous = fieldXmlType != null && fieldXmlType.getLocalPart().startsWith(">");

                    if (qname != null) {
                        // FIXME!
                        // Check to see if this is in the right namespace -
                        // if it's not, we need to use an <element ref="">
                        // to represent it!!!

                        // Use the default...
                        propName = qname.getLocalPart();
                    }
                    if (!field.isElement()) {
                        writeAttribute(types,
                                       propName,
                                       fieldType,
                                       fieldXmlType,
                                       complexType);
                    } else {
                        writeField(types,
                                   propName,
                                   fieldXmlType,
                                   fieldType,
                                   propertyDescriptor[i].isIndexed(),
                                   field.isMinOccursZero(),
                                   all, isAnonymous,
                                   ((ElementDesc)field).getItemQName());
                    }
                } else {
                    writeField(types,
                               propName,
                               null,
                               fieldType,
                               propertyDescriptor[i].isIndexed(), false, all, false, null);
                }
            } else {
                boolean done = false;
                if (propertyDescriptor[i] instanceof FieldPropertyDescriptor){
                    FieldPropertyDescriptor fpd = (FieldPropertyDescriptor) propertyDescriptor[i];
                    Class clazz = fpd.getField().getType();
                    if(types.getTypeQName(clazz)!=null) {
                        writeField(types,
                                   propName,
                                   null,
                                   clazz,
                                   false, false, all, false, null);
                   
                        done = true;
                    }
                }
                if(!done) {
                    writeField(types,
                               propName,
                               null,
                               propertyDescriptor[i].getType(),
                               propertyDescriptor[i].isIndexed(), false, all, false, null);
                }                    
                
            }
        }

        // done
        return complexType;
    }

    /**
     * write a schema representation of the given Class field and append it to
     * the where Node, recurse on complex types
     * @param fieldName name of the field
     * @param xmlType the schema type of the field
     * @param fieldType type of the field
     * @param isUnbounded causes maxOccurs="unbounded" if set
     * @param where location for the generated schema node
     * @param itemQName
     * @throws Exception
     */
    protected void writeField(Types types,
                              String fieldName,
                              QName xmlType,
                              Class fieldType,
                              boolean isUnbounded,
                              boolean isOmittable,
                              Element where,
                              boolean isAnonymous,
                              QName itemQName) throws Exception {
        Element elem;
        String elementType = null;

        if (isAnonymous) {
            elem = types.
                    createElementWithAnonymousType(fieldName,
                                                   fieldType,
                                                   isOmittable,
                                                   where.getOwnerDocument());
        } else {
            if (!SchemaUtils.isSimpleSchemaType(xmlType) &&
                    Types.isArray(fieldType)) {
                xmlType = null;
            }

            if (itemQName != null &&
                    SchemaUtils.isSimpleSchemaType(xmlType) &&
                    Types.isArray(fieldType)) {
                xmlType = null;
            }

            QName typeQName = types.writeTypeAndSubTypeForPart(fieldType, xmlType);
            elementType = types.getQNameString(typeQName);

            if (elementType == null) {
                // If writeType returns null, then emit an anytype.
                QName anyQN = Constants.XSD_ANYTYPE;
                String prefix = types.getNamespaces().
                        getCreatePrefix(anyQN.getNamespaceURI());
                elementType = prefix + ":" + anyQN.getLocalPart();
            }

            // isNillable default value depends on the field type
            boolean isNillable = Types.isNullable(fieldType);
            if (typeDesc != null) {
                FieldDesc field = typeDesc.getFieldByName(fieldName);
                if (field != null && field.isElement()) {
                    isNillable = ((ElementDesc)field).isNillable();
                }
            }

            elem = types.createElement(fieldName,
                    elementType,
                    isNillable,
                    isOmittable,
                    where.getOwnerDocument());
        }

        if (isUnbounded) {
            elem.setAttribute("maxOccurs", "unbounded");
        }

        where.appendChild(elem);
    }

    /**
     * write aa attribute element and append it to the 'where' Node
     * @param fieldName name of the field
     * @param fieldType type of the field
     * @param where location for the generated schema node
     * @throws Exception
     */
    protected void writeAttribute(Types types,
                                String fieldName,
                                Class fieldType,
                                QName fieldXmlType,
                                Element where) throws Exception {

        // Attribute must be a simple type.
        if (!types.isAcceptableAsAttribute(fieldType)) {
            throw new AxisFault(Messages.getMessage("AttrNotSimpleType00",
                                                     fieldName,
                                                     fieldType.getName()));
        }
        Element elem = types.createAttributeElement(fieldName,
                                           fieldType, fieldXmlType,
                                           false,
                                           where.getOwnerDocument());
        where.appendChild(elem);
    }

    /**
     * Check for meta-data in the bean that will tell us if any of the
     * properties are actually attributes, add those to the element
     * attribute list
     *
     * @param value the object we are serializing
     * @return attributes for this element, null if none
     */
    protected Attributes getObjectAttributes(Object value,
                                           Attributes attributes,
                                           SerializationContext context) {

        if (typeDesc == null || !typeDesc.hasAttributes())
            return attributes;

        AttributesImpl attrs;
        if (attributes == null) {
            attrs = new AttributesImpl();
        } else if (attributes instanceof AttributesImpl) {
            attrs = (AttributesImpl)attributes;
        } else {
            attrs = new AttributesImpl(attributes);
        }

        try {
            // Find each property that is an attribute
            // and add it to our attribute list
            for (int i=0; i<propertyDescriptor.length; i++) {
                String propName = propertyDescriptor[i].getName();
                if (propName.equals("class"))
                    continue;

                FieldDesc field = typeDesc.getFieldByName(propName);
                // skip it if its not an attribute
                if (field == null || field.isElement())
                    continue;

                QName qname = field.getXmlName();
                if (qname == null) {
                    qname = new QName("", propName);
                }

                if (propertyDescriptor[i].isReadable() &&
                    !propertyDescriptor[i].isIndexed()) {
                    // add to our attributes
                    Object propValue = propertyDescriptor[i].get(value);
                    // Convert true/false to 1/0 in case of soapenv:mustUnderstand
                    if (qname.equals(MUST_UNDERSTAND_QNAME)) {
                    	if (propValue.equals(Boolean.TRUE)) {
                                propValue = "1";
                    	} else if (propValue.equals(Boolean.FALSE)) {
                    		propValue = "0";
                    	}
                    }
                    // If the property value does not exist, don't serialize
                    // the attribute.  In the future, the decision to serializer
                    // the attribute may be more sophisticated.  For example, don't
                    // serialize if the attribute matches the default value.
                    if (propValue != null) {
                        setAttributeProperty(propValue,
                                             qname,
                                             field.getXmlType(), 
                                             field.getJavaType(),
                                             attrs,
                                             context);
                    }
                }
            }
        } catch (Exception e) {
            // no attributes
            return attrs;
        }

        return attrs;
    }

    private void setAttributeProperty(Object propValue,
                                      QName qname,
                                      QName xmlType,
                                      Class javaType,
                                      AttributesImpl attrs,
                                      SerializationContext context) throws Exception {

        String namespace = qname.getNamespaceURI();
        String localName = qname.getLocalPart();

        // org.xml.sax.helpers.AttributesImpl JavaDoc says: "For the
        // sake of speed, this method does no checking to see if the
        // attribute is already in the list: that is the
        // responsibility of the application." check for the existence
        // of the attribute to avoid adding it more than once.
        if (attrs.getIndex(namespace, localName) != -1) {
            return;
        }

        String propString = context.getValueAsString(propValue, xmlType, javaType);

        attrs.addAttribute(namespace,
                           localName,
                           context.attributeQName2String(qname),
                           "CDATA",
                           propString);
    }
}
