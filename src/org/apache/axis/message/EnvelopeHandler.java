package org.apache.axis.message;

/**
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.axis.Constants;
import org.apache.axis.utils.QName;
import org.apache.axis.encoding.DeserializationContext;

public class EnvelopeHandler extends SOAPHandler
{
    public SOAPHandler onStartChild(String namespace, String localName,
                                     Attributes attributes,
                                     DeserializationContext context)
        throws SAXException
    {
        return new EnvelopeBuilder();
    }
}
