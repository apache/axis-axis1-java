package org.apache.axis.message;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.rpc.namespace.QName;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

public class SAXOutputter extends DefaultHandler
{
    static Category category =
            Category.getInstance(SAXOutputter.class.getName());
    
    SerializationContext context;
    
    public SAXOutputter(SerializationContext context)
    {
        this.context = context;
    }
    
    public void startDocument() throws SAXException {
    }
    
    public void endDocument() throws SAXException {
        if (category.isDebugEnabled()) {
            category.debug("SAXOutputter: end document.");
        }
    }
    
    public void startPrefixMapping(String p1, String p2) throws SAXException {
        context.registerPrefixForURI(p1,p2);
    }
    
    public void endPrefixMapping(String p1) throws SAXException {
        // !!!
    }
    
    public void characters(char[] p1, int p2, int p3) throws SAXException {
        if (category.isDebugEnabled()) {
            category.debug("(out) characters ['" +
                               new String(p1, p2, p3) + "']");
        }
        try {
            context.writeChars(p1, p2, p3);
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
        if (category.isDebugEnabled()) {
            category.debug("(out) startElement ['" + namespace + "' " +
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
        if (category.isDebugEnabled()) {
            category.debug("(out) endElement ['" + namespace + "' " +
                           localName + "]");
        }
        
        try {
            context.endElement();
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
}
