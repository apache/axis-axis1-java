package org.apache.axis.message.soap;

import org.apache.axis.util.xml.*;
import org.apache.axis.message.api.*;
import org.w3c.dom.*;
import java.util.Vector;

public class SOAPElement implements MessageElement {
	
	Element entity;
	DOMWriter writer;
	DOMReader reader;
	
	public SOAPElement(Element entity) { this.entity = entity; init(); }
	public SOAPElement(Document dom) { this.entity = dom.getDocumentElement(); init();}
	public SOAPElement(Document dom, String namespaceURI, String prefix, String name) {
		entity = dom.createElementNS(namespaceURI, prefix + ":" + name);
		init();
	}
		
	void init() { 
		try {
			writer = new DOMWriter(this.entity); 
			reader = new DOMReader(this.entity); 
		} catch (Exception e) {}
	}
	
	public String toXML() {
		DOMPrinter out = new DOMPrinter(false);
		return out.writeToString(entity);
	}
	
	public Element getDOMEntity() {
		return entity;
	}
	
	public void setDOMEntity(Element entity) {
		this.entity = entity;
	}
	
	public void setEncodingStyle(String uri) {
		writer.wans(Constants.ATTR_ENCODING_STYLE, Constants.URI_SOAP_ENC, Constants.NSPREFIX_SOAP_ENC, new DOMReader(entity).getPrefix());
	}
	
	public String getEncodingStyle() {
        String value ;
        if (!"".equals(value = reader.getAttributeNS(Constants.ATTR_ENCODING_STYLE, Constants.URI_SOAP_ENV)))
            return value ;
        return null ;
	}
	
	public void removeEncodingStyle() {
		entity.removeAttributeNS(Constants.URI_SOAP_ENV, Constants.ATTR_ENCODING_STYLE);
	}
	
	public void ownedBy(Element parent) {
        ownedBy(parent.getOwnerDocument()) ;
        if (Constants.URI_SOAP_ENV.equals(entity.getNamespaceURI())) {
            String prefix = reader.getPrefixForNamespaceURI(Constants.URI_SOAP_ENV) ;
            entity.setPrefix(prefix) ;
        }
	}
	
	public void ownedBy(Document dom) {
        if (dom != entity.getOwnerDocument())
			entity = (Element)dom.importNode((Node)entity, true);
	}
	
	public void declareNamespace(String namespaceURI, String prefix) {
		writer.wnd(namespaceURI, prefix);
	}
	
	public void declareNamespace(String namespaceURI) {
		writer.wnd(namespaceURI);
	}
	
	public void setID(String id) {
		writer.wa(Constants.ATTR_ID,id);
	}
	
	public String getID() {
		return reader.getAttribute(Constants.ATTR_ID);
	}
	
	public void removeID() {
		entity.removeAttributeNS(getDOMEntity().getNamespaceURI(), Constants.ATTR_ID);
	}
	
	public void setReference(MessageElement element) {
		writer.wa(Constants.ATTR_HREF, element.getID());
	}
	
	public void removeReference() {
		entity.removeAttributeNS(getDOMEntity().getNamespaceURI(), Constants.ATTR_HREF);
	}
	
	public MessageElement getReference() {
		// not implemented yet
		return null;
	}
	
	public MessageElement getClone() {
		return new SOAPElement((Element)entity.cloneNode(true));
	}
	
	public int getEntryCount() {
		return entity.getChildNodes().getLength();
	}
	
	public MessageElementEntry getEntry(int index) {
		MessageElementEntry[] entries = getEntries();
		return entries[index];
	}
	
	public MessageElementEntry[] getEntries() {
        Element[] elements = reader.getChildren();
        MessageElementEntry[] entries = new MessageElementEntry[elements.length] ;
        for (int n = 0 ; n < entries.length ; n++)
            entries[n] = new SOAPElementEntry(elements[n]) ;
        return entries ;
	}
	
	public void removeEntry(MessageElementEntry entry) {
		getDOMEntity().removeChild(entry.getDOMEntity());
	}
	
	public void appendEntry(MessageElementEntry entry) {
		getDOMEntity().appendChild(entry.getDOMEntity());
	}
	
	public void insertEntry(MessageElementEntry entry, MessageElementEntry before) {
		getDOMEntity().insertBefore(entry.getDOMEntity(), before.getDOMEntity());
	}	

    Element[] getElementsNamed(String name) {
		return reader.getElementsByTagName(name);
    }
	
	Element[] getElementsNamedNS(String name, String namespaceURI) {
		return reader.getElementsByTagNameNS(name, namespaceURI);
	}
}
