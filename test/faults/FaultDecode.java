/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
package test.faults;

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFaultElement;
import org.apache.axis.server.AxisServer;

/**
 * This class tests a case where the fault is not filled in..
 *
 * @author Mark Roder <mroder@wamnet.com>
 */

public class FaultDecode extends TestCase {

    public FaultDecode(String name) {
        super(name);
    } // ctor

    public void testFault() throws Exception {
        String messageText = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
            + "<soap:Header>"
            + ""
            + "</soap:Header> "
            + "<soap:Body>  "
            + "     <soap:Fault>"
            + "              <faultcode>Some.FaultCode</faultcode>"
            + "              <faultstring>This caused a fault</faultstring>"
            + "              <detail>This was a really bad thing</detail>"
            + "     </soap:Fault>"
            + "</soap:Body>"
            + "</soap:Envelope>";

        AxisServer server = new AxisServer();
        Message message = new Message(messageText);
        message.setMessageContext(new MessageContext(server));

        SOAPEnvelope envelope = (SOAPEnvelope) message.getAsSOAPEnvelope();
        this.assertNotNull("envelope", envelope);

        SOAPBodyElement respBody = envelope.getFirstBody();
        this.assertTrue("respBody should be a SOAPFaultElement", respBody
                        instanceof SOAPFaultElement);
        AxisFault aFault = ((SOAPFaultElement) respBody).getAxisFault();

        System.out.println(aFault);

        this.assertNotNull("Fault should not be null", aFault);
        this.assertNotNull("faultCode should not be null", aFault.getFaultCode());
        this.assertNotNull("faultString should not be null", aFault.getFaultString());
        this.assertNotNull("faultDetails should not be null", aFault.getFaultDetails());
    } // testFault
}
