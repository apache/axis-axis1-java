/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package test.functional;

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.axis.client.AdminClient;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import samples.attachments.EchoAttachment;

/** Test the attachments sample code.
 */
public class TestAttachmentsSample extends TestCase {
    static Log log =
            LogFactory.getLog(TestAttachmentsSample.class.getName());

    public TestAttachmentsSample(String name) {
        super(name);
    }
    
    public void doTestDeploy () throws Exception {
        String[] args = { "samples/attachments/attachdeploy.wsdd" };
        AdminClient.main(args);
    }
    
    public void doTestAttachments1() throws Exception {
        Options opts = new Options( new String[]{});
        boolean res = new EchoAttachment(opts).echo("samples/attachments/README");
       assertEquals("Didn't process attachment correctly", res, true) ;
    }
    
    public void doTestAttachments2() throws Exception {
        Options opts = new Options( new String[]{});
        boolean res = new EchoAttachment(opts).echo("samples/attachments");
        assertEquals("Didn't process attachments correctly", res, true);
    }
    
    public void doTestUndeploy () throws Exception {
        String[] args = { "samples/attachments/attachundeploy.wsdd" };
        AdminClient.main(args);
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
            log.info("Testing undeployment...");
            doTestUndeploy();
            log.info("Test complete.");
        }
        catch( Exception e ) {
            if ( e instanceof AxisFault ) ((AxisFault)e).dump();
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }
    
}


