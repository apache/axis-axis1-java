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

import org.apache.axis.Constants;
import org.apache.axis.utils.JavaUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author James Snell (jasnell@us.ibm.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 */
public class TypeMappingRegistry implements Serializer { 
    /**
     * Default constructor (transient registry)
     */
    public TypeMappingRegistry() {}

    static class SerializerDescriptor implements Serializable {
        QName typeQName;
        Serializer serializer;
        SerializerDescriptor(QName typeQName, Serializer serializer)
        {
            this.typeQName = typeQName;
            this.serializer = serializer;
        }
    }

    static class DeserializerDescriptor implements Serializable {
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
    
    public boolean isEmpty()
    {
        return (d == null || d.isEmpty());
    }
    
    /**
     * Define a "parent" TypeMappingRegistry that will be used to service
     * any requests that are not satisfied by this this instance.  This
     * enables a chain of TypeMappingRegistries to be employed that contain,
     * say, a transient set of types, followed by a persistent set of
     * deployed types, followed by the system defined SOAPTypes
     */
    public void setParent(TypeMappingRegistry parent) {
        this.parent = parent;
        
        // debug assertions: every tmr must have exactly one
        // SOAPTypeMappingRegistry at the top.
        if (parent == null)
            new Exception(JavaUtils.getMessage("nullParent00")).printStackTrace();
        if (this instanceof SOAPTypeMappingRegistry)
            new Exception(JavaUtils.getMessage("withParent00")).printStackTrace();
        for (TypeMappingRegistry t = parent; t!=null; t=t.getParent())
            if (t instanceof SOAPTypeMappingRegistry) return;
        new Exception(JavaUtils.getMessage("noParent00")).printStackTrace();
    }

    public TypeMappingRegistry getParent() {
        return parent;
    }

    public void addSerializer(Class _class,
                              QName qName,
                              Serializer serializer) {
        if (s == null) s = new Hashtable();
        if (serializer instanceof BeanSerializer) {
            ((BeanSerializer)serializer).setCls(_class);
        }
        s.put(_class, new SerializerDescriptor(qName, serializer));
    }
    
    public void addDeserializerFactory(QName qname,
                                       Class _class,
                                       DeserializerFactory deserializerFactory) {
        if (d == null) d= new Hashtable();
        d.put(qname, new DeserializerDescriptor(_class, deserializerFactory));
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
    
    public Deserializer getDeserializer(QName qname) {
        if (qname == null)
            return null;
        
        if (d != null) {
            DeserializerDescriptor desc = (DeserializerDescriptor)d.get(qname);
            if ((desc != null) && (desc.factory != null))
               return desc.factory.getDeserializer(desc.cls);
        }
        if (parent != null) return parent.getDeserializer(qname);
        return null;
    }
    
    public void removeSerializer(Class _class) {
        if (s != null) s.remove(_class);
    }
    
    public void removeDeserializer(QName qname) {
        if (d != null) d.remove(qname);
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
    
    public Attributes setTypeAttribute(Attributes attributes, QName type,
                                       SerializationContext context)
    {
        if (type == null ||
            !context.shouldSendXSIType() ||
            ((attributes != null) &&
             (attributes.getIndex(Constants.URI_CURRENT_SCHEMA_XSI,
                                "type") != -1)))
            return attributes;
        
        AttributesImpl attrs = new AttributesImpl();
        if (attributes != null)
            attrs.setAttributes(attributes);
        
        String prefix = context.
                           getPrefixForURI(Constants.URI_CURRENT_SCHEMA_XSI,
                                           "xsi");
        
        
        attrs.addAttribute(Constants.URI_CURRENT_SCHEMA_XSI,
                           "type",
                           prefix + ":type",
                           "CDATA", context.qName2String(type));
        return attrs;
    }
    
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (value != null) {
            Class _class = value.getClass();
            
            // Find a Serializer for this class, walking up the inheritance
            // hierarchy and implemented interfaces list.
            while (_class != null) {
                Serializer ser = getSerializer(_class);
                if (ser != null) {
                    QName type = getTypeQName(_class);
                    attributes = setTypeAttribute(attributes, type, context);
                    ser.serialize(name, attributes, value, context);
                    return;
                }

                Class [] ifaces = _class.getInterfaces();
                for (int i = 0; i < ifaces.length; i++) {
                    Class iface = ifaces[i];
                    ser = getSerializer(iface);
                    if (ser != null) {
                        QName type = getTypeQName(iface);
                        attributes = setTypeAttribute(attributes, type, context);
                        ser.serialize(name, attributes, value, context);
                        return;
                    }
                }
                
                _class = _class.getSuperclass();
            }
            
            throw new IOException(JavaUtils.getMessage("noSerializer00",
                    value.getClass().getName(), "" + this));
        }
        // !!! Write out a generic null, or get type info from somewhere else?
    }
    
    public void dump(PrintStream out, String header) {
        out.println(header);
        out.println("  Deserializers:");
        if (d != null) {
            java.util.Enumeration e = d.keys();
            while (e.hasMoreElements()) {
                Object key = e.nextElement();
                DeserializerDescriptor desc = (DeserializerDescriptor)d.get(key);
                String classname = (desc.cls != null) ? desc.cls.getName() : "null";
                out.println("    " + key + " => " + classname);
            }
        }
        if (parent != null)
            parent.dump(out, "Parent");
    }

    public static final QName typeMappingQName = new QName("axis",
                                                           "typeMapping");
    
    public void dumpToSerializationContext(SerializationContext ctx)
      throws IOException
    {
        if (d == null) {
            return;
        }
        
        Enumeration enum = d.keys();
        while (enum.hasMoreElements()) {
            QName typeQName = (QName)enum.nextElement();
            DeserializerDescriptor desc = 
                                    (DeserializerDescriptor)d.get(typeQName);
            if (desc.cls == null)
                continue;

            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute("", "type", "type",
                               "CDATA", ctx.qName2String(typeQName));
            attrs.addAttribute("", "class", "class",
                               "CDATA", desc.cls.getName());
            
            String dser = desc.factory.getClass().getName();
            attrs.addAttribute("", "dser", "dser",
                               "CDATA", dser);
            
            SerializerDescriptor serDesc =
                                       (SerializerDescriptor)s.get(desc.cls);
            if (serDesc != null) {
                attrs.addAttribute("", "ser", "ser",
                                   "CDATA",
                                   serDesc.serializer.getClass().getName());
            }
            
            ctx.startElement(typeMappingQName, attrs);
            ctx.endElement();
        }
    }

    public void dumpToElement(Element root)
    {
        if ((d == null) || (parent == null)) {
            return;
        }

        Document doc = root.getOwnerDocument();
        
        Enumeration enum = d.keys();
        while (enum.hasMoreElements()) {
            QName typeQName = (QName)enum.nextElement();
            DeserializerDescriptor desc = 
                                   (DeserializerDescriptor)d.get(typeQName);
            if (desc.cls == null)
                continue;
            
            Element mapEl = doc.createElementNS("", "typeMapping");

            mapEl.setAttribute("type", "ns:" + typeQName.getLocalPart());
            mapEl.setAttribute("xmlns:ns", typeQName.getNamespaceURI());
            
            mapEl.setAttribute("classname", desc.cls.getName());
            
            String dser = desc.factory.getClass().getName();
            mapEl.setAttribute("deserializerFactory", dser);
            
            SerializerDescriptor serDesc =
                                      (SerializerDescriptor)s.get(desc.cls);
            if (serDesc != null) {
                mapEl.setAttribute("serializer", serDesc.serializer.
                                                      getClass().getName());
            }

            root.appendChild(mapEl);
        }
    }
}
