// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Data structure to hold class fields for schema to Java output files.
 * Creation date: (5/29/00 1:55:51 AM)
 * @author: Tian Zhao (tzhao@cs.purdue.edu)
 */
public class ClassField {
	
	String fieldType;
	boolean isArray;
	String fieldName;
/**
 * Insert the method's description here.
 * Creation date: (5/29/00 1:59:29 AM)
 * @param field java.lang.String
 * @param name java.lang.String
 * @param array boolean
 */
public ClassField(String fieldType, String fieldName, boolean isArray) {

	if (fieldType == null)
		this.fieldType = "";
	else
		this.fieldType = fieldType;
	this.fieldName = fieldName;
	this.isArray = isArray;	
}
}
