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

package org.apache.axis.handlers.soap;

import org.apache.axis.*;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.Debug;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.registries.* ;

/** A <code>SOAPService</code> is a Handler which encapsulates a SOAP
 * invocation.  It has an input chain, an output chain, and a pivot-point,
 * and handles the SOAP semantics when invoke()d.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com) 
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SOAPService extends SimpleTargetedChain
{
    public static final String OPTION_PIVOT = "pivot";
    
    /** Service-specific type mappings
     * 
     * !!! This is just a test for now, these do not get serialized (and thus
     * restored) with the registered services.  This should come out of our
     * XML storage eventually.
     * 
     */
    private transient TypeMappingRegistry typeMap = new TypeMappingRegistry();
    
    /** Standard, no-arg constructor.
     */
    public SOAPService()
    {
    }
    
    public TypeMappingRegistry getTypeMappingRegistry()
    {
        return typeMap;
    }
    
    public void setTypeMappingRegistry(TypeMappingRegistry map)
    {
        typeMap = map;
    }
    
    /** Convenience constructor for wrapping SOAP semantics around
     * "service handlers" which actually do work.
     */
    public SOAPService(Handler serviceHandler, String pivotName)
    {
        super();
        setPivotHandler(serviceHandler);
        addOption(OPTION_PIVOT, pivotName);
    }
    
    public void invoke(MessageContext msgContext) throws AxisFault
    {
        Debug.Print( 1, "Enter: SOAPService::invoke" );
        
        //msgContext.setServiceHandler( this );
        
        Handler h = getInputChain() ;
        if ( h != null ) {
            Debug.Print( 2, "Invoking input chain" );
            h.invoke(msgContext);
        } else {
            Debug.Print( 3, "No input chain" );
        }

        // Do SOAP semantics here
        Debug.Print( 2, "Doing SOAP semantic checks...");

        h = getPivotHandler();
        if ( h != null ) {
            Debug.Print( 2, "Invoking service/pivot" );
            h.invoke(msgContext);
        } else {
            Debug.Print( 3, "No service/pivot" );
        }
        
        h = getOutputChain();
        if ( h != null ) {
            Debug.Print( 2, "Invoking output chain" );
            h.invoke(msgContext);
        } else {
            Debug.Print( 3, "No output chain" );
        }

        Debug.Print( 1, "Exit : SOAPService::invoke" );
    }

    public void undo(MessageContext msgContext) 
    {
        Debug.Print( 1, "Enter: SOAPService::undo" );
        Debug.Print( 1, "Exit: SOAPService::undo" );
    }
}
