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
package test.soap;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;

/**
 * This is a test Handler which interacts with the TestService below in
 * order to test header processing.  This one runs before the TestHandler,
 * so when the TestHandler throws an Exception (triggered by a particular
 * header being sent from TestOnFaultHeaders), the onFault() method gets
 * called.  In there, we get the response message and add a header to it.
 * This header gets picked up by the client, who checks that it looks right
 * and has successfully propagated from here to the top of the call stack.
 * 
 * @author Glen Daniels (gdaniels@apache.org) 
 */ 
public class TestFaultHandler extends BasicHandler {
    public void invoke(MessageContext msgContext) throws AxisFault {
    }

    public void onFault(MessageContext msgContext) {
        try {
            SOAPEnvelope env = msgContext.getResponseMessage().getSOAPEnvelope();
            SOAPHeaderElement header = new SOAPHeaderElement(
                    TestOnFaultHeaders.TRIGGER_NS,
                    TestOnFaultHeaders.RESP_NAME,
                    "here's the value"
            );
            env.addHeader(header);
        } catch (Exception e) {
            throw new RuntimeException("Exception during onFault processing");
        }
    }
}
