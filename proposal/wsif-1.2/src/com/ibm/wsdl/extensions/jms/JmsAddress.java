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
public class JmsAddress implements ExtensibilityElement, Serializable {

	protected QName fieldElementType = JmsBindingConstants.Q_ELEM_JMS_ADDRESS;
	// Uses the wrapper type so we can tell if it was set or not.
	protected Boolean fieldRequired = null;

	protected String jmsVendorURL;
	protected String initCxtFact;
	protected String jndiProvURL;
	protected String destStyle;
	protected String jndiConnFactName;
	protected String jndiDestName;
	protected String jmsProvDestName;
	protected String jmsImplSpecURL;

	/**
	 * accessors
	 */
	public String getJmsVendorURL() {
		return jmsVendorURL;
	}
	
	public String getInitCxtFact() {
		return initCxtFact;
	}
	
	public String getJndiProvURL() {
		return jndiProvURL;
	}
	
	public String getDestStyle() {
		return destStyle;
	}
	
	public String getJndiConnFactName() {
		return jndiConnFactName;
	}
	
	public String getJndiDestName() {
		return jndiDestName;
	}
	
	public String getJmsProvDestName() {
		return jmsProvDestName;
	}
	
	public String getJmsImplSpecURL() {
		return jmsImplSpecURL;
	}
	
	/**
	   * mutators
	   */
	public void setJmsVendorURL(String rhs) {
		jmsVendorURL = rhs;
	}

	public void setInitCxtFact(String rhs) {
		initCxtFact = rhs;
	}

	public void setJndiProvURL(String rhs) {
		jndiProvURL = rhs;
	}

	public void setDestStyle(String rhs) {
		destStyle = rhs;
	}

	public void setJndiConnFactName(String rhs) {
		jndiConnFactName = rhs;
	}

	public void setJndiDestName(String rhs) {
		jndiDestName = rhs;
	}

	public void setJmsProvDestName(String rhs) {
		jmsProvDestName = rhs;
	}

	public void setJmsImplSpecURL(String rhs) {
		jmsImplSpecURL = rhs;
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

		strBuf.append("\nJmsAddress (" + fieldElementType + "):");
		strBuf.append("\nrequired=" + fieldRequired);

		strBuf.append("\njmsVendorURL="+jmsVendorURL==null?"null":jmsVendorURL);		
		strBuf.append("\ninitCxtFact="+initCxtFact==null?"null":initCxtFact);		
		strBuf.append("\njndiProvURL="+jndiProvURL==null?"null":jndiProvURL);		
		strBuf.append("\ndestStyle="+destStyle==null?"null":destStyle);		
		strBuf.append("\njndiConnFactName="+jndiConnFactName==null?"null":jndiConnFactName);		
		strBuf.append("\njndiDestName="+jndiDestName==null?"null":jndiDestName);		
		strBuf.append("\njmsProvDestName="+jmsProvDestName==null?"null":jmsProvDestName);		
		strBuf.append("\njmsImplSpecURL="+jmsImplSpecURL==null?"null":jmsImplSpecURL);		

		return strBuf.toString();
	}
}