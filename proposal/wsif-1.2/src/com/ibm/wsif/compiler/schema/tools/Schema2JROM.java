// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Convert XML schema file into Java class files.
 * Creation date: (5/29/00 1:55:51 AM)
 * @author: Tian Zhao (tzhao@cs.purdue.edu)
 * @author Nirmal Mukhi (nmukhi@us.ibm.com)
 */ 
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.apache.soap.*;
import org.apache.soap.util.xml.DOMUtils;
import com.ibm.wsif.compiler.schema.*;
import com.ibm.wsif.compiler.util.*;
import javax.wsdl.QName;

public class Schema2JROM {

    private Hashtable registry = new Hashtable ();
    private Hashtable namespaceRegistry = new Hashtable ();

    private Hashtable undefinedRegistry = new Hashtable ();
    private Vector schemaElementList = new Vector ();

    private String packageName = "";
    private boolean verbose = true;
    private boolean overwrite = false;
    private boolean javac = true;

    /**
     * Insert the method's description here.
     * Creation date: (6/22/00 10:08:16 AM)
     */
    public Schema2JROM (String schemaURI) {
			registry.put(new QName(schemaURI,"string"),"com.ibm.jrom.JROMStringValue");
			registry.put(new QName(schemaURI,"float"),"com.ibm.jrom.JROMFloatValue");
			registry.put(new QName(schemaURI,"double"),"com.ibm.jrom.JROMDoubleValue");
			registry.put(new QName(schemaURI,"integer"),"com.ibm.jrom.JROMIntegerValue");
			registry.put(new QName(schemaURI,"int"),"com.ibm.jrom.JROMIntegerValue");
			registry.put(new QName(schemaURI,"boolean"),"com.ibm.jrom.JROMBooleanValue");
			registry.put(new QName(schemaURI,"byte"),"com.ibm.jrom.JROMByteValue");
			registry.put(new QName(schemaURI,"short"),"com.ibm.jrom.JROMShortValue");
			registry.put(new QName(schemaURI,"long"),"com.ibm.jrom.JROMLongValue");
    }

    /**
     * This method takes inputs of an Schema Element, a Hashtable and returns the same Hashtable filled with
     * the mappings from schema QNames to JROM class names.
     * Repeatedly call this method on the same object will have cumulative effect. That is, the registry 
     * returned will have all the mappings for every DOM elements processed.
     *
     * @return java.util.Hashtable
     * @param root org.w3c.dom.Element
     * @param registry java.util.Hashtable
     */
 
    public Hashtable createJROMMapping(Element root, Hashtable reg) throws SchemaException {
			if (root == null || reg == null) 
				throw new IllegalArgumentException("Argument to 'createJROMMapping' " +
																					 "cannot be null.");
			String targetURI = root.getAttribute ("targetNamespace");  
			// set target namespace uri for the schema model
			Vector elements = parseSchemaRoot (root, targetURI);
			
			for (int i=0; i<elements.size (); i++) {
				schemaElementList.addElement (elements.elementAt (i));
			}
			
			// First reset the registry to builtin types only.
			updateRegistry (schemaElementList);
			
			return getRegistry (reg);
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/22/00 12:07:49 AM)
     * @return org.apache.soap.util.xml.QName
     * @param elment org.w3c.dom.Element
     * @param name java.lang.String
     */
    private QName getAttributeQName (Element element, String attr) {
	
	if (element == null || attr == null)
    throw new IllegalArgumentException("Argument to 'getAttrQName' " +
                                       "cannot be null.");

	String name = DOMUtils.getAttribute (element, attr);

	if (name == null) return null;

	int index = name.lastIndexOf (":");
	String prefix = null;

	if (index != -1) {
	    prefix = name.substring (0, index);
	    name = name.substring (index+1);	
	} 
	String uri = DOMUtils.getNamespaceURIFromPrefix (element, prefix);

	return new QName (uri, name);

    }

