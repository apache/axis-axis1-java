package org.apache.axis.util.xml;

import java.io.*;
import org.w3c.dom.*;

public class DOMPrinter
{
	protected StringWriter sout;
	protected PrintWriter out;
	protected boolean canonical;
	
	public DOMPrinter(boolean canonical) {
		sout = new StringWriter();
		out = new PrintWriter(sout);
		this.canonical = canonical;
	}
	
	public String writeToString(Document dom) {
		print(dom);
		String ret = sout.getBuffer().toString();
		sout.flush();
		return ret;
	}

	public String writeToString(Element element) {
		print(element);
		String ret = sout.getBuffer().toString();
		sout.flush();
		return ret;
	}
	
	public void print(Node node) {
		if (node == null) { return; }
		
		int type = node.getNodeType();
		switch (type) {
		case Node.DOCUMENT_NODE: {
			out.print("<?xml version=\"1.0\" ?>");
			NodeList children = node.getChildNodes();
			for (int n = 0; n < children.getLength(); n++) {print(children.item(n));}
			break; }
		case Node.ELEMENT_NODE: {
			out.print("<");
			out.print(node.getNodeName());
			Attr[] attrs = getAttributes(node);
			for (int n = 0; n < attrs.length; n++) {
				Attr attr = attrs[n];
				out.print(" ");
				out.print(attr.getNodeName());
				out.print("=\"");
				out.print(normalize(attr.getNodeValue()));
				out.print("\"");
			}
			
			if (!canonical && !node.hasChildNodes()) out.print("/");
			
			out.print(">");
			NodeList children = node.getChildNodes();
			if (children != null) {
				for (int n = 0; n < children.getLength(); n++) {print(children.item(n));}
			}
			break; }
		case Node.ENTITY_REFERENCE_NODE: {
			if (canonical) {
				NodeList children = node.getChildNodes();
				if ( children != null ) {
					for ( int n = 0; n < children.getLength(); n++ ) {print(children.item(n));}
				}
			} else {
				out.print("&");
				out.print(node.getNodeName());
				out.print(";");
			}
			break; }
		case Node.CDATA_SECTION_NODE: {
			if (canonical) {
				out.print(normalize(node.getNodeValue()));
			} else {
				out.print("<![CDATA[");
				out.print(node.getNodeValue());
				out.print("]]>");
			}
			break; }
		case Node.TEXT_NODE: {
			out.print(normalize(node.getNodeValue()));
			break; }
		case Node.PROCESSING_INSTRUCTION_NODE: {
			out.print("<?");
			out.print(node.getNodeName());
			String data = node.getNodeValue();
			if ( data != null && data.length() > 0 ) {
				out.print(" ");
				out.print(data);
			}
			out.println("?>");
			break; }
		}

		if (canonical || (!canonical && node.hasChildNodes())) {
		if ( type == Node.ELEMENT_NODE ) {
			out.print("</");
			out.print(node.getNodeName());
			out.print(">");
		}
		}
	}
	
	private Attr[] getAttributes(Node node) {
		NamedNodeMap list = node.getAttributes();
		Attr[] attrs = new Attr[list.getLength()];
		for (int n = 0; n < list.getLength(); n++) {attrs[n] = (Attr)list.item(n);}
		return attrs;
	}
	
    protected String normalize(String s) {
        StringBuffer str = new StringBuffer();

        int len = (s != null) ? s.length() : 0;
        for ( int i = 0; i < len; i++ ) {
            char ch = s.charAt(i);
            switch ( ch ) {
            case '<': {
                    str.append("&lt;");
                    break;
                }
            case '>': {
                    str.append("&gt;");
                    break;
                }
            case '&': {
                    str.append("&amp;");
                    break;
                }
            case '"': {
                    str.append("&quot;");
                    break;
                }
            case '\r':
            case '\n': {
                    if ( canonical ) {
                        str.append("&#");
                        str.append(Integer.toString(ch));
                        str.append(';');
                        break;
                    }
                }
            default: {
                    str.append(ch);
                }
            }
        }
        return(str.toString());
    }

}
