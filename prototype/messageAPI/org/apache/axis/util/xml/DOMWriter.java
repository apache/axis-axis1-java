package org.apache.axis.util.xml;

import org.w3c.dom.*;
import org.apache.xerces.dom.*;

public class DOMWriter
{	
	Element scope;
	Document scopeDom;
	
	public Element gs() { return getScope(); }
	public Element getScope() { return scope; }
	public Document gd() { return getDocument(); }
	public Document getDocument() { return scopeDom; }
	
	public DOMWriter() {
		scopeDom = new DocumentImpl();
		scope = scopeDom.getDocumentElement();
	}
	
	public DOMWriter(Document dom) {
		scope = dom.getDocumentElement();
		scopeDom = dom;
	}
	
	public DOMWriter(Element element) {
		scope = element;
		scopeDom = element.getOwnerDocument();
	}
	
	public Element re(String name, Element replace) { return replaceElement(name, replace); }
	public Element replaceElement(String name, Element replace) {
		if (scope != null)
			scope = (Element)scope.replaceChild(scopeDom.createElement(name), replace);
		else we(name);
		return scope;
	}
	
	public Element  ie(String name, Element before) { return insertElement(name, before); }
	public Element insertElement(String name, Element before) {
		if (scope != null)
			scope = (Element)scope.insertBefore(scopeDom.createElement(name), before);
		else we(name);
		return scope;
	}

	public Element iens(String name, String namespaceURI, Element before) { return insertElement(name, namespaceURI, before); }
	public Element insertElement(String name, String namespaceURI, Element before) {
		if (scope != null)
			scope = (Element)scope.insertBefore(scopeDom.createElementNS(namespaceURI,name), before);
		else wens(name, namespaceURI);
		return scope;
	}		
	
	public Element we(String name) { return writeElement(name); }
	public Element writeElement(String name) {
		if (scope != null)
			scope = (Element)scope.appendChild(scopeDom.createElement(name));
		else
			scope = (Element)scopeDom.appendChild(scopeDom.createElement(name));
		return scope;
	}

	public Element wens(String name, String namespaceURI) { return writeElementNS(name, namespaceURI); }
	public Element writeElementNS(String name, String namespaceURI) {
		if (scope != null)
			scope = (Element)scope.appendChild(scopeDom.createElementNS(namespaceURI,name));
		else
			scope = (Element)scopeDom.appendChild(scopeDom.createElementNS(namespaceURI,name));
		return scope;
	}
	
	public Element wens(String name, String namespaceURI, String prefix) { return writeElementNS(name, namespaceURI, prefix); }
	public Element writeElementNS(String name, String namespaceURI, String prefix) {
		if (scope != null)
			scope = (Element)scope.appendChild(scopeDom.createElementNS(namespaceURI,prefix + ":" + name));
		else
			scope = (Element)scopeDom.appendChild(scopeDom.createElementNS(namespaceURI,prefix + ":" + name));
		return scope;
	}
	
	public void wee() { writeEndElement(); }
	public void writeEndElement() {
		try {
			scope = (Element)scope.getParentNode();
		} catch(Exception e) {
			scope = scopeDom.getDocumentElement();
		}
	}

	public void wnd(String namespaceURI) { writeNamespaceDeclaration(namespaceURI); }
	public void writeNamespaceDeclaration(String namespaceURI) {
		scope.setAttribute("xmlns", namespaceURI) ;
	}

	public void wnd(String namespaceURI, String prefix) { writeNamespaceDeclaration(namespaceURI, prefix); }	
	public void writeNamespaceDeclaration(String namespaceURI, String prefix) {
		scope.setAttribute("xmlns:" + prefix, namespaceURI) ;
	}

	public void wa(String name, String value) { writeAttribute(name, value); }
	public void writeAttribute(String name, String value) {
		scope.setAttribute(name, value);
	}
	
	public void wans(String name, String value, String namespaceURI) { writeAttributeNS(name, value, namespaceURI); }
	public void writeAttributeNS(String name, String value, String namespaceURI) {
		scope.setAttributeNS(namespaceURI, name, value);
	}

	public void wans(String name, String value, String namespaceURI, String prefix) { writeAttributeNS(name, value, namespaceURI, prefix); }
	public void writeAttributeNS(String name, String value, String namespaceURI, String prefix) {
		scope.setAttributeNS(namespaceURI, prefix + ":" + name, value);
	}
	
	public CDATASection wcd(String value) { return writeCDATA(value); }
	public CDATASection writeCDATA(String value) {
		return (CDATASection)scope.appendChild(scopeDom.createCDATASection(value));
	}
	
	public Comment wc(String comment) { return writeComment(comment); }
	public Comment writeComment(String comment) {
		return (Comment)scope.appendChild(scopeDom.createComment(comment));
	}
	
	public ProcessingInstruction wpi(String name, String value) { return writeProcessingInstruction(name, value); }
	public ProcessingInstruction writeProcessingInstruction(String name, String value) {
		return (ProcessingInstruction)scope.appendChild(scopeDom.createProcessingInstruction(name, value));
	}

	public Text wv(String value) { return writeValue(value); }
	public Text writeValue(String value) {
		return (Text)scope.appendChild(scopeDom.createTextNode(value));
	}
	
	public Element wd(Document dom) { return writeDocument(dom); }
	public Element writeDocument(Document dom) {
		Element element = (Element)scopeDom.importNode(dom.getDocumentElement(),true);
		return (Element)scope.appendChild(element);
	}

	public Element we(Element element) { return writeElement(element); }
	public Element writeElement(Element element) {
		element = (Element)scopeDom.importNode(element, true);
		return (Element)scope.appendChild(element);
	}
	
	public Element ie(Element element, Element before) { return insertElement(element, before); }
	public Element insertElement(Element element, Element before) {
		element = (Element)scopeDom.importNode(element, true);
		return (Element)scope.insertBefore(element, before);
	}

	public Element re(Element element, Element replace) { return replaceElement(element, replace); }
	public Element replaceElement(Element element, Element replace) {
		element = (Element)scopeDom.importNode(element, true);
		return (Element)scope.replaceChild(element, replace);
	}
	
	public void wa(Attr attr) { writeAttribute(attr); }
	public void writeAttribute(Attr attr) {
		attr = (Attr)scopeDom.importNode(attr,true);
		scope.getAttributes().setNamedItem(attr);
	}
	
	public void wes(Element[] elements) { writeElements(elements); }
	public void writeElements(Element[] elements) {
		for (int n = 0; n < elements.length; n++) {
			writeElement(elements[n]);
		}
	}
	
	public void was(Attr[] attr) { writeAttributes(attr); }
	public void writeAttributes(Attr[] attr) {
		for (int n = 0; n < attr.length; n++) {
			writeAttribute(attr[n]);
		}
	}
}
