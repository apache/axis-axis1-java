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
import org.apache.axis.client.AdminClient;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import samples.attachments.EchoAttachment;
import samples.attachments.TestRef;


/** Test the attachments sample code.
 */
public class TestAttachmentsSample extends TestCase {
    static Log log =
            LogFactory.getLog(TestAttachmentsSample.class.getName());

    public TestAttachmentsSample(String name) {
        super(name);
    }
    
    public void doTestDeploy () throws Exception {
        AdminClient.main(new String[]{"samples/attachments/attachdeploy.wsdd" });
        AdminClient.main(new String[]{"samples/attachments/testref.wsdd"});
    }
    
    public void doTestAttachments1() throws Exception {
        Options opts = new Options( new String[]{});
        boolean res = new EchoAttachment(opts).echo(false, "samples/attachments/README");
       assertEquals("Didn't process attachment correctly", res, true) ;
    }

    public void doTestAttachmentsD1() throws Exception {
        Options opts = new Options( new String[]{});
        boolean res = new EchoAttachment(opts).echo(true, "samples/attachments/README");
       assertEquals("Didn't process attachment correctly", res, true) ;
    }
    
    public void doTestAttachments2() throws Exception {
        Options opts = new Options( new String[]{});
        boolean res = new EchoAttachment(opts).echoDir(false, "samples/attachments");
        assertEquals("Didn't process attachments correctly", res, true);
    }

    public void doTestAttachmentsD2() throws Exception {
        Options opts = new Options( new String[]{});
        boolean res = new EchoAttachment(opts).echoDir(true, "samples/attachments");
        assertEquals("Didn't process attachments correctly", res, true);
    }

    public void doTestAttachmentsTestRef() throws Exception {
        Options opts = new Options( new String[]{});
        boolean res = new TestRef(opts).testit();
        assertEquals("Didn't process attachments correctly", res, true);
    }
    
    public void doTestUndeploy () throws Exception {
        AdminClient.main(new String[]{ "samples/attachments/attachundeploy.wsdd" });
        AdminClient.main(new String[]{ "samples/attachments/testrefundeploy.wsdd" });
    }

    public static void main(String args[]) throws Exception {
        TestAttachmentsSample tester = new TestAttachmentsSample("tester");
        tester.testAttachmentsService();
    }

    public void testAttachmentsService () throws Exception {
        try {
            log.info("Testing deployment...");
            doTestDeploy();
            log.info("Testing single file attachment...");
            doTestAttachments1();
            log.info("Testing multiple file attachments...");
            doTestAttachments2();
            log.info("Testing single file DIME attachment...");
            doTestAttachmentsD1();
            log.info("Testing multiple file DIME attachments...");
            doTestAttachmentsD2();
            log.info("Testing attachment references...");
            doTestAttachmentsTestRef();
            log.info("Testing undeployment...");
            doTestUndeploy();
            log.info("Test complete.");
        }
        catch( Exception e ) {
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }
    
}


