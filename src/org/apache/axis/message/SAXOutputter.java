/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis.message;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.namespace.QName;
import java.io.IOException;

public class SAXOutputter extends DefaultHandler implements LexicalHandler
{
    protected static Log log =
        LogFactory.getLog(SAXOutputter.class.getName());
    
    SerializationContext context;
    boolean isCDATA = false;
    
    public SAXOutputter(SerializationContext context)
    {
        this.context = context;
    }
    
    public void startDocument() throws SAXException {
        try {
			context.writeString("<?xml version=\"1.0\" encoding=\"");
	        String encoding = XMLUtils.getEncoding(context.getMessageContext());
	        context.writeString(encoding);
	        context.writeString("\"?>\n");
	        context.setSendDecl(false);
		} catch (IOException e) {
			throw new SAXException(e);
		}
    }
    
    public void endDocument() throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug("SAXOutputter.endDocument");
        }
    }
    
    public void startPrefixMapping(String p1, String p2) throws SAXException {
        context.registerPrefixForURI(p1,p2);
    }
    
    public void endPrefixMapping(String p1) throws SAXException {
        // !!!
    }
    
    public void characters(char[] p1, int p2, int p3) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug("SAXOutputter.characters ['" + new String(p1, p2, p3) + "']");
        }
        try {
            if(!isCDATA) {
                context.writeChars(p1, p2, p3);
            } else { 
                context.writeString(new String(p1, p2, p3));
            }
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
    
    public void ignorableWhitespace(char[] p1, int p2, int p3) 
        throws SAXException
    {
        try {
            context.writeChars(p1, p2, p3);
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
 
    public void skippedEntity(String p1) throws SAXException {
    }
    
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes)
        throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug("SAXOutputter.startElement ['" + namespace + "' " +
                           localName + "]");
        }

        try {
            context.startElement(new QName(namespace,localName), attributes);
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
    
    public void endElement(String namespace, String localName, String qName)
        throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug("SAXOutputter.endElement ['" + namespace + "' " +
                           localName + "]");
        }
        
        try {
            context.endElement();
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void startDTD(java.lang.String name,
                     java.lang.String publicId,
                     java.lang.String systemId)
              throws SAXException
    {
    }
    
    public void endDTD()
            throws SAXException
    {
    }
    
    public void startEntity(java.lang.String name)
                 throws SAXException
    {
    }
    
    public void endEntity(java.lang.String name)
               throws SAXException
    {
    }
    
    public void startCDATA()
                throws SAXException
    {
        try {
            isCDATA = true;
            context.writeString("<![CDATA[");
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
    
    public void endCDATA()
              throws SAXException
    {
        try {
            isCDATA = false;
            context.writeString("]]>");
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
    
    public void comment(char[] ch,
                    int start,
                    int length)
             throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug("SAXOutputter.comment ['" + new String(ch, start, length) + "']");
        }
        try {
            context.writeString("<!--");
            context.writeChars(ch, start, length);
            context.writeString("-->");
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
}