    /**
     * Insert the method's description here.
     * Creation date: (6/22/00 12:23:44 PM)
     * @return java.lang.String
     * @param namespaceURI java.lang.String
     */
    public String getNamespaceURIMapping(String namespaceURI) {
	
	if (namespaceURI == null)
    throw new IllegalArgumentException("Argument to " +
                                       "'getNamespaceURIMapping' " +
                                       "cannot be null.");

	if (namespaceURI.compareTo ("") == 0)
	    return "";

  // MJD - debug
  if (packageName != null && !packageName.equals("")) {
    return packageName;
  }
  // MJD - debug

	String javaPath = (String) namespaceRegistry.get (namespaceURI);
	if (javaPath == null) {
	    javaPath = Conventions.namespaceURI2JavaPath (namespaceURI);
	    namespaceRegistry.put (namespaceURI, javaPath);
	}
	return javaPath;
	
    }
    /**
     * Insert the method's description here.
     * Creation date: (7/2/00 4:06:35 PM)
     * @return java.util.Hashtable
     * @param reg java.util.Hashtable
     */
    public Hashtable getRegistry(Hashtable reg) {
			for (Enumeration e = registry.keys (); e.hasMoreElements (); ) {
				QName key = (QName) e.nextElement ();
				reg.put (key, new TypeMapping (key, (String) registry.get(key)));
			}
			return reg;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/21/00 6:51:57 PM)
     * @param root org.w3c.dom.Element
 */
  private Vector parseSchemaRoot (Element root, String targetURI)
    throws SchemaException {
    if (root == null)
      throw new IllegalArgumentException("Argument to 'parseSchemaRoot' " +
                                         "cannot be null.");
		
    Node child = root.getFirstChild();
    Vector elements = new Vector ();
		
    while (child != null) {
      if (child.getNodeType () == Node.ELEMENT_NODE) {
        Element element = (Element) child;
				
        String elementType = element.getLocalName ();
				
        if (elementType == null) {
          return null;
        }
				
        String name = DOMUtils.getAttribute (element, "name");
        QName type = getAttributeQName (element, "type");
        boolean isArray = false;
				
        if (elementType.equals("complexType")) {
          boolean isFinal = false;
          boolean isAbstract = false;
          QName base = null;
          Vector children = parseSchemaRoot(element, targetURI);
          elements.addElement(new SchemaComplexType(name, base, isAbstract, isFinal, children, targetURI));
        } else if (elementType.equals("element")) {
					QName ref = null;
					elements.addElement(new SchemaElement(name, ref, type, isArray, null, targetURI));					
        } else {
        // Ignore any other element types and continue parsing the tree below
          return parseSchemaRoot(element, targetURI);
        }
      }
			
      child = child.getNextSibling();
    }
		
    return elements;
  }

    /**
     * Set the mapping between namespace URI and the java package name used in the generated class files.
     * Creation date: (6/22/00 12:20:21 PM)
     * @param namespaceURI java.lang.String
     * @param javaPath java.lang.String
     */
    public void setNamespaceURIMapping(String namespaceURI, String javaPath) {
	
	if (namespaceURI == null || javaPath == null)
    throw new IllegalArgumentException("Argument to " +
                                       "'setNamespaceURIMapping' " +
                                       "cannot be null.");

	namespaceRegistry.put (namespaceURI, javaPath); 	
    }

    public void setVerbose(boolean verbose)
    {
      this.verbose = verbose;
    }

    public boolean getVerbose()
    {
      return verbose;
    }

    /**
     * This method takes a list of schema elements, target uri and update the registry content based on the
     * schema element list.
     *  
     * Creation date: (6/22/00 10:15:25 AM)
     * @return java.util.Hashtable
     * @param registry java.util.Hashtable
     * @param elements java.util.Vector
     */
    
