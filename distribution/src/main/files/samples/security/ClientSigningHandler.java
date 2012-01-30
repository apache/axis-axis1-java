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

package samples.security;

import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.commons.logging.Log;


public class ClientSigningHandler extends BasicHandler {
    static Log log =
            LogFactory.getLog(ClientSigningHandler.class.getName());

    static {
        org.apache.xml.security.Init.init();
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        /** Sign the SOAPEnvelope
         */
        try {
            Handler serviceHandler = msgContext.getService();
            String filename = (String) getOption("keystore");
            if ((filename == null) || (filename.equals("")))
                throw new AxisFault("Server.NoKeyStoreFile",
                        "No KeyStore file configured for the ClientSigningHandler!",
                        null, null);
            Message requestMessage = msgContext.getRequestMessage();
            SOAPEnvelope unsignedEnvelope = requestMessage.getSOAPEnvelope();
            // need to correctly compute baseuri
            SignedSOAPEnvelope signedEnvelope = new SignedSOAPEnvelope(msgContext, unsignedEnvelope, "http://xml-security", filename);
            requestMessage = new Message(signedEnvelope);
            msgContext.setCurrentMessage(requestMessage);
            // and then pass on to next handler
            //requestMessage.getSOAPPart().writeTo(System.out);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    public void onFault(MessageContext msgContext) {
        try {
            // probably needs to fault.
        } catch (Exception e) {
            log.error(e);
        }
    }
}
