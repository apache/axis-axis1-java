package org.apache.axis.message;

/**
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BodyBuilder extends SOAPHandler
{
    static Category category =
            Category.getInstance(BodyBuilder.class.getName());

    private SOAPBodyElement element;
    boolean gotRPCElement = false;
    boolean isRPCElement = false;
    
    private SOAPEnvelope envelope;
    
    BodyBuilder(SOAPEnvelope envelope)
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
        if (category.isDebugEnabled()) {
            category.debug("In BodyBuilder.onStartChild()");
        }
        SOAPHandler handler = null;
        
        /** We're about to create a body element.  So we really need
         * to know at this point if this is an RPC service or not.  It's
         * possible that no one has set the service up until this point,
         * so if that's the case we should attempt to set it based on the
         * namespace of the first root body element.  Setting the
         * service may (should?) result in setting the service
         * description, which can then tell us what to create.
         */
        boolean isRoot = true;
        String root = attributes.getValue(Constants.URI_SOAP_ENC,
                                        Constants.ATTR_ROOT);
        if ((root != null) && root.equals("0")) isRoot = false;

        if (isRoot &&
            context.getMessageContext().getServiceHandler() == null) {
            category.debug( "Dispatching to body namespace '" +
                        namespace + "'");
            context.getMessageContext().setTargetService(namespace);
        }
        
        /** Now we make a plain SOAPBodyElement IF we either:
         * a) have an non-root element, or
         * b) have a non-RPC service
         */
        MessageContext msgContext = context.getMessageContext();

        if (localName.equals(Constants.ELEM_FAULT) &&
            namespace.equals(Constants.URI_SOAP_ENV)) {
            element = new SOAPFaultElement(namespace, localName, prefix,
                                           attributes, context);
            handler = new SOAPFaultBuilder((SOAPFaultElement)element,
                                           context);
        } else if (!gotRPCElement &&
            isRoot && 
            msgContext.isPropertyTrue(MessageContext.ISRPC, true) ) {
                gotRPCElement = true;
                element = new RPCElement(namespace, localName, prefix,
                                         attributes, context);
                //handler = new RPCHandler((RPCElement)element);
        } else {
            element = new SOAPBodyElement(namespace, localName, prefix,
                                      attributes, context);
            if (element.getFixupDeserializer() != null)
                handler = element.getFixupDeserializer();
        }

        if (handler == null)
            handler = new SOAPHandler();
        
        handler.myElement = element;
        
        if (category.isDebugEnabled()) {
            category.debug("Out BodyBuilder.onStartChild()");
        }
        return handler;
    }
    
    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
    {
        if (category.isDebugEnabled()) {
            category.debug("In BodyBuilder.onEndChild()");
        }
        
        if (element != null) {
            envelope.addBodyElement(element);
            element = null;
        }

        if (category.isDebugEnabled()) {
            category.debug("Out BodyBuilder.onEndChild()");
        }
    }
}
