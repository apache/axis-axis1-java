// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.jms;

import java.io.Serializable;

import javax.wsdl.QName;
import javax.wsdl.extensions.ExtensibilityElement;

/**
 * WSDL Jms Binding extension.
 * This class holds one attribute. It does not validate it.
 * 
 * @author Mark Whitlock
 */
public class JmsAttribute implements ExtensibilityElement, Serializable {

	protected QName fieldElementType = JmsBindingConstants.Q_ELEM_JMS_ATTRIBUTE;
	// Uses the wrapper type so we can tell if it was set or not.
	protected Boolean fieldRequired = null;

	protected String fieldName;
	protected String fieldPart;

	/**
	 * accessors
	 */
	public String getName() {
		return fieldName;
	}
	
	public String getPart() {
		return fieldPart;
	}

	/**
	   * mutators
	   */
	public void setName(String rhs) {
		fieldName = rhs;
	}

	public void setPart(String rhs) {
		fieldPart = rhs;
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

		strBuf.append("\nname=" + fieldName);		
		strBuf.append(
			"\npart=" + fieldPart);

		return strBuf.toString();
	}
}