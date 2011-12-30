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

package org.apache.axis ;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.commons.logging.Log;

/**
 * A SimpleTargetedChain has a request handler, a pivot handler, and a response
 * handler (any of which may themselves be chains).
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glyn Normington (norm@uk.ibm.com)
 */
public class SimpleTargetedChain extends SimpleChain implements TargetedChain
{
    protected static Log log =
            LogFactory.getLog(SimpleTargetedChain.class.getName());

    protected Handler    requestHandler ;
    protected Handler    pivotHandler ;
    protected Handler    responseHandler ;

    /**
     * Pivot indicator sets "past pivot point" before the response handler
     * runs. This avoids having to reimplement SimpleChain.invoke and
     * SimpleChain.generateWSDL.
     */
    private class PivotIndicator extends BasicHandler {
        public PivotIndicator() {}

        public void invoke(MessageContext msgContext) throws AxisFault {
            msgContext.setPastPivot(true);
        }
    }

    /**
     * Default no-arg constructor.
     */
    public SimpleTargetedChain() {}

    /**
     * Constructor for an instance with effectively only a pivot handler.
     *
     * @param  handler the <code>Handler</code> to use
     */
    public SimpleTargetedChain(Handler handler) {
        pivotHandler = handler;
        if (pivotHandler != null) {
            addHandler(pivotHandler);
            addHandler(new PivotIndicator());
        }
    }

    /**
     * Constructor which takes real or null request, pivot, and response
     * handlers.
     */
    public SimpleTargetedChain(Handler reqHandler, Handler pivHandler,
                               Handler respHandler) {
        init(reqHandler, null, pivHandler, null, respHandler);
    }

    /**
     * Initialiser which takes real or null request, pivot, and response
     * handlers and which allows for special request and response
     * handlers to be inserted just before and after any pivot handler.
     *
     * @param reqHandler  the request <code>Handler</code>
     * @param specialReqHandler the special request <code>Handler</code>
     * @param pivHandler  the pivot <code>Handler</code>
     * @param specialRespHandler the special response <code>Handler</code>
     * @param respHandler the response <code>Handler</code>
     */
    protected void init(Handler reqHandler, Handler specialReqHandler,
                        Handler pivHandler, Handler specialRespHandler,
                        Handler respHandler) {

        requestHandler = reqHandler;
        if (requestHandler != null)
            addHandler(requestHandler);

        if (specialReqHandler != null)
            addHandler(specialReqHandler);

        pivotHandler = pivHandler;
        if (pivotHandler != null) {
            addHandler(pivotHandler);
            addHandler(new PivotIndicator());
        }

        if (specialRespHandler != null)
            addHandler(specialRespHandler);

        responseHandler = respHandler;
        if (responseHandler != null)
            addHandler(responseHandler);
    }

    public Handler getRequestHandler() { return( requestHandler ); }

    public Handler getPivotHandler() { return( pivotHandler ); }

    public Handler getResponseHandler() { return( responseHandler ); }
};
