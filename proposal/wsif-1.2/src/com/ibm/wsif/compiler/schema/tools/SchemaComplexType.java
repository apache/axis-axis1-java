// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Processed information of a XML Schema complexType
 * Creation date: (5/29/00 1:55:51 AM)
 * @author: Tian Zhao (tzhao@cs.purdue.edu)
 */

import java.util.Vector;
//import org.apache.soap.util.xml.*;
import javax.wsdl.QName;

public class SchemaComplexType implements SchemaType {

	// list of attributes
	// Ignore: id, content, block, final in restriction or derived by restriction
	
	private String name;

	// if derived by restriction, then base is not null
	
	private QName base;
	private boolean isAbstract = false;
	private boolean isFinal = false;

	private Vector children;
	private String targetURI;

/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:13:26 PM)
 */
public SchemaComplexType(String name, QName base, boolean isAbstract, boolean isFinal, Vector children, String targetURI) {

	this.name = name;
	this.base = base;
	this.isAbstract = isAbstract;
	this.isFinal = isFinal;
	this.children = children;
	this.targetURI = targetURI;
	
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:15:20 PM)
 * @return org.apache.soap.util.xml.QName
 */
public QName getBase() {
	return base;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:31:14 PM)
 * @return java.util.Vector
 */
public Vector getChildren() {
	return children;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:29:26 PM)
 * @return int
 */
public int getElementType() {
	return COMPLEXTYPE;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:14:57 PM)
 * @return java.lang.String
 */
public String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/00 5:06:30 PM)
 * @return java.lang.String
 */
public String getTargetURI() {
	
	return targetURI;
	
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:15:40 PM)
 * @return boolean
 */
public boolean isAbstract() {
	return isAbstract;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:16:16 PM)
 * @return boolean
 */
public boolean isFinal() {
	return isFinal;
}
}
