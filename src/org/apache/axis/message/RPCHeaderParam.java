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

package org.apache.axis.message;

import org.apache.axis.MessageContext;
import org.apache.axis.encoding.SerializationContext;

public class RPCHeaderParam extends SOAPHeaderElement
{
    /**
     * Create a SOAPHeaderElement that is represented by an RPCParam
     */
    public RPCHeaderParam(RPCParam rpcParam) {
        super(rpcParam.getQName().getNamespaceURI(),
              rpcParam.getQName().getLocalPart(),
              rpcParam);
    }

    /**
     * outputImpl serializes the RPCParam 
     */
    protected void outputImpl(SerializationContext context) throws Exception
    {
        MessageContext msgContext = context.getMessageContext();

        // Get the RPCParam and serialize it
        RPCParam rpcParam = (RPCParam) getObjectValue();
        if (encodingStyle != null && encodingStyle.equals("")) {
            context.registerPrefixForURI("", rpcParam.getQName().getNamespaceURI());
        }
        rpcParam.serialize(context);
    }
}
