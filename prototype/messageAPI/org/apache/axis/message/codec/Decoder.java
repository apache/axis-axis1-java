package org.apache.axis.message.codec;

import org.w3c.dom.*;
import org.apache.axis.message.soap.*;
import org.apache.axis.util.xml.QName;
import org.apache.axis.util.xml.DOMReader;

public class Decoder
{
	
	TypeCodecRegistry codecs;
	boolean useExplicit = true;
	
	public Decoder(TypeCodecRegistry codecs, boolean useExplicitTyping) { this.codecs = codecs; this.useExplicit = useExplicitTyping;}
	
	
	public Object[] decode(Element[] elements) throws IllegalArgumentException {
		Object[] values = new Object[elements.length];
		for (int n = 0; n < elements.length; n++) {
			TypeCodec codec = codecs.queryCodec(getElementType(elements[n]));
			values[n] = codec.decode(elements[n]);
		}
		return values;
	}
	
	public Object[] decode(Element[] elements, TypeCodecRegistry codecs) throws IllegalArgumentException  {
		Object[] values = new Object[elements.length];
		for (int n = 0; n < elements.length; n++) {
			TypeCodec codec = codecs.queryCodec(getElementType(elements[n]));
			values[n] = codec.decode(elements[n]);
		}
		return values;
	}
	
	public Object decode(Element element) throws IllegalArgumentException  {
		TypeCodec codec = codecs.queryCodec(getElementType(element));
		return codec.decode(element);
	}
	
	public Object decode(Element element, TypeCodecRegistry codecs) throws IllegalArgumentException  {
		TypeCodec codec = codecs.queryCodec(getElementType(element));
		return codec.decode(element);
	}
	
	public Object decode(Element element, TypeCodec codec) throws IllegalArgumentException  {
		return codec.decode(element);
	}
	
	private QName getElementType(Element elem) {
		String type = null;
		String namespaceURI = null;
		if (useExplicit) {
			type = clean(getXSDType(elem));
			namespaceURI = Constants.URI_SCHEMA_XSD;
		}
		if (type == null) {
			type = clean(elem.getNodeName());
			namespaceURI = elem.getNamespaceURI();
		}
		return new QName(namespaceURI, type);
	}

    private String getXSDType(Element elem) {
		try {
			DOMReader reader = new DOMReader(elem.getOwnerDocument());
			String prefix = reader.getPrefixForNamespaceURI(Constants.URI_SCHEMA_XSI);
			if (prefix.length() != 0) prefix += ":";		
			String xsdType = elem.getAttribute(prefix + Constants.ATTR_TYPE) ;
			return "".equals(xsdType) ? null : xsdType ;
		} catch (Exception e) {return null;}
    }
	
	private String clean(String type) {
		if (type == null) {return null;}
		char[] typec = type.toCharArray();
		for (int n = 0; n < typec.length; n++) {
			switch (typec[n]) {
			case ':' : return type.substring(n + 1);
			}
		}
		return type;
	}
}