	private void updateRegistry(Vector elements) throws SchemaException {
		
		if (elements == null)
			throw new IllegalArgumentException("Argument to 'updateRegistry' " +
																				 "cannot be null.");
		
		Hashtable simpleTypeTable = new Hashtable ();
		
		for (int i=0; i < elements.size(); i++) {
	    SchemaType element = (SchemaType) elements.elementAt (i);
	    String name = element.getName ();
	    if (name == null) name = "";
	    QName qName = new QName (element.getTargetURI (), name);
		
	    if (element.getElementType () == SchemaType.SIMPLETYPE) {
				simpleTypeTable.put (qName, element);
			}
		}
		
		boolean changed = true;
		
		while (changed) {
	    changed = false;
	    for (Enumeration e = simpleTypeTable.keys (); e.hasMoreElements (); ) {
				QName qName = (QName) e.nextElement ();	
				
				SchemaSimpleType simpleType = (SchemaSimpleType) simpleTypeTable.get (qName);
				QName base = simpleType.getBase ();
				boolean isList = simpleType.isList ();

				if (base != null) {
					String javaType = (String) registry.get (base);
					if (javaType == null)
						if (simpleTypeTable.containsKey (base)) continue;
						else javaType = "com.ibm.jrom.JROMComplexValue";
					
					if (isList) javaType = javaType + "[]";
					registry.put (qName, javaType);
					changed = true;
					simpleTypeTable.remove (qName);
				} else {
					throw new SchemaException("'simpleType' must have 'base' attribute.");
				}
			}
		}
	
		for (Enumeration e = simpleTypeTable.keys (); e.hasMoreElements (); ) {
			if (! registry.contains ((QName) e.nextElement ()))
				throw new SchemaException ("'simpleType' definitions have cyclic " +
																	 "dependency.");
		}
		
		for (int i = 0; i < elements.size (); i ++) {
			SchemaType element = (SchemaType) elements.elementAt (i);
			String targetURI = element.getTargetURI ();
			String targetNSPrefix = getNamespaceURIMapping (targetURI);
			if (targetNSPrefix.compareTo ("") != 0)
				targetNSPrefix = targetNSPrefix + ".";
	    String name = element.getName ();
	    if (name == null) name = "";
	    QName qName = new QName (targetURI, name);
			// don't know what i'm doing here - NKM 11/06/01
	    switch (element.getElementType ()) {
	    case SchemaType.COMPLEXTYPE: 
				registry.put (qName, "com.ibm.jrom.JROMComplexValue");
				break; 
	    case SchemaType.GROUP:
				registry.put (qName, "com.ibm.jrom.JROMComplexValue");
				break;
	    case SchemaType.ATTRIBUTEGROUP:
				registry.put (qName, "com.ibm.jrom.JROMComplexValue");
				break;
	    }		
		}
	
		for (int i = 0; i < elements.size (); i ++) {
	    SchemaType element = (SchemaType) elements.elementAt (i);
	    String name = element.getName ();
	    String targetURI = element.getTargetURI ();
	    String targetNSPrefix = getNamespaceURIMapping (targetURI);
	    if (targetNSPrefix.compareTo ("") != 0)
				targetNSPrefix = targetNSPrefix + ".";
	    if (name == null) name = "";
	    QName qName = new QName (targetURI, name);
	    switch (element.getElementType ()) {
	    case SchemaType.ELEMENT:
				SchemaElement schemaElement = (SchemaElement) element;
				QName type = schemaElement.getType ();
				SchemaType child = schemaElement.getChild ();
				if (type != null) {
					String javaType = (String) registry.get (type);
					if (javaType != null) 
						registry.put (qName, javaType);
				}
				else if (child != null) {
					if (child.getElementType () == SchemaType.SIMPLETYPE) {
						QName base = (QName) ((SchemaSimpleType) child).getBase ();
						String javaType = (String) registry.get (base);
						if (javaType != null) { 
							if (((SchemaSimpleType) child).isList ())
								javaType = javaType + "[]";
							registry.put (qName, javaType);
						}
					}
					else if (child.getElementType () == SchemaType.COMPLEXTYPE) {
						registry.put (qName, "com.ibm.jrom.JROMComplexValue");
					}
				}
				break;
	    case SchemaType.ATTRIBUTE:
				SchemaAttribute schemaAttribute = (SchemaAttribute) element;
				type = schemaAttribute.getType ();
				SchemaSimpleType simpleType = schemaAttribute.getChild ();
				if (type != null) {
					String javaType = (String) registry.get (type);
					if (javaType != null)  
						registry.put (qName, javaType);
				}
				else if (simpleType != null) {
					QName base = simpleType.getBase ();
					String javaType = (String) registry.get (base);
					if (javaType != null) { 
						if (simpleType.isList ())
							javaType = javaType + "[]";
						registry.put (qName, javaType);
					}
				}
				break;
	    }
		}
	}
}
