// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Insert the type's description here.
 * Creation date: (5/30/00 2:04:09 PM)
 * @author: Tian Zhao (tzhao@cs.purdue.edu)
 */

import java.util.*;

public class ClassFile {

	private Vector fields = new Vector();
	private Vector innerClasses = new Vector();
	String className; // This is a required field.
	String packageName; // By default, no package or super class name is required.
	String superClassName; 
	boolean isAbstract = false;
	boolean isFinal = false;
/**
 * Insert the method's description here.
 * Creation date: (5/30/00 2:52:00 PM)
 * @param cName java.lang.String
 * @param pName java.lang.String
 */
public ClassFile(String className) {
	
	this.className = className;
 
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/00 2:11:49 PM)
 * @param field schema2JavaVersion2.ClassField
 */
public void addField(ClassField field) {
	
	fields.addElement(field);
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/00 2:17:15 PM)
 * @param innerClass schema2JavaVersion2.ClassFile
 */
public void addInnerClass(ClassFile innerClass) {
	
	innerClasses.addElement(innerClass);	
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/00 2:48:41 PM)
 * @return schema2JavaVersion2.ClassField
 * @param i int
 */
public ClassField getField(int i) {

	return (ClassField) fields.elementAt(i);
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/00 2:18:37 PM)
 * @return int
 */
public int getFieldCount() {
	return fields.size();
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/00 2:49:54 PM)
 * @return schema2JavaVersion2.ClassFile
 * @param i int
 */
public ClassFile getInnerClass(int i) {
	return (ClassFile) innerClasses.elementAt(i);
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/00 2:19:05 PM)
 * @return int
 */
public int getInnerClassCount() {
	return innerClasses.size();
}
}
