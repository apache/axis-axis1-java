/*
 * Copyright 2002-2004 The Apache Software Foundation.
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