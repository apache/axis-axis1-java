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
import org.apache.axis.utils.Options;
import samples.attachments.EchoAttachment;
import samples.attachments.TestRef;

/** Test the attachments sample code.
 */
public class TestAttachmentsSample extends TestCase {
    private Options opts;
    
    protected void setUp() throws Exception {
        opts = new Options(new String[] { "-p", System.getProperty("test.functional.ServicePort", "8080") });
    }

    public void testAttachments1() throws Exception {
        boolean res = new EchoAttachment(opts).echo(false, "pom.xml");
       assertEquals("Didn't process attachment correctly", res, true) ;
    }

    public void testAttachmentsD1() throws Exception {
        boolean res = new EchoAttachment(opts).echo(true, "pom.xml");
       assertEquals("Didn't process attachment correctly", res, true) ;
    }
    
    public void testAttachmentsDimeLeaveEmpty() throws Exception {
        boolean res = new EchoAttachment(opts).echo(true, "src/test/files/leaveempty.txt");
       assertEquals("Didn't process attachment correctly", res, true) ;
    }

    public void testAttachments2() throws Exception {
        boolean res = new EchoAttachment(opts).echoDir(false, "src/main/java/samples/attachments");
        assertEquals("Didn't process attachments correctly", res, true);
    }

    public void testAttachmentsD2() throws Exception {
        boolean res = new EchoAttachment(opts).echoDir(true, "src/main/java/samples/attachments");
        assertEquals("Didn't process attachments correctly", res, true);
    }

    public void testAttachmentsTestRef() throws Exception {
        boolean res = new TestRef(opts).testit();
        assertEquals("Didn't process attachments correctly", res, true);
    }
}


