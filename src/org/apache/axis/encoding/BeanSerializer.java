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
 */
public class BeanSerializer extends Deserializer
    implements Serializer, Serializable
{
    static Category category =
            Category.getInstance(BeanSerializer.class.getName());

    /**
     * Class being serialized/deserialized
     */
    private Class cls;

    protected void setCls(Class cls) {
        this.cls = cls;
    }

    /**
     * Property Descriptors.  Retrieved once and cached in the serializer.
     */
    private PropertyDescriptor[] pd = null;
    private EnumSerializer enumSerializer = null;

    protected PropertyDescriptor[] getPd() {
        if (pd==null) {
            try {
               pd = Introspector.getBeanInfo(cls).getPropertyDescriptors();
               pd = sortPropertyDescriptors(pd,cls);
            } catch (Exception e) {
               // this should never happen
               throw new NullPointerException(e.toString());
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
     */
    protected static PropertyDescriptor[] sortPropertyDescriptors(PropertyDescriptor[] oldPd, Class cls) {
        try {
            // Create a new pd array and index into the array
            PropertyDescriptor[] newPd = new PropertyDescriptor[oldPd.length];
            int index = 0;
            
            // Get the methods of the class and build a new pd array
            // defined by the order of the get methods.
            Method[] methods = cls.getMethods();
            for (int i=0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getName().startsWith("set")) {
                    boolean found = false;
                    for (int j=0; j < oldPd.length && !found; j++) {
                        if (oldPd[j].getWriteMethod() != null &&
                            oldPd[j].getWriteMethod().equals(method)) {
                            found = true;
                            newPd[index] = oldPd[j];
                            index++;
                        }
                    }
                }
            }
            // Now if there are any additional property descriptors, add them to the end.
            if (index < oldPd.length) {
                for (int m=0; m < oldPd.length && index < oldPd.length; m++) {
                    boolean found = false;           
                    for (int n=0; n < index && !found; n++) {
                        found = (oldPd[m]==newPd[n]);
                    }
                    if (!found) {
                        newPd[index] = oldPd[m];
                        index++;
                    }
                }
            }
            // If newPd has same number of elements as oldPd, use newPd.
            if (index == oldPd.length) {
                oldPd = newPd;
            }
        } catch (Exception e) {
            // Don't sort Property Descriptors if problems occur
            return oldPd;
        }
        return oldPd;
    }

    protected PropertyDescriptor [] getPd(Object val)
    {
      if (cls == null) cls = val.getClass();
      return getPd();
    }

    protected void setPd(PropertyDescriptor[] pd) {
        this.pd = pd;
    }

    /**
     * Determine if the class is a JAX-RPC enum class.
     * An enumeration class is recognized by
     * a getValue() method, a fromValue(type) method and the lack
     * of a setValue(type) method
     */
    protected static boolean isEnumClass(Class cls) {
        try {
            java.lang.reflect.Method m = cls.getMethod("getValue", null);
            if (m != null &&
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
        this.cls = cls;
    }

    /**
     * An array of nothing, defined only once.
     */
    private static final Object[] noArgs = new Object[] {};

    public static DeserializerFactory getFactory()
    {
      return new BeanSerFactory();
    }

    /**
     * BeanSerializer Factory that creates instances with the specified
     * class.  Caches the PropertyDescriptor
     */
    public static class BeanSerFactory implements DeserializerFactory {
        private Hashtable propertyDescriptors = new Hashtable();
      
        public Deserializer getDeserializer(Class cls) {
            PropertyDescriptor [] pd =
                  (PropertyDescriptor [])propertyDescriptors.get(cls);
            if (pd == null) {
                try {
                    pd = Introspector.getBeanInfo(cls).getPropertyDescriptors();
                    pd = sortPropertyDescriptors(pd, cls);
                } catch (IntrospectionException e) {
                    return null;
                }
                propertyDescriptors.put(cls, pd);
            }

            // If an enum class.  Return the Deserializer for Enumeration
            if (isEnumClass(cls)) {
                return EnumSerializer.getFactory().getDeserializer(cls);
            }                

            BeanSerializer bs = new BeanSerializer();
            bs.setCls(cls);
            bs.setPd(pd);

            try {
                bs.setValue(cls.newInstance());
            } catch (Exception e) {
                // I'm not allowed to throw much, so I throw what I can!
                throw new NullPointerException(e.toString());
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
        private PropertyDescriptor pd;

        public PropertyTarget(Object object, PropertyDescriptor pd) {
            this.object = object;
            this.pd     = pd;
        }

        public void set(Object value) throws SAXException {
            try {
                pd.getWriteMethod().invoke(object, new Object[] {value});
            } catch (Exception e) {
                value = JavaUtils.convert(value, pd.getPropertyType());
                try {
                    pd.getWriteMethod().invoke(object, new Object[] {value});
                } catch (Exception ex) {
                    category.error(JavaUtils.getMessage(
                            "cantConvert02",
                            new String[] {
                                    value.getClass().getName(),
                                    pd.getName(),
                                    pd.getPropertyType().getName()}));
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
        PropertyDescriptor[] pd = getPd();

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
        // search.
        for (int i=0; i<pd.length; i++) {
            if (pd[i].getName().equals(localName)) {

                // determine the QName for this child element
                TypeMappingRegistry tmr = context.getTypeMappingRegistry();
                QName qn = tmr.getTypeQName(pd[i].getPropertyType());
                if (qn == null)
                    throw new SAXException(
                            JavaUtils.getMessage("unregistered00", "" + pd[i].getPropertyType()));

                // get the deserializer
                Deserializer dSer = tmr.getDeserializer(qn);
                if (dSer == null)
                    throw new SAXException(
                            JavaUtils.getMessage("noDeser00", "" + pd[i].getPropertyType()));

                // Success!  Register the target and deserializer.
                dSer.registerValueTarget(new PropertyTarget(value, pd[i]));
                return dSer;
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
        PropertyDescriptor[] pd = getPd(value);
        if (enumSerializer != null) {
          enumSerializer.serialize(name, attributes, value, context);
          return;
        }

        context.startElement(name, attributes);

        try {
            for (int i=0; i<pd.length; i++) {
                String propName = pd[i].getName();
                if (propName.equals("class")) continue;
                Object propValue = pd[i].getReadMethod().invoke(value,noArgs);
                context.serialize(new QName("", propName), null, propValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.toString());
        }

        context.endElement();
    }

    /**
     * Override serialization - all that is needed is the class
     */
    private static final ObjectStreamField[] serialPersistentFields =
        {new ObjectStreamField("cls", Class.class)};

}
