/*
 * Copyright 2002,2004 The Apache Software Foundation.
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
package org.apache.axis.tools.ant.wsdl;

import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.namespace.QName;

public class ComplexType {
    private String className;
    private String serializer = 
            "org.apache.axis.encoding.ser.BeanSerializerFactory";
    private String deserializer = 
            "org.apache.axis.encoding.ser.BeanDeserializerFactory";
    private String namespace;
    
    public ComplexType() {}
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }
    
    public void setDeserializer(String deserializer) {
        this.deserializer = deserializer;
    }
    
    public void setNameSpace(String namespace) {
        this.namespace = namespace;
    }
    
    public void register(TypeMapping tm) throws ClassNotFoundException {
        Class cl = Class.forName(className);
        String localName = className.substring((className.lastIndexOf(".") + 1));  
        QName qName = new QName(namespace,localName);
        SerializerFactory sf = BaseSerializerFactory.createFactory(
                                    Class.forName(serializer), cl, qName);
        DeserializerFactory df = BaseDeserializerFactory.createFactory(
                                    Class.forName(deserializer), cl, qName);
        
        tm.register(cl, qName, sf, df);
    }
}