// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema.tools;

/**
 * Contain some static methods for mapping conventions.
 * Creation date: (6/20/00 5:05:21 PM)
 * @author:Tian Zhao (tzhao@cs.purdue.edu)
 * @author:Sanjiva Weerawarana (sanjiva@us.ibm.com)
 * @author:Matthew J. Duftler (duftler@us.ibm.com)
 */

import java.io.*;
import java.util.StringTokenizer;
import java.util.Hashtable;
import org.w3c.dom.*;
import org.apache.soap.util.xml.DOMUtils;
import javax.wsdl.QName;

public class Conventions {
    
    private static Hashtable postfixTable = init();
    private static boolean verbose = true;

    /**
     * Insert the method's description here.
     * Creation date: (6/20/00 6:20:11 PM)
     */
    private static Hashtable init() {
	
	Hashtable postfixTable = new Hashtable(11);
	
	postfixTable.put ("simpleType", ""); // These two lines cannot be changed.
	postfixTable.put ("complexType", "");// The code will break otherwise.
	postfixTable.put ("group", "_Group");
	postfixTable.put ("attributeGroup", "_AttrGp");
	postfixTable.put ("element", "");
	postfixTable.put ("attribute", "_Attr");
	/*      postfixTable.put ("any", "_Any");
		postfixTable.put ("sequence", "_Seqn");
		postfixTable.put ("choice", "_choice");
		postfixTable.put ("anyAttribute", "_AnyAttr");
		postfixTable.put ("all", "_All");
	*/
	return postfixTable;
	
    }
    /**
     * Convert name space url into a directory name and a package name with '.' as delimiter.
     * Creation date: (5/30/00 9:56:31 PM)
     * @return java.lang.String
     * @param nameSpace java.lang.String
     */
    public static String namespaceURI2JavaPath (String namespaceURI)
      throws IllegalArgumentException {
	
	if (namespaceURI == null)
	    throw new IllegalArgumentException ("Argument to " +
                                          "'namespaceURI2JavaPath' cannot " +
                                          "be null.");

	if (namespaceURI.startsWith("http://"))
	    namespaceURI = namespaceURI.substring(7);
	
	if (namespaceURI.compareTo("") == 0)
	    return namespaceURI;
	
	if (namespaceURI.endsWith("/"))
	    namespaceURI = namespaceURI.substring(0, namespaceURI.lastIndexOf("/"));

	StringTokenizer tokens = new StringTokenizer(namespaceURI, "/", false);

	String javaPath = tokens.nextToken();
	while (tokens.hasMoreTokens()) {
	    javaPath = tokens.nextToken() + "." + javaPath;
	}
	
	tokens = new StringTokenizer(javaPath, ".", false);
	javaPath = tokens.nextToken();
	
	while (tokens.hasMoreTokens()) {
	    javaPath = tokens.nextToken() + "." + javaPath;
	}

  javaPath = javaPath.replace(':', '_');
  javaPath = javaPath.replace('-', '_');

	return javaPath;
    }
    /**
     * Map top-level schema type names to corresponding java class names.
     * and map schema component names to java field names. 
     * If schema name is intended to map to a Java class name then set the 
     * boolean variable <code> isClass </code> to true. If the schema name 
     * is mapped to a java field name, then set <code> isClass </code> to false.
     * Creation date: (6/20/00 5:13:45 PM)
     * @return java.lang.String
     * @param schemaType java.lang.String
     * @param schemaName java.lang.String
     * @param isClass boolean
     */
    public static String schema2JavaName(String schemaType,
                                         String schemaName,
                                         boolean isClass)
                                           throws IllegalArgumentException {
	if (schemaType == null)
	    throw new IllegalArgumentException("Illegal arguments to " +
                                         "'schema2JavaName'.");

	if (schemaType.compareTo ("any") == 0)
		return "any";
	else if (schemaType.compareTo ("simpleType") == 0) { // schemaName should not be null in this case.
	    if (schemaName == null)
        throw new IllegalArgumentException ("Illegal arguments to " +
                                            "'schema2JavaName'.");
	    schemaName = schemaName.replace ('-', '_'); 
	    return schemaName;
	}
	else if (schemaType.compareTo ("attribute") == 0) {
	    if (schemaName == null)
        throw new IllegalArgumentException ("Illegal arguments to " +
                                            "'schema2JavaName'.");
	    schemaName = schemaName.replace ('-', '_');
	    return schemaName + postfixTable.get ("attribute");
	}
	else if (schemaType.compareTo ("anyAttribute") == 0)
	    return "anyAttribute";
	else if (schemaType.compareTo ("all") == 0)
	    schemaName = "all";
	else if (schemaType.compareTo ("choice") == 0)
	    schemaName = "choice";
	else if (schemaType.compareTo ("sequence") == 0)
	    schemaName = "sequence";
	
	if (schemaName == null)
    throw new IllegalArgumentException ("Illegal arguments to " +
                                        "'schema2JavaName'.");
	
	schemaName = schemaName.replace ('-', '_'); // some of the id's in Schema is illegal in Java.
	
 	String postfix = (String) postfixTable.get(schemaType);
 	if (postfix == null) postfix = "";
 	
	String javaName = schemaName;
	if (isClass)
	    javaName = Character.toUpperCase(schemaName.charAt(0)) + schemaName.substring(1);
	return javaName+postfix;
	
    }
    /**
     * This method takes an Element type and a target namespace uri and
     * return a fully qualified java class name.
     * The targetURI is translated to java package name genericly.
     * If the targetURI is mapped specifically to a java package name, then
     * this method should NOT be used.
     * Creation date: (6/21/00 2:25:18 PM)
     * @return java.lang.String
     */
    public static String schema2JavaName(Node node, String targetURI) {
	if (node == null || targetURI == null)
    throw new IllegalArgumentException ("Illegal arguments to " +
                                        "'schema2JavaName'.");

	String targetNSPrefix = namespaceURI2JavaPath(targetURI) + ".";
	String name = DOMUtils.getAttribute((Element) node, "name");
	String type = node.getLocalName();
	
	if (name == null || type == null)
	    return null;
	else {
	    name = schema2JavaName (type, name, true);
	    return targetNSPrefix + name;
	}
	
    }
    public static String schema2JavaName(NodeList nl, NodeList targetURI) {
	if (nl.getLength() == 0)
	{
    throw new IllegalArgumentException("No type name found for serializer " +
                                       "class.");
	}
	Node node = nl.item(0);
	
	if (targetURI.getLength() == 0)
  {
    throw new IllegalArgumentException("No type name found for serializer " +
                                       "class.");
	}
	Node tnode = targetURI.item(0);
	String targetNS = tnode.getNodeValue();
	
	// now produce the fully qualified name

	return schema2JavaName(node, targetNS);
	
    }

