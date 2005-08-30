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
//File: AntTestCase.java
package test.wsdl.selectivefilegen;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import test.wsdl.filegen.FileGenTestCase;

/**
 * This test verifies that only the classes for the included service
 * namespace were generated.
 
    @author Jim Stafford    jim.stafford@raba.com
*/
public class IncludeServiceTestCase extends FileGenTestCase {

    /**
     * List of files which should be generated.
     */
    protected Set shouldExist() {
        HashSet set = new HashSet();
        //not included - set.add(bean1Dir + File.separator + "Bean1.java");
        //not included - set.add(bean2Dir + File.separator + "Bean2.java");
        set.add("Reporter.java");
        set.add("ReporterSoapBindingImpl.java");
        set.add("ReporterSoapBindingStub.java");
        set.add("ReporterSoapBindingSkeleton.java");
        set.add("deploy.wsdd");
        set.add("undeploy.wsdd");
        return set;
    }

    protected String rootDir() {
        return "build" + File.separator + "work" + File.separator + 
                "test" + File.separator + "wsdl" + File.separator +
                "selectivefilegen" + File.separator +
                "includedsvc";
    }

    public IncludeServiceTestCase(String name) {
        super(name);
    }
}
