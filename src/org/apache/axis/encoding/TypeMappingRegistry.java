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
import org.apache.axis.utils.*;
import org.apache.axis.utils.events.*;
import org.apache.axis.message.*;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.*;

/**
 * @author James Snell (jasnell@us.ibm.com)
 */
public class TypeMappingRegistry implements Serializer, Deserializer { 

    Hashtable s;
    Hashtable d;
    
    public void addSerializer(Class _class, Serializer serializer) {
        if (s == null) s = new Hashtable();
        s.put(_class, serializer);
    }
    
    public void addDeserializer(QName qname, Deserializer deserializer) {
        if (d == null) d= new Hashtable();
        d.put(qname, deserializer);
    }

    public Serializer getSerializer(Class _class) {
        if (s != null) return (Serializer)s.get(_class);
        return null;
    }
    
    public Deserializer getDeserializer(QName qname) {
        if (d != null) return (Deserializer)d.get(qname);
        return null;
    }
    
    public void removeSerializer(Class _class) {
        if (s != null) s.remove(_class);
    }
    
    public void removeDeserializer(QName qname) {
        if (d != null) d.remove(qname);
    }
    
    public boolean hasSerializer(Class _class) {
        if (s != null) return s.containsKey(_class);
        return false;
    }
    
    public boolean hasDeserializer(QName qname) {
        if (d != null) return d.containsKey(qname);
        return false;
    }
    
    public void save(OutputStream out) throws Exception {
        Hashtable reg = new Hashtable();
        reg.put("SERIALIZERS", s);
        reg.put("DESERIALIZERS", d);
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(reg);
        oos.close();
    }
    
    public void load(InputStream in) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(in);
        Hashtable reg = (Hashtable)ois.readObject();
        s = (Hashtable)reg.get("SERIALIZERS");
        d = (Hashtable)reg.get("DESERIALIZERS");
        ois.close();
    }
    
    public void save(String filename) throws Exception {
        FileOutputStream fos = new FileOutputStream(filename);
        save(fos);
    }
    
    public void load(String filename) throws Exception {
        FileInputStream fis = new FileInputStream(filename);
        load(fis);
    }

    public Element serialize(QName name, Object value, TypeMappingRegistry tmr, Document doc) {
        if (value != null) {
            Class _class = value.getClass();
            Serializer ser = getSerializer(_class);
            if (tmr == null) tmr = this;
            return ser.serialize(name, value, tmr, doc);
        }
        return null;
    }

    public Object deserialize(Element element, TypeMappingRegistry tmr) {
        QName q = new QName(element.getNamespaceURI(), element.getLocalName());
        Deserializer des = getDeserializer(q);
        if (tmr == null) tmr = this;
        return des.deserialize(element, tmr);
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
