package org.apache.axis.message.soap;

import org.apache.axis.message.*;
import org.apache.axis.message.api.*;
import org.w3c.dom.*;

public class SOAPWriter implements CanonicalWriter {
	
	Document dom;

	public SOAPWriter() {}
	public SOAPWriter(Message message) {}
	public SOAPWriter(MessageReader reader) {}
	public SOAPWriter(Document dom) { this.dom = dom; }
	public SOAPWriter(Element element) { this.dom = element.getOwnerDocument();}
	
	public Document getDocument() { return dom; }
	
	public void writeTo(Message message) {}
	public void writeTo(MessageWriter writer) {}
	public void writeTo(Document dom) { this.dom = dom; }
	public void writeTo(Element element) { this.dom = element.getOwnerDocument();}
	
	public void readWith(MessageReader reader) {
	}
	
	public MessageEnvelope createEnvelope() {
		return new SOAPEnvelope(dom);
	}
	
	public MessageElement createHeader() {
		return new SOAPElement(dom, Constants.URI_SOAP_ENV, dom.getDocumentElement().getPrefix(), Constants.ELEM_HEADER);
	}
	
	public MessageElement createBody() {
		return new SOAPElement(dom, Constants.URI_SOAP_ENV, dom.getDocumentElement().getPrefix(), Constants.ELEM_BODY);
	}
	
	public MessageHeaderEntry createHeaderEntry(String name, String namespaceURI) {
		SOAPHeaderEntry entry = new SOAPHeaderEntry(dom.createElementNS(namespaceURI, name));
		return entry;
	}
	
	public MessageHeaderEntry createHeaderEntry(String name, String namespaceURI, boolean mustUnderstand) {
		SOAPHeaderEntry entry = (SOAPHeaderEntry)createHeaderEntry(name, namespaceURI);
		entry.setMustUnderstand(mustUnderstand);
		return entry;
	}
	
	public MessageHeaderEntry createHeaderEntry(String name, String namespaceURI, String actor) {
		SOAPHeaderEntry entry = (SOAPHeaderEntry)createHeaderEntry(name, namespaceURI);
		entry.setActor(actor);
		return entry;
	}
	
	public MessageHeaderEntry createHeaderEntry(String name, String namespaceURI, boolean mustUnderstand, String actor) {
		SOAPHeaderEntry entry = (SOAPHeaderEntry)createHeaderEntry(name, namespaceURI);
		entry.setMustUnderstand(mustUnderstand);
		entry.setActor(actor);
		return entry;
	}
	
	public MessageElementEntry createBodyEntry(String name, String namespaceURI) {
		SOAPElementEntry entry = new SOAPElementEntry(dom.createElementNS(namespaceURI, name));
		return entry;
	}
	
	public MessageFault createFault(String first, String second, Object details) {
		return null;
	}
	
	public void appendHeaderEntry(MessageHeaderEntry entry) {
		SOAPEnvelope env = new SOAPEnvelope(dom);
		env.getHeader().appendEntry((MessageElementEntry)entry);
	}
	
	public void appendBodyEntry(MessageElementEntry entry) {
		SOAPReader reader = new SOAPReader(dom);
		reader.getEnvelope().getBody().appendEntry(entry);
	}
	
	public void insertHeaderEntry(MessageHeaderEntry entry, MessageHeaderEntry before) {
		SOAPReader reader = new SOAPReader(dom);
		reader.getEnvelope().getHeader().insertEntry((MessageElementEntry)entry, (MessageElementEntry)before);
	}
	
	public void insertBodyEntry(MessageElementEntry entry, MessageElementEntry before) {
		SOAPReader reader = new SOAPReader(dom);
		reader.getEnvelope().getBody().insertEntry(entry, before);
	}
	
	public void removeHeaderEntry(MessageHeaderEntry entry) {
		SOAPReader reader = new SOAPReader(dom);
		reader.getEnvelope().getHeader().removeEntry((MessageElementEntry)entry);
	}
	
	public void removeBodyEntry(MessageElementEntry entry) {
		SOAPReader reader = new SOAPReader(dom);
		reader.getEnvelope().getBody().removeEntry(entry);
	}
	
	public void setFault(MessageFault fault) {
		SOAPReader reader = new SOAPReader(dom);
		reader.getEnvelope().getBody().appendEntry((MessageElementEntry)fault);
	}	
}