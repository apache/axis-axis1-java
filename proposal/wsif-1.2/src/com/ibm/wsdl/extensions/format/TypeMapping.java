// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.format;
import javax.wsdl.*;
import javax.wsdl.extensions.*;

public class TypeMapping implements java.io.Serializable, ExtensibilityElement { 
	
	protected QName fieldElementType = FormatBindingConstants.Q_ELEM_FORMAT_BINDING;
	private String fieldEncoding;
	private String fieldStyle;
	protected java.util.List fieldTypeMaps = new java.util.Vector();

/**
 * 
 */
public TypeMapping() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/19/2001 2:59:59 PM)
 * @param newPartTypes java.util.List
 */
public void addMap(TypeMap typeMap) {
	fieldTypeMaps.add(typeMap);
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
 * Creation date: (6/19/2001 1:23:29 PM)
 * @return java.lang.String
 */
public java.lang.String getStyle() {
	return fieldStyle;
}

public java.lang.String getEncoding() {
	return fieldEncoding;
}


/**
 * Insert the method's description here.
 * Creation date: (6/19/2001 2:59:59 PM)
 * @return java.util.List
 */
public java.util.List getMaps() {
	return fieldTypeMaps;
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
	
	this.fieldElementType = aElementType;	
}
/**
 * Insert the method's description here.
 * Creation date: (6/19/2001 1:23:29 PM)
 * @param newEncodingStyle java.lang.String
 */
public void setStyle(java.lang.String newStyle) {
	fieldStyle = newStyle;
}

public void setEncoding(java.lang.String newEncoding) {
	fieldEncoding = newEncoding;
}
 /**
   * Set whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
public void setRequired(Boolean required) {}
}
