package org.apache.axis.encoding;

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

import java.util.*;
import org.apache.axis.utils.QName;

/** A very simple service description class, to demonstrate one way
 * to get type information out of band.
 * 
 * !!! This wants to be extended beyond just RPC... get away from
 * the focus on Parameters, and just type arbitrarily named
 * elements, hook up with schema systems, build an adapter for
 * parsing types using Castor, etc...  So we probably want to
 * migrate the type mapping stuff into here.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class ServiceDescription
{
    public static final String REQUEST = "Request";
    public static final String RESPONSE = "Response";
    
    String name;
    
    class Param {
        public String name;
        public QName type;
        Param(String name, QName type)
        {
            this.name = name;
            this.type = type;
        }
        Param() {}
    }
    
    /** This probably wants to be split into a WSDL-like
     * set of MessageDescription objects, each of which
     * may then have named parts... that would allow
     * supporting arbitrary message patterns, too.
     * 
     * this is just a tiny demo for req/resp RPC.
     */
    public Vector inputParams = new Vector();
    public Vector outputParams = new Vector();
    
    // Should we tack on "xsi:type" attributes?
    public boolean sendXsiType = true;
    
    public ServiceDescription(String name)
    {
        this.name = name;
    }
    
    public void addInputParam(String name, QName type)
    {
        inputParams.addElement(new Param(name, type));
    }
    
    public void addOutputParam(String name, QName type)
    {
        outputParams.addElement(new Param(name, type));
    }
    
    public void setSendTypeAttr(boolean sendType)
    {
        sendXsiType = sendType;
    }
    
    public boolean getSendTypeAttr()
    {
        return sendXsiType;
    }
    
    Param findByName(String name, Vector list)
    {
        Enumeration e = list.elements();
        while (e.hasMoreElements()) {
            Param p = (Param)e.nextElement();
            if (p.name.equals(name))
                return p;
        }
        
        return null;
    }

    public QName getInputParamTypeByName(String paramName)
    {
        Param param = findByName(paramName, inputParams);
        if (param != null)
            return param.type;
        return null;
    }
    
    public QName getInputParamTypeByPos(int position)
    {
        Param param = (Param)inputParams.elementAt(position);
        if (param != null)
            return param.type;
        return null;
    }
    
    /** This one is what the outside world wants to use, I think.
     */
    public QName getParamTypeByName(String messageType, String paramName)
    {
        System.out.println("[" + messageType + "] - " + paramName);
        
        if (messageType.equals(REQUEST))
            return getInputParamTypeByName(paramName);
        if (messageType.equals(RESPONSE))
            return getOutputParamTypeByName(paramName);
        
        // Only understand these two at present...
        
        return null;
    }
    
    public QName getOutputParamTypeByName(String paramName)
    {
        Param param = findByName(paramName, outputParams);
        if (param != null)
            return param.type;
        return null;
    }
    
    public QName getOutputParamTypeByPos(int position)
    {
        Param param = (Param)outputParams.elementAt(position);
        if (param != null)
            return param.type;
        return null;
    }
}
