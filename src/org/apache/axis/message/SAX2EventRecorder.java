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

import org.apache.axis.encoding.DeserializationContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * This class records SAX2 Events and allows
 * the events to be replayed by start and stop index
 */
public class SAX2EventRecorder { 
    
    private static final Integer Z = new Integer(0);

    private static final Integer STATE_SET_DOCUMENT_LOCATOR = new Integer(0);
    private static final Integer STATE_START_DOCUMENT = new Integer(1);
    private static final Integer STATE_END_DOCUMENT = new Integer(2);
    private static final Integer STATE_START_PREFIX_MAPPING = new Integer(3);
    private static final Integer STATE_END_PREFIX_MAPPING = new Integer(4);
    private static final Integer STATE_START_ELEMENT = new Integer(5);
    private static final Integer STATE_END_ELEMENT = new Integer(6);
    private static final Integer STATE_CHARACTERS = new Integer(7);
    private static final Integer STATE_IGNORABLE_WHITESPACE = new Integer(8);
    private static final Integer STATE_PROCESSING_INSTRUCTION = new Integer(9);
    private static final Integer STATE_SKIPPED_ENTITY = new Integer(10);
    
    // This is a "custom" event which tells DeserializationContexts
    // that the current element is moving down the stack...
    private static final Integer STATE_NEWELEMENT = new Integer(11);

    // Lexical handler events...
    private static final Integer STATE_START_DTD = new Integer(12);
    private static final Integer STATE_END_DTD = new Integer(13);
    private static final Integer STATE_START_ENTITY = new Integer(14);
    private static final Integer STATE_END_ENTITY = new Integer(15);
    private static final Integer STATE_START_CDATA = new Integer(16);
    private static final Integer STATE_END_CDATA = new Integer(17);
    private static final Integer STATE_COMMENT = new Integer(18);
    
    org.xml.sax.Locator locator;
    objArrayVector events = new objArrayVector();
    
    public void clear() {
        locator = null;
        events = new objArrayVector();
    }
    public int getLength()
    {
        return events.getLength();
    }
    
    public int setDocumentLocator(org.xml.sax.Locator p1) {
        locator = p1;
        return events.add(STATE_SET_DOCUMENT_LOCATOR, Z,Z,Z,Z);
    }
    public int startDocument() {
        return events.add(STATE_START_DOCUMENT, Z,Z,Z,Z);
    }
    public int endDocument() {
        return events.add(STATE_END_DOCUMENT, Z,Z,Z,Z);
    }
    public int startPrefixMapping(String p1, String p2) {
        return events.add(STATE_START_PREFIX_MAPPING, p1, p2, Z,Z);
    }
    public int endPrefixMapping(String p1) {
        return events.add(STATE_END_PREFIX_MAPPING, p1,Z,Z,Z);
    }
    public int startElement(String p1, String p2, String p3, org.xml.sax.Attributes p4) {
        return events.add(STATE_START_ELEMENT, p1, p2, p3, p4);
    }
    public int endElement(String p1, String p2, String p3) {
        return events.add(STATE_END_ELEMENT, p1, p2, p3, Z);
    }
    public int characters(char[] p1, int p2, int p3) {
        return events.add(STATE_CHARACTERS, new String(p1, p2, p3), Z,Z,Z);
    }
    public int ignorableWhitespace(char[] p1, int p2, int p3) {
        return events.add(STATE_IGNORABLE_WHITESPACE, new String(p1, p2, p3), Z,Z,Z);
    }
    public int processingInstruction(String p1, String p2) {
        return events.add(STATE_PROCESSING_INSTRUCTION, p1, p2, Z,Z);
    }
    public int skippedEntity(String p1) {
        return events.add(STATE_SKIPPED_ENTITY, p1, Z,Z,Z);
    }
    
    public void startDTD(java.lang.String name,
                     java.lang.String publicId,
                     java.lang.String systemId) {
        events.add(STATE_START_DTD, name, publicId, systemId, Z);
    }
    public void endDTD() {
        events.add(STATE_END_DTD, Z, Z, Z, Z);
    }
    public void startEntity(java.lang.String name) {
        events.add(STATE_START_ENTITY, name, Z, Z, Z);
    }
    public void endEntity(java.lang.String name) {
        events.add(STATE_END_ENTITY, name, Z, Z, Z);
    }
    public void startCDATA() {
        events.add(STATE_START_CDATA, Z, Z, Z, Z);
    }
    public void endCDATA() {
        events.add(STATE_END_CDATA, Z, Z, Z, Z);
    }
    public void comment(char[] ch,
                    int start,
                    int length) {
        events.add(STATE_COMMENT, new String(ch, start, length), Z, Z, Z);
    }
    
    public int newElement(MessageElement elem) {
        return events.add(STATE_NEWELEMENT, elem, Z,Z,Z);
    }
    
