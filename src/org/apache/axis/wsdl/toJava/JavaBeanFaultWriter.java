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
package org.apache.axis.wsdl.toJava;

import java.util.Vector;
import java.io.PrintWriter;
import java.io.IOException;

import org.apache.axis.wsdl.symbolTable.TypeEntry;

/**
 * This is Wsdl2java's Complex Faylt Writer.
 * It generates bean-like class for complexTypes used
 * in a operation fault message.
 */
public class JavaBeanFaultWriter extends JavaBeanWriter {
    /**
     * Constructor.
     * @param emitter   
     * @param type        The type representing this class
     * @param elements    Vector containing the Type and name of each property
     * @param extendType  The type representing the extended class (or null)
     * @param attributes  Vector containing the attribute types and names    
     * @param helper      Helper class writer                                
     */
    protected JavaBeanFaultWriter(
            Emitter emitter,
            TypeEntry type,
            Vector elements,
            TypeEntry extendType,
            Vector attributes,
            JavaWriter helper) {
        super(emitter, type, elements, 
              extendType, attributes, helper);

        // The Default Constructor is not JSR 101 v1.0 compliant, but
        // is the only way that Axis can get something back over the wire.
        // This will need to be changed when fault contents are supported
        // over the wire.
        enableDefaultConstructor = true;

        // JSR 101 v1.0 requires a full constructor
        enableFullConstructor = true;

        // JSR 101 v1.0 does not support write access methods
        enableSetters = true;
    } // ctor
    
    /**
     * Returns the appropriate extends text
     * @return "" or " extends <class> "
     */
    protected String getExtendsText() {
        // See if this class extends another class
        String extendsText = super.getExtendsText();
        if (extendsText.equals("")) {
            // JSR 101 compliant code should extend java.lang.Exception!
            //extendsText = " extends java.lang.Exception ";
            extendsText = " extends org.apache.axis.AxisFault ";
        }
        return extendsText;
    }

    /**
     * Write the Exception serialization code
     * 
     * NOTE: This function is written in JavaFaultWriter.java also. 
     */ 
    protected void writeFileFooter(PrintWriter pw) throws IOException {
        // We need to have the Exception class serialize itself
        // with the correct namespace, which can change depending on which
        // operation the exception is thrown from.  We therefore have the
        // framework call this generated routine with the correct QName,
        // and allow it to serialize itself.

        // method that serializes this exception (writeDetail)
        pw.println();
        pw.println("    /**");
        pw.println("     * Writes the exception data to the faultDetails");
        pw.println("     */");
        pw.println("    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {");
        pw.println("        context.serialize(qname, null, this);");
        pw.println("    }");
        
        super.writeFileFooter(pw);
    } // writeFileFooter
} // class JavaBeanFaultWriter
