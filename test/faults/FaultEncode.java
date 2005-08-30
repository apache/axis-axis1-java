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
package test.faults;

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;

/**
 * This class tests Fault deserialization.
 *
 * @author Sam Ruby (rubys@us.ibm.com)
 */

public class FaultEncode extends TestCase {
    
    public FaultEncode(String name) {
        super(name);
    } // ctor
    
    public void testFault() throws Exception {
        AxisFault fault = new AxisFault("<code>", "<string>", "<actor>", null);
        fault.setFaultDetailString("<detail>");

        AxisServer server = new AxisServer();
        Message message = new Message(fault);
        message.setMessageContext(new MessageContext(server));

        String data = message.getSOAPPartAsString();
        assertTrue("Fault code not encoded correctly",
            data.indexOf("&lt;code&gt;")>=0);
        assertTrue("Fault string not encoded correctly",
            data.indexOf("&lt;string&gt;")>=0);
        assertTrue("Fault actor not encoded correctly",
            data.indexOf("&lt;actor&gt;")>=0);
        assertTrue("Fault detail not encoded correctly",
            data.indexOf("&lt;detail&gt;")>=0);
        
    } // testFault
}
