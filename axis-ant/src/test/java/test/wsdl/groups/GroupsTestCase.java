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

/**
 * This tests the file generation of only the items that are referenced in WSDL
 * 
 * @author Steve Green (steve.green@epok.net)
 */ 
package test.wsdl.groups;

import java.io.IOException;

public class GroupsTestCase extends junit.framework.TestCase {
    public GroupsTestCase(String name) {
        super(name);
    }

    public void testGroups() throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException {
    	// Test for the proper members
    
        Class ourClass = Class.forName("test.wsdl.groups.SomeType");
        ourClass.getDeclaredMethod("getA", null);
        ourClass.getDeclaredMethod("getB", null);
        ourClass.getDeclaredMethod("getZ", null);

        return;
    }
}

