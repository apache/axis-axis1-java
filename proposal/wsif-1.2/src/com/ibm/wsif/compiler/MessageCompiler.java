// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler;

import java.io.*;
import java.util.*;
import javax.wsdl.*;
import org.w3c.dom.*;
import org.apache.soap.Constants;
import org.apache.soap.util.xml.DOM2Writer;
import org.apache.soap.util.xml.DOMUtils;
import com.ibm.wsdl.util.*;
import com.ibm.wsdl.xml.*;
import com.ibm.wsdl.extensions.java.JavaExtensionRegistry;
import com.ibm.wsdl.extensions.format.FormatExtensionRegistry;
import com.ibm.wsif.*;
import com.ibm.wsif.stub.*;
import com.ibm.wsif.compiler.schema.*;
import com.ibm.wsif.compiler.schema.tools.*;
import com.ibm.wsif.compiler.util.*;
import com.ibm.bsf.util.ObjInfo;
import com.ibm.bsf.util.CodeBuffer;

/**
 * The WSIF Message compiler. 
 * 
 * WSIF includes compilation of a number of entities, one being
 * the WSIFMessage. Compiling WSIFMessages provides specific objects
 * that only support a given message signature. This allows generated
 * code that knows the message is compiled to access the parts of the 
 * message directly, with a very considerable performance improvement
 * compared to the standard WSIFMessage interface.
 * 
 * @author Paul Fremantle
 * @author Matt Duftler
 * @author Ant Elder
 */
public class MessageCompiler {
	
   private Map byTypes;
   private StringBuffer getPartsStmnts;
   private StringBuffer setPartsStmnts;
   private StringBuffer getObjectStmnts;
   private StringBuffer setObjectStmnts;
   private StringBuffer constructorCopyStmnts;

   private String outputDirectory;
   private boolean verbose = false;
   private boolean overwrite = false;
   private boolean includeCopyMethods = false;
   
   private static final String[] basicTypes =
      { "boolean", "char", "string", "byte", "short", "int", 
        "long", "float", "double", "Object" };

   private static final String SCHEMA_URI = Constants.NS_URI_1999_SCHEMA_XSD;
   	
   /**
    * This creates a Java class source file for each message
    * in a WSDL Definition.
    * 
    * @param def   the WSDL Definition 
    */
   public void compileMessages(Definition def) throws WSIFException {
      Map messages = def.getMessages();
	  Iterator messageNames = messages.keySet().iterator();
	  while (messageNames.hasNext()) {
 		 Message msg = (Message)messages.get(messageNames.next());
		 makeClass( msg );
	  }
   }
   
   /**
    * This outputs a .java source file for a WSDL message
    */
   private void makeClass(Message msg) {
   	
      includeCopyMethods = false;
      getPartsStmnts = new StringBuffer();
      setPartsStmnts = new StringBuffer();
      getObjectStmnts = new StringBuffer();
      setObjectStmnts = new StringBuffer();
      constructorCopyStmnts = new StringBuffer();
      byTypes = new HashMap();
	  for (int i = 0; i< basicTypes.length; i++) {
	     byTypes.put(basicTypes[i], new LinkedList());
	  }
		 
      CodeBuffer cb = new CodeBuffer();

      cb.setPackageName(getPackageName(msg.getQName().getNamespaceURI()));
      cb.setClassName( getClassName( msg.getQName() ) );
      cb.addImplements("WSIFMessage");
      cb.addConstructorStatement( "" ); // otherwise wont make no args constructor
      
      makeImports( cb );
      makeNameAccessorMethods( cb, msg );
      makeGetPartNamesMethod( cb );
      makeNamedPartAccessorMethods( cb, msg );
      makeTypedPartAccessorMethods( cb );
      makeObjectPartAccessorMethods( cb );
      makePartsAccessorMethods( cb );

      makeConstructor( cb, msg ); // must be after makeNamedPartAccessorMethods call

      makeRepresentationAccessorMethods( cb );

      makeCloneMethod( cb, getClassName( msg.getQName() ) );

      if ( includeCopyMethods ) {
         makeCopyMethod( cb );
         makeCopybyCloningMethod( cb );
         makeCopybySerilizationMethod( cb );
      }

      writeClassToFile( cb );		   	

   }
   
