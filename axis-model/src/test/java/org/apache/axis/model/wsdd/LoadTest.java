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
package org.apache.axis.model.wsdd;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.InputSource;

public class LoadTest extends TestCase {
    private final File file;
    
    public LoadTest(File file) {
        this.file = file;
        setName(file.getName());
    }
    
    protected void runTest() throws Throwable {
        assertNotNull(WSDDUtil.load(new InputSource(file.toURL().toString())));
    }
    
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        File[] files = new File("src/test/wsdd").listFiles();
        for (int i=0; i<files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                suite.addTest(new LoadTest(file));
            }
        }
        return suite;
    }
}