    public static String schema2NonQualifiedJavaName(NodeList nl, NodeList targetURI) {
    
	String fullname = schema2JavaName(nl, targetURI);
	StringTokenizer st = new StringTokenizer(fullname, ".");
	String name = null;
	while( st.hasMoreTokens() )
	    {
		name = st.nextToken();
	    }
	
	return name;
    }

    public static String getJavaPathName(String targetDirectoryName,
                                         String packageName)
    {
      if (packageName != null && !packageName.equals(""))
      {
        targetDirectoryName += File.separatorChar
                               + packageName.replace('.', File.separatorChar);
      }

      return targetDirectoryName;
    }

  public static String getJavaFileName(NodeList nl, NodeList targetURI,
                                       String javaFileSuffix)
  {
    String javaFileName = schema2NonQualifiedJavaName(nl, targetURI) +
                          javaFileSuffix + ".java";

    return javaFileName;
  }

  public static boolean JDKcompile(String fileName, String workingDirectory)
    throws IllegalArgumentException
  {
    String classPath = System.getProperty("java.class.path");

    if (workingDirectory != null && !workingDirectory.equals(""))
    {
      classPath += System.getProperty("path.separator") + workingDirectory;
    }

    String args[] = {"-classpath", classPath, fileName};

    try
    {
      return new sun.tools.javac.Main(System.err, "javac").compile(args);
    }
    catch (Throwable th)
    {
      System.err.println("Unable to load JDK compiler.");

      return false;
    }
  }

  public static void setVerbose(boolean ver)
  {
    verbose = ver;
  }

  public static boolean getVerbose()
  {
    return verbose;
  }
}
