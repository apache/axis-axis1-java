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
package test.wsdl.multibinding;

import test.wsdl.filegen.FileGenTestCase;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class VerifyFilesTestCase extends FileGenTestCase {
    public VerifyFilesTestCase(String name) {
        super(name);
    }

    /**
     * List of files which should be generated.
     */
    protected Set shouldExist() {
        HashSet set = new HashSet();
        set.add("BindingAllLitImpl.java");
        set.add("BindingAllLitSkeleton.java");
        set.add("BindingAllLitStub.java");
        set.add("BindingNoLitImpl.java");
        set.add("BindingNoLitSkeleton.java");
        set.add("BindingNoLitStub.java");
        set.add("BindingSomeLitImpl.java");
        set.add("BindingSomeLitSkeleton.java");
        set.add("BindingSomeLitStub.java");
        set.add("MbPT.java");
        set.add("MbService.java");
        set.add("MbServiceLocator.java");
        set.add("MbServiceTestCase.java");
        set.add("VerifyFilesTestCase.java");
        set.add("deploy.wsdd");
        set.add("undeploy.wsdd");
        return set;
    } // shouldExist

    /**
     * The directory containing the files that should exist.
     */
    protected String rootDir() {
        return "build" + File.separator + "work" + File.separator + 
                "test" + File.separator + "wsdl" + File.separator +
                "multibinding";
    } // rootDir

} // class VerifyFilesTestCase
