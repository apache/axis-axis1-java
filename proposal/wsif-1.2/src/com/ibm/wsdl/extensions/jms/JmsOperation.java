// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.jms;

import java.io.Serializable;

import javax.wsdl.QName;
import javax.wsdl.extensions.ExtensibilityElement;

/**
 * WSDL Jms extension
 * 
 * @author <a href="mailto:ake@de.ibm.com">Hermann Akermann</a>
 */
public class JmsOperation implements ExtensibilityElement, Serializable {

	protected QName fieldElementType = JmsBindingConstants.Q_ELEM_JMS_OPERATION;
	// Uses the wrapper type so we can tell if it was set or not.
	protected Boolean fieldRequired = null;

   	
	/**
	 * @see ExtensibilityElement#getElementType()
	 */
	public QName getElementType() {
		return fieldElementType;
	}

	
	/**
	 * @see ExtensibilityElement#getRequired()
	 */
	public Boolean getRequired() {
		return fieldRequired;
	}

    /**
	 * @see ExtensibilityElement#setElementType(QName)
	 */
	public void setElementType(QName elementType) {
		fieldElementType = elementType;
	}
	
	
	/**
	 * @see ExtensibilityElement#setRequired(Boolean)
	 */
	public void setRequired(Boolean required) {
		fieldRequired = required;
	}

	/**
	 * helper
	 */
	public String toString() {
		StringBuffer strBuf = new StringBuffer(super.toString());

		strBuf.append("\nJmsOperation (" + fieldElementType + "):");
		strBuf.append("\nrequired=" + fieldRequired);

		return strBuf.toString();
	}

}