   private void makeImports(CodeBuffer cb) {
      cb.addImport("com.ibm.wsif.WSIFMessage");
      cb.addImport("com.ibm.wsif.WSIFException");
      cb.addImport("java.io.*");
      cb.addImport("java.util.Map");
      cb.addImport("java.util.List");
      cb.addImport("java.util.Iterator");
   }

   private void makeNameAccessorMethods(CodeBuffer cb, Message msg) {
		 cb.addMethodDeclaration("public String getName() {\n" +
		    "return \"" + msg.getQName().getLocalPart()+"\";\n" +
		    "}");
		 cb.addMethodDeclaration("public void setName(String s) { }");
   }

   private void makeGetPartNamesMethod(CodeBuffer cb) {
      cb.addMethodDeclaration(
         "public java.util.Iterator getPartNames() {\n" +
         "List resp = new java.util.LinkedList(); \n"+
         "for(int i=0; i < partNames.length ; i++) {\n" + 
         "resp.add(partNames[i]);\n}\n" + 
         "return resp.iterator();\n}");
   }

   private void makeNamedPartAccessorMethods(CodeBuffer cb, Message msg) {
      Map parts = msg.getParts();
      Iterator partNames = parts.keySet().iterator();
      String comma = "";
      cb.addFieldDeclaration("private String partNames[] = { ");
      while (partNames.hasNext()) {
         cb.addFieldDeclaration(comma + "\""+(String)partNames.next()+"\"" );
         comma = ",";
      }
      cb.addFieldDeclaration(" };");
      
      partNames = parts.keySet().iterator();
      while (partNames.hasNext()) {
         Part part = (Part)parts.get(partNames.next());
         QName typeName = part.getTypeName();

         String type = typeName.getLocalPart();
         if ( typeName.getNamespaceURI().equals( SCHEMA_URI ) 
         && byTypes.get( type ) != null ) { // basic type
            ((List)byTypes.get(type)).add( part.getName() );
            if ( type.equals( "string" ) ) {
               type = "String";
            }
            cb.addFieldDeclaration(
               "protected " + type +" _" + part.getName() + ";"
            );
            cb.addMethodDeclaration(
               "public " + type + " getPt_" + 
               capitalizeFirst( part.getName() ) + "() { " +
               " return _" + part.getName() + "; } "
            );
            cb.addMethodDeclaration(
               "public void setPt_" + 
               capitalizeFirst( part.getName() ) + 
               "(" + type + " value) { this._" + 
               part.getName() + " = value; } "
            );
            addPartToConstructorCopy( part.getName(), type );
         }   else { // complex Type
            ((List)byTypes.get( "Object" )).add( part.getName() );
            cb.addFieldDeclaration( "Object _" + part.getName() + ";" );
            cb.addMethodDeclaration(
               "public Object getPt_" + capitalizeFirst( part.getName() ) +
               "() { " + " return _" + part.getName() + "; } "
            );
            cb.addMethodDeclaration(
               "public void setPt_" + capitalizeFirst( part.getName() ) +
               "(Object value) { " + "this._" + part.getName() + " = value; } "
            );
            addPartToConstructorCopy( part.getName(), "Object" );
         }
      }
   }

   private void makeTypedPartAccessorMethods(CodeBuffer cb) {
      for (int i = 0; i< basicTypes.length; i++) {
      	 
         String type = basicTypes[i];
         if ( type.equals("Object") ) {
            Iterator it = ((List)byTypes.get(basicTypes[i]) ).iterator();
            buildStatements( it, type ); 
         	continue;  // Objects done in makeObjectPartAccessorMethods 
         }
      	 if ( type.equals("string") ) {
            type = "String";
         }
         cb.addMethodDeclaration(
            "public " + type + " get" + capitalizeFirst(type) + 
            "Part(String partName) {"
         );
				
         Iterator it = ( (List) byTypes.get( basicTypes[i] ) ).iterator();
         while ( it.hasNext() ) {
            String partName = (String) it.next();
            cb.addMethodDeclaration(
               "if (partName.equals(\"" + partName + "\"))  { return " +
               "_" + partName + "; } else "
            );
         }
         cb.addMethodDeclaration(
            "throw new IllegalArgumentException(\"Part \"+partName+\" not found\"); }"
         );
         
         cb.addMethodDeclaration(
            "public void set" + capitalizeFirst( type ) + 
            "Part(String partName, " + type + " value) {"
         );
         it = ((List)byTypes.get( basicTypes[i] )).iterator();
         while ( it.hasNext() ) {
            String partName = (String)it.next();
            cb.addMethodDeclaration(
            "if (partName.equals(\""+partName+"\"))  { this._"+partName+" = value; } else ");
         }
         cb.addMethodDeclaration("throw new IllegalArgumentException(\"Part \"+partName+\" not found\"); }");

         it = ((List)byTypes.get(basicTypes[i]) ).iterator();
         buildStatements( it, type ); 

      }
   }

