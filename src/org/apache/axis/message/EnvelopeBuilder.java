package org.apache.axis.message;

/**
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.apache.axis.Constants;
import org.apache.axis.encoding.DeserializationContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;

public class EnvelopeBuilder extends SOAPHandler
{
    private MessageElement element;
    private SOAPEnvelope envelope;
    
    private boolean gotHeader = false;
    private boolean gotBody = false;
    
    private static final QName headerQName = new QName(Constants.URI_SOAP_ENV,
                                                       Constants.ELEM_HEADER);
    private static final QName bodyQName = new QName(Constants.URI_SOAP_ENV,
                                                       Constants.ELEM_BODY);
    
    public EnvelopeBuilder(String messageType)
    {
        envelope = new SOAPEnvelope(false);
        envelope.setMessageType(messageType);
        myElement = envelope;
    }
    
    public EnvelopeBuilder(SOAPEnvelope env, String messageType)
    {
        envelope = env ;
        envelope.setMessageType(messageType);
        myElement = envelope;
    }
    
    public SOAPEnvelope getEnvelope()
    {
        return envelope;
    }
    
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        if (!namespace.equals(Constants.URI_SOAP_ENV))
            throw new SAXException("Bad envelope namespace '" + namespace +
                                         "'");
        
        if (!localName.equals(Constants.ELEM_ENVELOPE))
            throw new SAXException("Bad envelope tag " + localName);

        String prefix = "";
        int idx = qName.indexOf(":");
        if (idx > 0)
            prefix = qName.substring(0, idx);

        envelope.setPrefix(prefix);
        envelope.setNamespaceURI(namespace);
        envelope.setNSMappings(context.getCurrentNSMappings());
        context.pushNewElement(envelope);
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
            return new HeaderBuilder(envelope);
        }
        
        if (thisQName.equals(bodyQName)) {
            if (gotBody)
                throw new SAXException("Only one Body element allowed!");
            
            gotBody = true;
            return new BodyBuilder(envelope);
        }
        
        if (!gotBody)
            throw new SAXException("No custom elements allowed at top level "+
                                   "until after the <Body>");

        /*
        element = new MessageElement(namespace, localName, prefix,
                                     attributes, context);
        
        if (element.getFixupDeserializer() != null)
            return element.getFixupDeserializer();
        */
        
        return null;
    }
    
    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
    {
        if (element != null) {
            envelope.addTrailer(element);
        }
    }

    public void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        // Envelope isn't dirty yet by default...
        envelope.setDirty(false);
    }
}
