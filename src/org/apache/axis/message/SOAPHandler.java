package org.apache.axis.message;

/* Copyright (c) 2000, Allaire Corp.
 * 
 * All rights reserved.
 */

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
    
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        // By default, make a new element
        if (!context.doneParsing) {
            if (myElement == null)
                myElement = new MessageElement(namespace, localName,
                                               qName, attributes, context);
            context.pushNewElement(myElement);
        }
    }
    
    public void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        if (myElement != null)
            myElement.setEndIndex(context.getCurrentRecordPos());
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
