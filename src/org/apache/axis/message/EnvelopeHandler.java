package org.apache.axis.message;

/**
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.apache.axis.encoding.DeserializationContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EnvelopeHandler extends SOAPHandler
{
    SOAPHandler realHandler;
    
    public EnvelopeHandler(SOAPHandler realHandler)
    {
        this.realHandler = realHandler;
    }
    
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        return realHandler;
    }
}
