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
import org.apache.axis.strategies.InvocationStrategy;
import org.apache.axis.strategies.WSDLGenStrategy;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A Simple Chain is a 'composite' Handler in that it aggregates a collection
 * of Handlers and also acts as a Handler which delegates its operations to
 * the collection.
 * <p>
 * A Simple Chain initially has no Handlers. Handlers may be added until the
 * chain is invoke()d after which Handlers may not be added (and any attempt
 * to do so will throw an exception).
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glyn Normington (norm@uk.ibm.com)
 */
public class SimpleChain extends BasicHandler implements Chain {
    private static Log log =
        LogFactory.getLog(SimpleChain.class.getName());

    protected Vector handlers = new Vector();
    protected boolean invoked = false;
    
    private String CAUGHTFAULT_PROPERTY = 
            "org.apache.axis.SimpleChain.caughtFaultInResponse";

    public void init() {
        for ( int i = 0 ; i < handlers.size() ; i++ )
            ((Handler) handlers.elementAt( i )).init();
    }

    public void cleanup() {
        for ( int i = 0 ; i < handlers.size() ; i++ )
            ((Handler) handlers.elementAt( i )).cleanup();
    }

    private static final HandlerIterationStrategy iVisitor =
        new InvocationStrategy();

    private static final HandlerIterationStrategy wsdlVisitor =
        new WSDLGenStrategy();

    /**
     * Iterate over the chain invoking each handler.  If there's a fault
     * then call 'onFault' for each completed handler in reverse order, then
     * rethrow the exception.
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: SimpleChain::invoke");
        }

       invoked = true;
        doVisiting(msgContext, iVisitor);

        if (log.isDebugEnabled()) {
            log.debug("Exit: SimpleChain::invoke");
        }
   }

    /**
     * Iterate over the chain letting each handler have a crack at
     * contributing to a WSDL description.
     */
    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: SimpleChain::generateWSDL");
        }

        invoked = true;
        doVisiting(msgContext, wsdlVisitor);

        if (log.isDebugEnabled()) {
            log.debug("Exit: SimpleChain::generateWSDL");
        }
    }

    private void doVisiting(MessageContext msgContext,
                            HandlerIterationStrategy visitor) throws AxisFault {
        int i = 0 ;
        try {
            Enumeration enumeration = handlers.elements();
            while (enumeration.hasMoreElements()) {
                visitor.visit((Handler)enumeration.nextElement(), msgContext);
                i++;
            }
        } catch( AxisFault f ) {
            // Something went wrong.  If we haven't already put this fault
            // into the MessageContext's response message, do so and make sure
            // we only do it once.  This allows onFault() methods to safely
            // set headers and such in the response message without them
            // getting stomped.
            if (!msgContext.isPropertyTrue(CAUGHTFAULT_PROPERTY)) {
                // Attach the fault to the response message; enabling access to the
                // fault details while inside the handler onFault methods.
                Message respMsg = new Message(f);
                msgContext.setResponseMessage(respMsg);
                msgContext.setProperty(CAUGHTFAULT_PROPERTY, Boolean.TRUE);
            }
            while( --i >= 0 )
                ((Handler) handlers.elementAt( i )).onFault( msgContext );
            throw f;
        }
    }

    /**
     * Notify the handlers in this chain because some handler
     * later on has faulted - in reverse order. If any handlers
     * have been added since we visited the chain, they will get
     * notified too!
     */
    public void onFault(MessageContext msgContext) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: SimpleChain::onFault");
        }

        for ( int i = handlers.size()-1 ; i >= 0 ; i-- )
            ((Handler) handlers.elementAt( i )).onFault( msgContext );

        if (log.isDebugEnabled()) {
            log.debug("Exit: SimpleChain::onFault");
        }
    }

    public boolean canHandleBlock(QName qname) {
        for ( int i = 0 ; i < handlers.size() ; i++ )
            if ( ((Handler) handlers.elementAt( i )).canHandleBlock(qname) )
                return( true );
        return( false );
    }

    public void addHandler(Handler handler) {
        if (handler == null)
            throw new InternalException(
                Messages.getMessage("nullHandler00",
                                     "SimpleChain::addHandler"));

        if (invoked)
            throw new InternalException(
              Messages.getMessage("addAfterInvoke00",
                                   "SimpleChain::addHandler"));

        handlers.add( handler );
    }

    public boolean contains(Handler handler) {
        return( handlers.contains( handler ));
    }

    public Handler[] getHandlers() {
        if (handlers.size() == 0)
            return null;

        Handler [] ret = new Handler[handlers.size()];
        return( (Handler[]) handlers.toArray(ret) );
    }

    public Element getDeploymentData(Document doc) {
        if (log.isDebugEnabled()) {
            log.debug( Messages.getMessage("enter00",
                                            "SimpleChain::getDeploymentData") );
        }

        Element  root = doc.createElementNS("", "chain" );

        StringBuffer str = new StringBuffer();
        int i = 0;
        while (i < handlers.size()) {
            if ( i != 0 ) str.append(",");
            Handler h = (Handler) handlers.elementAt(i);
            str.append( h.getName() );
            i++;
        }
        if (i > 0) {
            root.setAttribute( "flow", str.toString() );
        }

        if ( options != null ) {
            Enumeration e = options.keys();
            while ( e.hasMoreElements() ) {
                String k = (String) e.nextElement();
                Object v = options.get(k);
                Element e1 = doc.createElementNS("", "option");
                e1.setAttribute( "name", k );
                e1.setAttribute( "value", v.toString() );
                root.appendChild( e1 );
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: SimpleChain::getDeploymentData");
        }

        return( root );
    }
}
