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

package org.apache.axis.description;

import javax.xml.namespace.QName;
import java.util.ArrayList;

/**
 * Holds information about a fault for an operation
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Tom Jordahl (tomj@apache.org)
 */
public class FaultDesc {
    private QName qname;
    private ArrayList parameters;
    private String className;
    private QName xmlType;
    private boolean complex;

    public QName getQName() {
        return qname;
    }

    public void setQName(QName name) {
        this.qname = name;
    }

    public String getName()
    {
        if (qname != null)
            return qname.getLocalPart();
        return null;
    }

    public void setName(String name)
    {
        qname = new QName("", name);
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
        text+= indent + "qname: " + getQName() + "\n";
        text+= indent + "Class: " + getClassName() + "\n";
        for (int i=0; parameters != null && i < parameters.size(); i++) {
            text+= indent +" ParameterDesc[" + i + "]:\n";
            text+= indent + ((ParameterDesc)parameters.get(i)).toString("  ") + "\n";
        }
        return text;
    }
}
