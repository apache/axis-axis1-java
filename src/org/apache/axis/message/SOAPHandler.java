package org.apache.axis.message;

/* Copyright (c) 2000, Allaire Corp.
 * 
 * All rights reserved.
 */

/** A <code>SOAPHandler</code>
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
import org.apache.axis.encoding.DeserializationContext;

public class SOAPHandler extends DefaultHandler
{
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
    }
    
    public void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
    }
    
    public final SOAPHandler onStartChild(String namespace, String localName,
                             Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        return null;
    }
    
    public SOAPHandler onStartChild(String namespace, 
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        return null;
    }
    
    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
    }
}