   private void makeObjectPartAccessorMethods(CodeBuffer cb) {
      cb.addMethodDeclaration(
         "public Object getObjectPart(String partName) {"
      );
      cb.addMethodDeclaration(getObjectStmnts.toString());
      cb.addMethodDeclaration(
         "throw new IllegalArgumentException(\"Part \"+partName+\" not found\");"
      );
      cb.addMethodDeclaration("}");

      cb.addMethodDeclaration(
         "public Object getObjectPart(String partName, Class sourceClass) {"
      );
      cb.addMethodDeclaration(getObjectStmnts.toString());
      cb.addMethodDeclaration(
         "throw new IllegalArgumentException(\"Part \"+partName+\" not found\");"
      );
      cb.addMethodDeclaration("}");

      cb.addMethodDeclaration(
         "public void setObjectPart(String partName, Object value) {"
      );
      cb.addMethodDeclaration(setObjectStmnts.toString());
      cb.addMethodDeclaration(
         "throw new IllegalArgumentException(\"Part \"+partName+\" not found\");"
      );
      cb.addMethodDeclaration("}");

   }

   /**
    * Adds info to the map and setPartsMap Hashtables 
    */
   private void buildStatements(Iterator it, String type) {
      while ( it.hasNext() ) {
         String partName = (String)it.next();
         if ( type.equals( "Object" ) ) {
            getPartsStmnts.append( 
               "resp.put(\"" + partName + "\", _" + partName + ");"
            );	
            setPartsStmnts.append( 
               "_" + partName + " =  source.get(\"" + partName + "\");" 
            );
            getObjectStmnts.append(
               "if (partName.equals(\"" + partName + "\")) { return _" + partName + "; } else " 
            );
            setObjectStmnts.append(
               "if (partName.equals(\"" + partName + "\")) { this._" + partName + 
               " = (" + type + ")value; } else "
            );
         } else if ( type.equals( "String" ) ) {
            getPartsStmnts.append( 
               "resp.put(\"" + partName + "\", _" + partName + ");" 
            );	
            setPartsStmnts.append( 
               "_" + partName + " =  (String)source.get(\"" + partName + "\");" 
            );					
            getObjectStmnts.append(
               "if (partName.equals(\"" + partName + "\")) { return _" + partName + "; } else " 
            );
            setObjectStmnts.append(
               "if (partName.equals(\"" + partName + "\")) { this._" + partName + 
               " = (" + type + ")value; } else "
            );
         } else {
            String typeObject = 
               type.equals( "int" ) ? "Integer" : capitalizeFirst(type);
            getPartsStmnts.append( 
               "resp.put(\"" + partName + "\", new " +
               typeObject + "(_" + partName + "));"
            );
            setPartsStmnts.append(
               "_" + partName + " = ((" + typeObject + ")source.get(\"" +
               partName + "\"))." + type + "Value();"
            );
            getObjectStmnts.append(
               "if (partName.equals(\"" + partName + "\")) { return new " + 
               typeObject + "(_" + partName + "); } else "
            );
            setObjectStmnts.append(
               "if (partName.equals(\"" + partName + "\")) { this._" + partName + 
               " = ((" + typeObject + ")value)." + type + "Value(); } else "
            );
         }
      }
   }
   
   private void makePartsAccessorMethods(CodeBuffer cb) {
      cb.addMethodDeclaration(
         "public Iterator getParts() {\n" +
         "Map resp = new java.util.HashMap(); "
      );
      cb.addMethodDeclaration(getPartsStmnts.toString());
      cb.addMethodDeclaration("return resp.values().iterator();");
      cb.addMethodDeclaration(" }");

      cb.addMethodDeclaration(
         "public void getParts(Map resp) {" 
      );
      cb.addMethodDeclaration(getPartsStmnts.toString());
      cb.addMethodDeclaration(" }");

      cb.addMethodDeclaration("public void setParts(Map source) {");
      cb.addMethodDeclaration(setPartsStmnts.toString());
      cb.addMethodDeclaration("}");
   }

