/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.rpc.namespace.QName;
import java.io.Serializable;
import java.util.Hashtable;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public interface Handler extends Serializable {
    /**
     * Init is called when the chain containing this Handler object
     * is instantiated.
     */
    public void init();

    /**
     * Cleanup is called when the chain containing this Handler object
     * is done processing the chain.
     */
    public void cleanup();

    /**
     * Invoke is called to do the actual work of the Handler object.
     * If there is a fault during the processing of this method it is
     * invoke's job to catch the exception and undo any partial work
     * that has been completed.  Once we leave 'invoke' if a fault
     * is thrown, this classes 'undo' method will be called to undo
     * the work that 'invoke' did.
     * Invoke should rethrow any exceptions it catches.
     */
    public void invoke(MessageContext msgContext) throws AxisFault ;

    /**
     * Called when a fault occurs to 'undo' whatever 'invoke' did.
     */
    public void undo(MessageContext msgContext);

    /**
     * Can this Handler process this QName?
     */
    public boolean canHandleBlock(QName qname);

    /**
     * Add the given option (name/value) to this handler's bag of options
     */
    public void addOption(String name, Object value);

    /**
     * Returns the option corresponding to the 'name' given
     */
    public Object getOption(String name);
    
    /**
     * Set the name (i.e. registry key) of this Handler
     */
    public void setName(String name);
    
    /**
     * Return the name (i.e. registry key) for this Handler
     */
    public String getName();

    /**
     * Return the entire list of options
     */
    public Hashtable getOptions();

    /**
     * Sets a whole list of options
     */
    public void setOptions(Hashtable opts);

    /**
     * This will return the root element of an XML doc that describes the
     * deployment information about this handler.  This is NOT the WSDL,
     * this is all of the static internal data use by Axis - WSDL takes into
     * account run-time information (like which service we're talking about)
     * this is just the data that's stored in the registry.  Used by the
     * 'list' Admin function.
     */
    public Element getDeploymentData(Document doc);

    /**
     * Obtain WSDL information.  Some Handlers will implement this by
     * merely setting properties in the MessageContext, others (providers)
     * will take responsibility for doing the "real work" of generating
     * WSDL for a given service.
     *
     */
    public void generateWSDL(MessageContext msgContext) throws AxisFault;
};
