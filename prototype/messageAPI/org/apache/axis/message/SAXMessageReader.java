package org.apache.axis.message;

import org.xml.sax.InputSource;
import java.io.Reader;

public class SAXMessageReader implements MessageReader 
{
	InputSource source;
	
	public SAXMessageReader() {}
	public SAXMessageReader(Message message) { read(message); }
	public SAXMessageReader(MessageWriter writer) { read(writer); }
	
	public void read(Message message) {
		Reader reader = message.getContent();
		source = new InputSource(reader);
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
	
	public InputSource getInputSource() { return source; }
}
