/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package org.apache.axis.security.servlet;

import org.apache.axis.security.AuthenticatedUser;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * ServletAuthenticatedUser is a sligtly odd implementation of
 * AuthenticatedUser.  It serves to store an HttpServletRequest,
 * so that request can be used by the ServletSecurityProvider to
 * check roles.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class ServletAuthenticatedUser implements AuthenticatedUser {
    private String name;
    private HttpServletRequest req;

    public ServletAuthenticatedUser(HttpServletRequest req)
    {
        this.req = req;
        Principal principal = req.getUserPrincipal();
        this.name = (principal == null) ? null : principal.getName();
    }

    /** Return a string representation of the user's name.
     *
     * @return the user's name as a String.
     */
    public String getName() {
        return name;
    }

    public HttpServletRequest getRequest()
    {
        return req;
    }
}
