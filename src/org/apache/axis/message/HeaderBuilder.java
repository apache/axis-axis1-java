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
                             Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        header = new SOAPHeader(namespace, localName,
                                           attributes, context);
        return null;
    }
    
    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
    {
        System.out.println("header is " + header);
        System.out.println("context is " + context);
        
        header.setEndIndex(context.getCurrentRecordPos());
       
        context.envelope.addHeader(header);
    }
}
