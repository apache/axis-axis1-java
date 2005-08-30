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

import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.PortType;
import javax.wsdl.Service;

/**
 * Generator and Generatoractory are part of the generator framework.
 * Folks who want to use the emitter to generate stuff from WSDL should
 * do 3 things:
 * 1.  Write implementations of the Generator interface, one each fo
 * Message, PortType, Binding, Service, and Type.  These
 * implementations generate the stuff for each of these WSDL types.
 * 2.  Write an implementation of the GeneratorFactory interface that
 * returns instantiations of these Generator implementations as
 * appropriate.
 * 3.  Implement a class with a main method (like WSDL2Java) that
 * instantiates an Emitter and passes it the GeneratorFactory
 * implementation.
 */
public interface GeneratorFactory {

    /**
     * Allow the Generator extension to make a pass through the
     * symbol table doing any pre-generation logic, like creating
     * the Java names for each object and constructing signature
     * strings.
     * 
     * @param def         
     * @param symbolTable 
     */
    public void generatorPass(Definition def, SymbolTable symbolTable);

    /**
     * Get a Generator implementation that will generate bindings for the given Message.
     * 
     * @param message     
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(Message message, SymbolTable symbolTable);

    /**
     * Get a Generator implementation that will generate bindings for the given PortType.
     * 
     * @param portType    
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(PortType portType, SymbolTable symbolTable);

    /**
     * Get a Generator implementation that will generate bindings for the given Binding.
     * 
     * @param binding     
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(Binding binding, SymbolTable symbolTable);

    /**
     * Get a Generator implementation that will generate bindings for the given Service.
     * 
     * @param service     
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(Service service, SymbolTable symbolTable);

    /**
     * Get a Generator implementation that will generate bindings for the given Type.
     * 
     * @param type        
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(TypeEntry type, SymbolTable symbolTable);

    /**
     * Get a Generator implementation that will generate anything that doesn't
     * fit into the scope of any of the other Generators.
     * 
     * @param definition  
     * @param symbolTable 
     * @return 
     */
    public Generator getGenerator(Definition definition,
                                  SymbolTable symbolTable);

    /**
     * Get TypeMapping to use for translating
     * QNames to base types
     * 
     * @param btm 
     */
    public void setBaseTypeMapping(BaseTypeMapping btm);

    /**
     * Method getBaseTypeMapping
     * 
     * @return 
     */
    public BaseTypeMapping getBaseTypeMapping();
}
