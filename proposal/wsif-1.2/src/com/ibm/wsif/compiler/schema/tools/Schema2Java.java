// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Convert XML schema file into Java class files.
 * Creation date: (5/29/00 1:55:51 AM)
 * @author: Tian Zhao (tzhao@cs.purdue.edu)
 */ 
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.apache.soap.*;
import org.apache.soap.util.xml.DOMUtils;
import com.ibm.wsif.compiler.schema.*;
import com.ibm.wsif.compiler.util.*;
import javax.wsdl.QName;

public class Schema2Java {

    private Hashtable registry = new Hashtable ();
    private String workingDirectory = System.getProperty("user.dir");
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
    public Schema2Java (String schemaURI) {
      registry.put(new QName (schemaURI, "string"), "java.lang.String");
      registry.put(new QName (schemaURI, "integer"), "java.lang.Integer");
      registry.put(new QName (schemaURI, "boolean"), "boolean");
      registry.put(new QName (schemaURI, "float"), "float");
      registry.put(new QName (schemaURI, "double"), "double");
      registry.put(new QName (schemaURI, "binary"), "byte[]");
      registry.put(new QName (schemaURI, "long"), "long");
      registry.put(new QName (schemaURI, "int"), "int");
      registry.put(new QName (schemaURI, "short"), "short");
      registry.put(new QName (schemaURI, "byte"), "byte");
      registry.put(new QName (schemaURI, "void"), "void");
      registry.put(new QName (schemaURI, "ur-type"), "java.lang.Object");
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/24/00 3:02:14 PM)
     * @return com.ibm.wsif.compiler.schema.tools.ClassFile
     * @param any com.ibm.wsif.compiler.schema.tools.SchemaAny
     * @param file com.ibm.wsif.compiler.schema.tools.ClassFile
     */
    private ClassFile any2Field(SchemaAny any, ClassFile file) {
	
	if (any == null || file == null) 
		throw new IllegalArgumentException("Argument to 'any2Field' cannot " +
                                       "be null.");

	file.addField (new ClassField ("byte[]", Conventions.schema2JavaName ("any", null, false), any.isArray()));
	return file;
	
}
    /**
     * Insert the method's description here.
     * Creation date: (6/24/00 3:07:11 PM)
     * @return com.ibm.wsif.compiler.schema.tools.ClassFile
     * @param anyAttr com.ibm.wsif.compiler.schema.tools.SchemaAnyAttribute
     * @param file com.ibm.wsif.compiler.schema.tools.ClassFile
     */
    private ClassFile anyAttribute2Field(SchemaAnyAttribute anyAttr, ClassFile file) {

	if (anyAttr == null || file == null)
    throw new IllegalArgumentException("Argument to 'anyAttribute2Field' " +
                                       "cannot be null.");

	file.addField (new ClassField ("byte[]", Conventions.schema2JavaName ("anyAttribute", null, false), false));
	return file;
    }
    /**
     * Insert the method's description here.
     * Creation date: (6/23/00 11:48:17 AM)
     * @return com.ibm.wsif.compiler.schema.tools.ClassFile
     * @param attr com.ibm.wsif.compiler.schema.tools.SchemaAttribute
     * @param file com.ibm.wsif.compiler.schema.tools.ClassFile
     */
 
