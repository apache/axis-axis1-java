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