    public void replay(ContentHandler handler) throws SAXException {
        if (events.getLength() > 0) {
            replay(0, events.getLength() - 1, handler);
        }
    }
    
    public void replay(int start, int stop, ContentHandler handler) throws SAXException {
        // Special case : play the whole thing for [0, -1]
        if ((start == 0) && (stop == -1)) {
            replay(handler);
            return;
        }
        
        if (stop + 1 > events.getLength() ||
            stop < start) {
            return; // should throw an error here
        }        
        
        LexicalHandler lexicalHandler = null;
        if (handler instanceof LexicalHandler) {
            lexicalHandler = (LexicalHandler) handler;
        }
        
        for (int n = start; n <= stop; n++) {
            Object event = events.get(n,0);
            if (event == STATE_START_ELEMENT) {
                handler.startElement((String)events.get(n,1), 
                                     (String)events.get(n,2),
                                     (String)events.get(n,3),
                                     (org.xml.sax.Attributes)events.get(n,4));
                
            } else if (event == STATE_END_ELEMENT) {
                handler.endElement((String)events.get(n,1), 
                                   (String)events.get(n,2),
                                   (String)events.get(n,3));
                
            } else if (event == STATE_CHARACTERS) {
                char chars[] = ((String)(events.get(n,1))).toCharArray();
                handler.characters(chars, 0, chars.length);
                
            } else if (event == STATE_IGNORABLE_WHITESPACE) {
                char chars[] = ((String)(events.get(n,1))).toCharArray();
                handler.ignorableWhitespace(chars, 0, chars.length);
                
            } else if (event == STATE_PROCESSING_INSTRUCTION) {
                handler.processingInstruction((String)events.get(n,1),
                                              (String)events.get(n,2));
                
            } else if (event == STATE_SKIPPED_ENTITY) {
                handler.skippedEntity((String)events.get(n,1));
                
            } else if (event == STATE_SET_DOCUMENT_LOCATOR) {
                handler.setDocumentLocator(locator);
                
            } else if (event == STATE_START_DOCUMENT) {
                handler.startDocument();
                
            } else if (event == STATE_END_DOCUMENT) {
                handler.endDocument();
                
            } else if (event == STATE_START_PREFIX_MAPPING) {
                handler.startPrefixMapping((String)events.get(n, 1),
                                           (String)events.get(n, 2));
                
            } else if (event == STATE_END_PREFIX_MAPPING) {
                handler.endPrefixMapping((String)events.get(n, 1));
                
            } else if (event == STATE_START_DTD && lexicalHandler != null) {
                lexicalHandler.startDTD((String)events.get(n,1), 
                                   (String)events.get(n,2),
                                   (String)events.get(n,3));
            } else if (event == STATE_END_DTD && lexicalHandler != null) {
                lexicalHandler.endDTD();
            
            } else if (event == STATE_START_ENTITY && lexicalHandler != null) {
                lexicalHandler.startEntity((String)events.get(n,1));
            
            } else if (event == STATE_END_ENTITY && lexicalHandler != null) {
                lexicalHandler.endEntity((String)events.get(n,1));
            
            } else if (event == STATE_START_CDATA && lexicalHandler != null) {
                lexicalHandler.startCDATA();
            
            } else if (event == STATE_END_CDATA && lexicalHandler != null) {
                lexicalHandler.endCDATA();
            
            } else if (event == STATE_COMMENT && lexicalHandler != null) {
                char chars[] = ((String)(events.get(n,1))).toCharArray();
                lexicalHandler.comment(chars, 0, chars.length);             
            
            } else if (event == STATE_NEWELEMENT) {
                if (handler instanceof DeserializationContext) {
                    DeserializationContext context =
                              (DeserializationContext)handler;
                    context.setCurElement(
                              (MessageElement)(events.get(n,1)));
                }
            }
        }
    }
    
/////////////////////////////////////////////
    class objArrayVector {
        private int RECORD_SIZE = 5;
        private int currentSize = 0;
        private Object[] objarray = new Object[50 * RECORD_SIZE];  // default to 50 5 field records
        
        public int add(Object p1, Object p2, Object p3, Object p4, Object p5) {
            if (currentSize == objarray.length) {
                Object[] newarray = new Object[currentSize * 2];
                System.arraycopy(objarray, 0, newarray, 0, currentSize);
                objarray = newarray;
            }
            int pos = currentSize / RECORD_SIZE;
            objarray[currentSize++] = p1;
            objarray[currentSize++] = p2;
            objarray[currentSize++] = p3;
            objarray[currentSize++] = p4;
            objarray[currentSize++] = p5;
            return pos;
        }

        public Object get(int pos, int fld) {
            return objarray[(pos * RECORD_SIZE) + fld];
        }
    
        public int getLength() {
            return (currentSize / RECORD_SIZE);
        }
    }
/////////////////////////////////////////////
}
