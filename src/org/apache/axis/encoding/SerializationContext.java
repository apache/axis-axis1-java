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

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.axis.Constants;
import org.apache.axis.message.*;
import org.apache.axis.utils.*;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.ServiceDescription;

/** Manage a serialization, including keeping track of namespace mappings
 * and element stacks.
 * 
 * WARNING : HIGHLY PRELIMINARY!!!
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class SerializationContext
{
    private static final boolean DEBUG_LOG = false;
    
    public NSStack nsStack = new NSStack();
                                        
    public Hashtable pendingNSMappings = new Hashtable();
    boolean writingStartTag = false;
    
    Stack elementStack = new Stack();
    Writer writer;
    
    int lastPrefixIndex = 1;
    
    private MessageContext msgContext;
    
    public SerializationContext(Writer writer, MessageContext msgContext)
    {
        this.writer = writer;
        this.msgContext = msgContext;
        if (msgContext==null) throw new NullPointerException();
    }
    
    public ServiceDescription getServiceDescription()
    {
        return msgContext.getServiceDescription();
    }
    
    public TypeMappingRegistry getTypeMappingRegistry()
    {
        return msgContext.getTypeMappingRegistry();
    }
    
    public String getPrefixForURI(String uri)
    {
        String prefix = nsStack.getPrefix(uri);
        
        if (prefix == null)
            prefix = (String)pendingNSMappings.get(uri);
        
        if (prefix == null && uri.equals(Constants.URI_SOAP_ENC))
            prefix = Constants.NSPREFIX_SOAP_ENC;
        
        if (prefix == null) {
            prefix = "ns" + lastPrefixIndex++;
            registerPrefixForURI(prefix, uri);
        }
        
        return prefix;
    }
    
    public void registerPrefixForURI(String prefix, String uri)
    {
        if (DEBUG_LOG) {
            System.out.println("register '" + prefix + "' - '" + uri + "'");
        }
        
        if ((uri != null) && (prefix != null) && !prefix.equals("") &&
            !uri.equals("")) {
            /*if ((pendingNSMappings.get(uri) != null) ||
                (nsStack.getPrefix(uri) != null))
                return;*/
            
            pendingNSMappings.put(uri, prefix);
        }
    }
    
    public String qName2String(QName qName)
    {
        return getPrefixForURI(qName.getNamespaceURI()) +
               ":" +
               qName.getLocalPart();
    }
    
    public QName getQNameForClass(Class cls)
    {
        return getTypeMappingRegistry().getTypeQName(cls);
    }
    
    public void serialize(QName qName, Attributes attributes, Object value)
        throws IOException
    {
        getTypeMappingRegistry().serialize(qName, attributes, value, this);
    }
    
    public void startElement(QName qName, Attributes attributes)
        throws IOException
    {
        if (DEBUG_LOG) {
            System.out.println("Out: Starting element [" + qName.getNamespaceURI() + "]:" + qName.getLocalPart());
        }
        
        if (writingStartTag) {
            writer.write(">");
        }
        
        String nsURI = qName.getNamespaceURI();
        
        //nsStack.dump();
        
        String elementQName;
        StringBuffer buf = new StringBuffer();
        buf.append("<");
        
        if ((nsURI != null) && (!nsURI.equals(""))) {
            elementQName = getPrefixForURI(nsURI) + ":";
        } else {
            elementQName = "";
        }
        elementQName += qName.getLocalPart();
        buf.append(elementQName);
        
        if (!pendingNSMappings.isEmpty()) {
            nsStack.push((Hashtable)pendingNSMappings.clone());
        } else {
            nsStack.push();
        }
        
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                buf.append(" " + attributes.getQName(i) + "=\"" +
                           attributes.getValue(i) +"\"");
            }
        }
        
        Enumeration e = pendingNSMappings.keys();
        while (e.hasMoreElements()) {
            String uri = (String)e.nextElement();
            String pref = (String)pendingNSMappings.get(uri);
            buf.append(" xmlns:" + pref + "=\"" + uri + "\"");
        }

        writingStartTag = true;
        
        elementStack.push(elementQName);
        
        pendingNSMappings.clear();
        
        writer.write(buf.toString());
        writer.flush();
    }
    
    public void endElement()
        throws IOException
    {
        String elementQName = (String)elementStack.pop();
        
        if (DEBUG_LOG) {
            System.out.println("Out: Ending element " + elementQName);
        }
        
        nsStack.pop();

        if (writingStartTag) {
            writer.write("/>");
            writingStartTag = false;
            return;
        }
        
        StringBuffer buf = new StringBuffer();
        buf.append("</" + elementQName + ">");
        writer.write(buf.toString());
        writer.flush();
    }
    
    public void writeChars(char [] p1, int p2, int p3)
        throws IOException
    {
        if (writingStartTag) {
            writer.write(">");
            writingStartTag = false;
        }
        writer.write(p1, p2, p3);
        writer.flush();
    }

    public void writeString(String string)
        throws IOException
    {
        if (writingStartTag) {
            writer.write(">");
            writingStartTag = false;
        }
        writer.write(string);
        writer.flush();
    }
}
