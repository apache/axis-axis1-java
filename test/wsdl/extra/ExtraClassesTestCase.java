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

package test.wsdl.extra;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * This tests the file generation of only the items that are referenced in WSDL
 * This should extend FileGenTestCase, but we have a dependancy problem as
 * "extra" comes before "filegen".  Oh well.
 * 
 */
public class ExtraClassesTestCase  extends junit.framework.TestCase {
    public ExtraClassesTestCase(String name) {
        super(name);
    }

    /**
     * List of files which should be generated.
     */
    protected Set shouldExist() {
        HashSet set = new HashSet();
        set.add("Extra.java");  // this is the important one
        set.add("MyService.java");
        set.add("MyServiceService.java");
        set.add("MyServiceServiceLocator.java");
        set.add("MyServiceSoapBindingStub.java");
        set.add("MyService.wsdl");
        set.add("ExtraClassesTestCase.java");
        return set;
    } // shouldExist

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
                "extra";
    } // rootDir

        public void testFileGen() throws IOException {
        String rootDir = rootDir();
        Set shouldExist = shouldExist();
        Set mayExist = mayExist();

        // open up the output directory and check what files exist.
        File outputDir = new File(rootDir);
        
        String[] files = outputDir.list();

        Vector shouldNotExist = new Vector();

        for (int i = 0; i < files.length; ++i) {
            if (shouldExist.contains(files[i])) {
                shouldExist.remove(files[i]);
            } 
            else if (mayExist.contains(files[i])) {
                mayExist.remove(files[i]);
            }
            else {
                shouldNotExist.add(files[i]);
            }
        }

        if (shouldExist.size() > 0) {
            fail("The following files should exist but do not:  " + shouldExist);
        }

        if (shouldNotExist.size() > 0) {
            fail("The following files should NOT exist, but do:  " + shouldNotExist);
        }
    }
} // class AllOptionTestCase
