/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.encoding;

import org.apache.axis.InternalException;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.JavaUtils;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Hashtable;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

/**
 * General purpose serializer/deserializerFactory for an arbitrary java bean.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 * @author Rich Scheuerle <scheu@us.ibm.com>
 */
public class BeanSerializer extends Deserializer
    implements Serializer, Serializable
{
    static Category category =
            Category.getInstance(BeanSerializer.class.getName());
    private Class cls;
    private MyPropertyDescriptor[] pd = null;
    private EnumSerializer enumSerializer = null;
    private static final Object[] noArgs = new Object[] {};  // For convenience

    // When serializing, the property element names passed over the wire
    // are the names of the properties (format=PROPERTY_NAME).
    // Setting the format to FORCE_UPPER will cause the
    // serializer to uppercase the first letter of the property element name.
    // Setting the format to FORCE_LOWER will cause the
    // serializer to uppercase the first letter of the property element name.
    private short elementPropertyFormat = PROPERTY_NAME;
    public static short PROPERTY_NAME = 0;
    public static short FORCE_UPPER   = 1;
    public static short FORCE_LOWER   = 2;
    
    private static class MyPropertyDescriptor {
        private String name;
        private Method getter;
        private Method setter;

        public MyPropertyDescriptor(String _name, Method _getter, Method _setter) {
            name = _name;
            getter = _getter;
            setter = _setter;
        }
        public Method getReadMethod()  { return getter; }
        public Method getWriteMethod() { return setter; }
        public String getName() {return name;}
        public Class getType() {return getter.getReturnType(); }
    }

    // This counter is updated to deal with deserialize collection properties
    protected int collectionIndex = -1;
    /**
     * Default constructor.
     */
    public BeanSerializer() {
        super();
    }

    /**
     * Constructor that takes a class.  Provided only for convenience.
     * Equivalent to calling setCls(cls) on a new instance.
     */
    public BeanSerializer(Class cls) {
        super();
        setCls(cls);
    }

    /**
     * Constructor that takes a class and a format (PROPERTY_NAME, FORCE_UPPER, FORCE_LOWER)
     * Equivalent to calling setCls(cls) on a new instance.
     */
    public BeanSerializer(Class cls, short format) {
        this(cls);
        if (format > FORCE_LOWER ||
            format < PROPERTY_NAME)
            format = PROPERTY_NAME;
        this.elementPropertyFormat = format;
    }

    /**
     * Constructor that takes a class and a PropertyDescriptor array
     */
    public BeanSerializer(Class cls, PropertyDescriptor[] pd) {
        this(cls);
        this.pd = processPropertyDescriptors(pd,cls);
    }

    /**
     * Class being serialized/deserialized
     */
    protected void setCls(Class cls) {
        this.cls = cls;
    }


    /**
     * Property Descriptors.  Retrieved once and cached in the serializer.
     */
    protected MyPropertyDescriptor[] getPd() {
        if (pd==null) {
            try {
               PropertyDescriptor[] rawPd = Introspector.getBeanInfo(cls).getPropertyDescriptors();
               pd = processPropertyDescriptors(rawPd,cls);
            } catch (Exception e) {
               // this should never happen
               throw new InternalException(e);
            }
            // If this is an enumeration class, delegate all serialization to the enum serializer
            if (isEnumClass(cls)) {
                enumSerializer = new EnumSerializer(cls);
            }
        }

        return pd;
    }

    /** 
     * This method attempts to sort the property descriptors to match the 
     * order defined in the class.  This is necessary to support 
     * xsd:sequence processing, which means that the serialized order of 
     * properties must match the xml element order.  (This method assumes that the
     * order of the set methods matches the xml element order...the emitter 
     * will always order the set methods according to the xml order.)
     *
     * This routine also looks for set(i, type) and get(i) methods and adjusts the 
     * property to use these methods instead.  These methods are generated by the
     * emitter for "collection" of properties (i.e. maxOccurs="unbounded" on an element).
     * JAX-RPC is silent on this issue, but web services depend on this kind of behaviour.
     * The method signatures were chosen to match bean indexed properties.
     */
    protected static MyPropertyDescriptor[] processPropertyDescriptors(
                  PropertyDescriptor[] rawPd, Class cls) {
        MyPropertyDescriptor[] myPd = new MyPropertyDescriptor[rawPd.length];

        for (int i=0; i < rawPd.length; i++) {
            myPd[i] = new MyPropertyDescriptor(rawPd[i].getName(), 
                                               rawPd[i].getReadMethod(), 
                                               rawPd[i].getWriteMethod());
        }
        
        try {
            // Create a new pd array and index into the array
            int index = 0;

            // Build a new pd array
            // defined by the order of the get methods. 
            MyPropertyDescriptor[] newPd = new MyPropertyDescriptor[rawPd.length];
            Method[] methods = cls.getMethods();
            for (int i=0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getName().startsWith("set")) {
                    boolean found = false;
                    for (int j=0; j < myPd.length && !found; j++) {
                        if (myPd[j].getWriteMethod() != null &&
                            myPd[j].getWriteMethod().equals(method)) {
                            found = true;
                            newPd[index] = myPd[j];
                            index++;
                        }
                    }
                }
            }
            // Now if there are any additional property descriptors, add them to the end.
            if (index < myPd.length) {
                for (int m=0; m < myPd.length && index < myPd.length; m++) {
                    boolean found = false;           
                    for (int n=0; n < index && !found; n++) {
                        found = (myPd[m]==newPd[n]);
                    }
                    if (!found) {
                        newPd[index] = myPd[m];
                        index++;
                    }
                }
            }
            // If newPd has same number of elements as myPd, use newPd.
            if (index == myPd.length) {
                myPd = newPd;
            }

            // Get the methods of the class and look for the special set and
            // get methods for property "collections"
            for (int i=0; i < methods.length; i++) {
                if (methods[i].getName().startsWith("set") && 
                    methods[i].getParameterTypes().length == 2) {
                    for (int j=0; j < methods.length; j++) {
                        if ((methods[j].getName().startsWith("get") ||
                             methods[j].getName().startsWith("is")) &&
                            methods[j].getParameterTypes().length == 1 &&
                            methods[j].getReturnType() == methods[i].getParameterTypes()[1] &&
                            methods[j].getParameterTypes()[0] == int.class &&
                            methods[i].getParameterTypes()[0] == int.class) {
                            for (int k=0; k < myPd.length; k++) {
                                if (myPd[k].getReadMethod() != null &&
                                    myPd[k].getWriteMethod() != null &&
                                    myPd[k].getReadMethod().getName().equals(methods[j].getName()) &&
                                    myPd[k].getWriteMethod().getName().equals(methods[i].getName())) {
                                    myPd[k] = new MyPropertyDescriptor(myPd[k].getName(),
                                                                       methods[j],
                                                                       methods[i]);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Don't process Property Descriptors if problems occur
            return myPd;
        }
        return myPd;
    }

    protected MyPropertyDescriptor [] getPd(Object val)
    {
      if (cls == null) cls = val.getClass();
      return getPd();
    }

    protected void setPd(PropertyDescriptor[] rawPd) {
        this.pd = processPropertyDescriptors(rawPd, cls);
    }

    /**
     * Determine if the class is a JAX-RPC enum class.
     * An enumeration class is recognized by
     * a getValue() method, a toString() method, a fromString(String) method
     * a fromValue(type) method and the lack
     * of a setValue(type) method
     */
    protected static boolean isEnumClass(Class cls) {
        try {
            java.lang.reflect.Method m  = cls.getMethod("getValue", null);
            java.lang.reflect.Method m2 = cls.getMethod("toString", null);
            java.lang.reflect.Method m3 = cls.getMethod("fromString",
                                                        new Class[] {java.lang.String.class});

            if (m != null && m2 != null && m3 != null &&
                cls.getMethod("fromValue", new Class[] {m.getReturnType()}) != null) {
                try {
                    if (cls.getMethod("setValue",  new Class[] {m.getReturnType()}) == null)
                        return true;
                    return false;
                } catch (java.lang.NoSuchMethodException e) {
                    return true;  // getValue & fromValue exist.  setValue does not exist.  Thus return true. 
                }
            }
        } catch (java.lang.NoSuchMethodException e) {}
        return false;
    }  

    public static DeserializerFactory getFactory()
    {
      return new BeanDeserFactory();
    }

    /**
     * BeanDeSerializer Factory that creates instances with the specified
     * class.  Caches the PropertyDescriptor
     */
    public static class BeanDeserFactory implements DeserializerFactory {
        private static Hashtable propertyDescriptors = new Hashtable();
        private MyPropertyDescriptor [] pd;
      
        private Class cls;
        private DeserializerFactory realDeserializerFactory;

        public void setJavaClass(Class cls) throws IntrospectionException {
            if ( (this.cls != null) && (this.cls != cls) ) {
                throw new InternalException("Attempt to change class");
            }

            this.cls = cls;

            pd = (MyPropertyDescriptor [])propertyDescriptors.get(cls);
            if (pd == null) {
                PropertyDescriptor[] rawPd = 
                    Introspector.getBeanInfo(cls).getPropertyDescriptors();
                pd = processPropertyDescriptors(rawPd, cls);
                propertyDescriptors.put(cls, pd);
            }

            // If an enum class, use that factory class instead...
            if (isEnumClass(cls)) {
                realDeserializerFactory = EnumSerializer.getFactory(cls);
            }                
        }

        public Deserializer getDeserializer() {

            // If this factory is just a proxy, use the real deserializer
            // instead.
            //
            // Question: wouldn't it be better to require the deployer to use
            // the right factory in the first place?
            if (realDeserializerFactory != null) {
                return realDeserializerFactory.getDeserializer();
            }                

            BeanSerializer bs = new BeanSerializer();
            bs.setCls(cls);
            bs.pd = pd;

            try {
                bs.setValue(cls.newInstance());
            } catch (Exception e) {
                throw new InternalException(e);
            }

            return bs;
        }

        /**
         * Override serialization - all that is needed is the class
         */
        private static final ObjectStreamField[] serialPersistentFields =
            {new ObjectStreamField("cls", Class.class)};
    }

    /**
     * Class which knows how to update a bean property
     */
    class PropertyTarget implements Target {
        private Object object;
        private MyPropertyDescriptor pd;
        private int index = -1;

        /** 
         * This constructor is used for a normal property.
         * @param Object is the bean class
         * @param pd is the property
         **/
        public PropertyTarget(Object object, MyPropertyDescriptor pd) {
            this.object = object;
            this.pd     = pd;
            this.index  = -1;  // disable indexing
        }

        /** 
         * This constructor is used for an indexed property.
         * @param Object is the bean class
         * @param pd is the property
         * @param i is the index          
         **/
        public PropertyTarget(Object object, MyPropertyDescriptor pd, int i) {
            this.object = object;
            this.pd     = pd;
            this.index  = i;
        }

        public void set(Object value) throws SAXException {
            try {
                if (index < 0)
                    pd.getWriteMethod().invoke(object, new Object[] {value});
                else
                    pd.getWriteMethod().invoke(object, new Object[] {new Integer(index), value});
            } catch (Exception e) {
                Class type = pd.getReadMethod().getReturnType();
                value = JavaUtils.convert(value, type);
                try {
                    if (index < 0)
                        pd.getWriteMethod().invoke(object, new Object[] {value});
                    else
                        pd.getWriteMethod().invoke(object, new Object[] {new Integer(index), value});
                } catch (Exception ex) {
                    String field= pd.getName();
                    int i = 0;
                    if (index >=0) {
                        field += "[" + index + "]";
                        i = 1;
                    }
                    category.error(JavaUtils.getMessage(
                            "cantConvert02",
                            new String[] {
                                    value.getClass().getName(),
                                    field,
                                    pd.getType().getName()}));
                    throw new SAXException(ex);
                }
            }
        }
    }

    /**
     * Deserializer interface called on each child element encountered in
     * the XML stream.
     */
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        MyPropertyDescriptor[] pd = getPd();

        // create a value if there isn't one already...
        if (value==null) {
            try {
                value=cls.newInstance();
            } catch (Exception e) {
                throw new SAXException(e.toString());
           }
        }

        // look for a field by this name.  Assumes the the number of
        // properties in a bean is (relatively) small, so uses a linear
        // search.  Accept a property if it differs only by the 
        // capitalization of the first character.
        String localNameUp = format(localName, FORCE_UPPER);
        String localNameLo = format(localName, FORCE_LOWER);
        for (int i=0; i<pd.length; i++) {
            if (pd[i].getName().equals(localNameUp) ||
                pd[i].getName().equals(localNameLo)) {

                // determine the QName for this child element
                TypeMappingRegistry tmr = context.getTypeMappingRegistry();
                Class type = pd[i].getType();
                QName qn = tmr.getTypeQName(type);
                if (qn == null)
                    throw new SAXException(
                            JavaUtils.getMessage("unregistered00", "" + type));

                // get the deserializer
                Deserializer dSer = tmr.getDeserializer(qn);
                if (dSer == null)
                    throw new SAXException(
                            JavaUtils.getMessage("noDeser00", "" + type));

                if (pd[i].getWriteMethod().getParameterTypes().length == 1) {
                    // Success!  Register the target and deserializer.
                    collectionIndex = -1;
                    dSer.registerValueTarget(new PropertyTarget(value, pd[i]));
                    return dSer;
                } else {
                    // Success! This is a collection of properties so use the index
                    collectionIndex++;
                    dSer.registerValueTarget(new PropertyTarget(value, pd[i], collectionIndex));
                    return dSer;
                }
                    
            }
        }

        // No such field
        throw new SAXException(
                JavaUtils.getMessage("badElem00", cls.getName(), localName));
    }

    /**
     * Serialize a bean.  Done simply by serializing each bean property.
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        MyPropertyDescriptor[] pd = getPd(value);
        if (enumSerializer != null) {
          enumSerializer.serialize(name, attributes, value, context);
          return;
        }

        context.startElement(name, attributes);

        try {
            for (int i=0; i<pd.length; i++) {
                String propName = pd[i].getName();                
                if (propName.equals("class")) continue;
                propName = format(propName, elementPropertyFormat);
                
                Method readMethod = pd[i].getReadMethod();
                if (readMethod.getParameterTypes().length == 0) {
                    // Normal case: serialize the value
                    Object propValue = pd[i].getReadMethod().invoke(value,noArgs);
                    context.serialize(new QName("", propName), null, propValue);
                } else {
                    // Collection of properties: serialize each one
                    int j=0;
                    while(j >= 0) {
                        Object propValue = null;
                        try {
                            propValue = pd[i].getReadMethod().invoke(value,
                                                                     new Object[] { new Integer(j) });
                            j++;
                        } catch (Exception e) {
                            j = -1;
                        }
                        if (j >= 0) {
                            context.serialize(new QName("", propName), null, propValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.toString());
        }

        context.endElement();
    }

    /**
     * Returns the property name string formatted in the specified manner
     * @param name to format
     * @param fmt (PROPERTY_NAME, FORCE_LOWER, FORCE_UPPER)
     * @return formatted name 
     */
    private String format(String name, short fmt) {
        if (fmt == PROPERTY_NAME)
            return name;
        String theRest = "";
        if (name.length() > 1)
            theRest = name.substring(1);
        if (fmt == FORCE_UPPER)
            return Character.toUpperCase(name.charAt(0)) + theRest;
        else
            return Character.toLowerCase(name.charAt(0)) + theRest;
    }


    /**
     * Override serialization - all that is needed is the class
     */
    private static final ObjectStreamField[] serialPersistentFields =
        {new ObjectStreamField("cls", Class.class)};

}
