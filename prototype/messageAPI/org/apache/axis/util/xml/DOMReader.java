package org.apache.axis.util.xml;

import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;

public class DOMReader
{
	Element scope;
	Document scopeDom;
	
	private static final String XMLNS_ = "xmlns:" ;
	
	public static Document parse(Reader in) throws IOException, SAXException
    {
        org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser() ;
        parser.setFeature("http://xml.org/sax/features/namespaces", true) ;
        parser.setErrorHandler(new ErrorHandlerImpl()) ;
		parser.parse(new InputSource(in));
        return parser.getDocument() ;
    }	

	public DOMReader(String xml) throws SAXException {
        try {
            StringReader in = new StringReader(xml) ;
            scopeDom = parse(in) ;
			scope = scopeDom.getDocumentElement();
            in.close() ;
        } catch (IOException e) {
            throw new UnknownError(e.getMessage());
        }	
	}

    public DOMReader(Reader in) throws IOException, SAXException {
        scopeDom = parse(in) ;
		scope = scopeDom.getDocumentElement();
    }
	
	public DOMReader(Document dom) {
		scopeDom = dom;
		scope = dom.getDocumentElement();
	}
	
	public DOMReader(Element element) {
		scopeDom = element.getOwnerDocument();
		scope = element;
	}
	
	public void moveNext() { scope = (Element)scope.getNextSibling(); }
	public void moveBack() { scope = (Element)scope.getPreviousSibling(); }
	public void moveUp() { scope = (Element)scope.getParentNode(); }
	public void moveDown() { scope = (Element)scope.getFirstChild(); }
	public String getLocalName() { return scope.getLocalName(); }
	public String getPrefix() { return scope.getPrefix(); }
	public String getNamespaceURI() { return scope.getNamespaceURI(); }
	public String getAttribute(String name) {return scope.getAttribute(name);}
	public String getAttributeNS(String name, String namespace) { return scope.getAttributeNS(name, namespace); }
	public boolean hasChildren() { return scope.hasChildNodes();}
	public boolean hasAttributes() { return scope.hasAttributes();}
	public boolean hasAttribute(String name) { return scope.hasAttribute(name);}
	public String getValue() { return scope.getNodeValue();}
	public short getNodeType() { return scope.getNodeType();}
	public Element[] getElementsByTagName(String name) {
		NodeList list = scope.getElementsByTagName(name);
		Element[] elements = new Element[list.getLength()];
		for (int n = 0; n < list.getLength(); n++) {elements[n] = (Element)list.item(n);}
		return elements;
	}
	public Element[] getChildren() {
		NodeList list = scope.getChildNodes();
		Element[] elements = new Element[list.getLength()];
		for (int n = 0; n < list.getLength(); n++) {elements[n] = (Element)list.item(n);}
		return elements;
	}
	public Element[] getElementsByTagNameNS(String name, String namespaceURI) {
		NodeList list = scope.getElementsByTagNameNS(name,namespaceURI);
		Element[] elements = new Element[list.getLength()];
		for (int n = 0; n < list.getLength(); n++) {elements[n] = (Element)list.item(n);}
		return elements;		
	}
	public Attr[] getAttributes() {
		NamedNodeMap list = scope.getAttributes();
		Attr[] attrs = new Attr[list.getLength()];
		for (int n = 0; n < list.getLength(); n++) {attrs[n] = (Attr)list.item(n);}
		return attrs;
	}
	public Element getScope() { return scope; }
	public Document getDocument() { return scopeDom; }
	
    public String getPrefixForNamespaceURI(String namespaceURI) {
        if (scope.getNodeType() == scope.ELEMENT_NODE) {
            NamedNodeMap map = scope.getAttributes() ;
            int length = map.getLength() ;
            for (int i = 0 ; i < length ; i++) {
                Attr attr = (Attr)map.item(i) ;
                String name ;
                if ((name = attr.getName()).startsWith(XMLNS_) &&
                    attr.getValue().equals(namespaceURI)) { // found
                    if (name.length() <= XMLNS_.length()) // "xmlns:" only
                        throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, XMLNS_+" attribute does not have prefix part") ;
                    return attr.getName().substring(XMLNS_.length()) ;
                }
            }
        }
        Node parent ;
        if ((parent = scope.getParentNode()) != null)
            return getPrefixForNamespaceURI(parent, namespaceURI) ;
        throw new DOMException(DOMException.NAMESPACE_ERR, "There is no namespace prefix for the namespace URI: "+namespaceURI) ;
    }

    private String getPrefixForNamespaceURI(Node node, String namespaceURI) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap map = ((Element)node).getAttributes() ;
            int length = map.getLength() ;
            for (int i = 0 ; i < length ; i++) {
                Attr attr = (Attr)map.item(i) ;
                String name ;
                if ((name = attr.getName()).startsWith(XMLNS_) &&
                    attr.getValue().equals(namespaceURI)) { // found
                    if (name.length() <= XMLNS_.length()) // "xmlns:" only
                        throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, XMLNS_+" attribute does not have prefix part") ;
                    return attr.getName().substring(XMLNS_.length()) ;
                }
            }
        }
        Node parent ;
        if ((parent = node.getParentNode()) != null)
            return getPrefixForNamespaceURI(parent, namespaceURI) ;
        throw new DOMException(DOMException.NAMESPACE_ERR, "There is no namespace prefix for the namespace URI: "+namespaceURI) ;
    }
}


class ErrorHandlerImpl implements ErrorHandler {
    int errorCount = 0 ;
    public int getErrorCount() { return errorCount ; }
    public void warning(SAXParseException ex) {
        System.err.println("[Warning] " + getLocationString(ex)+": "+ ex.getMessage()) ;
    }
    public void error(SAXParseException ex) {
        System.err.println("[Error] " + getLocationString(ex) + ": " + ex.getMessage()) ;
        errorCount++ ;
    }
    public void fatalError(SAXParseException ex) throws SAXException {
        System.err.println("[Fatal Error] " + getLocationString(ex) + ": " + ex.getMessage()) ;
        errorCount++ ;
        throw ex ;
    }
    
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer() ;
    
        String systemId = ex.getSystemId() ;
        if (systemId != null) {
            int index = systemId.lastIndexOf('/') ;
            if (index != -1) 
                systemId = systemId.substring(index + 1) ;
            str.append(systemId) ;
        }
        str.append(':') ;
        str.append(ex.getLineNumber()) ;
        str.append(':') ;
        str.append(ex.getColumnNumber()) ;
    
        return str.toString() ;
    }
}
