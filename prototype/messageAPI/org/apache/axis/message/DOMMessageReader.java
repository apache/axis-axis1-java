package org.apache.axis.message;

import org.apache.axis.util.xml.DOMReader;
import org.w3c.dom.*;
import java.io.Reader;

public class DOMMessageReader implements MessageReader {
	Document dom;
	
	public DOMMessageReader() {}
	public DOMMessageReader(Message message) { read(message); }
	public DOMMessageReader(MessageWriter writer) { read(writer); }
	
	public void read(Message message) {
		try {
		dom = DOMReader.parse(message.getContent());
		} catch (Exception e) {}
	}
	
	public void read(MessageWriter writer) {
		// not implemented right now
	}
	
	public boolean validate() {
		// not implemented right now
		return false;
	}
	
	public void writeTo(MessageWriter writer) {
		// not implemented right now
	}
	
	public Document getDocument() { return dom; }
}
