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

package org.apache.axis ;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A <code>FaultableHandler</code> is essentially a wrapper for any other
 * Handler which provides flexible fault handling semantics.
 *
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class FaultableHandler extends BasicHandler {
    protected static Log log =
        LogFactory.getLog(FaultableHandler.class.getName());

    // The enterprise category is for stuff that an enterprise product might
    // want to track, but in a simple environment (like the AXIS build) would
    // be nothing more than a nuisance.
    protected static Log entLog =
        LogFactory.getLog(Constants.ENTERPRISE_LOG_CATEGORY);

    protected Handler    workHandler ;

    /** Constructor
     *
     * @param workHandler the Handler we're going to wrap with Fault semantics.
     */
    public FaultableHandler(Handler workHandler)
    {
        this.workHandler = workHandler;
    }

    public void init() {
        workHandler.init();
    }

    public void cleanup() {
        workHandler.cleanup();
    }

    /**
     * Invokes the specified handler.  If there's a fault the appropriate
     * key will be calculated and used to find the fault chain to be
     * invoked.  This assumes that the workHandler has caught the exception
     * and already done its fault processing - as needed.
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug("Enter: FaultableHandler::invoke");
        try {
            workHandler.invoke( msgContext );
        }
        catch( Exception e ) {
            entLog.info(Messages.getMessage("toAxisFault00"), e );
            AxisFault fault = AxisFault.makeFault(e);

//            AxisEngine engine = msgContext.getAxisEngine();

            /** Index off fault code.
             *
             * !!! TODO: This needs to be able to handle searching by faultcode
             * hierarchy, i.e.  "Server.General.*" or "Server.*", with the
             * most specific match winning.
             */
            /*
            QFault   key          = fault.getFaultCode() ;
            Handler  faultHandler = (Handler) faultHandlers.get( key );
            */
            Handler faultHandler = null;

            Hashtable options = getOptions();
            if (options != null) {
                Enumeration enum = options.keys();
                while (enum.hasMoreElements()) {
                    String s = (String) enum.nextElement();
                    if (s.equals("fault-" + fault.getFaultCode().getLocalPart())) {
                        faultHandler = (Handler)options.get(s);
                    }
                }
            }

            if ( faultHandler != null ) {
                /** faultHandler will (re)throw if it's appropriate, but it
                 * might also eat the fault.  Which brings up another issue -
                 * should we have a way to pass the Fault directly to the
                 * faultHandler? Maybe another well-known MessageContext
                 * property?
                 */
                faultHandler.invoke( msgContext );
            } else {
                throw fault;
            }
        }
        log.debug("Exit: FaultableHandler::invoke");
    }

    /**
     * Some handler later on has faulted so we need to process the fault.
     */
    public void onFault(MessageContext msgContext) {
        log.debug("Enter: FaultableHandler::onFault");
        workHandler.onFault( msgContext );
        log.debug("Exit: FaultableHandler::onFault");
    }

    public boolean canHandleBlock(QName qname) {
        return( workHandler.canHandleBlock(qname) );
    }
};
