/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.test.interop.mock;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

public class MockGetHandler extends SOAPHandler implements InitializingBean {
    private static final Log log = LogFactory.getLog(MockGetHandler.class);
    
    private Resource response;
    private Element responseMessage;
    private String responseContentType;

    public void setResponse(Resource response) {
        this.response = response;
    }

    public void afterPropertiesSet() throws Exception {
        responseMessage = DOMUtil.parse(response).getDocumentElement();
        DOMUtil.removeWhitespace(responseMessage);
        responseContentType = SOAPUtil.getContentType(responseMessage);
    }

    @Override
    protected void handleSOAPRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        if (!httpRequest.getMethod().equals("GET")) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "This endpoint only supports GET requests");
            return;
        }
        httpResponse.setContentType(responseContentType + "; " + Constants.CHARSET_PARAM + "=UTF-8");
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException ex) {
            throw new ServletException("Unexpected exception", ex);
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        if (log.isDebugEnabled()) {
            log.debug("Returning " + response);
        }
        try {
            transformer.transform(new DOMSource(responseMessage), new StreamResult(httpResponse.getOutputStream()));
        } catch (TransformerException ex) {
            throw new ServletException(ex);
        }
    }
}
