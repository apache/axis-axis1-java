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

package test.functional;

import junit.framework.TestCase;
import org.apache.axis.utils.NetworkUtils;
import samples.encoding.TestElem;

/** Test the ElementService sample code.
 */
public class TestElementSample extends TestCase {
    public void testElement () throws Exception {
        String thisHost = NetworkUtils.getLocalHostname();
        String thisPort = System.getProperty("test.functional.ServicePort","8080");

        String[] args = {thisHost,thisPort};
        String   xml = "<x:hello xmlns:x=\"urn:foo\">a string</x:hello>";
        System.out.println("Sending : " + xml );
        String res = TestElem.doit(args, xml);
        System.out.println("Received: " + res );
        assertEquals("TestElementSample.doit(): xml must match", res, xml);
    }
}


