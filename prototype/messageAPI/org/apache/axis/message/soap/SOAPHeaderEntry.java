package org.apache.axis.message.soap;

import org.apache.axis.message.api.*;
import org.w3c.dom.*;
import org.apache.axis.util.xml.*;

public class SOAPHeaderEntry extends SOAPElementEntry implements MessageHeaderEntry {
	
	public SOAPHeaderEntry(Element entity) { super(entity); }
	
	public void setMustUnderstand(boolean flag) {
		String prefix = reader.getDocument().getDocumentElement().getPrefix();
		if (prefix.length() != 0) prefix += ":";
		writer.writeAttributeNS(prefix + Constants.ATTR_MUST_UNDERSTAND, flag ? "1" : "0", Constants.URI_SOAP_ENV);
	}
	
	public void removeMustUnderstand() {
		String prefix = reader.getDocument().getDocumentElement().getPrefix();
		if (prefix.length() != 0) prefix += ":";
		entity.removeAttributeNS(Constants.URI_SOAP_ENV, prefix + Constants.ATTR_MUST_UNDERSTAND);
	}
	
	public boolean isMustUnderstand() {
		String prefix = reader.getDocument().getDocumentElement().getPrefix();
		if (prefix.length() != 0) prefix += ":";
		return "1".equals(reader.getAttributeNS(prefix + Constants.ATTR_MUST_UNDERSTAND, Constants.URI_SOAP_ENV));
	}
	
	public void setActor(String uri) {
		String prefix = reader.getDocument().getDocumentElement().getPrefix();
		if (prefix.length() != 0) prefix += ":";
		writer.writeAttributeNS(prefix + Constants.ATTR_ACTOR, uri, Constants.URI_SOAP_ENV);
	}
	
	public void setActorAsNext() {
		setActor(Constants.URI_NEXT_ACTOR);
	}
	
	public boolean isNextActor() {
		return Constants.URI_NEXT_ACTOR.equals(getActor()) ;
	}
	
	public String getActor() {
		String prefix = reader.getDocument().getDocumentElement().getPrefix();
		if (prefix.length() != 0) prefix += ":";
        String value ;
        if (!"".equals(value = reader.getAttributeNS(prefix + Constants.ATTR_ACTOR, Constants.URI_SOAP_ENV)))
            return value ;
        return null ;
	}
	
	public void removeActor() {
		String prefix = reader.getDocument().getDocumentElement().getPrefix();
		if (prefix.length() != 0) prefix += ":";
		entity.removeAttributeNS(Constants.URI_SOAP_ENV, prefix + Constants.ATTR_ACTOR);
	}	
}
