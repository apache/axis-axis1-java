// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Insert the type's description here.
 * Creation date: (6/21/00 6:35:05 PM)
 * @author: Tian Zhao (tzhao@cs.purdue.edu)
 */

import java.util.Vector;

public class SchemaMGS implements SchemaType {

	// Ignore: id, allAttrs|choiceAtts|sequenceAtts
	
	private boolean isArray=false;
	private int elementType;
	private Vector children;
	private String targetURI;
	 
 
public SchemaMGS (boolean isArray, int elementType, Vector children, String targetURI) {
	
	this.isArray = isArray;
	this.elementType = elementType;
	this.children = children;
	this.targetURI = targetURI;
	
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 10:00:01 PM)
 * @return java.util.Vector
 */
public Vector getChildren() {
	return children;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 6:40:26 PM)
 * @return int
 */
public int getElementType() {
	return elementType;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 3:59:15 PM)
 * @return java.lang.String
 */
public String getName() {
	return null;
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
 * Creation date: (6/21/00 6:41:10 PM)
 * @return boolean
 */
public boolean isArray() {
	return isArray;
}
}