   // Really this should use the CodeBuffer addConstructorXXX methods
   // but this requires that constructor arguments be passed as Class
   // objects, and as the compiled message class hasn't been compiled
   // yet you can't make a Class object for them. Using addMethodDeclaration
   // seems to work ok for now as CodeBuffer doesn't check that a return 
   // type has been specified. 
   // This must be called after the makeNamedPartAccessorMethods call as that 
   // method builds up fieldCopyStmnts with calls to addPartToConstructorCopy. 
   private void makeConstructor(CodeBuffer cb, Message msg) {
      String cn = getClassName( msg.getQName() );
      cb.addMethodDeclaration( 
         "private " + cn + "(" + cn + " msg) throws WSIFException {" 
      );
      cb.addMethodDeclaration(
         "this.partNames = msg.partNames;"
      );
      cb.addMethodDeclaration(
         "this.representationStyle = msg.representationStyle;"
      );
      cb.addMethodDeclaration( constructorCopyStmnts.toString() );

      cb.addMethodDeclaration( "}" );
   }

   private void addPartToConstructorCopy(String partName, String type) {
      String s;
      if ( "String".equals( type ) ) {
         s = "this._" + partName + " = new String( msg._" + partName + " );";
      } else if ( "Object".equals( type ) ) {
         includeCopyMethods = true;
         s = "this._" + partName + "= copy( msg._" + partName + " );";
      } else {
         s = "this._" + partName + " = msg._" + partName + ";";
      }

      constructorCopyStmnts.append( s );

   }

   private void makeRepresentationAccessorMethods(CodeBuffer cb) {
      cb.addFieldDeclaration( "private String representationStyle;" );

      cb.addMethodDeclaration( "public String getRepresentationStyle() {" );
      cb.addMethodDeclaration( "return representationStyle;" );
      cb.addMethodDeclaration( "}" );

      cb.addMethodDeclaration( "public void setRepresentationStyle(String style) {" );
      cb.addMethodDeclaration( "representationStyle = style;" );
      cb.addMethodDeclaration( "}" );
   }

   private void makeCloneMethod(CodeBuffer cb, String className) {
      cb.addMethodDeclaration(
         "public Object clone() throws CloneNotSupportedException {" 
	  );
	  cb.addMethodDeclaration( "try {" );
	  cb.addMethodDeclaration( "return new " + className + "( this );" );
	  cb.addMethodDeclaration( "} catch (WSIFException ex) {" );
	  cb.addMethodDeclaration( "throw new CloneNotSupportedException( ex.getMessage() );" );
	  cb.addMethodDeclaration( "}" );
	  cb.addMethodDeclaration( " }" );
   }

   private void makeCopyMethod(CodeBuffer cb) {
      cb.addMethodDeclaration(
         "public Object copy(Object o) throws WSIFException {"
  	  );
	  cb.addMethodDeclaration( "if ( o == null ) {" );
	  cb.addMethodDeclaration( "return null;" );
	  cb.addMethodDeclaration( "} else if ( o instanceof Cloneable ) {" );
	  cb.addMethodDeclaration( "return copyByCloning( o );" );
	  cb.addMethodDeclaration( "} else if (o instanceof Serializable) {" );
	  cb.addMethodDeclaration( "return copyBySerialization( o );" ); 
	  cb.addMethodDeclaration( "} else {" );
	  cb.addMethodDeclaration( "throw new WSIFException( \"can't copy WSIFMessage part: \" + o );" );
	  cb.addMethodDeclaration( "}" );
	  cb.addMethodDeclaration( "}" );
   }
 
   private void makeCopybyCloningMethod(CodeBuffer cb) {
      cb.addMethodDeclaration(
         "private Object copyByCloning(Object o) throws WSIFException {"
	  );
	  cb.addMethodDeclaration( "Class cls = o.getClass();" );
	  cb.addMethodDeclaration( "try {" );
	  cb.addMethodDeclaration( "java.lang.reflect.Method m = cls.getMethod( \"clone\", null );" );
	  cb.addMethodDeclaration( "return m.invoke( o, null );" );
	  cb.addMethodDeclaration( "} catch(Exception ex) {" );
	  cb.addMethodDeclaration( 
        "throw new WSIFException( \"error cloning WSIFMessage part \" + o + \": \" + ex.getMessage() );" );
	  cb.addMethodDeclaration( "}" );
	  cb.addMethodDeclaration( "}" );
  }

