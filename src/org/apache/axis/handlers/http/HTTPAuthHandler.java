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

import org.apache.axis.AxisFault;
import org.apache.axis.AxisInternalServices;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.Base64;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.JavaUtils;

import org.apache.commons.logging.Log;


/** An <code>HTTPAuthHandler</code> simply sets the context's username
 * and password properties from the HTTP auth headers.
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 * @author Doug Davis (dug@us.ibm.com)
 */
public class HTTPAuthHandler extends BasicHandler
{
    protected static Log log =
        AxisInternalServices.getLog(HTTPAuthHandler.class.getName());

    public void invoke(MessageContext msgContext) throws AxisFault
    {
        log.debug("Enter: HTTPAuthHandler::invoke");
        
        /* Process the Basic Auth stuff in the headers */
        /***********************************************/
        String tmp = (String)msgContext.getProperty(HTTPConstants.HEADER_AUTHORIZATION);
        if ( tmp != null ) tmp = tmp.trim();
        if ( tmp != null && tmp.startsWith("Basic ") ) {
            String user=null ;
            int  i ;

            tmp = new String( Base64.decode( tmp.substring(6) ) );
            i = tmp.indexOf( ':' );
            if ( i == -1 ) user = tmp ;
            else           user = tmp.substring( 0, i);
            msgContext.setUsername( user );
            log.debug( JavaUtils.getMessage("httpUser00", user) );
            if ( i != -1 )  {
                String pwd = tmp.substring(i+1);
                if ( pwd != null && pwd.equals("") ) pwd = null ;
                if ( pwd != null ) {
                    msgContext.setPassword( pwd );
                    log.debug( JavaUtils.getMessage("httpPassword00", pwd) );
                }
            }
        }

        log.debug("Exit: HTTPAuthHandler::invoke");
    }
}
