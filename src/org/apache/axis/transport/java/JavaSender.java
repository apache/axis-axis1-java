/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

package org.apache.axis.transport.java;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.enum.Scope;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.MsgProvider;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.commons.logging.Log;

public class JavaSender extends BasicHandler {
    protected static Log log =
        LogFactory.getLog(JavaSender.class.getName());

    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: JavaSender::invoke");
        }

        SOAPService   service     = null ;
        SOAPService   saveService = msgContext.getService();
        OperationDesc saveOp      = msgContext.getOperation();

        Call   call = (Call) msgContext.getProperty( MessageContext.CALL );
        String url  = call.getTargetEndpointAddress();
        String cls  = url.substring(5);
  
        msgContext.setService( null );
        msgContext.setOperation( null );

        if ( msgContext.getProperty(msgContext.IS_MSG) == null ) 
          service   = new SOAPService(new RPCProvider());
        else
          service   = new SOAPService(new MsgProvider());

        if ( cls.startsWith("//") ) cls = cls.substring(2);
        service.setOption(RPCProvider.OPTION_CLASSNAME, cls);
        service.setEngine(msgContext.getAxisEngine());
  
        service.setOption( RPCProvider.OPTION_ALLOWEDMETHODS, "*" );
        service.setOption( RPCProvider.OPTION_SCOPE, Scope.DEFAULT.getName());
        service.getInitializedServiceDesc( msgContext );
        service.init();
  
        msgContext.setService( service );

        service.invoke( msgContext );
  
        msgContext.setService( saveService );
        msgContext.setOperation( saveOp );

        if (log.isDebugEnabled()) {
            log.debug("Exit: JavaSender::invoke");
        }
    }
}
