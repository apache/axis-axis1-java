/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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

import java.util.Hashtable;
import org.apache.axis.Constants;
import org.apache.axis.utils.*;
import org.apache.axis.utils.events.*;
import org.apache.axis.message.*;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import java.io.*;

/**
 * @author James Snell (jasnell@us.ibm.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 */
public class TypeMappingRegistry implements Serializer { 

    // default location for save/load
    private String fileName = null;

    /**
     * Constructor for persistent registries
     */
    public TypeMappingRegistry(String fileName) {
        this();
        this.fileName = fileName;
    }

    /**
     * Default constructor (transient registry)
     */
    public TypeMappingRegistry() {}

    /**
     * Init (ie. load settings...)
     */
    public void init() {
        // load();
    }

    class SerializerDescriptor implements Serializable {
        QName typeQName;
        Serializer serializer;
        SerializerDescriptor(QName typeQName, Serializer serializer)
        {
            this.typeQName = typeQName;
            this.serializer = serializer;
        }
    }

    class DeserializerDescriptor implements Serializable {
        Class cls;
        DeserializerFactory factory;
        DeserializerDescriptor(Class cls, DeserializerFactory factory)
        {
            this.cls = cls;
            this.factory = factory;
        }
    }
    
    TypeMappingRegistry parent = null;
    Hashtable s;
    Hashtable d;
    
    /**
     * Define a "parent" TypeMappingRegistry that will be used to service
     * any requests that are not satisfied by this this instance.  This
     * enables a chain of TypeMappingRegistries to be employed that contain,
     * say, a transient set of types, followed by a persistent set of
     * deployed types, followed by the system defined SOAPTypes
     */
    public void setParent(TypeMappingRegistry parent) {
        this.parent = parent;
    }

    public void addSerializer(Class _class,
                              QName qName,
                              Serializer serializer) {
        if (s == null) s = new Hashtable();
        s.put(_class, new SerializerDescriptor(qName, serializer));
        save();
    }
    
    public void addDeserializerFactory(QName qname,
                                       Class _class,
                                       DeserializerFactory deserializerFactory) {
        if (d == null) d= new Hashtable();
        d.put(qname, new DeserializerDescriptor(_class, deserializerFactory));
        save();
    }

    public Serializer getSerializer(Class _class) {
        if (s != null) {
            SerializerDescriptor desc = (SerializerDescriptor)s.get(_class);
            if (desc != null) return desc.serializer;
        }
        if (parent != null) return parent.getSerializer(_class);
        return null;
    }
    
    public QName getTypeQName(Class _class) {
        if (s != null) {
            SerializerDescriptor desc = (SerializerDescriptor)s.get(_class);
            if (desc != null) return desc.typeQName;
        }
        if (parent != null) return parent.getTypeQName(_class);
        return null;
    }
    
    public Class getClassForQName(QName type) {
        if (d != null) {
            DeserializerDescriptor desc = (DeserializerDescriptor)d.get(type);
            if (desc != null) return desc.cls;
        }
        if (parent != null) return parent.getClassForQName(type);
        return null;
    }
    
    public DeserializerBase getDeserializer(QName qname) {
        if (d != null) {
            DeserializerDescriptor desc = (DeserializerDescriptor)d.get(qname);
            if ((desc != null) && (desc.factory != null))
               return desc.factory.getDeserializer();
        }
        if (parent != null) return parent.getDeserializer(qname);
        return null;
    }
    
    public void removeSerializer(Class _class) {
        if (s != null) s.remove(_class);
        save();
    }
    
    public void removeDeserializer(QName qname) {
        if (d != null) d.remove(qname);
        save();
    }
    
    public boolean hasSerializer(Class _class) {
        if (s != null && s.containsKey(_class)) return true;
        if (parent != null) return parent.hasSerializer(_class);
        return false;
    }
    
    public boolean hasDeserializer(QName qname) {
        if (d != null && d.containsKey(qname)) return true;
        if (parent != null) return parent.hasDeserializer(qname);
        return false;
    }
    
    public void save() {
        if (fileName == null) return;

        try {
            FileOutputStream out = new FileOutputStream(fileName);
            Hashtable reg = new Hashtable();
            if (s!=null) reg.put("SERIALIZERS", s);
            if (d!=null) reg.put("DESERIALIZERS", d);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(reg);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace( System.err );
        }
    }
    
    private void load() {
        if (fileName == null) return;

        try {
            FileInputStream in = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(in);
            Hashtable reg = (Hashtable)ois.readObject();
            s = (Hashtable)reg.get("SERIALIZERS");
            d = (Hashtable)reg.get("DESERIALIZERS");
            ois.close();
        } catch (FileNotFoundException fnfe) {
        } catch (Exception e) {
            e.printStackTrace( System.err );
        }
    }
    
    public Attributes setTypeAttribute(Attributes attributes, QName type,
                                       SerializationContext context)
    {
        if ((attributes != null) &&
            (attributes.getIndex(Constants.URI_CURRENT_SCHEMA_XSI,
                                "type") != -1))
            return attributes;
        
        AttributesImpl attrs = new AttributesImpl();
        if (attributes != null)
            attrs.setAttributes(attributes);
        
        attrs.addAttribute(Constants.URI_CURRENT_SCHEMA_XSI, "type",
                           "xsi:type", "CDATA", context.qName2String(type));
        return attrs;
    }
    
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (value != null) {
            Class _class = value.getClass();
            Serializer ser = getSerializer(_class);
            if (ser != null) {
                QName type = getTypeQName(_class);
                attributes = setTypeAttribute(attributes, type, context);
                ser.serialize(name, attributes, value, context);
            } else {
                throw new IOException("No serializer found for class " + _class.getName());
            }
        }
        // !!! Write out a generic null, or get type info from somewhere else?
    }

    //public MessageElement serialize(QName name, Object value, NSStack nsStack, Message message) {
    //    if (value != null) {
    //        Class _class = value.getClass();
    //        Serializer ser = getSerializer(_class);
    //        return ser.serialize(name, value, nsStack, this, message);
    //    }
    //    return null;
    //}
    
    //public MessageElement[] serialize(QName[] name, Object[] value, NSStack nsStack, Message message) {
    //    MessageElement[] me = new MessageElement[name.length];
    //    for (int n = 0; n < name.length; n++) {
    //        me[n] = serialize(name[n], value[n], nsStack, message);
    //    }
    //    return me;
    //}
    
    //public Object deserialize(MessageElement element) {
    //    Deserializer des = getDeserializer(element.getQName());
    //    return des.deserialize(element, this);
    //}
    
    //public Object[] deserialize(MessageElement[] element) {
    //    Object[] o = new Object[element.length];
    //    for (int n = 0; n < element.length; n++) {
    //        o[n] = deserialize(element[n]);
    //    }
    //    return o;
    //}
}
