package org.apache.axis.message;

import org.w3c.dom.*;

public class DOMMessageWriter implements MessageWriter 
{
	Document dom;
	
	DOMMessageWriter(Message message) {
		DOMMessageReader domReader = new DOMMessageReader(message);
		dom = domReader.getDocument();
	}
	DOMMessageWriter(MessageReader message) {}
	DOMMessageWriter(Document dom) { this.dom = dom; }
		
	public void readWith(MessageReader reader) {
	}
	
	// I'm going to build this using a similar model as the .NET XMLWriter
}
