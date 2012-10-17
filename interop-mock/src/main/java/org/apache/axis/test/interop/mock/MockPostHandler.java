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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class MockPostHandler extends SOAPHandler implements InitializingBean {
    private static final Log log = LogFactory.getLog(MockPostHandler.class);
    
    private List<MessageProcessor> requestProcessors;
    private List<Exchange> exchanges;
    private Set<String> supportedContentTypes;
    
    public void setRequestProcessors(List<MessageProcessor> requestProcessors) {
        this.requestProcessors = requestProcessors;
    }

    public void setExchanges(List<Exchange> exchanges) {
        this.exchanges = exchanges;
    }
    
    public void afterPropertiesSet() throws Exception {
        supportedContentTypes = new HashSet<String>();
        for (Exchange exchange : exchanges) {
            supportedContentTypes.add(exchange.getRequestContentType());
        }
    }

    @Override
    protected void handleSOAPRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        if (!httpRequest.getMethod().equals("POST")) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "This endpoint only supports POST requests");
            return;
        }
        String requestContentTypeHeader = httpRequest.getContentType();
        if (requestContentTypeHeader == null) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "No Content-Type header");
            return;
        }
        MimeType requestContentType;
        try {
            requestContentType = new MimeType(requestContentTypeHeader);
        } catch (MimeTypeParseException ex) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Content-Type header: " + ex.getMessage());
            return;
        }
        String requestBaseContentType = requestContentType.getBaseType().toLowerCase(Locale.ENGLISH);
        if (!supportedContentTypes.contains(requestBaseContentType)) {
            httpResponse.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Only " + supportedContentTypes + " are supported");
            return;
        }
        String charset = requestContentType.getParameter(Constants.CHARSET_PARAM);
        Document requestDocument;
        try {
            requestDocument = DOMUtil.parse(httpRequest.getInputStream(), charset);
        } catch (SAXException ex) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unparsable request " + ex.getMessage());
            return;
        }
        if (charset == null) {
            charset = requestDocument.getXmlEncoding();
            if (log.isDebugEnabled()) {
                log.debug("Setting charset from request entity: " + charset);
            }
        }
        Element request = requestDocument.getDocumentElement();
        DOMUtil.removeWhitespace(request);
        if (requestProcessors != null) {
            for (MessageProcessor processor : requestProcessors) {
                if (log.isDebugEnabled()) {
                    log.debug("Executing message processor " + processor);
                }
                processor.process(request);
            }
        }
        Element responseMessage = null;
        for (Exchange exchange : exchanges) {
            if (exchange.getRequestContentType().equals(requestBaseContentType)) {
                responseMessage = exchange.matchRequest(request);
                if (responseMessage != null) {
                    break;
                }
            }
        }
        if (responseMessage == null) {
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Don't know how to respond");
            return;
        }
        MimeType responseContentType;
        try {
            responseContentType = new MimeType(SOAPUtil.getContentType(responseMessage));
        } catch (MimeTypeParseException ex) {
            throw new ServletException("Unexpected exception", ex);
        }
        responseContentType.setParameter(Constants.CHARSET_PARAM, charset);
        httpResponse.setContentType(responseContentType.toString());
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException ex) {
            throw new ServletException("Unexpected exception", ex);
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, charset);
        try {
            transformer.transform(new DOMSource(responseMessage), new StreamResult(httpResponse.getOutputStream()));
        } catch (TransformerException ex) {
            throw new ServletException(ex);
        }
    }
}
