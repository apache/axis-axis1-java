// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.jms;

import java.io.Serializable;

import javax.wsdl.QName;
import javax.wsdl.extensions.ExtensibilityElement;

/**
 * WSDL Jms service-port extension
 * 
 * @author <a href="mailto:ake@de.ibm.com">Hermann Akermann</a>
 */
public class JmsHeader implements ExtensibilityElement, Serializable {

	protected QName fieldElementType = JmsBindingConstants.Q_ELEM_JMS_ADDRESS;
	// Uses the wrapper type so we can tell if it was set or not.
	protected Boolean fieldRequired = null;

	protected String fieldValue;

	/**
	 * accessors
	 */
	public String getValue() {
		return fieldValue;
	}
	
	/**
	   * mutators
	   */
	public void setValue(String rhs) {
		fieldValue = rhs;
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

		strBuf.append("\nJavaAddress (" + fieldElementType + "):");
		strBuf.append("\nrequired=" + fieldRequired);

		strBuf.append("\nvalue=" + fieldValue);		

		return strBuf.toString();
	}
}