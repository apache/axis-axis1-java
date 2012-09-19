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
package samples.integrationGuide.example1;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

public class VerifyFilesTest extends TestCase {
    public void testDeployUseless() throws Exception {
        File file = new File(System.getProperty("basedir", "."), "target/work/example1/com/examples/www/wsdl/HelloService_wsdl/Hello_Service.lst");
        assertTrue(file.exists());
        assertEquals("Hello_Port", FileUtils.readFileToString(file).trim());
    }
}