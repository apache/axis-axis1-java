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

package org.apache.axis.handlers.http;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;


/** An <code>URLMapper</code> attempts to use the extra path info
 * of this request as the service name.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class URLMapper extends BasicHandler
{
    protected static Log log =
        LogFactory.getLog(URLMapper.class.getName());

    public void invoke(MessageContext msgContext) throws AxisFault
    {
        log.debug("Enter: URLMapper::invoke");

        /** If there's already a targetService then just return.
         */
        if ( msgContext.getService() == null ) {
            // Assumes "/" + servicename
            String path = (String)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETPATHINFO);
            if ((path != null) && (path.length() > 1)) {
                path = path.substring(1);

                msgContext.setTargetService( path );
            }
        }

        log.debug("Exit: URLMapper::invoke");
    }

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        invoke(msgContext);
    }
}
