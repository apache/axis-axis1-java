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
package org.apache.axis.wsdl.gen;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import javax.wsdl.Service;

import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.DefaultSOAPEncodingTypeMappingImpl;

import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

/**
* This factory returns a bunch of NoopGenerators
*/

public class NoopFactory implements GeneratorFactory {
    public void generatorPass(Definition def, SymbolTable symbolTable) {
    } // generatorPass

    public Generator getGenerator(Message message, SymbolTable symbolTable) {
        return new NoopGenerator();
    } // getGenerator
    
    public Generator getGenerator(PortType portType, SymbolTable symbolTable) {
        return new NoopGenerator();
    } // getGenerator
    
    public Generator getGenerator(Binding binding, SymbolTable symbolTable) {
        return new NoopGenerator();
    } // getGenerator
    
    public Generator getGenerator(Service service, SymbolTable symbolTable) {
        return new NoopGenerator();
    } // getGenerator
    
    public Generator getGenerator(TypeEntry type, SymbolTable symbolTable) {
        return new NoopGenerator();
    } // getGenerator

    public Generator getGenerator(Definition definition, SymbolTable symbolTable) {
        return new NoopGenerator();
    } // getGenerator

    private BaseTypeMapping btm = null;

    public void setBaseTypeMapping(BaseTypeMapping btm) {
        this.btm = btm;
    } // setBaseTypeMapping

    public BaseTypeMapping getBaseTypeMapping() {
        if (btm == null) {
            btm = new BaseTypeMapping() {
                    TypeMapping defaultTM = DefaultSOAPEncodingTypeMappingImpl.create();
                    public String getBaseName(QName qNameIn) {
                        javax.xml.namespace.QName qName = 
                            new javax.xml.namespace.QName(
                              qNameIn.getNamespaceURI(),
                              qNameIn.getLocalPart());
                        Class cls = defaultTM.getClassForQName(qName);
                        if (cls == null) {
                            return null;
                        }
                        else {
                            // RJB NOTE:  Javaism - bad bad bad
                            return JavaUtils.getTextClassName(cls.getName());
                        }
                    }
            };
        }
        return btm;
    } // getBaseTypeMapping
} // class NoopFactory