   private void makeCopybySerilizationMethod(CodeBuffer cb) {
      cb.addMethodDeclaration(
         "private Object copyBySerialization(Object o) throws WSIFException {"
      );
	  cb.addMethodDeclaration( "try {" );
	  cb.addMethodDeclaration( "ByteArrayOutputStream b = new ByteArrayOutputStream();" );
	  cb.addMethodDeclaration( "ObjectOutputStream out = new ObjectOutputStream( b );" );
	  cb.addMethodDeclaration( "out.writeObject( o );" );
	  cb.addMethodDeclaration( "out.flush();" );
	  cb.addMethodDeclaration( "out.close();" );
	  cb.addMethodDeclaration( "byte[] data = b.toByteArray();" );
	  cb.addMethodDeclaration( "ObjectInputStream in = new ObjectInputStream(" );
	  cb.addMethodDeclaration( "new ByteArrayInputStream( data ) );" );
	  cb.addMethodDeclaration( "Object poc = in.readObject();" );
	  cb.addMethodDeclaration( "in.close();" );
	  cb.addMethodDeclaration( "return poc;" );
	  cb.addMethodDeclaration( "} catch(Exception ex) {" );
	  cb.addMethodDeclaration( 
	     "throw new WSIFException( \"error serialising WSIFMessage part \" + o + \": \" + ex.getMessage() );" );
	  cb.addMethodDeclaration( "}" );
	  cb.addMethodDeclaration( "}" );
   }

   private void writeClassToFile(CodeBuffer cb) {
      String fn = getFileName( cb );
      try {
         System.out.println( "Writing file: " + fn );
         createPath( cb );
         File f = new File( fn );
         if ( !overwrite && f.exists() ) {
         	System.err.println( fn + " already exists, skipping. Use option -overwrite" );
         } else {
            FileOutputStream fos = new FileOutputStream( f );
            cb.print( new PrintWriter(fos), true  );
            fos.close();
         }
      } catch (Exception ex) {
         System.err.println( "Error writing file " + fn );
    	 ex.printStackTrace();
      }   
   }

   /**
    * Returns the full file name including the working directory,
    * package name, and class name. 
    */    
   private String getFileName(CodeBuffer cb) {
      String s = outputDirectory + "." + cb.getPackageName() + '.' + cb.getClassName();
      s = s.replace( '.', File.separatorChar ) ;
      return s + ".java";
   }
 
   /**
    * Checks that the path to the output file exists (both the working directory 
    * and the package name) and creates it if not.
    */    
   private void createPath(CodeBuffer cb) throws IOException { 
	  String s = outputDirectory + "." + cb.getPackageName();
	  s = s.replace( '.', File.separatorChar ) ;
	  File dirs = new File( s );
	  dirs.mkdirs();
   }
 
   protected String getPackageName(String nsURI) {
      String packageString = "";
	  try {
	     java.net.URL url = new java.net.URL( nsURI );
         String host = url.getHost();
         StringTokenizer st = new StringTokenizer( host, "." );
		 while ( st.hasMoreTokens() ) {
		    packageString = st.nextToken() + 
		                    ( packageString.equals( "" ) ? "" : "." + packageString );
         }
		
		 String path = url.getPath();
		 st = new StringTokenizer( path, "/" );
		 while ( st.hasMoreTokens() ) {
		    packageString = ( packageString.equals( "" ) ? "" : packageString + "." ) +
		                    st.nextToken();			
		 }
      } catch (java.net.MalformedURLException mue) {
	     mue.printStackTrace();
	  }		
	  return packageString;
   }

   private String getClassName(QName qn) {
      return qn.getLocalPart() + "Message";
   }
   
   protected String capitalizeFirst(String name) {
      char first[] = { (char) ( (name.charAt(0) & (char)223) ) };
      return (new String( first) ).concat( name.substring(1) );
   }

   private void setVerbose(boolean b) {
      verbose = b;
   }
   
   private void setOverwrite(boolean b) {
      overwrite = b;
   }
   
   private void setOutputDir(String dir) {
      outputDirectory = dir;
   }

