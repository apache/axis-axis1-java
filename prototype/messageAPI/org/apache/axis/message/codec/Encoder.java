package org.apache.axis.message.codec;

import org.w3c.dom.*;

public class Encoder
{
	private TypeCodecRegistry codecs;
	
	public Encoder(TypeCodecRegistry codecs) { this.codecs = codecs; }
	
	public Element[] encode(Document dom, Object[] values) throws IllegalArgumentException {
		Element[] elements = new Element[values.length];
		
		for (int n = 0; n < values.length; n++) {
			TypeCodec codec = codecs.queryCodec(values[n].getClass());
			elements[n] = codec.encode(dom, values[n]);
		}
		return elements;
	}
	
	public Element[] encode(Document dom, Object[] values, TypeCodecRegistry codecs) throws IllegalArgumentException  {
		Element[] elements = new Element[values.length];
		
		for (int n = 0; n < values.length; n++) {
			TypeCodec codec = codecs.queryCodec(values[n].getClass());
			elements[n] = codec.encode(dom, values[n]);
		}
		return elements;		
	}
	
	public Element encode(Document dom, Object value) throws IllegalArgumentException  {
		TypeCodec codec = codecs.queryCodec(value.getClass());
		return codec.encode(dom, value);
	}
	
	public Element encode(Document dom, Object value, TypeCodecRegistry codecs) throws IllegalArgumentException  {
		TypeCodec codec = codecs.queryCodec(value.getClass());
		return codec.encode(dom, value);		
	}
	
	public Element encode(Document dom, Object value, TypeCodec codec) throws IllegalArgumentException  {
		return codec.encode(dom, value);
	}

}
