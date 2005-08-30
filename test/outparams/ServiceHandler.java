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

package test.outparams;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;

public class ServiceHandler extends BasicHandler {
    public static final String OUTPARAM1 = "Output param value";
    public static final Float OUTPARAM2 = new Float(4.56);
    public static final Integer RESPONSE = new Integer(5);

    public void invoke(MessageContext msgContext) throws AxisFault {
        SOAPEnvelope env = new SOAPEnvelope();

        RPCParam retVal = new RPCParam("return", RESPONSE);
        RPCParam outParam1 = new RPCParam("out1", OUTPARAM1);
        RPCParam outParam2 = new RPCParam("out2", OUTPARAM2);

        RPCElement rpc = new RPCElement("namespace", "response", new Object []
                            { retVal, outParam1, outParam2 });

        env.addBodyElement(rpc);

        msgContext.setResponseMessage(new Message(env));
    }
}
