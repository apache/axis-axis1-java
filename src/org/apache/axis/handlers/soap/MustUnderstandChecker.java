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

package org.apache.axis.handlers.soap;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

/**
 * MustUnderstandChecker is used to inject SOAP semantics just before
 * the pivot handler.
 */
public class MustUnderstandChecker extends BasicHandler {

    private static Log log =
            LogFactory.getLog(MustUnderstandChecker.class.getName());

    private SOAPService service = null;

    public MustUnderstandChecker(SOAPService service) {
        this.service = service;
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        // Do SOAP semantics here
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("semanticCheck00"));
        }
        SOAPEnvelope env =
                (SOAPEnvelope) msgContext.getCurrentMessage().getSOAPEnvelope();
        Vector headers = null;
        if (service != null) {
            ArrayList acts = service.getActors();
            headers = env.getHeadersByActor(acts);
        } else {
            headers = env.getHeaders();
        }
            
        // 1. Check mustUnderstands
        Vector misunderstoodHeaders = null;
        Enumeration enumeration = headers.elements();
        while (enumeration.hasMoreElements()) {
            SOAPHeaderElement header = (SOAPHeaderElement) enumeration.
                    nextElement();
            if (header.getMustUnderstand() && !header.isProcessed()) {
                if (misunderstoodHeaders == null)
                    misunderstoodHeaders = new Vector();
                misunderstoodHeaders.addElement(header);
            }
        }
        SOAPConstants soapConstants = msgContext.getSOAPConstants();
        // !!! we should indicate SOAP1.2 compliance via the
        // MessageContext, not a boolean here....

        if (misunderstoodHeaders != null) {
            AxisFault fault =
                    new AxisFault(soapConstants.getMustunderstandFaultQName(),
                            null, null,
                            null, null,
                            null);
            StringBuffer whatWasMissUnderstood = new StringBuffer(256);

            // !!! If SOAP 1.2, insert misunderstood fault headers here
            if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                enumeration = misunderstoodHeaders.elements();
                while (enumeration.hasMoreElements()) {
                    SOAPHeaderElement badHeader = (SOAPHeaderElement) enumeration.
                            nextElement();
                    QName badQName = new QName(badHeader.getNamespaceURI(),
                            badHeader.getName());
                    if (whatWasMissUnderstood.length() != 0)
                        whatWasMissUnderstood.append(", ");
                    whatWasMissUnderstood.append(badQName.toString());
                    SOAPHeaderElement newHeader = new
                            SOAPHeaderElement(Constants.URI_SOAP12_ENV,
                                    Constants.ELEM_NOTUNDERSTOOD);
                    newHeader.addAttribute(null,
                            Constants.ATTR_QNAME,
                            badQName);
                    fault.addHeader(newHeader);
                }
            }
            fault.setFaultString(Messages.getMessage("noUnderstand00",
                    whatWasMissUnderstood.toString()));
            throw fault;
        }
    }
}
