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
import org.xml.sax.helpers.*;

/** A convenience base class for deserializers, which handles throwing
 * exceptions when unexpected events occur.
 * 
 * !!! Can probably simplify this by just having all methods throw in
 * here, then people can just overload what they allow.
 * 
 * The "value" object needs to be kept somewhere.  We can either do it
 * here, which necessitates a deserializer instance for each deserialization,
 * or could somehow store the state in the MessageElement which is
 * being blessed with a value.  Might bear some investigating.
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

public class DeserializerBase extends DefaultHandler
{
    public static long STARTDOCUMENT  = 0x0002;
    public static long ENDDOCUMENT    = 0x0004;
    public static long STARTPREFIXMAP = 0x0008;
    public static long ENDPREFIXMAP   = 0x0010;
    public static long STARTELEMENT   = 0x0020;
    public static long ENDELEMENT     = 0x0040;
    public static long CHARACTERS     = 0x0080;
    public static long WHITESPACE     = 0x0100;
    public static long SKIPPEDENTITY  = 0x0200;
    public static long PROCESSINGINST = 0x0400;
    
    private long allowedEvents = 0;
    protected Object value = null;
    
    public Object getValue()
    {
        return value;
    }
    
    protected void setAllowedEvents(long mask)
    {
        allowedEvents = mask;
    }
    
    public void startDocument() throws SAXException {
        if ((allowedEvents & STARTDOCUMENT) == 0)
            throw new SAXException(
                "StartDocument event not allowed in this context.");
    }
    
    public void endDocument() throws SAXException {
        if ((allowedEvents & ENDDOCUMENT) == 0)
            throw new SAXException(
                "EndDocument event not allowed in this context.");
    }
    
    public void startPrefixMapping(String p1, String p2) throws SAXException {
        if ((allowedEvents & STARTPREFIXMAP) == 0)
            throw new SAXException(
                "StartPrefixMapping event not allowed in this context.");
    }
    
    public void endPrefixMapping(String p1) throws SAXException {
        if ((allowedEvents & ENDPREFIXMAP) == 0)
            throw new SAXException(
                "EndPrefixMapping event not allowed in this context.");
    }
    
    public void characters(char[] p1, int p2, int p3) throws SAXException {
        if ((allowedEvents & CHARACTERS) == 0)
            throw new SAXException(
                "Characters event not allowed in this context.");
    }
    
    public void ignorableWhitespace(char[] p1, int p2, int p3) 
        throws SAXException
    {
        if ((allowedEvents & WHITESPACE) == 0)
            throw new SAXException(
                "IgnorableWhitespace event not allowed in this context.");
    }
 
    public void skippedEntity(String p1) throws SAXException {
        if ((allowedEvents & SKIPPEDENTITY) == 0)
            throw new SAXException(
                "SkippedEntity event not allowed in this context.");
    }
    
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes)
        throws SAXException
    {
        if ((allowedEvents & STARTELEMENT) == 0)
            throw new SAXException(
                "StartElement event not allowed in this context.");
    }
}
