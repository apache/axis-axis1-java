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
package samples.integrationGuide.example2;

import org.apache.axis.wsdl.toJava.JavaDefinitionWriter;
import org.apache.axis.wsdl.toJava.JavaGeneratorFactory;
import org.apache.axis.wsdl.toJava.JavaUndeployWriter;

import javax.wsdl.Definition;

/**
 * IBM Extension to WSDL2Java Emitter 
 * This class is used to locate the IBM extension writers.
 * (For example the IBM JavaType Writer)
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)     
 * @author Russell Butek (butek@us.ibm.com)     
 */
public class MyGeneratorFactory extends JavaGeneratorFactory {
    /*
     * NOTE!  addDefinitionGenerators is the ONLY addXXXGenerators method
     * that works at this point in time (2002-17-May).  This rest of them
     * are soon to be implemented.
     */
    protected void addDefinitionGenerators() {
        addGenerator(Definition.class, JavaDefinitionWriter.class); // WSDL2Java's JavaDefinitionWriter
        addGenerator(Definition.class, MyDeployWriter.class); // our DeployWriter
        addGenerator(Definition.class, JavaUndeployWriter.class); // WSDL2Java's JavaUndeployWriter
    } // addDefinitionGenerators

} // class MyGeneratorFactory
