package org.apache.axis.message.codec;

import org.w3c.dom.*;
import org.apache.axis.util.xml.QName;

public interface TypeCodec
{
	public Element encode(Document dom, Object value);
	public Element encode(Document dom, Object value, String name);
	public Object decode(Element element);
}
