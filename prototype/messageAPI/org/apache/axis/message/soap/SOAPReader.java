package org.apache.axis.message.soap;

import org.apache.axis.message.*;
import org.apache.axis.message.api.*;
import org.w3c.dom.*;

public class SOAPReader implements CanonicalReader {
	
	Document dom = null;
	MessageEnvelope envelope = null;
	
	public SOAPReader() {}
	public SOAPReader(Message message) { read(message); }
	public SOAPReader(MessageWriter writer) { read(writer); }
	public SOAPReader(Document dom) { this.dom = dom; }
	public SOAPReader(Element element) { this.dom = element.getOwnerDocument();}
	
	public void read(Message message) {
		DOMMessageReader reader = new DOMMessageReader(message);
		dom = reader.getDocument();
	}
	
	public void read(MessageWriter writer) {
		SOAPWriter swriter = (SOAPWriter)writer;
		dom = swriter.getDocument();
	}
	
	public void read(Document dom) { this.dom = dom; }
	public void read(Element element) { this.dom = element.getOwnerDocument();}
	
	public boolean validate() {
		// not implemented yet
		return false;
	}
	
	public void writeTo(MessageWriter writer) {
		writer = new SOAPWriter(this);
	}
	
	public MessageEnvelope getEnvelope() {
		if (envelope == null && dom != null) envelope = new SOAPEnvelope(dom);
		return envelope;
	}
	
	public MessageHeaderEntry[] getHeaderEntries() {
		if (envelope != null) {
			MessageElementEntry[] entries = envelope.getHeader().getEntries();
			MessageHeaderEntry[] headers = new SOAPHeaderEntry[entries.length];
			for (int n = 0; n < entries.length; n++) {
				SOAPElementEntry element = (SOAPElementEntry)entries[n];
				SOAPHeaderEntry header = new SOAPHeaderEntry(element.getDOMEntity());
				headers[n] = header;
			}
			return headers;
		}
		return null;
	}
	
	public MessageElementEntry[] getBodyEntries() {
		if (envelope != null) return envelope.getBody().getEntries();
		return null;
	}
	
	public MessageHeaderEntry getHeaderEntry(int index) {
		MessageHeaderEntry[] entries = getHeaderEntries();
		return entries[index];
	}
	
	public MessageElementEntry getBodyEntry(int index) {
		MessageElementEntry[] entries = getBodyEntries();
		return entries[index];
	}
	
	public MessageFault getFault() {
		// not implemented yet
		return null;
	}
	
	public Document getDocument() { return dom; }
}
