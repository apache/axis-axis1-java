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

package samples.echo;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;


/** This handler processes the SOAP header "echoMeString" defined in the 
 *  SOAPBuilder Round2C interop tests.
 *
 * <p>Essentially, you install it on both the request and response chains of
 * your service, on the server side.</p>
 *
 * @author Simon Fell (simon@zaks.demon.co.uk)
 */
public class echoHeaderStringHandler extends BasicHandler
{
    static Log log =
            LogFactory.getLog(echoHeaderStringHandler.class.getName());

    public static final String ECHOHEADER_STRING_ID = "echoHeaderStringHandler.id";
    public static final String HEADER_NS = "http://soapinterop.org/echoheader/";
    public static final String HEADER_REQNAME = "echoMeStringRequest";
    public static final String HEADER_RESNAME = "echoMeStringResponse";
    public static final String ACTOR_NEXT = "http://schemas.xmlsoap.org/soap/actor/next";

    public boolean canHandleBlock(QName qname) {
        if (HEADER_NS.equals(qname.getNamespaceURI()) &&
                HEADER_REQNAME.equals(qname.getLocalPart())) {
            return true;
        }
        
        return false;
    }

    /**
     * Process a MessageContext.
     */
    public void invoke(MessageContext context) throws AxisFault
    {    
        if (context.getPastPivot()) {
            // This is a response.  Add the response header, if we saw
            // the requestHeader
            String strVal = (String)context.getProperty(ECHOHEADER_STRING_ID);
            if (strVal == null)
                return;
            
            Message msg = context.getResponseMessage();
            if (msg == null)
                return;
            SOAPEnvelope env = msg.getSOAPEnvelope();
            SOAPHeaderElement header = new SOAPHeaderElement(HEADER_NS,
                                                             HEADER_RESNAME,
                                                             strVal);
            env.addHeader(header);
        } else {
            // Request. look for the header
            Message msg = context.getRequestMessage();
            if (msg == null)
                throw new AxisFault(Messages.getMessage("noRequest00"));
            
            SOAPEnvelope env = msg.getSOAPEnvelope();
            SOAPHeaderElement header = env.getHeaderByName(HEADER_NS,
                                                           HEADER_REQNAME);
            
            if (header != null) {
                // seems Axis has already ignored any headers not tageted
                // at us
                String strVal ;
                // header.getValue() doesn't seem to be connected to anything
                // we always get null.
                try {
                    strVal = (String)header.getValueAsType(Constants.XSD_STRING);
                } catch (Exception e) {
                    throw AxisFault.makeFault(e);
                }
                context.setProperty(ECHOHEADER_STRING_ID, strVal) ;
                header.setProcessed(true);
            }
        }
    }
}
