/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.server.AxisServer;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.namespace.QName;

/**
 * This class tests Fault deserialization.
 *
 * @author Mark Roder <mroder@wamnet.com>
 * @author Glen Daniels (gdaniels@macromedia.com)
 */

public class FaultDecode extends TestCase {
    public static final String FAULT_CODE = "Some.FaultCode";
    public static final String FAULT_STRING = "This caused a fault";
    public static final String DETAIL_ENTRY_TEXT =
        "This was a really bad thing";
    
    public FaultDecode(String name) {
        super(name);
    } // ctor
    
    public static Test suite() {
        return new TestSuite(FaultDecode.class);
    }

    public void testFault() throws Exception {
        String messageText = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
            + "<soap:Header>"
            + ""
            + "</soap:Header> "
            + "<soap:Body>  "
            + "     <soap:Fault>"
            + "          <faultcode>" + FAULT_CODE + "</faultcode>"
            + "          <faultstring>" + FAULT_STRING + "</faultstring>"
            + "          <detail><d1>" + DETAIL_ENTRY_TEXT + "</d1></detail>"
            + "     </soap:Fault>"
            + "</soap:Body>"
            + "</soap:Envelope>";

        AxisServer server = new AxisServer();
        Message message = new Message(messageText);
        message.setMessageContext(new MessageContext(server));

        SOAPEnvelope envelope = (SOAPEnvelope) message.getSOAPEnvelope();
        assertNotNull("envelope", envelope);

        SOAPBodyElement respBody = envelope.getFirstBody();
        assertTrue("respBody should be a SOAPFaultElement", respBody
                        instanceof SOAPFault);
        AxisFault aFault = ((SOAPFault) respBody).getFault();

        assertNotNull("Fault should not be null", aFault);
        
        QName faultCode = aFault.getFaultCode();
        assertNotNull("faultCode should not be null", faultCode);
        assertEquals("faultCode should match",
                     faultCode.getLocalPart(), 
                     "Some.FaultCode");
        
        String faultString = aFault.getFaultString();
        assertNotNull("faultString should not be null", faultString);
        assertEquals("faultString should match", faultString,
                     FAULT_STRING);
        
        Element [] details = aFault.getFaultDetails();
        assertNotNull("faultDetails should not be null", details);
        assertEquals("details should have exactly one element", details.length, 
                     1);
        
        Element el = details[0];
        assertEquals("detail entry tag name should match",
                     el.getLocalName(), "d1");
        
        Text text = (Text)el.getFirstChild();
        assertEquals("detail entry string should match",
                     text.getData(), DETAIL_ENTRY_TEXT);
        
    } // testFault
    
    public static void main(String[] args) throws Exception {
        FaultDecode tester = new FaultDecode("test");
        TestRunner runner = new TestRunner();
        runner.doRun(tester.suite(), false);
    }
}
