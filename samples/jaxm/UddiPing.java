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
package samples.jaxm;

import javax.xml.messaging.URLEndpoint;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

public class UddiPing {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: UddiPing business-name uddi-url");
            System.exit(1);
        }
        searchUDDI(args[0], args[1]);
    }

    public static void searchUDDI(String name, String url) throws Exception {
        // Create the connection and the message factory.
        SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = scf.createConnection();
        MessageFactory msgFactory = MessageFactory.newInstance();

        // Create a message
        SOAPMessage msg = msgFactory.createMessage();

        // Create an envelope in the message
        SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();

        // Get hold of the the body
        SOAPBody body = envelope.getBody();

        javax.xml.soap.SOAPBodyElement bodyElement = body.addBodyElement(envelope.createName("find_business", "",
                "urn:uddi-org:api"));

        bodyElement.addAttribute(envelope.createName("generic"), "1.0")
                .addAttribute(envelope.createName("maxRows"), "100")
                .addChildElement("name")
                .addTextNode(name);

        URLEndpoint endpoint = new URLEndpoint(url);
        msg.saveChanges();

        SOAPMessage reply = connection.call(msg, endpoint);
        //System.out.println("Received reply from: " + endpoint);
        //reply.writeTo(System.out);
        connection.close();
    }
}






