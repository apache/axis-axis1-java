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

package org.apache.axis.description;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Holds information about a fault for an operation
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Tom Jordahl (tomj@apache.org)
 */
public class FaultDesc implements Serializable {
    private String name;
    private QName qname;
    private ArrayList parameters;
    private String className;
    private QName xmlType;
    private boolean complex;

    /**
     * Default constructor
     */
    public FaultDesc() {
    }

    /**
     * Full constructor
     */
    public FaultDesc(QName qname, String className,
                     QName xmlType, boolean complex) {
        this.qname = qname;
        this.className = className;
        this.xmlType = xmlType;
        this.complex = complex;
    }

    public QName getQName() {
        return qname;
    }

    public void setQName(QName name) {
        this.qname = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList parameters) {
        this.parameters = parameters;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isComplex() {
        return complex;
    }

    public void setComplex(boolean complex) {
        this.complex = complex;
    }

    public QName getXmlType() {
        return xmlType;
    }

    public void setXmlType(QName xmlType) {
        this.xmlType = xmlType;
    }

    public String toString() {
        return toString("");
    }
    public String toString(String indent) {
        String text ="";
        text+= indent + "name: " + getName() + "\n";
        text+= indent + "qname: " + getQName() + "\n";
        text+= indent + "type: " + getXmlType() + "\n";
        text+= indent + "Class: " + getClassName() + "\n";
        for (int i=0; parameters != null && i < parameters.size(); i++) {
            text+= indent +" ParameterDesc[" + i + "]:\n";
            text+= indent + ((ParameterDesc)parameters.get(i)).toString("  ") + "\n";
        }
        return text;
    }
}
