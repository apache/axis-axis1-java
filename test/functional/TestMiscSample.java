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
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
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
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }

    public static void main(String[] args) throws Exception {
        TestMiscSample tester = new TestMiscSample("tester");
        tester.testService();
    }
}

