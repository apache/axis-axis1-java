// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Insert the type's description here.
 * Creation date: (6/21/00 5:21:48 PM)
 * @author: Tian Zhao (tzhao@cs.purdue.edu)
 */
public interface SchemaType {

	public static final int COMPLEXTYPE = 0;
	public static final int SIMPLETYPE = 1;
	public static final int ELEMENT = 2;
	public static final int ATTRIBUTE = 3;
	public static final int ATTRIBUTEGROUP = 4;
	public static final int GROUP = 5;
	public static final int ANY = 6;
	public static final int ANYATTRIBUTE = 7;
	public static final int ALL = 8;
	public static final int CHOICE = 9;
	public static final int SEQUENCE = 10;

/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:26:52 PM)
 * @return int
 */
int getElementType();
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 3:58:18 PM)
 * @return java.lang.String
 */
String getName();
/**
 * Insert the method's description here.
 * Creation date: (6/25/00 5:04:56 PM)
 * @return java.lang.String
 */
String getTargetURI();
}
