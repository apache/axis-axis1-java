package org.apache.axis.message.soap.codec;

import org.apache.axis.message.codec.*;
import org.apache.axis.message.soap.*;
import org.apache.axis.util.xml.*;
import org.w3c.dom.*;

public class SOAPCodec implements TypeCodec
{	
	public SOAPCodec() {}
	
	private static final String xsd_string = "string";
	private static final String xsd_boolean = "boolean";
	private static final String xsd_double = "double";
	private static final String xsd_float = "float";
	private static final String xsd_long = "long";
	private static final String xsd_int = "int";
	private static final String xsd_short = "short";
	private static final String xsd_byte = "byte";
	
	public Element encode(Document dom, Object value) {
		try {
		DOMReader reader = new DOMReader(dom);
		String prefix = reader.getPrefixForNamespaceURI(Constants.URI_SOAP_ENC);
		if (prefix.length() != 0) { prefix += ":"; }

		if (value.getClass() == String.class )
			return encode(dom, value, prefix + xsd_string);
		if (value.getClass() == Boolean.class )
			return encode(dom, value, prefix + xsd_boolean);
		if (value.getClass() == Double.class )
			return encode(dom, value, prefix + xsd_double);
		if (value.getClass() == Float.class )
			return encode(dom, value, prefix + xsd_float);
		if (value.getClass() == Long.class )
			return encode(dom, value, prefix + xsd_long);
		if (value.getClass() == Integer.class )
			return encode(dom, value, prefix + xsd_int);
		if (value.getClass() == Short.class )
			return encode(dom, value, prefix + xsd_short);
		if (value.getClass() == Byte.class )
			return encode(dom, value, prefix + xsd_byte);
		return null;
		} catch (Exception e) {
			System.out.println(e.getClass());
			return null;
		}
	}
	
	public Element encode(Document dom, Object value, String name) {
		
		Element element = dom.createElement(name);
		DOMWriter writer = new DOMWriter(element);
		DOMReader reader = new DOMReader(dom);
		String prefix = reader.getPrefixForNamespaceURI(Constants.URI_SOAP_ENC);
		String xsi = reader.getPrefixForNamespaceURI(Constants.URI_SCHEMA_XSI);
		String xsd = reader.getPrefixForNamespaceURI(Constants.URI_SCHEMA_XSD);
		if (prefix.length() != 0) { prefix += ":"; }
		if (xsi.length() != 0) { xsi += ":"; }
		if (xsd.length() != 0) { xsd += ":"; }
		
		if (value.getClass() == String.class) {
			if (!name.equals(prefix + xsd_string)) 
				writer.wans(xsi + "type", xsd + xsd_string, Constants.URI_SCHEMA_XSI);
			writer.wv(value.toString());
		}
		if (value.getClass() == Boolean.class) {
			if (!name.equals(prefix + xsd_boolean))
				writer.wans(xsi + "type", xsd + xsd_boolean, Constants.URI_SCHEMA_XSI);
			writer.wv(value.toString());
		}
		if (value.getClass() == Double.class) {
			if (!name.equals(prefix + xsd_double))
				writer.wans(xsi + "type", xsd + xsd_double, Constants.URI_SCHEMA_XSI);
			writer.wv(value.toString());
		}
		if (value.getClass() == Float.class) {
			if (!name.equals(prefix + xsd_float))
				writer.wans(xsi + "type", xsd + xsd_float, Constants.URI_SCHEMA_XSI);
			writer.wv(value.toString());
		}
		if (value.getClass() == Long.class) {
			if (!name.equals(prefix + xsd_long))
				writer.wans(xsi + "type", xsd + xsd_long, Constants.URI_SCHEMA_XSI);
			writer.wv(value.toString());
		}
		if (value.getClass() == Integer.class) {
			if (!name.equals(prefix + xsd_int))
				writer.wans(xsi + "type", xsd + xsd_int, Constants.URI_SCHEMA_XSI);
			writer.wv(value.toString());
		}
		if (value.getClass() == Short.class) {
			if (!name.equals(prefix + xsd_short))
				writer.wans(xsi + "type", xsd + xsd_short, Constants.URI_SCHEMA_XSI);
			writer.wv(value.toString());
		}
		if (value.getClass() == Byte.class) {
			if (!name.equals(prefix + xsd_byte))
				writer.wans(xsi + "type", xsd + xsd_byte, Constants.URI_SCHEMA_XSI);
			writer.wv(value.toString());
		}
		return element;
	}
	
	public Object decode(Element element) {
		Object obj;
		DOMReader reader = new DOMReader(element.getOwnerDocument());
		String prefix = reader.getPrefixForNamespaceURI(Constants.URI_SOAP_ENC);
		String xsi = reader.getPrefixForNamespaceURI(Constants.URI_SCHEMA_XSI);
		String xsd = reader.getPrefixForNamespaceURI(Constants.URI_SCHEMA_XSD);
		if (prefix.length() != 0) { prefix += ":"; }
		if (xsi.length() != 0) { xsi += ":"; }
		if (xsd.length() != 0) { xsd += ":"; }
		String name;
		
		try {
		String eprefix = element.getPrefix();
		if (eprefix.length() != 0) { eprefix += ":"; }
		name = eprefix + element.getLocalName();
		} catch (Exception e) { name = element.getNodeName();}	
		
		if (name.equals(prefix + xsd_string))	{return new String(element.getFirstChild().getNodeValue());}
		if (name.equals(prefix + xsd_boolean))	{return new Boolean(element.getFirstChild().getNodeValue());}
		if (name.equals(prefix + xsd_double))	{return new Double(element.getFirstChild().getNodeValue());}
		if (name.equals(prefix + xsd_float))	{return new Float(element.getFirstChild().getNodeValue());}
		if (name.equals(prefix + xsd_long))		{return new Long(element.getFirstChild().getNodeValue());}
		if (name.equals(prefix + xsd_int))		{return new Integer(element.getFirstChild().getNodeValue());}
		if (name.equals(prefix + xsd_short))	{return new Short(element.getFirstChild().getNodeValue());}
		if (name.equals(prefix + xsd_byte))		{return new Byte(element.getFirstChild().getNodeValue());}
		
		if (element.getAttribute(xsi + "type").equals(xsd + xsd_string))  { return new String(element.getFirstChild().getNodeValue()); }
		if (element.getAttribute(xsi + "type").equals(xsd + xsd_boolean)) { return new Boolean(element.getFirstChild().getNodeValue()); }
		if (element.getAttribute(xsi + "type").equals(xsd + xsd_double))  { return new Double(element.getFirstChild().getNodeValue()); }
		if (element.getAttribute(xsi + "type").equals(xsd + xsd_float))   { return new Float(element.getFirstChild().getNodeValue()); }
		if (element.getAttribute(xsi + "type").equals(xsd + xsd_long))    { return new Long(element.getFirstChild().getNodeValue()); }
		if (element.getAttribute(xsi + "type").equals(xsd + xsd_int))     { return new Integer(element.getFirstChild().getNodeValue()); }
		if (element.getAttribute(xsi + "type").equals(xsd + xsd_short))   { return new Short(element.getFirstChild().getNodeValue()); }
		if (element.getAttribute(xsi + "type").equals(xsd + xsd_byte))    { return new Byte(element.getFirstChild().getNodeValue()); }
		
		return null;
	}
}
