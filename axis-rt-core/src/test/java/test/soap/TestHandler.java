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
 * order to test header processing.  If a particular header appears in
 * the message, we mark the MessageContext so the TestService knows to
 * double its results (which is a detectable way of confirming header
 * processing on the client side).
 */ 
public class TestHandler extends BasicHandler {
    public void invoke(MessageContext msgContext) throws AxisFault {
        SOAPEnvelope env = msgContext.getRequestMessage().getSOAPEnvelope();
        if (env.getHeaderByName(TestHeaderAttrs.GOOD_HEADER_NS, 
                                TestHeaderAttrs.GOOD_HEADER_NAME) != null) {
            // Just the header's presence is enough - mark the property
            // so it can be picked up by the service (see below)
            msgContext.setProperty(TestHeaderAttrs.PROP_DOUBLEIT, Boolean.TRUE);
        }
        
        if (env.getHeaderByName(TestOnFaultHeaders.TRIGGER_NS,
                                TestOnFaultHeaders.TRIGGER_NAME) != null) {
            // Fault trigger header is there, so throw an Exception
            throw new AxisFault("triggered exception");
        }
    }

    public void onFault(MessageContext msgContext) {
        try {
            SOAPEnvelope env = msgContext.getResponseMessage().getSOAPEnvelope();
            SOAPHeaderElement header = new SOAPHeaderElement("ns", "local", "val");
            env.addHeader(header);
        } catch (Exception e) {
            throw new RuntimeException("Exception during onFault processing");
        }
    }
}
