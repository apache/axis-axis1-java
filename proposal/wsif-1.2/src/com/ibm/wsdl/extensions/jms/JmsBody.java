// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.jms;

import java.io.Serializable;
import javax.wsdl.QName;
import javax.wsdl.extensions.ExtensibilityElement;

/**
 * WSDL Jms body extension
 * 
 * @author <a href="mailto:ake@de.ibm.com">Hermann Akermann</a>
 */

public class JmsBody implements ExtensibilityElement, Serializable {
	
	protected QName fieldElementType = JmsBindingConstants.Q_ELEM_JMS_BODY;
	
	// Uses the wrapper type so we can tell if it was set or not.
	protected Boolean fieldRequired = null;
	
	protected String fieldParts;
	protected String fieldUse;
	protected String fieldNameSpace;
	protected String fieldEncodingStyle;


	public void setParts(String parts) {
		fieldParts = parts;
	}

	public String getParts() {
		return fieldParts;
	}

	public void setUse(String use) {
		fieldUse = use;
	}

	public String getUse() {
		return fieldUse;
	}

	public void setNameSpace(String ns) {
		fieldNameSpace = ns;
	}

	public String getNameSpace() {
		return fieldNameSpace;
	}

	public void setEncodingStyle(String es) {
		fieldEncodingStyle = es;
	}

	public String getEncodingStyle() {
		return fieldEncodingStyle;
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

		strBuf.append("\nJmsBody (" + fieldElementType + "):");
		strBuf.append("\nrequired=" + fieldRequired);

		strBuf.append("\nparts=" + fieldParts);		
		strBuf.append("\nuse=" + fieldUse);
		strBuf.append("\nnamespace=" + fieldNameSpace);
		strBuf.append("\nencodingstyle=" + fieldEncodingStyle);

		return strBuf.toString();
	}
}

