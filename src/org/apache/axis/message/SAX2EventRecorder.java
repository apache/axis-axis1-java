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

import java.util.ArrayList;

/**
 * This class records SAX2 Events and allows
 * the events to be replayed by start and stop index
 */
public class SAX2EventRecorder { 
    
    private static final byte STATE_SET_DOCUMENT_LOCATOR = 0;
    private static final byte STATE_START_DOCUMENT = 1;
    private static final byte STATE_END_DOCUMENT = 2;
    private static final byte STATE_START_PREFIX_MAPPING = 3;
    private static final byte STATE_END_PREFIX_MAPPING = 4;
    private static final byte STATE_START_ELEMENT = 5;
    private static final byte STATE_END_ELEMENT = 6;
    private static final byte STATE_CHARACTERS = 7;
    private static final byte STATE_IGNORABLE_WHITESPACE = 8;
    private static final byte STATE_PROCESSING_INSTRUCTION = 9;
    private static final byte STATE_SKIPPED_ENTITY = 10;
    
    // This is a "custom" event which tells DeserializationContexts
    // that the current element is moving down the stack...
    private static final byte STATE_NEWELEMENT = 11;

    org.xml.sax.Locator locator;
    intArrayVector events = new intArrayVector();
    Object[] attrs = new Object[20];
    int numattrs = 0;
    SymbolTable st = new SymbolTable();
    
    public void clear() {
        locator = null;
        events = new intArrayVector();
        attrs = new Object[20];
        numattrs = 0;
        st = new SymbolTable();
    }
    public int getLength()
    {
        return events.getLength();
    }
    
    public int setDocumentLocator(org.xml.sax.Locator p1) {
        locator = p1;
        return events.add(STATE_SET_DOCUMENT_LOCATOR, 0,0,0,0);
    }
    public int startDocument() {
        return events.add(STATE_START_DOCUMENT, 0,0,0,0);
    }
    public int endDocument() {
        return events.add(STATE_END_DOCUMENT, 0,0,0,0);
    }
    public int startPrefixMapping(String p1, String p2) {
        return events.add(STATE_START_PREFIX_MAPPING, st.addSymbol(p1), st.addSymbol(p2), 0,0);
    }
    public int endPrefixMapping(String p1) {
        return events.add(STATE_END_PREFIX_MAPPING, st.addSymbol(p1),0,0,0);
    }
    public int startElement(String p1, String p2, String p3, org.xml.sax.Attributes p4) {
        if (numattrs == attrs.length) {
            Object[] nattrs = new Object[numattrs * 2];
            System.arraycopy(attrs, 0, nattrs, 0, numattrs);
            attrs = nattrs;
        }
        
        attrs[numattrs++] = p4;
        return events.add(STATE_START_ELEMENT, st.addSymbol(p1), st.addSymbol(p2), st.addSymbol(p3), numattrs-1);
    }
    public int endElement(String p1, String p2, String p3) {
        return events.add(STATE_END_ELEMENT, st.addSymbol(p1), st.addSymbol(p2), st.addSymbol(p3),0);
    }
    public int characters(char[] p1, int p2, int p3) {
        /*
        return events.add(STATE_CHARACTERS, st.addSymbol(p1, p2, p3), p2, p3, 0);
        */
        return events.add(STATE_CHARACTERS, st.addSymbol(p1, p2, p3), 0, p3, 0);        
    }
    public int ignorableWhitespace(char[] p1, int p2, int p3) {
        return events.add(STATE_IGNORABLE_WHITESPACE, st.addSymbol(p1, p2, p3), p2, p3, 0);
    }
    public int processingInstruction(String p1, String p2) {
        return events.add(STATE_PROCESSING_INSTRUCTION, st.addSymbol(p1), st.addSymbol(p2), 0,0);
    }
    public int skippedEntity(String p1) {
        return events.add(STATE_SKIPPED_ENTITY, st.addSymbol(p1), 0,0,0);
    }
    
