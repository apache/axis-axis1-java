package org.apache.axis.message;

import org.apache.axis.message.api.*;
import org.w3c.dom.*;

public interface CanonicalWriter extends MessageWriter
{
	public void writeTo(Message message);
	public void writeTo(MessageWriter writer);
	public void writeTo(Document dom);
	public void writeTo(Element element);
	public MessageEnvelope createEnvelope();
	public MessageElement createHeader();
	public MessageElement createBody();
	public MessageHeaderEntry createHeaderEntry(String name, String namespaceURI);
	public MessageHeaderEntry createHeaderEntry(String name, String namespaceURI, boolean mustUnderstand);
	public MessageHeaderEntry createHeaderEntry(String name, String namespaceURI, String actor);
	public MessageHeaderEntry createHeaderEntry(String name, String namespaceURI, boolean mustUnderstand, String actor);
	public MessageElementEntry createBodyEntry(String name, String namespaceURI);
	public MessageFault createFault(String first, String second, Object details);
	public void appendHeaderEntry(MessageHeaderEntry entry);
	public void appendBodyEntry(MessageElementEntry entry);
	public void insertHeaderEntry(MessageHeaderEntry entry, MessageHeaderEntry before);
	public void insertBodyEntry(MessageElementEntry entry, MessageElementEntry before);
	public void removeHeaderEntry(MessageHeaderEntry entry);
	public void removeBodyEntry(MessageElementEntry entry);
	public void setFault(MessageFault fault);
}
