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

package org.apache.axis.message;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;

public class PrefixedQName implements Name {
    /** comment/shared empty string */
    private static final String emptyString = "".intern();
    
    private String prefix;
    private QName qName;
    
    public PrefixedQName(String uri, String localName, String pre) {
        qName = new QName(uri, localName);
        prefix = (pre == null)
                            ? emptyString
                            : pre.intern();
    }

    public PrefixedQName(QName qname) {
        this.qName = qname;
        prefix = emptyString;
    }

    public String getLocalName() {
        return qName.getLocalPart();
    }
    
    public String getQualifiedName() {
        StringBuffer buf = new StringBuffer(prefix);
        if(prefix != emptyString)
            buf.append(':');
        buf.append(qName.getLocalPart());
        return buf.toString();
    }
    
    public String getURI() {
        return qName.getNamespaceURI();
    }
    
    public String getPrefix() {
        return prefix;
    }
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PrefixedQName)) {
            return false;
        }
        if (!qName.equals(((PrefixedQName)obj).qName)) {
            return false;
        }
        if (prefix == ((PrefixedQName) obj).prefix) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return prefix.hashCode() + qName.hashCode();
    }
}
