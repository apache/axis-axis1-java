package org.apache.axis.message.soap;

import org.apache.axis.message.api.*;
import org.w3c.dom.*;
import org.apache.axis.util.xml.*;

// this whole class not yet implemented
public class SOAPFault extends SOAPElementEntry implements MessageFault {
	
	SOAPFault(Element entity) { super(entity); }
	
    public void setFaultCode(String first, String second) {
	}
	
    public void setFaultString(String value) {
	}
	
    public void setFaultActor(String value) {
	}
	
    public void setDetail(Object detail) {
	}
	
    public String getFaultCode() {
		return null;
	}
	
    public String getFaultString() {
		return null;
	}
	
    public String getFaultActor() {
		return null;
	}
	
    public Object getDetail() {
		return null;
	}
	
    public void removeFaultCode() {
	}
	
    public void removeFaultString() {
	}
	
    public void removeFaultActor() {
	}
	
    public void removeDetail() {
	}
}
