/*
 *  The Apache Software License, Version 1.1
 *
 *  Copyright (c) 2002 The Apache Software Foundation.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution, if
 *  any, must include the following acknowlegement:
 *  "This product includes software developed by the
 *  Apache Software Foundation (http://www.apache.org/)."
 *  Alternately, this acknowlegement may appear in the software itself,
 *  if and wherever such third-party acknowlegements normally appear.
 *
 *  4. The names "The Jakarta Project", "Ant", and "Apache Software
 *  Foundation" must not be used to endorse or promote products derived
 *  from this software without prior written permission. For written
 *  permission, please contact apache@apache.org.
 *
 *  5. Products derived from this software may not be called "Apache"
 *  nor may "Apache" appear in their names without prior written
 *  permission of the Apache Group.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of the Apache Software Foundation.  For more
 *  information on the Apache Software Foundation, please see
 *  <http://www.apache.org/>.
 */
package org.apache.axis.wsdl.toJava;

/**
   This class is used within the context of a FactorySpec to express
   namespaces that should be either included and/or excluded from source
   code generation. The ability to include/exclude specific namespaces from
   wsdl2java generation allows certain namespaces to be mapped to custom
   bean classes, have wsdl-generated stubs/skeletons declared to pass those
   types, and not have the wsdl2java process generate classes which would 
   conflict with the externally developed custom beans.

   @author Jim Stafford (jim.stafford@raba.com)
*/
public class NamespaceSelector {
    private String namespace_ = "";
    
    public NamespaceSelector() {}
    public NamespaceSelector(String namespace) {
        namespace_ = namespace;
    }
    
    public void setNamespace(String value) {
        namespace_ = value;
    }
    
    public String getNamespace() {
        return namespace_;
    }
    
    public String toString() {
        if (namespace_ != null) {
            return "namespace=" + namespace_; 
        }
        else {
            return "";
        }
    }

    public boolean equals(Object value) {
        boolean isEqual = false;
        if (value == null) {
            isEqual = false;
        }
        else if (value instanceof String) {
            isEqual = ((String)value).equals(namespace_);
        }
        else if (value instanceof NamespaceSelector) {
            isEqual = ((NamespaceSelector)value).namespace_.equals(namespace_);
        }
        return isEqual;
    }
}
