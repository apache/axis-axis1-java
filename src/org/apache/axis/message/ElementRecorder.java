package org.apache.axis.message;

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
import org.apache.axis.message.events.*;
import org.apache.axis.encoding.*;
import java.util.*;
import java.io.*;

/** This guy records the basic SAX events into an event queue.  Then it can
 * play them back to any SAX ContentHandler.
 * 
 * !!! TODO: Record the rest of the events...
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class ElementRecorder extends DeserializerBase
{
    private static final boolean DEBUG_LOG = false;
    
    // The recorded list of SAX events "inside" this element
    protected Vector _events = new Vector();
    
    // If non-null, pass events to this guy after recording.
    private DeserializerBase nextHandler = null;
    
    public ElementRecorder()
    {
        if (DEBUG_LOG)
            System.out.println("New ElementRecorder " + this);
    }
    
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes)
        throws SAXException
    {
        if (DEBUG_LOG) {
            System.err.println("(rec) startElement ['" + namespace + "' " +
                           localName + "]");
        }
        
        _events.addElement(new StartElementEvent(namespace, localName, qName, attributes));
        
        if (nextHandler != null)
            nextHandler.startElement(namespace, localName, qName, attributes);
    }
    
    public void endElement(String namespace, String localName, String qName)
        throws SAXException
    {
        if (DEBUG_LOG) {
            System.err.println("(rec) endElement ['" + namespace + "' " +
                           localName + "]");
        }

        _events.addElement(new EndElementEvent(namespace, localName, qName));

        if (nextHandler != null)
            nextHandler.endElement(namespace, localName, qName);
    }
    
    public void characters(char [] chars, int start, int length)
        throws SAXException
    {
        if (DEBUG_LOG) {
            System.err.println("(rec) characters ['" +
                               new String(chars, start, length) + "']");
        }
        
        _events.addElement(new CharactersEvent(chars, start, length));

        if (nextHandler != null)
            nextHandler.characters(chars, start, length);
    }

    /** Someone wants to deal with the XML inside me using SAX.
      * So replay all the events to their handler.
      * 
      */
    public void publishToHandler(ContentHandler handler)
        throws SAXException
    {
        Enumeration e = _events.elements();
        while (e.hasMoreElements()) {
            SAXEvent event = (SAXEvent)e.nextElement();
            if (DEBUG_LOG) {
                System.err.println("Publishing : " + event);
            }
            event.publishToHandler(handler);
        }
    }

    public void publishChildrenToHandler(ContentHandler handler)
        throws SAXException
    {
        Enumeration e = _events.elements();
        SAXEvent event = null;
        
        // read the first element
        if (e.hasMoreElements()) e.nextElement();

        // read the second element
        if (e.hasMoreElements()) event = (SAXEvent)e.nextElement();

        while (e.hasMoreElements()) {
            if (DEBUG_LOG) {
                System.err.println("Publishing : " + event);
            }
            event.publishToHandler(handler);
            event = (SAXEvent)e.nextElement();
        }
    }

    /** 
      * Output the the full stream to a context.
      */
    public void output(SerializationContext context) 
        throws IOException
    {
        Enumeration e = _events.elements();
        while (e.hasMoreElements()) {
            ((SAXEvent)e.nextElement()).output(context);
        }
    }

    /** 
      * Output the children context.  This essentially means skipping
      * the first and last recorded events.
      */
    public void outputChildren(SerializationContext context) 
        throws IOException
    {
        Enumeration e = _events.elements();
        SAXEvent event = null;

        // read the first element
        if (e.hasMoreElements()) event = (SAXEvent)e.nextElement();

        // read the second element
        if (e.hasMoreElements()) event = (SAXEvent)e.nextElement();

        // output the elements until there is no successor
        while (e.hasMoreElements()) {
            event.output(context);
            event = (SAXEvent)e.nextElement();
        }
    }
}

