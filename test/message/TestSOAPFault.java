/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
package test.message;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;

/**
 * @author steve.johnson@riskmetrics.com (Steve Johnson)
 * @author Davanum Srinivas (dims@yahoo.com)
 * @version $Revision$
 */
public class TestSOAPFault extends TestCase {

    /**
     * Method suite
     * 
     * @return 
     */
    public static Test suite() {
        return new TestSuite(TestSOAPFault.class);
    }

    /**
     * Method main
     * 
     * @param argv 
     */
    public static void main(String[] argv) throws Exception {
        TestSOAPFault tester = new TestSOAPFault("TestSOAPFault");
        tester.testSoapFaultBUG();
    }

    /**
     * Constructor TestSOAPFault
     * 
     * @param name 
     */
    public TestSOAPFault(String name) {
        super(name);
    }

    String xmlString =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<soapenv:Body>" +
            "<soapenv:Fault>" +
            "<faultcode>soapenv:13001</faultcode>" +
            "<faultstring>java.lang.Exception: File already exists</faultstring>" +
            "<faultactor>urn:RiskMetricsDirect:1.0:object-service-service:CreateObject</faultactor>" +
            "<detail/>" +
            "</soapenv:Fault>" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";

    /**
     * Method testSoapFaultBUG
     * 
     * @throws Exception 
     */
    public void testSoapFaultBUG() throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(xmlString.getBytes());
        MessageFactory msgFactory = MessageFactory.newInstance();
        SOAPMessage msg = msgFactory.createMessage(null, bis);
			
        //now attempt to access the fault
        if (msg.getSOAPPart().getEnvelope().getBody().hasFault()) {
            SOAPFault fault =
                    msg.getSOAPPart().getEnvelope().getBody().getFault();
            System.out.println("Fault: " + fault.getFaultString());
        }
    }
}    