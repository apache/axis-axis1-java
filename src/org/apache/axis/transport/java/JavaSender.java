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

        if ( msgContext.getProperty(MessageContext.IS_MSG) == null ) 
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
