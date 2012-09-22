/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package test.wsdl.split;

import java.io.File;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class SplitTestCase extends TestCase {
    public void testValidateWSDL() throws Exception {
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        Definition definition = reader.readWSDL(new File(System.getProperty("basedir", ".")
                + "/target/work/test/wsdl/split/SplitTestImpl.wsdl").toURI().toString());
        Service service = definition.getService(new QName("http://split.wsdl.test", "MyPortTypeService"));
        Port port = service.getPort("SplitTest");
        // This is the critical part: the binding is defined in the imported WSDL
        Binding binding = port.getBinding();
        assertNotNull(binding);
        assertEquals("MyPortType", binding.getPortType().getQName().getLocalPart());
    }
}
