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
package org.apache.axis.transport.http.javanet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.Base64;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;

/**
 * Pivot handler for the HTTP transport based on the {@link HttpURLConnection} API.
 * 
 * @author Andreas Veithen
 */
public class JavaNetHTTPSender extends BasicHandler {
    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(JavaNetHTTPSender.class.getName());
    
    /**
     * The value of the <tt>User-Agent</tt> header. It is composed of the Axis version, the version
     * of JavaNetHTTPSender (which may be different, because it may work with older Axis versions as
     * well) and the Java version (which is important because we are using the HTTP client of the
     * JRE).
     */
    private static final String userAgent = Messages.getMessage("axisUserAgent") + " "
            + Messages.getMessage("userAgentToken") + " Java/" + System.getProperty("java.version");
    
    public void invoke(MessageContext msgContext) throws AxisFault {
        try {
            Message request = msgContext.getRequestMessage();
            URL url = new URL(msgContext.getStrProp(MessageContext.TRANS_URL));
            
            // Create and configure HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0); // 0 means default chunk size
            int timeout = msgContext.getTimeout();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setUseCaches(false);
            connection.addRequestProperty(HTTPConstants.HEADER_USER_AGENT, userAgent);
            connection.addRequestProperty(HTTPConstants.HEADER_ACCEPT,
                    HTTPConstants.HEADER_ACCEPT_APPL_SOAP + ", "
                            + HTTPConstants.HEADER_ACCEPT_APPLICATION_DIME + ", "
                            + HTTPConstants.HEADER_ACCEPT_MULTIPART_RELATED + ", "
                            + HTTPConstants.HEADER_ACCEPT_TEXT_ALL);
            
            // Set "SOAPAction" header
            String action = null;
            if (msgContext.useSOAPAction()) {
                action = msgContext.getSOAPActionURI();
            }
            if (action == null) {
                action = "";
            }
            connection.addRequestProperty(HTTPConstants.HEADER_SOAP_ACTION, action);
            
            // Set "Authorization" header
            String userID = msgContext.getUsername();
            if (userID != null) {
                String passwd = msgContext.getPassword();
                byte[] token = (userID + ":" + (passwd == null ? "" : passwd)).getBytes("UTF-8");
                connection.addRequestProperty(HTTPConstants.HEADER_AUTHORIZATION, "Basic " + Base64.encode(token));
            }
            
            // Set "Content-Type" header
            connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE,
                    request.getContentType(msgContext.getSOAPConstants()));
            
            // Send request
            OutputStream out = connection.getOutputStream();
            request.writeTo(out);
            out.close();
            
            // Process response
            int statusCode = connection.getResponseCode();
            String responseContentType = connection.getHeaderField(HTTPConstants.HEADER_CONTENT_TYPE);
            String rawResponseContentType;
            if (responseContentType == null) {
                rawResponseContentType = null;
            } else {
                int idx = responseContentType.indexOf(';');
                rawResponseContentType = idx == -1 ? responseContentType : responseContentType.substring(0, idx);
            }
            if (log.isDebugEnabled()) {
                log.debug("Status code: " + statusCode);
                log.debug("Content type: " + responseContentType);
                log.debug("Raw content type: " + rawResponseContentType);
            }
            InputStream in;
            // TODO: need to recognize at least 202 here!
            // TODO: if we enable chunkedStreamingMode, then redirection is not handled transparently
            if (statusCode == 200) {
                in = connection.getInputStream();
            } else if (statusCode == 500 && rawResponseContentType != null
                    && (rawResponseContentType.equalsIgnoreCase(SOAPConstants.SOAP11_CONSTANTS.getContentType())
                            || rawResponseContentType.equalsIgnoreCase(SOAPConstants.SOAP12_CONSTANTS.getContentType()))) {
                in = connection.getErrorStream();
            } else {
                // TODO: extract charset encoding from document type (reuse the code in org.apache.axisMessage?)
                Reader reader = new InputStreamReader(connection.getErrorStream(), "UTF-8");
                StringBuffer content = new StringBuffer();
                char[] buffer = new char[4096];
                int c;
                while ((c = reader.read(buffer)) != -1) {
                    content.append(buffer, 0, c);
                }
                reader.close();
                AxisFault fault = new AxisFault("HTTP", statusCode + " " + connection.getResponseMessage(), null, null);
                fault.setFaultDetailString(content.toString());
                fault.addFaultDetail(Constants.QNAME_FAULTDETAIL_HTTPERRORCODE, Integer.toString(statusCode));
                throw fault;
            }
            
            Message response = new Message(in, false, responseContentType,
                    connection.getHeaderField(HTTPConstants.HEADER_CONTENT_LOCATION));
            MimeHeaders mimeHeaders = response.getMimeHeaders();
            String key;
            for (int i=0; (key = connection.getHeaderField(i)) != null; i++) {
                mimeHeaders.addHeader(key, connection.getHeaderField(i));
            }
            response.setMessageType(Message.RESPONSE);
            msgContext.setResponseMessage(response);
            
            // Note: the JRE seems to automatically release/recycle the connection when the response stream has
            //       been consumed; therefore we don't need to do anything to explicitly close the connection
            
        } catch (IOException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to send to " + msgContext.getStrProp(MessageContext.TRANS_URL), ex);
            }
            throw AxisFault.makeFault(ex);
        } catch (SOAPException ex) {
            throw AxisFault.makeFault(ex);
        }
    }
}
