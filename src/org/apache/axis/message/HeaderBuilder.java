package org.apache.axis.message;

/**
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.axis.encoding.DeserializationContext;

public class HeaderBuilder extends SOAPHandler
{
    private SOAPHeader header;
    
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        header = new SOAPHeader(namespace, localName, prefix,
                                attributes, context);
        return null;
    }
    
    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
    {
        header.setEndIndex(context.getCurrentRecordPos());
       
        context.envelope.addHeader(header);
    }
}
