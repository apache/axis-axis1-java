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

package org.apache.axis.security.simple;

import org.apache.axis.security.AuthenticatedUser;

/**
 * SimpleAuthenticatedUser is a trivial implementation of the
 * AuthenticatedUser interface, for use with a default Axis installation
 * and the SimpleSecurityProvider.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class SimpleAuthenticatedUser implements AuthenticatedUser {
    private String name;

    public SimpleAuthenticatedUser(String name)
    {
        this.name = name;
    }

    /** Return a string representation of the user's name.
     *
     * @return the user's name as a String.
     */
    public String getName() {
        return name;
    }
}
