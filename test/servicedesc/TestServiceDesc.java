/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
package test.servicedesc;

import java.util.List;

import junit.framework.TestCase;

import org.apache.axis.description.JavaServiceDesc;
import org.apache.axis.description.OperationDesc;

import javax.xml.namespace.QName;

public class TestServiceDesc extends TestCase { 
    
    public TestServiceDesc(String name) {
        super(name);
    }
    
    public TestServiceDesc() {
        super("Test ServiceDesc Synch");
    }
 
    public void testFaultSynch() throws Exception {
        JavaServiceDesc desc = new JavaServiceDesc();

        desc.loadServiceDescByIntrospection(ServiceClass.class);

        List operations = desc.getOperations();

        assertTrue(operations != null);
        assertEquals("invalid number of registered operations",
                     2, operations.size());
        
        OperationDesc operation;
        List faults;

        operation = (OperationDesc)operations.get(0);
        assertEquals("doIt1", operation.getName());

        faults = operation.getFaults();

        assertTrue(faults != null);
        assertEquals("invalid number of registered faults", 
                     2, faults.size());

        operation = (OperationDesc)operations.get(1);
        assertEquals("doIt2", operation.getName());

        faults = operation.getFaults();

        assertTrue(faults != null);
        assertEquals("invalid number of registered faults", 
                     2, faults.size());


    }

}
