// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.format;

import javax.wsdl.*;
import javax.wsdl.extensions.*;

public class TypeMap implements java.io.Serializable, ExtensibilityElement {
	protected QName fieldElementType = FormatBindingConstants.Q_ELEM_FORMAT_BINDING_MAP;
	private QName fieldTypeName;
	private QName fieldElementName;
	private String fieldFormatType;
	/**
	 * ConnectorAddress constructor comment.
	 */
	public TypeMap() {
		super();
	}
	/**
	  * Get the type of this extensibility element.
	  *
	  * @return the extensibility element's type
	  */
	public QName getElementType() {
		return this.fieldElementType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/19/2001 9:27:15 PM)
	 * @return java.lang.String
	 */
	public String getFormatType() {
		return fieldFormatType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/19/2001 1:30:38 PM)
	 * @return java.lang.String
	 */
	public QName getTypeName() {
		return fieldTypeName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (7/20/2001 11:07:00 AM)
	 * @return java.lang.String
	 */
	public QName getElementName() {
		return fieldElementName;
	}
	/**
	  * Get whether or not the semantics of this extension
	  * are required. Relates to the wsdl:required attribute.
	  */
	public Boolean getRequired() {
		return null;
	}
	/**
	  * Set the type of this extensibility element.
	  *
	  * @param elementType the type
	  */
	public void setElementType(QName aElementType) {

		fieldElementType = aElementType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/19/2001 9:27:15 PM)
	 * @param newFormatType java.lang.String
	 */
	public void setFormatType(java.lang.String newFormatType) {
		fieldFormatType = newFormatType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/19/2001 1:30:38 PM)
	 * @param newPart java.lang.String
	 */
	public void setTypeName(QName newTypeName) {
		fieldTypeName = newTypeName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (7/20/2001 11:07:00 AM)
	 * @param newPosition java.lang.String
	 */
	public void setElementName(QName newElementName) {
		fieldElementName = newElementName;
	}
	/**
	  * Set whether or not the semantics of this extension
	  * are required. Relates to the wsdl:required attribute.
	  */
	public void setRequired(Boolean required) {
	}
}