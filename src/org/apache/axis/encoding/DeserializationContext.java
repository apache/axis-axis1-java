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

import org.xml.sax.*;
import java.util.*;
import org.apache.axis.Constants;
import org.apache.axis.encoding.*;
import org.apache.axis.message.*;
import org.apache.axis.utils.QName;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.ServiceDescription;

/** Keeps track of the active typeMappings, element IDs, parser, and
 * SOAPSAXHandler in an ongoing parse.  Might want to move the
 * storage for the namespace/prefix mappings into here too...
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

public class DeserializationContext
{
    public SOAPSAXHandler baseHandler;
    public Hashtable idMappings = new Hashtable();
    
    private MessageContext msgContext;

    public DeserializationContext(SOAPSAXHandler baseHandler, 
                                  MessageContext msgContext)
    {
        this.baseHandler = baseHandler;
        this.msgContext  = msgContext;
    }
    
    public SOAPSAXHandler getSAXHandler()
    {
        return baseHandler;
    }
    
    public void pushElementHandler(DeserializerBase handler)
    {
        baseHandler.pushElementHandler(handler);    
    }
    
    public String getNamespaceURI(String prefix)
    {
        return (String)baseHandler.getNamespaceURI(prefix);
    }
    
    public QName getQNameFromString(String qNameStr)
    {
        // OK, this is a QName, so look up the prefix in our current mappings.
        
        int i = qNameStr.indexOf(":");
        if (i == -1)
            return null;
        
        String nsURI = getNamespaceURI(qNameStr.substring(0, i));
        
        //System.out.println("namespace = " + nsURI);
        
        if (nsURI == null)
            return null;  // ???
        
        return new QName(nsURI, qNameStr.substring(i + 1));
    }
    
    public QName getTypeFromAttributes(Attributes attrs)
    {
        if (attrs == null)
            return null;
        
        // Check for type
        String type = null;
        for (int i=0; i<Constants.URIS_SCHEMA_XSI.length && type==null; i++)
            type = attrs.getValue(Constants.URIS_SCHEMA_XSI[i], "type");
        
        if (type == null)
          return null;
        
        return getQNameFromString(type);
    }
    
    public ServiceDescription getServiceDescription()
    {
        return msgContext.getServiceDescription();
    }
    
    public TypeMappingRegistry getTypeMappingRegistry()
    {
        return msgContext.getTypeMappingRegistry();
    }
    
    public MessageContext getMessageContext()
    {
        return msgContext;
    }
    
    public void registerID(String id, MessageElement element)
    {
        // Throw an exception if already registered?
        idMappings.put(id, element);
    }
    
    public MessageElement getElementByID(String id)
    {
        return (MessageElement)idMappings.get(id);
    }
    
    public DeserializerBase getDeserializer(QName qName)
    {
        DeserializerBase dSer = getTypeMappingRegistry().getDeserializer(qName);
        if (dSer != null)
            dSer.setDeserializationContext(this);
        return dSer;
    }
}
