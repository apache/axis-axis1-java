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
package org.apache.axis.wsdl;

import java.io.IOException;

import java.util.Vector;

import javax.wsdl.QName;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.axis.utils.JavaUtils;

/**
* This is Wsdl2java's Type Writer.  It writes the following files, as appropriate:
* <typeName>.java, <typeName>Holder.java.
*/
public class JavaTypeWriter implements Writer {
    public static final String HOLDER_IS_NEEDED = "Holder is needed";

    private Writer typeWriter = null;
    private Writer holderWriter = null;

    /**
     * Constructor.
     */
    public JavaTypeWriter(
            Emitter emitter,
            Type type,
            SymbolTable symbolTable) {
        if (type.isReferenced()) {

            // Determine what sort of type this is and instantiate the appropriate Writer.
            Node node = type.getNode();

            // Generate the proper class for either "complex" or "enumeration" types
            Vector v = SchemaUtils.getComplexElementTypesAndNames(
                    node, symbolTable);
            if (v != null) {
                typeWriter = new JavaComplexTypeWriter(emitter, type, v,
                    SchemaUtils.getComplexElementExtensionBase(node,symbolTable));
            }
            else {
                v = SchemaUtils.getEnumerationBaseAndValues(node, symbolTable);
                if (v != null) {
                    typeWriter = new JavaEnumTypeWriter(emitter, type, v);
                }
            }

            // If a type class is written, and the holder is needed (ie., something uses this
            // type as an out or inout parameter), instantiate the holder writer.
            if (v != null && holderIsNeeded(type)) {
                holderWriter = new JavaHolderWriter(emitter, type);
            }
        }
    } // ctor

    /**
     * Write all the service bindnigs:  service and testcase.
     */
    public void write() throws IOException {
        if (typeWriter != null)
            typeWriter.write();
        if (holderWriter != null) {
            holderWriter.write();
        }
    } // write

    /**
     * Does anything use this type as an inout/out parameter?  Query the Type dynamicVar
     */
    private boolean holderIsNeeded(SymTabEntry entry) {
        Boolean holderIsNeeded =
                (Boolean) entry.getDynamicVar(HOLDER_IS_NEEDED);
        return (holderIsNeeded != null && holderIsNeeded.booleanValue());
    } // holderIsNeeded
} // class JavaTypeWriter
