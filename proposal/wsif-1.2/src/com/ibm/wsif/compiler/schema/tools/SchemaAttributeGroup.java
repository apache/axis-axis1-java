// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Processed information of a XML Schema attribute group
 * Creation date: (5/29/00 1:55:51 AM)
 * @author: Tian Zhao (tzhao@cs.purdue.edu)
 */

import java.util.Vector;
//import org.apache.soap.util.xml.*;
import javax.wsdl.QName;

public class SchemaAttributeGroup implements SchemaType {

	// Ignore: id or attributeGroupAttrs
	
	private String name;
	private QName ref;
	private Vector children;
	private String targetURI;

/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:13:26 PM)
 */
public SchemaAttributeGroup(String name, QName ref, Vector children, String targetURI) {

	this.name = name;
	this.ref = ref;
	this.children = children;
	this.targetURI = targetURI;
	
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
	return ATTRIBUTEGROUP;
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
 * Creation date: (6/21/00 5:15:20 PM)
 * @return org.apache.soap.util.xml.QName
 */
public QName getRef() {
	return ref;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/00 5:06:30 PM)
 * @return java.lang.String
 */
public String getTargetURI() {
	
	return targetURI;
	
}
}