    public int newElement(MessageElement elem)
    {
        return events.add(STATE_NEWELEMENT, addElement(elem), 0, 0, 0);
    }
    
    ArrayList elements = new ArrayList();
    private int addElement(MessageElement elem)
    {
        elements.add(elem);
        return elements.size() - 1;
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
        for (int n = start; n <= stop; n++) {
            switch(events.get(n,0)) {
            case STATE_SET_DOCUMENT_LOCATOR:
                handler.setDocumentLocator(locator);
                break;
            case STATE_START_DOCUMENT:
                handler.startDocument();
                break;
            case STATE_END_DOCUMENT:
                handler.endDocument();
                break;
            case STATE_START_PREFIX_MAPPING:
                handler.startPrefixMapping(st.getSymbol(events.get(n, 1)),
                                           st.getSymbol(events.get(n, 2)));
                break;
            case STATE_END_PREFIX_MAPPING:
                handler.endPrefixMapping(st.getSymbol(events.get(n, 1)));
                break;
            case STATE_START_ELEMENT:
//                int attrIdx = events.get(n,4);
                
                handler.startElement(st.getSymbol(events.get(n,1)), 
                                     st.getSymbol(events.get(n,2)),
                                     st.getSymbol(events.get(n,3)),
                                     (org.xml.sax.Attributes)attrs[events.get(n,4)]);
                break;
            case STATE_END_ELEMENT:
                handler.endElement(st.getSymbol(events.get(n,1)), 
                                   st.getSymbol(events.get(n,2)),
                                   st.getSymbol(events.get(n,3)));
                break;
            case STATE_CHARACTERS:
                handler.characters(st.getSymbol(events.get(n,1)).toCharArray(), 
                                   events.get(n,2),
                                   events.get(n,3));
                break;
            case STATE_IGNORABLE_WHITESPACE:
                handler.characters(st.getSymbol(events.get(n,1)).toCharArray(), 
                                   events.get(n,2),
                                   events.get(n,3));
                break;
            case STATE_PROCESSING_INSTRUCTION:
                handler.processingInstruction(st.getSymbol(events.get(n,1)),
                                              st.getSymbol(events.get(n,2)));
                break;
            case STATE_SKIPPED_ENTITY:
                handler.skippedEntity(st.getSymbol(events.get(n,1)));
                break;
            case STATE_NEWELEMENT:
                if (handler instanceof DeserializationContext) {
                    DeserializationContext context =
                              (DeserializationContext)handler;
                    context.setCurElement(
                              (MessageElement)elements.get(events.get(n,1)));
                }
                break;
            }
        }
    }
    
/////////////////////////////////////////////
    class intArrayVector {
        private int RECORD_SIZE = 5;
        private int currentSize = 0;
        private int[] intarray = new int[50 * RECORD_SIZE];  // default to 50 5 field records
        
        public int add(int p1, int p2, int p3, int p4, int p5) {
            if (currentSize == intarray.length) {
                int[] newarray = new int[currentSize * 2];
                System.arraycopy(intarray, 0, newarray, 0, currentSize);
                intarray = newarray;
            }
            int pos = currentSize / RECORD_SIZE;
            intarray[currentSize++] = p1;
            intarray[currentSize++] = p2;
            intarray[currentSize++] = p3;
            intarray[currentSize++] = p4;
            intarray[currentSize++] = p5;
            return pos;
        }

        public int[] get(int pos) {
            int[] ints = {intarray[(pos * 5)], 
                          intarray[(pos * 5)+1],
                          intarray[(pos * 5)+2],
                          intarray[(pos * 5)+3],
                          intarray[(pos * 5)+4]};
            return ints;
        }
        
        public int get(int pos, int fld) {
            return intarray[(pos * RECORD_SIZE) + fld];
        }
    
        public int getLength() {
            return (currentSize / RECORD_SIZE);
        }
    }
/////////////////////////////////////////////
}
