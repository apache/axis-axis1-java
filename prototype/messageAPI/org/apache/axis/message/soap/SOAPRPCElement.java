package org.apache.axis.message.soap;

import org.apache.axis.message.*;
import org.apache.axis.message.api.*;
import org.apache.axis.message.codec.*;
import org.apache.axis.util.xml.DOMWriter;
import org.w3c.dom.*;

public class SOAPRPCElement extends SOAPElement implements RPCElement 
{
	Decoder decoder;
	Encoder encoder;
	
	public SOAPRPCElement(Element entity) { super(entity);}
	public SOAPRPCElement(Document dom) { super(dom);}
	public SOAPRPCElement(Document dom, String namespaceURI, String prefix, String name) {super(dom, namespaceURI, prefix, name);}
	public SOAPRPCElement(Element entity, Decoder decoder, Encoder encoder) { super(entity); this.decoder = decoder; this.encoder = encoder; }
	
	public String getMethodName() {
		return getDOMEntity().getFirstChild().getLocalName();
	}
	
	public String getMethodNamespaceURI() {
		return getDOMEntity().getFirstChild().getNamespaceURI();
	}
	
	public MessageElementEntry setMethodName(String name, String namespaceURI) {
		SOAPWriter writer = new SOAPWriter(entity);
		MessageElementEntry entry = writer.createBodyEntry(name, namespaceURI);
		appendEntry(entry);
		return entry;
	}
	
// still working on the SOAP Encoder/Decoder 
	
	public Object[] getArgs() {
		reader.moveDown();
		Object[] values = decoder.decode(reader.getChildren());
		reader.moveUp();
		return values;
	}
	
	public Object getArg(String name) {
		reader.moveDown();
		Object value = decoder.decode(reader.getElementsByTagName(name)[0]);
		reader.moveUp();
		return value;
	}
	
	public Object getArg(String name, TypeCodec codec) {
		reader.moveDown();
		Object value = codec.decode(reader.getElementsByTagName(name)[0]);
		reader.moveUp();
		return value;
	}
	
	public MessageElementEntry addArg(String name, String namespaceURI, Object value) {
		reader.moveDown();
		DOMWriter writer = new DOMWriter(reader.getScope());
		writer.we(encoder.encode(getDOMEntity().getOwnerDocument(), value));
		reader.moveUp();
		return null;
	}
	
	public MessageElementEntry addArg(String name, String namespaceURI, Object value, TypeCodec codec) {
		reader.moveDown();
		DOMWriter writer = new DOMWriter(reader.getScope());
		writer.we(codec.encode(getDOMEntity().getOwnerDocument(), value));
		reader.moveUp();
		return null;
	}
}
