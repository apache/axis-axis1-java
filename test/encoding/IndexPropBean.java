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

package test.encoding;

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;

import javax.xml.namespace.QName;

/**
 * Bean with an indexed property
 */ 
public class IndexPropBean  {
    private java.lang.String[] name;
    
    public IndexPropBean() {}
    
    public java.lang.String getName(int i) {
        return name[i];
    }
    
    public void setName(int i, java.lang.String name){
        this.name[i] = name;
    }
    
    public java.lang.String[] getName() {
        return name;
    }
    
    public void setName(java.lang.String name[]){
        this.name = name;
    }

    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof IndexPropBean))
            return false;
        IndexPropBean other = (IndexPropBean)obj;
        if (other.name == null && this.name == null) 
            return true;
        if (other.name != null &&
            java.util.Arrays.equals(other.name, name)) 
            return true;
        return false;
    }

    public int hashCode() {
        int _hashCode = 0;
        if (name != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(name);
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(name, i);
                if (obj != null) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        return _hashCode;
    }
    // Type metadata
    private static TypeDesc typeDesc;
    
    static {
        typeDesc = new TypeDesc(IndexPropBean.class);
        FieldDesc field;

        field = new ElementDesc();
        field.setFieldName("name");
        field.setXmlName(new QName("", "name"));
        typeDesc.addFieldDesc(field);
    }
    
    public static TypeDesc getTypeDesc()
    {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }
}
