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
package samples.integrationGuide.example1;

import org.apache.axis.wsdl.WSDL2Java;
import org.apache.axis.wsdl.toJava.JavaGeneratorFactory;

import javax.wsdl.Service;

public class MyWSDL2Java extends WSDL2Java {

    /**
     * Main
     */
    public static void main(String args[]) {
        MyWSDL2Java myWSDL2Java = new MyWSDL2Java();
        JavaGeneratorFactory factory = (JavaGeneratorFactory) myWSDL2Java.getParser().getFactory();
        factory.addGenerator(Service.class, MyListPortsWriter.class);
        myWSDL2Java.run(args);
    } // main
} // MyWSDL2Java
