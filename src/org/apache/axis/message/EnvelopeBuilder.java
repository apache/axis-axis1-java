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

public class EnvelopeBuilder extends SOAPHandler
{
    private MessageElement element;
    
    private boolean gotHeader = false;
    private boolean gotBody = false;
    
    private static final QName headerQName = new QName(Constants.URI_SOAP_ENV,
                                                       Constants.ELEM_HEADER);
    private static final QName bodyQName = new QName(Constants.URI_SOAP_ENV,
                                                       Constants.ELEM_BODY);
    
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        if (!namespace.equals(Constants.URI_SOAP_ENV))
            throw new SAXException("Bad envelope namespace " + namespace);
        
        if (!localName.equals(Constants.ELEM_ENVELOPE))
            throw new SAXException("Bad envelope tag " + localName);

        String prefix = "";
        int idx = qName.indexOf(":");
        if (idx > 0)
            prefix = qName.substring(0, idx);

        context.getEnvelope().setPrefix(prefix);
        context.getEnvelope().setNamespaceURI(namespace);
    }
    
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        QName thisQName = new QName(namespace, localName);
        if (thisQName.equals(headerQName)) {
            if (gotHeader)
                throw new SAXException("Only one Header element allowed!");
            
            gotHeader = true;
            return new HeaderBuilder();
        }
        
        if (thisQName.equals(bodyQName)) {
            if (gotBody)
                throw new SAXException("Only one Body element allowed!");
            
            gotBody = true;
            return new BodyBuilder();
        }
        
        if (!gotBody)
            throw new SAXException("No custom elements allowed at top level "+
                                   "until after the <Body>");

        element = new MessageElement(namespace, localName, prefix,
                                     attributes, context);
        
        if (element.getFixupDeserializer() != null)
            return element.getFixupDeserializer();
        
        return null;
    }
    
    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
    {
        if (element != null) {
            element.setEndIndex(context.getCurrentRecordPos());
            context.getEnvelope().addTrailer(element);
        }
    }
}
