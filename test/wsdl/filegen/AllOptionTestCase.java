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
 */ 
package test.wsdl.filegen;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class AllOptionTestCase extends FileGenTestCase {
    public AllOptionTestCase(String name) {
        super(name);
    }

    /**
     * List of files which should be generated.
     */
    protected Set shouldExist() {
        HashSet set = new HashSet();
        set.add("Address.java");
        set.add("OpFault.java");
        set.add("PortTypeSoap.java");
        set.add("ReferenceService.java");
        set.add("ReferenceServiceLocator.java");
        set.add("ReferenceSoapBindingStub.java");
        set.add("StateType.java");
        return set;
    } // shouldExist

    /**
     * The directory containing the files that should exist.
     */
    protected String rootDir() {
        return "build" + File.separator + "work" + File.separator + 
                "test" + File.separator + "wsdl" + File.separator +
                "filegenAll";
    } // rootDir

} // class AllOptionTestCase
