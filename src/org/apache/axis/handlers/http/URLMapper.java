/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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

package org.apache.axis.handlers.http;

import org.apache.axis.*;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.utils.Debug;
import org.apache.axis.transport.http.HTTPConstants;

import javax.servlet.http.* ;

/** An <code>URLMapper</code> attempts to use the extra path info
 * of this request as the service name.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class URLMapper extends BasicHandler
{
    public void invoke(MessageContext msgContext) throws AxisFault
    {
        Debug.Print( 1, "Enter: URLMapper::invoke" );

        /** If there's already a targetService (ie. JWSProcessor) then
         *  just return.
         */
        if ( msgContext.getTargetService() == null ) {
            HttpServletRequest req = (HttpServletRequest) msgContext.getProperty(
                                              HTTPConstants.MC_HTTP_SERVLETREQUEST);
            
            // Assumes "/" + servicename
            String path = req.getPathInfo().substring(1);
    
            msgContext.setTargetService( path );
        }

        Debug.Print( 1, "Exit : URLMapper::invoke" );
    }

    public void undo(MessageContext msgContext) 
    {
        Debug.Print( 1, "Enter: URLMapper::undo" );
        Debug.Print( 1, "Exit: URLMapper::undo" );
    }
}