    private ClassFile attribute2Field(SchemaAttribute attr, ClassFile file)
      throws SchemaException {

	if (attr == null || file == null)
    throw new IllegalArgumentException("Argument to 'attribute2Field' " +
                                       "cannot be null.");

	QName type = attr.getType ();
	QName ref = attr.getRef ();
	SchemaSimpleType simp = attr.getChild ();
	String targetURI = attr.getTargetURI ();
	
	ClassField field;
	if (ref != null) {
	    field = new ClassField (getJavaType (ref, targetURI, "attribute"), 
				    Conventions.schema2JavaName ("attribute", ref.getLocalPart (), false) , false);
	    file.addField (field);
	}
	else if (type != null) {
	    field = new ClassField (getJavaType (type, targetURI, "simpleType"),
				    Conventions.schema2JavaName ("attribute", attr.getName (), false), false);
		file.addField (field);
	}
	else if (simp != null) {
	    String name = Conventions.schema2JavaName ("attribute", attr.getName (), false);
	    file = simpleType2Field (name, false, simp, file);
	}
	
	return file;
    }
    /**
     * Insert the method's description here.
     * Creation date: (6/23/00 10:01:42 AM)
     * @return com.ibm.wsif.compiler.schema.tools.ClassFile
     * @param registry java.util.Hashtable
     * @param elm com.ibm.wsif.compiler.schema.tools.SchemaComplexType
     * @param targetURI java.lang.String
     */
    private ClassFile attributeGroup2Class(SchemaAttributeGroup attrGp) throws SchemaException {
	
	if (attrGp == null)
    throw new IllegalArgumentException("Argument to 'attributeGroup2Class' " +
                                       "cannot be null.");

	String className = Conventions.schema2JavaName ("attributeGroup", attrGp.getName (), true);
	ClassFile file = new ClassFile (className);
	
	Vector children = attrGp.getChildren ();
	for (int i = 0; i < children.size (); i ++) {
		SchemaType child = (SchemaType) children.elementAt (i);
		switch (child.getElementType ()) {
		case SchemaType.ATTRIBUTEGROUP:
		    file = attributeGroup2Field ((SchemaAttributeGroup) child, file);
		    break;
		case SchemaType.ATTRIBUTE:
		    file = attribute2Field ((SchemaAttribute) child, file);
		    break;
		case SchemaType.ANYATTRIBUTE:
		}
	}
	
	return file;
    }
    /**
     * Insert the method's description here.
     * Creation date: (6/23/00 11:31:18 AM)
     * @return com.ibm.wsif.compiler.schema.tools.ClassField
     * @param attrGp com.ibm.wsif.compiler.schema.tools.SchemaAttributeGroup
     */
    private ClassFile attributeGroup2Field(SchemaAttributeGroup attrGp, ClassFile file) throws SchemaException {
	
	if (attrGp == null || file == null)
    throw new IllegalArgumentException("Argument to 'attributeGroup2Field' " +
                                       "cannot be null.");
	
	ClassField field;
	
	QName ref = attrGp.getRef ();
	if (ref != null) 
	    field = new ClassField (getJavaType (ref, attrGp.getTargetURI (), "attributeGroup"), 
				    Conventions.schema2JavaName ("attributeGroup", ref.getLocalPart (), false), false);
	else {
    throw new SchemaException ("Attribute group can only be defined at " +
                               "the top level.");
	}
	file.addField (field);
	
	return file;
	
    }
    /**
     * Insert the method's description here.
     * Creation date: (6/23/00 10:01:42 AM)
     * @return com.ibm.wsif.compiler.schema.tools.ClassFile
     * @param registry java.util.Hashtable
     * @param elm com.ibm.wsif.compiler.schema.tools.SchemaComplexType
     */
    private ClassFile complexType2Class(String className, SchemaComplexType elm) throws SchemaException {
	
	if (className == null || elm == null)
    throw new IllegalArgumentException("Argument to 'complexType2Class' " +
                                       "cannot be null.");
	
	QName base = elm.getBase ();
	ClassFile file = new ClassFile (className);
	
	if (base != null) 
	    file.superClassName = getJavaType (base, elm.getTargetURI (), "complexType");
	// This just covers the case when the base is a undefined complexType. 
	// If it is a undefined simpleType, there is nothing we  can do anyway.
	
	Vector children = elm.getChildren ();
	for (int i = 0; i < children.size (); i ++) {
	    SchemaType child = (SchemaType) children.elementAt (i);
	    String name = child.getName ();
	    switch (child.getElementType ()) {
	    case SchemaType.ELEMENT:
		file = element2Field ((SchemaElement) child, file);
		break;
	    case SchemaType.GROUP:
		file = group2Field ((SchemaGroup) child, file);
		break;
	    case SchemaType.ATTRIBUTE:
		file = attribute2Field ((SchemaAttribute) child, file);
		break;
	    case SchemaType.ATTRIBUTEGROUP:
		file = attributeGroup2Field ((SchemaAttributeGroup) child, file);
		break;
	    case SchemaType.ANY:
		file = any2Field ((SchemaAny) child, file);
		break;
	    case SchemaType.ANYATTRIBUTE:
		file = anyAttribute2Field ((SchemaAnyAttribute) child, file);
		break;
	    case SchemaType.ALL:
	    case SchemaType.CHOICE:
	    case SchemaType.SEQUENCE:
		file = mgs2Field ((SchemaMGS) child, file);
		break;
	    }
	}
	
	return file;
    }
    /**
     * This method takes inputs of an Schema Element, a Hashtable and returns the same Hashtable filled with
     * the mappings from schema QNames to Java class names or primitive types.
     * Repeatedly call this method on the same object will have cumulative effect. That is, the registry 
     * returned will have all the mappings for every DOM elements processed.
     *
     * To output the java class files, call the public method: outputJavaMapping () once all the schema elements
     * have been processed.
     *  
     * Creation date: (6/22/00 6:04:35 PM)
     * @return java.util.Hashtable
     * @param root org.w3c.dom.Element
     * @param registry java.util.Hashtable
     */
 
