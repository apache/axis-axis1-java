/*
 * The Apache Software License, Version 1.1
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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

package test;

import junit.framework.TestCase;
import com.meterware.httpunit.*;

import java.io.*;
import java.net.MalformedURLException;

import org.xml.sax.SAXException;

/**
 * class to make it that much easier to validate httpunit requests
 */
public class HttpUnitTestBase extends TestCase {
    /**
     *  our url
     *
     */
    protected String url;

    public HttpUnitTestBase(String s) {
        super(s);
    }

    /**
     *  The JUnit setup method
     *
     */
    public void setUp() throws Exception {
        url=System.getProperty("server.url");
        assertNotNull("server.url not set",url);
        HttpUnitOptions.setExceptionsThrownOnErrorStatus(true);
        HttpUnitOptions.setMatchesIgnoreCase(true);
        HttpUnitOptions.setParserWarningsEnabled(true);
    }

    /**
     * assert that the response contains a string
     * @param response
     * @param searchfor
     * @param url
     * @throws IOException
     */
    public void assertStringInBody(WebResponse response,String searchfor, String url)
            throws IOException {
        String body=response.getText();
        boolean found=body.indexOf(searchfor)>=0;
        if(!found) {
            String message;
            message="failed to find "+searchfor+" at "+url;
            fail(message);
        }
    }

    /**
     * assert that a named string is in the request body of the
     * response to a request
     * @param request what we ask
     * @param searchfor string to look for
     * @throws IOException when the fetch fails
     * @throws org.xml.sax.SAXException
     */
    protected void assertStringInBody( WebRequest request,
                                       String searchfor
                                       )
                throws IOException, org.xml.sax.SAXException {
        WebResponse response = makeRequest(request);
        assertStringInBody(response,searchfor,request.getURL().toString());
    }

    /**
     * make a request in a new session
     * @param request   request to make
     * @return the response
     * @throws IOException
     * @throws SAXException
     */
    protected WebResponse makeRequest(WebRequest request) throws IOException, SAXException {
        WebConversation session = new WebConversation();
        WebResponse response=session.getResponse(request);
        return response;
    }

    /**
     * assert that a string is not in a response
     * @param response
     * @param searchfor
     * @param url
     * @throws IOException
     */
    protected void assertStringNotInBody(WebResponse response,
                                         String searchfor,
                                         String url)
            throws IOException {
        String body=response.getText();
        boolean found=body.indexOf(searchfor)>=0;
        if(found) {
            String message;
            message="unexpectedly found "+searchfor+" at "+url;
            fail(message);
        }

    }

    /**
     * assert that a string is not in the response to a request
     * @param request
     * @param searchfor
     * @throws IOException
     * @throws org.xml.sax.SAXException
     */
    protected void assertStringNotInBody( WebRequest request,
                                          String searchfor)
                throws IOException, org.xml.sax.SAXException {
        WebConversation session = new WebConversation();
        WebResponse response=session.getResponse(request);
        assertStringNotInBody(response,searchfor,
                request.getURL().toString());
    }

    /**
     * here we expect an errorCode other than 200, and look for it
     * @param request
     * @param errorCode
     * @throws MalformedURLException
     * @throws IOException
     * @throws SAXException
     */
    protected void expectErrorCode(WebRequest request,
                                   int errorCode)
                        throws MalformedURLException, IOException, SAXException {
        WebConversation session = new WebConversation();
        String errorText="Expected error "+errorCode+" from "+request.getURL();
        try {
            session.getResponse(request);
            fail(errorText+" -got success instead");
        } catch (HttpException e) {
            assertEquals(errorText,
                        errorCode,e.getResponseCode());
        }
    }
}
