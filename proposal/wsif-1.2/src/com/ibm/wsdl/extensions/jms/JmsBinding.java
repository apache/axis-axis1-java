// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.jms;

import java.io.Serializable;

import javax.wsdl.QName;
import javax.wsdl.extensions.ExtensibilityElement;

/**
 * WSDL Jms binding extension
 * 
 * @author <a href="mailto:ake@de.ibm.com">Hermann Akermann</a>
 */
public class JmsBinding implements ExtensibilityElement, Serializable {

	protected QName fieldElementType = JmsBindingConstants.Q_ELEM_JMS_BINDING;
	
	// Uses the wrapper type so we can tell if it was set or not.
	protected Boolean fieldRequired = null;
	
	protected int fieldJmsMessageType = JmsBindingConstants.MESSAGE_TYPE_NOTSET;

	
	public static String JmsMessageTypeAsString(int type ) {
		String str = "NOTSET";
		
		if (type == JmsBindingConstants.MESSAGE_TYPE_BYTEMESSAGE) {
			str = "ByteMessage";
		} else if (type == JmsBindingConstants.MESSAGE_TYPE_MAPMESSAGE) {
			str = "MapMessage"; 	 
		} else if (type == JmsBindingConstants.MESSAGE_TYPE_OBJECTMESSAGE) {
			str = "ObjectMessage";
		} else if (type == JmsBindingConstants.MESSAGE_TYPE_STREAMMESSAGE) {
			str = "StreamMessage";		
		} else if (type == JmsBindingConstants.MESSAGE_TYPE_TEXTMESSAGE) {
			str = "TextMessage";	
		}		
		
		return str;
	}
	
	/**
	   * accessors
	   */
	public int getJmsMessageType() {
		return fieldJmsMessageType;
	}
	

	/**
	     * mutators
	     */
	public void setJmsMessageType(int messageType) {
		fieldJmsMessageType = messageType;
	}

	/**
	 * @see ExtensibilityElement#setElementType(QName)
	 */
	public void setElementType(QName elementType) {
		fieldElementType = elementType;
	}

	/**
	 * @see ExtensibilityElement#getElementType()
	 */
	public QName getElementType() {
		return fieldElementType;
	}

	/**
	 * @see ExtensibilityElement#setRequired(Boolean)
	 */
	public void setRequired(Boolean required) {
		fieldRequired = required;
	}

	/**
	 * @see ExtensibilityElement#getRequired()
	 */
	public Boolean getRequired() {
		return fieldRequired;
	}

	public String toString() {
		StringBuffer strBuf = new StringBuffer(super.toString());

		strBuf.append("\nJmsBinding (" + fieldElementType + "):");
		strBuf.append("\nrequired=" + fieldRequired);

		strBuf.append("\nMessagType= " + fieldJmsMessageType);

		return strBuf.toString();
	}
}