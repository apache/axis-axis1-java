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

package encoding;

import junit.framework.TestCase;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.Constants;

import javax.xml.rpc.encoding.TypeMapping;

/**
 * Default Type Mapping tests
 */
public class TestDefaultTM extends TestCase {
    /**
     * This test makes sure that there aren't any SOAPENC types
     * mapped in the default type mappings for any of the valid
     * "version" strings.
     *
     * @throws Exception
     */
    public void testNoSOAPENCTypes() throws Exception {
        checkTypes(null);
        checkTypes("1.0");
        checkTypes("1.1");
        checkTypes("1.2");
        checkTypes("1.3");
    }

    private void checkTypes(String version) throws Exception {
        TypeMappingRegistryImpl tmr = new TypeMappingRegistryImpl();
        tmr.doRegisterFromVersion(version);
        TypeMapping tm = tmr.getDefaultTypeMapping();
        assertNull("Found mapping for soapenc:string in TM version " + version,
                   tm.getDeserializer(null, Constants.SOAP_STRING));
    }
}
