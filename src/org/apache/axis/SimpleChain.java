/*
 * The Apache Software License, Version 1.1
 *
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

package org.apache.axis ;

import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.strategies.InvocationStrategy;
import org.apache.axis.strategies.WSDLGenStrategy;
import org.apache.axis.utils.JavaUtils;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.rpc.namespace.QName;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SimpleChain extends BasicHandler implements Chain {
    static Category category =
            Category.getInstance(SimpleChain.class.getName());

    protected Vector     handlers ;
    protected Hashtable  options ;
    protected boolean    invoked;

    public void init() {
        for ( int i = 0 ; i < handlers.size() ; i++ )
            ((Handler) handlers.elementAt( i )).init();
    }

    public void cleanup() {
        for ( int i = 0 ; i < handlers.size() ; i++ )
            ((Handler) handlers.elementAt( i )).cleanup();
    }

    static InvocationStrategy iVisitor = new InvocationStrategy();
    static WSDLGenStrategy wsdlVisitor = new WSDLGenStrategy();

    /**
     * Iterate over the chain invoking each handler.  If there's a fault
     * then call 'undo' for each completed handler in reverse order, then 
     * rethrow the exception.
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        invoked = true;
        doVisiting(msgContext, iVisitor);
    }

    /**
     * Iterate over the chain letting each handler have a crack at
     * contributing to a WSDL description.
     */
    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        invoked = true;
        doVisiting(msgContext, wsdlVisitor);
    }

    private void doVisiting(MessageContext msgContext,
                            HandlerIterationStrategy visitor) throws AxisFault {

        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("enter00", 
                "SimpleChain::invoke"));
        }

        int i = 0 ;
        try {
            Vector localHandlers;
            // copies handlers to a local variable for thread-safe
            if ((localHandlers = handlers) != null) {
                Enumeration enum = localHandlers.elements();
                while (enum.hasMoreElements())
                    visitor.visit((Handler)enum.nextElement(), msgContext);
            }
        }
        catch( Exception e ) {
            // undo in reverse order - rethrow
            category.error( e );
            while( --i >= 0 )
                ((Handler) handlers.elementAt( i )).undo( msgContext );
            throw AxisFault.makeFault(e);
        }

        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("exit00", 
                "SimpleChain::invoke"));
        }
    }

    /**
     * Undo all of the work this chain completed because some handler
     * later on has faulted - in reverse order.
     */
    public void undo(MessageContext msgContext) {
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("enter00", 
                "SimpleChain::undo"));
        }

        for ( int i = handlers.size()-1 ; i >= 0 ; i-- )
            ((Handler) handlers.elementAt( i )).undo( msgContext );

        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("exit00", "SimpleChain::undo"));
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
            throw new NullPointerException(
                    JavaUtils.getMessage("nullHandler00", "SimpleChain::addHandler"));

        if (invoked)
            throw new NullPointerException(
                    JavaUtils.getMessage("addAfterInvoke00", "SimpleChain::addHandler"));
        
        if ( handlers == null ) handlers = new Vector();
        handlers.add( handler );
    }

    public boolean contains(Handler handler) {
        return( handlers != null ? handlers.contains( handler ) : false );
    }

    public Handler[] getHandlers() {
        if (handlers == null)
            return null;
        
        Handler [] ret = new Handler[handlers.size()];
        return( (Handler[]) handlers.toArray(ret) );
    }

    public Element getDeploymentData(Document doc) {
        if (category.isDebugEnabled()) {
            category.debug( JavaUtils.getMessage("enter00",
                "SimpleChain::getDeploymentData") );
        }

        Element  root = doc.createElementNS("", "chain" );

        if (handlers != null ) {
            StringBuffer str = new StringBuffer();
            Handler      h ;
            for ( int i = 0 ; i < handlers.size() ; i++ ) {
                if ( i != 0 ) str.append(",");
                h = (Handler) handlers.elementAt(i);
                str.append( h.getName() );
            }
            root.setAttribute( "flow", str.toString() );
        }

        options = this.getOptions();
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

        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("exit00", 
                "SimpleChain::getDeploymentData") );
        }

        return( root );
    }
};
