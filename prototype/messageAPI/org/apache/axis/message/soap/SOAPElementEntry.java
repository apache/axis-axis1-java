package org.apache.axis.message.soap;

import org.apache.axis.message.api.*;
import org.w3c.dom.*;
import org.apache.axis.util.xml.*;

public class SOAPElementEntry extends SOAPElement implements MessageElementEntry {

	public SOAPElementEntry(Element entity) { super(entity); }
	
	public void setRoot(boolean flag) {
		String prefix = reader.getDocument().getDocumentElement().getPrefix();
		if (prefix.length() != 0) prefix += ":";
		writer.writeAttributeNS(prefix + Constants.ATTR_ROOT, flag ? "1" : "0", Constants.URI_SOAP_ENV);
	}
	
	public void removeRoot() {
		String prefix = reader.getDocument().getDocumentElement().getPrefix();
		if (prefix.length() != 0) prefix += ":";
		entity.removeAttributeNS(Constants.URI_SOAP_ENC, prefix + Constants.ATTR_ROOT);
	}
	
	public boolean isRoot() {
		String prefix = reader.getDocument().getDocumentElement().getPrefix();
		if (prefix.length() != 0) prefix += ":";
		return "1".equals(reader.getAttributeNS(prefix + Constants.ATTR_ROOT, Constants.URI_SOAP_ENC)) ;
	}	
}
