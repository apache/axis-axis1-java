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

package org.apache.axis.security;

import org.apache.axis.MessageContext;

/** The Axis security provider interface
 * 
 * As Axis is designed for use in embedded environments, those
 * environments will often contain their own security databases and
 * potentially authentication managers.  This interface allows Axis
 * to obtain authentication information from an opaque source which
 * will presumably be configured into the engine at startup time.
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 */
public interface SecurityProvider
{
    /** Authenticate a user from a username/password pair.
     * 
     * @param msgContext the MessageContext containing authentication info
     * @return an AuthenticatedUser or null
     */
    public AuthenticatedUser authenticate(MessageContext msgContext);
    
    /** See if a user matches a principal name.  The name might be a user
     * or a group.
     * 
     * @return true if the user matches the passed name
     */
    public boolean userMatches(AuthenticatedUser user, String principal);
}