   private static void validateArgs(String[] args, String[] options, int nrArgs) {
   	  if ( args.length == 0 ) {
   	     printUsage();
   	  }
   	  
      // check only valid options specified 
      boolean foundArg = false;
   	  int optionsRemaining = options.length;
      for ( int i=0; i<args.length; i++ ) {
      	if ( args[i].startsWith( "-" ) ) {
      	   if ( foundArg ) {
      	      printUsage();
      	   } else {
      	   	  if ( optionsRemaining < 1 ) {
      	   	     printUsage();
      	   	  } else {
      	   	  	 boolean foundOption = false;
      	         for (int j=0; j<options.length; j++) {
      	            if ( options[j].equals( args[i].substring(1) ) ) {
      	      	       foundOption = true;
      	            }
      	         }
      	         if ( !foundOption ) {
      	         	printUsage();
      	         }
      	   	  }
      	   }
      	} else {  // doesn't start with "-"
      	   foundArg = true;
      	}
      }
      	
      // check atleast one and no more than nrArgs arguments specified
   	  int argsFound = 0;
      for ( int i=0; i<args.length; i++ ) {
         if ( !args[i].startsWith( "-" ) ) {
      	    argsFound++;
      	 }
      }
      if ( argsFound < 1 || argsFound > nrArgs ) {
         printUsage();
      }
   } 		

   private static boolean getArgOption(String[] args, String option) {
      for ( int i=0; i<args.length; i++ ) {
         if ( !args[i].startsWith("-") ) {
         	return false;
         }
         String o = args[i].substring(1);
         if ( option.startsWith( o ) ) {
         	return true;
         }
      }
      return false;
   } 		

   private static String getArg(String[] args, int argNr) {
      for ( int i=0; i<args.length; i++ ) {
         if ( !args[i].startsWith("-") ) {
         	argNr--;
         	if ( argNr < 1 ) {
         		return args[i];
         	}
         }
      }
      return null;
   } 		

   /**
    * Prints help on how to use the compiler.
    * 
    * There is one compulsory argument:
    *    wsdlLocation   this is the WSDL containg the messages to be compiled
    * 
    * There is one optional argument:
    *    outputLocation  this is the directory where the compiled message 
    *                    Java .java source will be written. It defaults to
    *                    the current working directory if not specified. 
    *  
    * There are two options:
    *    -verbose    which prints info about what the compiler is doing
    *    -overwrite  which causes existing files to be overwritten
    */
   private static void printUsage() {
      System.err.println("Usage: java " + MessageCompiler.class.getName() +
                         " <options>" +
                         " wsdlLocation" +
                         " [outputLocation]");
      System.err.println("where posible options include:" );
      System.err.println(" -verbose    output messages about what" +
                         " the compiler is doing" );
      System.err.println(" -overwrite  overwrite existing files in" +
                         " the outputLocation directory" );
      System.err.println("If outputLocation is not specified it" +
                         " defaults to the current working directory." );
      System.exit( 1 );
   }

   /**
    * Compile WSDL messages.
    * @param args   the command line parameters, see printUsage for details.
    */
   public static void main(String[] args) {

      String[] options = { "verbose", "overwrite" };
      boolean optVerbose, optOverwrite;
      int nrArgs = 2;
      String argSource, argOutputDir;
      
      validateArgs( args, options, nrArgs );
      
      optVerbose = getArgOption( args, "verbose" );
      optOverwrite = getArgOption( args, "overwrite" );
      
      argSource = getArg( args, 1 );
      argOutputDir = getArg( args, 2 );
      if ( argOutputDir == null ) {
         argOutputDir = System.getProperty("user.dir");
      }

 	  try {
 	     //javax.wsdl.xml.WSDLReader reader = javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader();
 	     javax.wsdl.xml.WSDLReader reader = 
 	        (new WSIFPrivateWSDLFactoryImpl()).newWSDLReader();
   		 Definition def = reader.readWSDL( null, argSource );
 		 MessageCompiler mc = new MessageCompiler();
 		 mc.setVerbose( optVerbose );
 		 mc.setOverwrite( optOverwrite );
         mc.setOutputDir( argOutputDir ); 		 
 		 mc.compileMessages( def ); 
 	  } catch (Exception e) {
 		 e.printStackTrace();
 	  }
 	    
   }

}
