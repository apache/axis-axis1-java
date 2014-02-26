/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package test.wsdl.axis2900;

import test.AxisFileGenTestBase;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class VerifyFilesTestCase extends AxisFileGenTestBase {
    public VerifyFilesTestCase(String name) {
        super(name);
    }

    /**
     * List of files which should be generated.
     */
    protected Set shouldExist() {
        HashSet set = new HashSet();
        set.add("Exception.java");
        set.add("MyWS.java");
        set.add("MyWSException.java");
        set.add("MyWSLocator.java");
        set.add("MyWSPortType.java");
        set.add("MyWSSoap11BindingStub.java");
        return set;
    } // shouldExist

    /**
     * List of files which may be generated.
     */
    protected Set mayExist() {
        HashSet set = new HashSet();
        return set;
    } // shouldExist

    /**
     * The directory containing the files that should exist.
     */
    protected String rootDir() {
        return "target" + File.separator + "work" + File.separator + 
                "test" + File.separator + "wsdl" + File.separator +
                "axis2900";
    } // rootDir

} // class VerifyFilesTestCase
