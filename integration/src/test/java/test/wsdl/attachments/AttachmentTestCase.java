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
package test.wsdl.attachments;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.axis.attachments.OctetStream;

import test.HttpTestUtil;

import java.util.Arrays;

import junit.framework.TestCase;

public class AttachmentTestCase extends TestCase {
    private static Pt1 getBinding() throws Exception {
        AttachmentLocator loc = new AttachmentLocator();
        return loc.getAttachmentPortRPC(HttpTestUtil.getTestEndpoint(loc.getAttachmentPortRPCAddress()));
    }
    
    private MimeMultipart createMimeMultipart(String data) throws Exception {
        // create the root multipart
        MimeMultipart mpRoot = new MimeMultipart("mixed");
        
        // Add text
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText(data);
        mpRoot.addBodyPart(mbp1);
        return mpRoot;
    }

    public void testGetCompanyInfo2() throws Exception {
        assertEquals("GetCompanyInfo2", getBinding().getCompanyInfo2(0, "GetCompanyInfo2", null));
    }

    public void testInputPlainText() throws Exception {
        getBinding().inputPlainText("InputPlainText");
        // TBD - validate results
    }

    public void testInoutPlainText() throws Exception {
        assertEquals("InoutPlainText", getBinding().inoutPlainText("InoutPlainText"));
    }

    public void testEchoPlainText() throws Exception {
        assertEquals("EchoPlainText", getBinding().echoPlainText("EchoPlainText"));
    }

    public void testOutputPlainText() throws Exception {
        assertEquals("OutputPlainText", getBinding().outputPlainText());
    }

    public void testInputMimeMultipart() throws Exception {
        getBinding().inputMimeMultipart(createMimeMultipart("InputMimeMultipart"));
        // TBD - validate results
    }

    public void testInoutMimeMultipart() throws Exception {
        MimeMultipart value = getBinding().inoutMimeMultipart(createMimeMultipart("InoutMimeMultipart"));
        // TBD - validate results
    }

    public void testEchoMimeMultipart() throws Exception {
        MimeMultipart value = getBinding().echoMimeMultipart(createMimeMultipart("EchoMimeMultipart"));
        // TBD - validate results
    }

    public void testOutputMimeMultipart() throws Exception {
        MimeMultipart value = getBinding().outputMimeMultipart();
        // TBD - validate results
    }

    public void testEchoAttachment() throws Exception {
        OctetStream input = new OctetStream("EchoAttachment".getBytes());
        OctetStream output = getBinding().echoAttachment(input);
        assertTrue(Arrays.equals(input.getBytes(), output.getBytes()));
    }
}
