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

package test.httpunit;

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

    private static String URL_PROPERTY="test.functional.webapp.url";
    /**
     *  The JUnit setup method
     *
     */
    public void setUp() throws Exception {
        url=System.getProperty(URL_PROPERTY);
        assertNotNull(URL_PROPERTY+" not set",url);
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
            message="failed to find ["+searchfor+"] at "+url;
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
     * checking for text is omitted as it doesnt work. It would never work on
     * java1.3, but one may have expected java1.4+ to have access to the
     * error stream in responses. clearly not
     * @param request
     * @param errorCode
     * @param errorText optional text string to search for
     * @throws MalformedURLException
     * @throws IOException
     * @throws SAXException
     */
    protected void expectErrorCode(WebRequest request,
                                   int errorCode, String errorText)
                        throws MalformedURLException, IOException, SAXException {
        WebConversation session = new WebConversation();
        String failureText="Expected error "+errorCode+" from "+request.getURL();
        try {
            session.getResponse(request);
            fail(errorText+" -got success instead");
        } catch (HttpException e) {
            assertEquals(failureText,errorCode,e.getResponseCode());
            /* checking for text omitted as it doesnt work.
            if(errorText!=null) {
                assertTrue(
                        "Failed to find "+errorText+" in "+ e.getResponseMessage(),
                        e.getMessage().indexOf(errorText)>=0);
            }
            */
        }
    }
}
