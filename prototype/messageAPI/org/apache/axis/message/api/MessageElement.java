package org.apache.axis.message.api;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

public interface MessageElement {
	public String toXML();
	public Element getDOMEntity();
	public void setDOMEntity(Element entity);
	public void setEncodingStyle(String uri);
	public String getEncodingStyle();
	public void removeEncodingStyle();
	public void ownedBy(Element element);
	public void ownedBy(Document document);	
	public void declareNamespace(String namespaceURI, String prefix);
	public void declareNamespace(String namespaceURI);
	public void setID(String id);
	public String getID();
	public void removeID();
	public void setReference(MessageElement element);
	public void removeReference();
	public MessageElement getReference();
	public MessageElement getClone();
	public int getEntryCount();
	public MessageElementEntry getEntry(int index);
	public MessageElementEntry[] getEntries();
	public void removeEntry(MessageElementEntry entry);
	public void appendEntry(MessageElementEntry entry);
	public void insertEntry(MessageElementEntry entry, MessageElementEntry before);
}