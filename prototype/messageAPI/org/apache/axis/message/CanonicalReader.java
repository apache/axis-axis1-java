package org.apache.axis.message;

import org.apache.axis.message.api.*;
import org.w3c.dom.*;

public interface CanonicalReader extends MessageReader
{
	public void read(Message message);
	public void read(MessageWriter writer);
	public void read(Document dom);
	public void read(Element element);
	public MessageEnvelope getEnvelope();
	public MessageHeaderEntry[] getHeaderEntries();
	public MessageElementEntry[] getBodyEntries();
	public MessageHeaderEntry getHeaderEntry(int index);
	public MessageElementEntry getBodyEntry(int index);
	public MessageFault getFault();
}
