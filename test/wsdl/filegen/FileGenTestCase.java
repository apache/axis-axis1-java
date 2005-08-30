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
 * @author Tom Jordahl (tomj@macromedia.com)
 */ 
package test.wsdl.filegen;

import test.AxisFileGenTestBase;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


public class FileGenTestCase extends AxisFileGenTestBase {
    public FileGenTestCase(String name) {
        super(name);
    }


    /**
     * List of files which should be generated.
     */
    protected Set shouldExist() {
        HashSet set = new HashSet();
        set.add("AllOptionTestCase.java");
        set.add("FileGenTestCase.java");
        set.add("OpFault.java");
        set.add("PortTypeSoap.java");
        set.add("ReferenceService.java");
        set.add("ReferenceServiceLocator.java");
        set.add("ReferenceSoapBindingStub.java");
        return set;
    }
    
    /**
     * List of files which may or may not be generated.
     */
    protected Set mayExist() {
        HashSet set = new HashSet();
        return set;
    }

    /**
     * The directory containing the files that should exist.
     */
    protected String rootDir() {
        return "build" + File.separator + "work" + File.separator + 
                "test" + File.separator + "wsdl" + File.separator +
                "filegen";
    }
    
}

