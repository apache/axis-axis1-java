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
package test.wsdl.selectivefilegen;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import test.wsdl.filegen.FileGenTestCase;

/**
 * This test verifies that no classes were generated besides the included
 * bean namespaces.
 
    @author Jim Stafford    jim.stafford@raba.com
*/
public class IncludeBeanTestCase extends FileGenTestCase {

    /**
     * List of files which should be generated.
     */
    protected Set shouldExist() {
        HashSet set = new HashSet();
        
        set.add("Bean1.java");
        set.add("Bean2.java");
        /* excluded - 
        set.add(servicesDir + File.separator + "Reporter.java");
        set.add(servicesDir + File.separator + "ReporterSoapBindingImpl.java");
        set.add(servicesDir + File.separator + "ReporterSoapBindingStub.java");
        set.add(servicesDir + File.separator + "ReporterSoapBindingSkeleton.java");
        set.add(servicesDir + File.separator + "deploy.wsdd");
        set.add(servicesDir + File.separator + "undeploy.wsdd");
        */
        return set;
    }

    protected String rootDir() {
        return "build" + File.separator + "work" + File.separator + 
                "test" + File.separator + "wsdl" + File.separator +
                "selectivefilegen" + File.separator +
                "includedbean";
    }

    public IncludeBeanTestCase(String name) {
        super(name);
    }
}
