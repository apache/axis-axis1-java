// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Insert the type's description here.
 * Creation date: (6/21/00 6:42:07 PM)
 * @author: Tian Zhao (tzhao@cs.purdue.edu)
 */
public class SchemaAny implements SchemaType {

	// Ignore: namespace, processContents, or anyAttrs
	
	private boolean isArray = false;
	private String targetURI;
	
/**
 * SchemaAny constructor comment.
 */
public SchemaAny(boolean isArray, String targetURI) {
	
	this.isArray = isArray;
	this.targetURI = targetURI;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 6:45:17 PM)
 * @return int
 */
public int getElementType() {
	return ANY;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 3:58:59 PM)
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
 * Creation date: (6/24/00 3:04:31 PM)
 * @return boolean
 */
public boolean isArray() {
	return isArray;
}
}
