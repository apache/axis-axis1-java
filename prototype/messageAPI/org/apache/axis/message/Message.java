package org.apache.axis.message;

import java.io.Reader;
import java.io.Serializable;

public class Message implements Serializable {
	java.io.Reader content;	
	public Message(Reader in) {content = in; }	
	public void setContent(Reader in) {content = in;}
	public Reader getContent() { return content; }
}