    public Hashtable createJavaMapping(Element root, Hashtable reg) throws SchemaException {
	
	if (root == null || reg == null) 
    throw new IllegalArgumentException("Argument to 'createJavaMapping' " +
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
     * Creation date: (6/23/00 6:09:47 PM)
     * @return com.ibm.wsif.compiler.schema.tools.ClassFile
     * @param elm com.ibm.wsif.compiler.schema.tools.SchemaElement
     * @param file com.ibm.wsif.compiler.schema.tools.ClassFile
     */
    private ClassFile element2Field(SchemaElement elm, ClassFile file) throws SchemaException {
	
	if (elm == null || file == null)
    throw new IllegalArgumentException("Argument to 'element2Field' " +
                                       "cannot be null.");
	
	String name = elm.getName ();
	QName ref = elm.getRef ();
	QName type = elm.getType ();
	SchemaType child = elm.getChild ();
	String targetURI = elm.getTargetURI ();
	
	if (name != null)
	    name = Conventions.schema2JavaName ("element", name, false);

	if (ref != null) {
	    String javaType = getJavaType (ref, targetURI, "element");
	    file.addField (new ClassField (javaType, 
					   Conventions.schema2JavaName ("element", ref.getLocalPart (), false), elm.isArray ()));
	}
	else if (type != null) {
	    // I just guess arbitrarily that the type attribute refers to a complexType and
	    // in case this type is not defined yet, I generate a generic Java Class name for it.
	    // However, it could also be a simpleType in which case we are screwed.
	    
	    String javaType = getJavaType (type, targetURI, "complexType");
	    file.addField (new ClassField (javaType, name, elm.isArray ()));
	}
  else if (child == null) {
    throw new SchemaException ("The type of this element is unknown: '" +
                               name + "'.");
  }
	else if (child.getElementType ()== SchemaType.SIMPLETYPE) {
	    file = simpleType2Field (name, elm.isArray (), (SchemaSimpleType) child, file);
  }
	else if (child.getElementType () == SchemaType.COMPLEXTYPE) {
    throw new SchemaException("Inner complex-types are not supported: '" +
                              name + "'.");

//      String cname = Conventions.schema2JavaName ("element", name, true);
//      file.addField (new ClassField (cname, name, elm.isArray ()));
//      file.addInnerClass (complexType2Class (cname, (SchemaComplexType) child));
	}
	
	return file;
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
     * Creation date: (6/22/00 4:56:42 PM)
     * @return java.lang.String
     * @param QName java.lang.String
     */
    private String getGenericJavaName(QName qName, String elementType) {
	
	if (qName == null)
    throw new IllegalArgumentException("Argument to 'getGenericJavaName' " +
                                       "cannot be null.");
	
	String javaName = (String) undefinedRegistry.get (qName);
	if (javaName != null)
	    return javaName;
	
	String javaPath = getNamespaceURIMapping (qName.getNamespaceURI ());
	
	String name = qName.getLocalPart ();
	name = Conventions.schema2JavaName (elementType, name, true);
	
	if (javaPath.compareTo("") != 0) 
	    javaName = javaPath + "." + name;
	else
	    javaName = name;
	
	undefinedRegistry.put (qName, javaName);

	System.err.println("Schema name " + qName + " is undefined, " +
                     "and generic Java name " + javaName +
                     " is used instead.");

	return javaName;	
    }
    /**
     * Insert the method's description here.
     * Creation date: (6/23/00 9:42:50 AM)
     * @return java.lang.String
     * @param Registry java.util.Hashtable
     * @param name org.apache.soap.util.xml.QName
     */
    private String getJavaType(QName name, String targetURI, String elementType)
      throws SchemaException {

	if (name == null)
    throw new IllegalArgumentException("Argument to 'getJavaType' " +
                                       "cannot be null.");

	String type = (String) registry.get (name);

	if (type == null) {
    throw new SchemaException("Basic type '" + name + "' not supported.");

//      type = getGenericJavaName (name, elementType);
  }

	String targetNS = getNamespaceURIMapping (targetURI);
	
	if (type.startsWith (targetNS)/* || type.startsWith (baseTypeDefinitionNS)*/)
	    type = type.substring (type.lastIndexOf (".") + 1);
	
	return type;
    }

  /**
   * Set the Java package into which the generated code should be placed.
   * Defaults to unnamed package.
   */
  public void setPackageName (String packageName) {
    this.packageName = packageName;
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

    private ClassFile group2Class(SchemaGroup gp) throws SchemaException {
	
	if (gp == null)
    throw new IllegalArgumentException("Argument to 'group2Class' " +
                                       "cannot be null.");

	SchemaMGS child = gp.getChild ();
	String className = Conventions.schema2JavaName ("group", gp.getName (), true);
	ClassFile file = new ClassFile (className);
	file = mgs2Field (child, file);
	
	return file;
    }
    /**
     * Insert the method's description here.
     * Creation date: (6/23/00 6:46:55 PM)
     * @return com.ibm.wsif.compiler.schema.tools.ClassFile
     * @param gp com.ibm.wsif.compiler.schema.tools.SchemaGroup
     * @param file com.ibm.wsif.compiler.schema.tools.ClassFile
     */
    private ClassFile group2Field(SchemaGroup gp, ClassFile file)
      throws SchemaException {
	
	if (gp == null || file == null)
    throw new IllegalArgumentException("Argument to 'group2Field' " +
                                       "cannot be null.");
	
	QName ref = gp.getRef ();
	
	if (ref != null) {
	    String javaType = getJavaType (ref, gp.getTargetURI (), "group");
	    file.addField (new ClassField (javaType, 
					   Conventions.schema2JavaName ("group", ref.getLocalPart (), false), gp.isArray ()));
	}
	
	return file;
    }

    private ClassFile mgs2Class(String className, SchemaMGS elm) throws SchemaException {
	
	if (className == null || elm == null)
    throw new IllegalArgumentException("Argument to 'mgs2Class' " +
                                       "cannot be null.");

	ClassFile file = new ClassFile (className);
	
	Vector children = elm.getChildren ();
	for (int i = 0; i < children.size (); i ++) {
	    SchemaType child = (SchemaType) children.elementAt (i);
	    
	    switch (child.getElementType ()) {
	    case SchemaType.ELEMENT:
		element2Field ((SchemaElement) child, file);
		break;
	    case SchemaType.GROUP:
		group2Field ((SchemaGroup) child, file);
		break;
	    case SchemaType.ANY:
	    case SchemaType.CHOICE:
	    case SchemaType.SEQUENCE:
		mgs2Field ((SchemaMGS) child, file);
	    }
	}
	
	return file;
    }
    /**
     * Insert the method's description here.
     * Creation date: (6/24/00 2:24:48 PM)
     * @return com.ibm.wsif.compiler.schema.tools.ClassFile
     * @param mgs com.ibm.wsif.compiler.schema.tools.SchemaMGS
     * @param file com.ibm.wsif.compiler.schema.tools.ClassFile
     */
    private ClassFile mgs2Field(SchemaMGS mgs, ClassFile file) throws SchemaException {
	
	if (mgs == null || file == null)
    throw new IllegalArgumentException("Argument to 'mgs2Field' " +
                                       "cannot be null.");

	String mgsType;
	switch (mgs.getElementType ()) {
	case SchemaType.ALL:
	    mgsType = "all"; break;
	case SchemaType.CHOICE:
	    mgsType = "choice"; break;
	case SchemaType.SEQUENCE:
	    mgsType = "sequence"; break;
	default:
	    return file;
	}
	
	String cname = Conventions.schema2JavaName (mgsType, null, true);
	String fname = Conventions.schema2JavaName (mgsType, null, false);
	file.addField (new ClassField (cname, fname, mgs.isArray ()));
	file.addInnerClass (mgs2Class (cname, mgs));
	return file;
	
    }

    /**
     * Output the Java class files created from XML Schema models to the working directory.
     * Call public method: setWorkingDirectory (String directory) to set the output directory.
     *
     * Creation date: (5/28/00 2:56:06 PM)
     * 
     */
    public void outputJavaMapping() throws SchemaException, IOException {
	
	Vector classFileList = new Vector ();

	for (int i = 0; i < schemaElementList.size (); i ++) {
	    SchemaType element = (SchemaType) schemaElementList.elementAt (i);
	    ClassFile file = null;
	    
	    switch (element.getElementType ()) {
	    case SchemaType.COMPLEXTYPE:
		SchemaComplexType cmp = (SchemaComplexType) element;
		String className = Conventions.schema2JavaName ("complexType", cmp.getName (), true);
		file = complexType2Class (className, cmp);
			break;
	    case SchemaType.GROUP:
		file = group2Class ((SchemaGroup) element);
		break;
	    case SchemaType.ATTRIBUTEGROUP:
		file = attributeGroup2Class ((SchemaAttributeGroup) element);
		break;
	    case SchemaType.ELEMENT:
		SchemaElement el = (SchemaElement) element;
		SchemaType child = el.getChild ();
		if (child != null && child.getElementType () == SchemaType.COMPLEXTYPE) {
		    className = Conventions.schema2JavaName ("element", el.getName (), true);
		    file = complexType2Class (className, (SchemaComplexType) el.getChild ());
		}
		break;
	    }
	    if (file != null) {
		file.packageName = getNamespaceURIMapping (element.getTargetURI ());
		classFileList.addElement (file);
	    }		
	}

	int classCount = classFileList.size();
  String[] fileNames = new String[classCount];
  StreamFactory streamFactory = new StreamFactory();

	for (int classIndex=0; classIndex<classCount; classIndex++) {
	  ClassFile classFile = (ClassFile) classFileList.elementAt(classIndex);
    String javaPathName = Conventions.getJavaPathName(workingDirectory,
                                                      classFile.packageName);
    String javaFileName = classFile.className + ".java";
    OutputStream os = streamFactory.getOutputStream(javaPathName,
                                                    javaFileName,
                                                    overwrite);
	  Writer classWriter = new OutputStreamWriter(os);

	  classWriter.write("/*\n *This class is automatically generated by schema to Java program.\n");
	  classWriter.write(" *Only a subset of schema is handled and some of the schema information\n");
	  classWriter.write(" *may be lost during translation\n */\n\n");

	  if (classFile.packageName != null)		
	    classWriter.write("package " + classFile.packageName + ";\n\n");
//    classWriter.write ("import " + baseTypeDefinitionNS + ".*;\n\n");
	  printJavaFile(classWriter, classFile, null);
	  
	  classWriter.flush();
	  classWriter.close();	

    fileNames[classIndex] = new File(javaPathName,
                                     javaFileName).getAbsolutePath();
	}

      if (javac) {
        for (int i = 0; i < classCount; i++) {
          if (Conventions.JDKcompile(fileNames[i], workingDirectory)
              && verbose) {
            System.out.println("Compiled file '" + fileNames[i] + "'.");
          }
        }
      }
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
     * Insert the method's description here.
     * Creation date: (5/30/00 4:14:39 PM)
     * @param classWriter java.io.FileWriter
     * @param classFile schema2JavaVersion2.ClassFile
     * @param tab java.lang.String
     * @exception java.io.IOException The exception description.
     */
    private void printJavaFile (Writer classWriter, ClassFile classFile, String tab) 
	throws java.io.IOException {
	
	String className = classFile.className;
	String superClassName = classFile.superClassName;
	
	// classWriter.write("import java.lang.*;\n");
	
	if (tab == null) {
	    tab = "";
		classWriter.write("public ");
	}
	else
	    classWriter.write(tab);
	if (classFile.isAbstract)
	    classWriter.write("abstract ");
	else if (classFile.isFinal)
	    classWriter.write("final");
	if (superClassName != null)
	    classWriter.write("class " + className + " extends " + superClassName + "{\n");
	else 
	    classWriter.write("class " + className + "{\n");
	
	classWriter.write("\n" + tab + "\t//instance variables\n");
	
	int fieldLength = classFile.getFieldCount();	
	for (int fieldIndex=0; fieldIndex<fieldLength; fieldIndex++) {
	    ClassField classField = classFile.getField(fieldIndex);
	    classWriter.write(tab + "\tprivate " + classField.fieldType);
	    if (classField.isArray)
		classWriter.write("[]");
	    classWriter.write("\t" + classField.fieldName + ";\n");
	}
	
	classWriter.write("\n" + tab + "\t//constructors\n");
	if (fieldLength > 0)
	    classWriter.write(tab + "\tpublic " + className + " () { }\n");
	
	classWriter.write("\n" + tab + "\tpublic " + className + " (");
	for (int fieldIndex=0; fieldIndex<fieldLength; fieldIndex++) {
	    ClassField classField = classFile.getField(fieldIndex);
	    classWriter.write(classField.fieldType);
	    if (classField.isArray)
		classWriter.write("[]");
	    classWriter.write(" " + classField.fieldName);
	    if (fieldIndex < fieldLength-1)
		classWriter.write(", ");
	}
	classWriter.write(") {\n");
	
	for (int fieldIndex=0; fieldIndex<fieldLength; fieldIndex++) {
	    ClassField classField = classFile.getField(fieldIndex);
	    classWriter.write(tab + "\t\tthis." + classField.fieldName + "\t= " + classField.fieldName + ";\n"); 
	}
	classWriter.write(tab + "\t}\n");

  // code added by NKM
  // let's have a setter and getter for each field
  // this will make the generated code a bean which will let us use it in nice ways
  for (int fieldIndex=0; fieldIndex<fieldLength; fieldIndex++) {
      ClassField classField = classFile.getField(fieldIndex);
      String propertyName =
        Character.toUpperCase(classField.fieldName.charAt(0)) +
        classField.fieldName.substring(1);
      classWriter.write("\n"+tab+"\tpublic ");
      if (classField.isArray)
    classWriter.write("[]");
      classWriter.write(classField.fieldType+" get"+propertyName+"() {\n");
      classWriter.write(tab+"\t\treturn "+classField.fieldName+";\n");
      classWriter.write(tab+"\t}\n");
      classWriter.write("\n"+tab+"\tpublic ");
      classWriter.write(" void set"+propertyName+"("+classField.fieldType);
      if (classField.isArray)
    classWriter.write("[]");
      classWriter.write(" "+classField.fieldName+") {\n");
      classWriter.write(tab + "\t\tthis." + classField.fieldName + "\t= " + classField.fieldName + ";\n"); 
      classWriter.write(tab+"\t}\n");
  }
  // end of code added by NKM      

    classWriter.write("\n\tpublic String toString() {\n");
    classWriter.write("\t\treturn ");

    for (int fieldIndex=0; fieldIndex<fieldLength; fieldIndex++) {
      ClassField classField = classFile.getField(fieldIndex);

      if (fieldIndex > 0) {
        classWriter.write(" + \"\\n\" + \n\t\t\t");
      }

      classWriter.write("\"" + classField.fieldName +
                        "=\" + " + classField.fieldName);
    }

    classWriter.write(";\n\t}\n");

  
	int innerClassCount = classFile.getInnerClassCount();
	if (innerClassCount > 0) {
	    classWriter.write("\n" + tab + "\t//Inner classes\n");
	    for (int innerClassIndex=0; innerClassIndex<innerClassCount; innerClassIndex++) {
		ClassFile innerClassFile = classFile.getInnerClass(innerClassIndex);
		printJavaFile(classWriter, innerClassFile, tab+"\t");
	    }
	}
	classWriter.write("\n" + tab + "}");
	
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

    /**
     * Set the working directory for generated Java files.
     * Creation date: (5/30/00 9:17:36 PM)
     * @param dir java.lang.String
     */
    public void setWorkingDirectory(String dir)
      throws IllegalArgumentException {

	if (dir == null)
    throw new IllegalArgumentException("Argument to " +
                                       "'setWorkingDirectory' " +
                                       "cannot be null.");

	workingDirectory = dir;	
    }

    public void setVerbose(boolean verbose)
    {
      this.verbose = verbose;
    }

    public boolean getVerbose()
    {
      return verbose;
    }

    public void setOverwrite(boolean overwrite)
    {
      this.overwrite = overwrite;
    }
  
    public boolean getOverwrite()
    {
      return overwrite;
    }

    public void setJavac(boolean javac)
    {
      this.javac = javac;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/23/00 5:45:54 PM)
     * @return com.ibm.wsif.compiler.schema.tools.ClassFile
     * @param name java.lang.String
     * @param simp com.ibm.wsif.compiler.schema.tools.SchemaSimpleType
     */
    private ClassFile simpleType2Field(String name, boolean isArray, SchemaSimpleType simp, ClassFile file)
      throws SchemaException {
	
	if (name == null || simp == null || file == null)
    throw new IllegalArgumentException("Argument to 'simpleType2Field' " +
                                       "cannot be null.");

	QName base = simp.getBase ();
	String type = getJavaType (base, simp.getTargetURI (), "simpleType");
	
	if (simp.isList ()) type = type + "[]";
	file.addField (new ClassField (type, name, isArray));
	
	return file;
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
			else javaType = getGenericJavaName (base, "simpleType");
				
		    if (isList) javaType = javaType + "[]";
		    registry.put (qName, javaType);
		    changed = true;
		    simpleTypeTable.remove (qName);
		}
		else
		  throw new SchemaException("'simpleType' must have 'base' attribute.");
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
	    switch (element.getElementType ()) {
	    case SchemaType.COMPLEXTYPE: 
		registry.put (qName, targetNSPrefix + Conventions.schema2JavaName ("complexType", name, true));
		break; 
	    case SchemaType.GROUP:
		registry.put (qName, targetNSPrefix + Conventions.schema2JavaName ("group", name, true));
		break;
	    case SchemaType.ATTRIBUTEGROUP:
		registry.put (qName, targetNSPrefix + Conventions.schema2JavaName ("attributeGroup", name, true));
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
			registry.put (qName, targetNSPrefix + Conventions.schema2JavaName ("complexType", name, true));
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
