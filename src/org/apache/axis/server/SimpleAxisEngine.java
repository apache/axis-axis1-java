/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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

package org.apache.axis.server ;

import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.handlers.* ;
import org.apache.axis.registries.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class SimpleAxisEngine extends BasicHandler
{
        public SimpleAxisEngine() {}

        /**
         * Allows the Listener to specify which handler/service registry
         * implementation they want to use.
         */
        public SimpleAxisEngine(HandlerRegistry handlers, HandlerRegistry services)
        {
                Debug.Print( 1, "Enter: SimpleAxisEngine::Constructor");
                handlers.init();
                services.init();
                addOption(Constants.HANDLER_REGISTRY, handlers);
                addOption(Constants.SERVICE_REGISTRY, services);
                Debug.Print( 1, "Exit: SimpleAxisEngine::Constructor");
        }

        /**
     * Find/load the registries and save them so we don't need to do this
     * each time we're called.
     */
    public void init() {
        // Load the simple handler registry and init it
        Debug.Print( 1, "Enter: SimpleAxisEngine::init" );
        //HandlerRegistry  hr = new SimpleRegistry("handlers.reg");
        HandlerRegistry  hr = new SupplierRegistry("handlers-supp.reg");
        hr.init();
        addOption( Constants.HANDLER_REGISTRY, hr );

        // Load the simple deployed services registry and init it
        //HandlerRegistry  sr = new SimpleRegistry("services.reg");
        HandlerRegistry  sr = new SupplierRegistry("services-supp.reg");
        sr.init();
        addOption( Constants.SERVICE_REGISTRY, sr );
        Debug.Print( 1, "Exit: SimpleAxisEngine::init" );
    }

    /**
     * Main routine of the AXIS server.  In short we locate the appropriate
     * handler for the desired service and invoke() it.
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        Debug.Print( 1, "Enter: SimpleAxisEngine::invoke" );
        HandlerRegistry hr = (HandlerRegistry)getOption(Constants.HANDLER_REGISTRY);
        HandlerRegistry sr = (HandlerRegistry)getOption(Constants.SERVICE_REGISTRY);

        /** Make sure later Handlers can get this directly.
        */
        msgContext.setProperty(Constants.SERVICE_REGISTRY, sr);

        /** We must have a TARGET_SERVICE to continue.  This tells us which Handler to
        * pull from the registry and call.  The Transport Listener is responsible
        * for making sure this gets set, and if it isn't, we FAIL.
        */
        String target = (String) msgContext.getProperty( MessageContext.TARGET_SERVICE );
        if ( target == null )
            throw new AxisFault("Server.NoTargetConfigured",
                "AxisEngine: Couldn't find a target property in the MessageContext!",
                null, null );

        Handler h = sr.find( target );

        if ( h == null ) {
            Debug.Print( 1, "No service found by name: " + target );
            throw new AxisFault( "Server.NoSuchService",
                "Service '" + target + "' was not found",
                null, null );
        }

        // Clear this for the next round of dispatch, if any.
        msgContext.clearProperty( MessageContext.TARGET_SERVICE );

        h.init();   // ???
        try {
            h.invoke( msgContext );
        }
        catch( Exception e ) {
            // Should we even bother catching it ?
            if ( !(e instanceof AxisFault) ) e = new AxisFault( e );
            throw (AxisFault) e ;
        }
        h.cleanup();   // ???
        Debug.Print( 1, "Exit: SimpleAxisEngine::invoke" );
    };

    public void undo(MessageContext msgContext) {
        Debug.Print( 1, "Enter: SimpleAxisEngine::undo" );
        Debug.Print( 1, "Exit: SimpleAxisEngine::undo" );
    };

};
