// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Insert the type's description here.
 * Creation date: (6/21/00 5:32:11 PM)
 * @author: Tian Zhao (tzhao@cs.purdue.edu)
 */

//import org.apache.soap.util.xml.*;
import javax.wsdl.QName;
 
public class SchemaAttribute implements SchemaType {

	//Ignore: ID, use, value, or form
	
	private String name;
	private QName ref;
	private QName type;

	private SchemaSimpleType child;
	private String targetURI;
		
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:40:04 PM)
 */
public SchemaAttribute(String name, QName ref, QName type, SchemaSimpleType child, String targetURI) {
	
	this.name = name;
	this.ref = ref;
	this.type = type;
	this.child = child;
	this.targetURI = targetURI;
		
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 6:48:04 PM)
 * @return com.ibm.wsif.compiler.schema.newtools.SchemaSimpleType
 */
public SchemaSimpleType getChild() {
	return child;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:41:20 PM)
 * @return int
 */
public int getElementType() {
	return ATTRIBUTE;
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
 * Creation date: (6/21/00 5:42:49 PM)
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
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 5:43:08 PM)
 * @return boolean
 */
public QName getType() {
	return type;
}
}
