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
package org.apache.axis.message;

/** A <code>SOAPHandler</code>
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.apache.axis.encoding.DeserializationContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SOAPHandler extends DefaultHandler
{
    public MessageElement myElement = null;
    private MessageElement[] myElements;
    private int myIndex = 0;

    public SOAPHandler() {
    }

   /**
    * This constructor allows deferred setting of any elements
    * @param elements array of message elements to be populated
    * @param index position in array where the message element is to be created
    */
    public SOAPHandler(MessageElement[] elements, int index) {
        myElements = elements;
        myIndex = index;
    }
    
    public void startElement(String namespace, String localName,
                             String prefix, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        // By default, make a new element
        if (!context.isDoneParsing() && !context.isProcessingRef()) {
            if (myElement == null) {
                myElement = makeNewElement(namespace, localName, prefix,
                                           attributes, context);
            }
            context.pushNewElement(myElement);
        }
    }

    public MessageElement makeNewElement(String namespace, String localName,
                             String prefix, Attributes attributes,
                             DeserializationContext context)
    {
        return new MessageElement(namespace, localName,
                                               prefix, attributes, context);
    }

    public void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        if (myElement != null) {
            if (myElements != null) {
                myElements[myIndex] = myElement;
            }
            myElement.setEndIndex(context.getCurrentRecordPos());
        }
    }
    
    public SOAPHandler onStartChild(String namespace, 
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        SOAPHandler handler = new SOAPHandler();
        return handler;
    }
    
    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
    }
}
