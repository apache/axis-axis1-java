package org.apache.axis.message;

/**
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.apache.axis.encoding.DeserializationContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HeaderBuilder extends SOAPHandler
{
    private SOAPHeader header;
    private SOAPEnvelope envelope;
    
    HeaderBuilder(SOAPEnvelope envelope)
    {
        this.envelope = envelope;
    }
    
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        header = new SOAPHeader(namespace, localName, prefix,
                                attributes, context);
        
        SOAPHandler handler = new SOAPHandler();
        handler.myElement = header;

        return handler;
    }
    
    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
    {
        envelope.addHeader(header);
    }
}
