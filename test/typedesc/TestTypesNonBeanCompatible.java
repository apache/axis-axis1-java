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
package test.typedesc;

import java.util.ArrayList;
import java.util.Locale;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;

import org.apache.axis.description.JavaServiceDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.wsdl.fromJava.Namespaces;
import org.apache.axis.wsdl.fromJava.Types;

import junit.framework.TestCase;

public class TestTypesNonBeanCompatible extends TestCase {

        private Types types;

        protected void setUp() throws Exception {

                Definition def = WSDLFactory.newInstance().newDefinition();
                TypeMapping tm = DefaultTypeMappingImpl.getSingleton();
                Namespaces namespaces = new Namespaces();
                ServiceDesc serviceDesc = new JavaServiceDesc();
                
                types = new Types(def, tm, tm, namespaces, "any:name.space", new ArrayList(), serviceDesc);
        }
        
/*
    Commented out for now because the suggested patch breaks Object arrays
    
    // Tests for Axis-1720  - http://nagoya.apache.org/jira/browse/AXIS-1720
        public void testWriteTypeNonBeanCompatibleOnce() throws Exception {

                String schema = types.writeType(Locale.class);
                assertEquals("Schema should be null for non-bean-compatible types", null, schema);
        }

        public void testWriteTypeNonBeanCompatibleTwice() throws Exception {

                String schema = types.writeType(Locale.class);
                assertEquals("Schema should be null for non-bean-compatible types", null, schema);

                schema = types.writeType(Locale.class);
                assertEquals("Schema should be null for non-bean-compatible types", null, schema);
        }

        public void testWriteTypeNonBeanCompatibleDifferent() throws Exception {

                String schema = types.writeType(Locale.class);
                assertEquals("Schema should be null for non-bean-compatible types", null, schema);

                schema = types.writeType(Locale.class);
                assertEquals("Schema should be null for non-bean-compatible types", null, schema);
        }
*/

    public void testWriteObjectType() throws Exception {

        String schema = types.writeType(Object.class);
        assertNotNull("Schema should NOT be null for Object class", schema);

        schema = types.writeType(Object[].class);
        assertNotNull("Schema should NOT be be null for Object[] class", schema);
    }

}
