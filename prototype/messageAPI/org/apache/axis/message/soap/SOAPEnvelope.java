package org.apache.axis.message.soap;

import org.apache.axis.message.api.*;
import org.w3c.dom.*;
import org.apache.axis.util.xml.*;

public class SOAPEnvelope extends SOAPElement implements MessageEnvelope {

	public SOAPEnvelope(Element entity) { super(entity); }
	
    public SOAPEnvelope(Document dom) {
		super(dom);
		DOMWriter writer = new DOMWriter(dom);
		entity = writer.wens(Constants.NSPREFIX_SOAP_ENV + ":" + Constants.ELEM_ENVELOPE, Constants.URI_SOAP_ENV);
		init();
		writer.wnd(Constants.URI_SOAP_ENV, Constants.NSPREFIX_SOAP_ENV);
		writer.wnd(Constants.URI_SOAP_ENC, Constants.NSPREFIX_SOAP_ENC);
		writer.wnd(Constants.URI_SCHEMA_XSD, Constants.NSPREFIX_SCHEMA_XSD);
		writer.wnd(Constants.URI_SCHEMA_XSI, Constants.NSPREFIX_SCHEMA_XSI);		
		writer.wens(Constants.NSPREFIX_SOAP_ENV + ":" + Constants.ELEM_HEADER, Constants.URI_SOAP_ENV);
		writer.wee();	// move scope back up
		writer.wens(Constants.NSPREFIX_SOAP_ENV + ":" + Constants.ELEM_BODY, Constants.URI_SOAP_ENV);
		writer.wee();	// move scope back up
    }
	
	public String getEnvelopeNamespaceURI() {
		return reader.getNamespaceURI();
	}
	
	public boolean hasHeader() {
		String prefix = reader.getPrefix();
		if (prefix.length() != 0) prefix += ":";
		return getElementsNamedNS(prefix + Constants.ELEM_HEADER, Constants.URI_SOAP_ENV).length > 0 ;
	}
	
	public MessageElement getHeader() {
		String prefix = reader.getPrefix();
		if (prefix.length() != 0) prefix += ":";
        //Element[] list = getElementsNamedNS(prefix + Constants.ELEM_HEADER,Constants.URI_SOAP_ENV) ;
		Element[] list = getElementsNamed(prefix + Constants.ELEM_HEADER) ;
        if (list.length == 0) {
            //list = getElementsNamedNS(prefix + Constants.ELEM_BODY, Constants.URI_SOAP_ENV);
			list = getElementsNamed(prefix + Constants.ELEM_BODY);
			MessageElement header = new SOAPElement(writer.iens(prefix + Constants.ELEM_HEADER, Constants.URI_SOAP_ENV, list[0]));
			writer.wee();
			return header;
		} else {
            return new SOAPElement(list[0]) ;
		}
	}
	
	public void removeHeader() {
		getDOMEntity().removeChild(getHeader().getDOMEntity()) ;
	}
	
	public void setHeader(MessageElement header) {
		String prefix = reader.getPrefix();
		if (prefix.length() != 0) prefix += ":";
        Element[] list = getElementsNamed(prefix + Constants.ELEM_HEADER) ;
        if (list.length > 0)
			writer.re(header.getDOMEntity(), list[0]).setPrefix(prefix);
		else
			writer.ie(header.getDOMEntity(), (Element)entity.getFirstChild()).setPrefix(prefix);
		writer.wee();
	}
	
	public MessageElement getBody() {
		String prefix = reader.getPrefix();
		if (prefix.length() != 0) prefix += ":";
        Element[] list = getElementsNamed(prefix + Constants.ELEM_BODY) ;
        if (list.length == 0)
            return null ;
        else
            return new SOAPElement((Element)list[0]) ;
	}
	
	public void setBody(MessageElement body) {
		String prefix = reader.getPrefix();
		if (prefix.length() != 0) prefix += ":";
		MessageElement oldbody = getBody();
		if (body != null) {
			writer.re(body.getDOMEntity(), oldbody.getDOMEntity());
		} else {
			writer.we(body.getDOMEntity());
		}
		writer.wee();
	}
	
}
