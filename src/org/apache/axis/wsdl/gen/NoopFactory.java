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
package org.apache.axis.wsdl.gen;

import org.apache.axis.encoding.DefaultSOAPEncodingTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

/**
 * This factory returns a bunch of NoopGenerators
 */
public class NoopFactory implements GeneratorFactory {

    /**
     * Method generatorPass
     * 
     * @param def         
     * @param symbolTable 
     */
    public void generatorPass(Definition def,
                              SymbolTable symbolTable) {
    }    // generatorPass

    /**
     * Method getGenerator
     * 
     * @param message     
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(Message message, SymbolTable symbolTable) {
        return new NoopGenerator();
    }    // getGenerator

    /**
     * Method getGenerator
     * 
     * @param portType    
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(PortType portType, SymbolTable symbolTable) {
        return new NoopGenerator();
    }    // getGenerator

    /**
     * Method getGenerator
     * 
     * @param binding     
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(Binding binding, SymbolTable symbolTable) {
        return new NoopGenerator();
    }    // getGenerator

    /**
     * Method getGenerator
     * 
     * @param service     
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(Service service, SymbolTable symbolTable) {
        return new NoopGenerator();
    }    // getGenerator

    /**
     * Method getGenerator
     * 
     * @param type        
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(TypeEntry type, SymbolTable symbolTable) {
        return new NoopGenerator();
    }    // getGenerator

    /**
     * Method getGenerator
     * 
     * @param definition  
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(Definition definition,
                                  SymbolTable symbolTable) {
        return new NoopGenerator();
    }    // getGenerator

    /** Field btm */
    private BaseTypeMapping btm = null;

    /**
     * Method setBaseTypeMapping
     * 
     * @param btm 
     */
    public void setBaseTypeMapping(BaseTypeMapping btm) {
        this.btm = btm;
    }    // setBaseTypeMapping

    /**
     * Method getBaseTypeMapping
     * 
     * @return 
     */
    public BaseTypeMapping getBaseTypeMapping() {

        if (btm == null) {
            btm = new BaseTypeMapping() {

                TypeMapping defaultTM =
                        DefaultSOAPEncodingTypeMappingImpl.create();

                public String getBaseName(QName qNameIn) {

                    javax.xml.namespace.QName qName =
                            new javax.xml.namespace.QName(qNameIn.getNamespaceURI(),
                                    qNameIn.getLocalPart());
                    Class cls =
                            defaultTM.getClassForQName(qName);

                    if (cls == null) {
                        return null;
                    } else {

                        // RJB NOTE:  Javaism - bad bad bad
                        return JavaUtils.getTextClassName(cls.getName());
                    }
                }
            };
        }

        return btm;
    }    // getBaseTypeMapping
}    // class NoopFactory
