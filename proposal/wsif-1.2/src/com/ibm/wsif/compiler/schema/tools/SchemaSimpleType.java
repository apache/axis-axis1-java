// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Insert the type's description here.
 * Creation date: (6/21/00 5:32:11 PM)
 * @author: Tian Zhao (tzhao@cs.purdue.edu)
 */

//import org.apache.soap.util.xml.*;
import javax.wsdl.QName;
 
public class SchemaSimpleType implements SchemaType {

	//Ignore: ID, abstract, derived by restriction, or all facets
	
	private String name;
	private QName base;
	private boolean isList;
	private String targetURI;
		
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:40:04 PM)
 */
public SchemaSimpleType(String name, QName base, boolean isList, String targetURI) {
	
	this.name = name;
	this.base = base;
	this.isList = isList;
	this.targetURI = targetURI;
		
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:42:49 PM)
 * @return org.apache.soap.util.xml.QName
 */
public QName getBase() {
	return base;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:41:20 PM)
 * @return int
 */
public int getElementType() {
	return SIMPLETYPE;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:42:29 PM)
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
 * Creation date: (6/21/00 5:43:08 PM)
 * @return boolean
 */
public boolean isList() {
	return isList;
}
}
