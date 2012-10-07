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
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.HttpRequestHandler;

public abstract class SOAPHandler implements HttpRequestHandler {
    private Resource wsdl;

    public void setWsdl(Resource wsdl) {
        this.wsdl = wsdl;
    }

    public final void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equals("GET") && request.getQueryString() != null && request.getQueryString().equalsIgnoreCase("wsdl")) {
            if (wsdl == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No WSDL available for this service");
            } else {
                response.setContentType("text/xml");
                InputStream in = wsdl.getInputStream();
                try {
                    IOUtils.copy(in, response.getOutputStream());
                } finally {
                    in.close();
                }
            }
        } else {
            handleSOAPRequest(request, response);
        }
    }

    protected abstract void handleSOAPRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
