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

package test.MSGDispatch;

import org.apache.axis.AxisFault;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * This class is a message-based service with three methods.  It tests:
 *
 * 1) Our ability to dispatch to multiple methods for a message service
 * 2) That each of the valid signatures works as expected
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class TestService {
    // Adding these dummy methods to make sure that when we deploy this
    // service using "allowedMethods="*" that we don't barf on them.
    // This will ensure that people can take classes that have public
    // methods (some available thru Axis and some not) and still be able
    // to deploy them.  (We used to throw exceptions about it)
    public void testBody(int t) {}
    public void testElement(int t) {}
    public void testEnvelope(int t) {}

    public SOAPBodyElement [] testBody(SOAPBodyElement [] bodies)
            throws Exception {

        String xml = "<m:bodyResult xmlns:m=\"http://db.com\"/>" ;
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        SOAPBodyElement result = new SOAPBodyElement(is);
        return new SOAPBodyElement [] { result };
    }

    public Element [] testElement(Element [] bodyElems)
            throws Exception {
        if (bodyElems == null || bodyElems.length != 1) {
            throw new AxisFault("Wrong number of Elements in array!");
        }
        Element el = bodyElems[0];
        if (el == null) {
            throw new AxisFault("Null Element in array!");
        }
        if (!"http://db.com".equals(el.getNamespaceURI())) {
            throw new AxisFault("Wrong namespace for Element (was \"" +
                                el.getNamespaceURI() + "\" should be " +
                                "\"http://db.com\"!");
        }
        String xml = "<m:elementResult xmlns:m=\"http://db.com\"/>" ;
        Document doc = XMLUtils.newDocument(
                new ByteArrayInputStream(xml.getBytes()));
        Element result = doc.getDocumentElement();
        return new Element [] { result };
    }

    public Element [] testElementEcho(Element [] bodyElems)
            throws Exception {
        return bodyElems;
    }
    
    public void testEnvelope(SOAPEnvelope req, SOAPEnvelope resp)
            throws Exception {
        // Throw a header in and echo back.
        SOAPBodyElement body = req.getFirstBody();
        resp.addBodyElement(body);
        resp.addHeader(new SOAPHeaderElement("http://db.com", "local", "value"));
    }
}
