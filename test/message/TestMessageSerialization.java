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

package test.message;

import junit.framework.TestCase;
import org.apache.axis.Message;
import org.apache.axis.message.MimeHeaders;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;

/**
 * Test serializability of org.apache.axis.Message
 * 
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class TestMessageSerialization extends TestCase {

    public TestMessageSerialization(String name) {
        super(name);
    }

    public void test1() throws Exception {
        String messageText = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
            + "<soap:Header>"
            + "</soap:Header> "
            + "<soap:Body>  "
            + "</soap:Body>"
            + "</soap:Envelope>";
        Message message = new Message(messageText);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(ostream);
        os.writeObject(message);
        ostream.flush();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(ostream.toByteArray()));
        Message m2 = (Message) ois.readObject();
    }
}
