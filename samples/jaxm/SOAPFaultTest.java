/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
package samples.jaxm;

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import java.util.Iterator;

public class SOAPFaultTest {

    public static void main(String[] args) throws Exception {
        MessageFactory msgFactory =
                MessageFactory.newInstance();
        SOAPMessage msg = msgFactory.createMessage();
        SOAPEnvelope envelope =
                msg.getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();
        SOAPFault fault = body.addFault();

        fault.setFaultCode("Client");
        fault.setFaultString(
                "Message does not have necessary info");
        fault.setFaultActor("http://gizmos.com/order");

        Detail detail = fault.addDetail();

        Name entryName = envelope.createName("order", "PO",
                "http://gizmos.com/orders/");
        DetailEntry entry = detail.addDetailEntry(entryName);
        entry.addTextNode(
                "quantity element does not have a value");

        Name entryName2 = envelope.createName("confirmation",
                "PO", "http://gizmos.com/confirm");
        DetailEntry entry2 = detail.addDetailEntry(entryName2);
        entry2.addTextNode("Incomplete address: no zip code");

        msg.saveChanges();

        // Now retrieve the SOAPFault object and its contents
        //after checking to see that there is one

        if (body.hasFault()) {
            fault = body.getFault();
            String code = fault.getFaultCode();
            String string = fault.getFaultString();
            String actor = fault.getFaultActor();

            System.out.println("SOAP fault contains: ");
            System.out.println("    fault code = " + code);
            System.out.println("    fault string = " + string);
            if (actor != null) {
                System.out.println("    fault actor = " + actor);
            }

            detail = fault.getDetail();
            if (detail != null) {
                Iterator it = detail.getDetailEntries();
                while (it.hasNext()) {
                    entry = (DetailEntry) it.next();
                    String value = entry.getValue();
                    System.out.println("    Detail entry = " + value);
                }
            }
        }
    }
}