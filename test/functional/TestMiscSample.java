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
import org.apache.axis.utils.Options;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.client.AdminClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import samples.misc.TestClient;

import java.io.ByteArrayInputStream;

/** Test the stock sample code.
 */
public class TestMiscSample extends TestCase {
    static Log log =
            LogFactory.getLog(TestMiscSample.class.getName());

    static final String deployDoc =
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n" +
            "  <service name=\"EchoService\" provider=\"Handler\">\n" +
            "    <parameter name=\"handlerClass\" " +
            "           value=\"org.apache.axis.handlers.EchoHandler\"/>\n" +
            "  </service>\n" +
            "</deployment>";

    static final String undeployDoc =
            "<undeployment xmlns=\"http://xml.apache.org/axis/wsdd/\">\n" +
            "  <service name=\"EchoService\"/>\n" +
            "</undeployment>";

    AdminClient client;
    Options opts = null;

    public TestMiscSample(String name) throws Exception {
        super(name);
        client = new AdminClient();
        opts = new Options(new String [] {
            "-lhttp://localhost:8080/axis/services/AdminService" } );
    }

    public void doDeploy () throws Exception {
        client.process(opts, new ByteArrayInputStream(deployDoc.getBytes()));
    }

    public void doUndeploy () throws Exception {
        client.process(opts, new ByteArrayInputStream(undeployDoc.getBytes()));
    }

    public void doTest () throws Exception {
        String[] args = { "-d" };
        TestClient.main(args);
    }
    
    public void testService () throws Exception {
        try {
            log.info("Testing misc sample.");
            doDeploy();
            log.info("Testing service...");
            doTest();
            doUndeploy();
            log.info("Test complete.");
        }
        catch( Exception e ) {
            if ( e instanceof AxisFault ) ((AxisFault)e).dump();
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }

    public static void main(String[] args) throws Exception {
        TestMiscSample tester = new TestMiscSample("tester");
        tester.testService();
    }
}

