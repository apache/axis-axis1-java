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
import com.ibm.wsdl.extensions.jms.JmsExtensionRegistry;
import com.ibm.wsdl.extensions.java.JavaExtensionRegistry;
import com.ibm.wsdl.extensions.format.FormatExtensionRegistry;
import com.ibm.wsif.*;
import com.ibm.wsif.stub.*;
import com.ibm.wsif.compiler.schema.*;
import com.ibm.wsif.compiler.schema.tools.*;
import com.ibm.wsif.compiler.util.*;

/**
 * PortType compile main class.
 *
 * @author Matthew J. Duftler
 * @deprecated
 */

public class PortTypeCompiler
{
  private static final String lineSeparator = StringUtils.lineSeparator;
  private static final String FILE_SEPARATOR =
    System.getProperty("file.separator");
  private String schemaURI = Constants.NS_URI_1999_SCHEMA_XSD;
  private QName qElemSchema = new QName(schemaURI, "schema");
  private String workingDirectory = System.getProperty("user.dir");
  private boolean verbose = true;
  private boolean overwrite = true;
  private StreamFactory streamFactory = new StreamFactory();
  private Hashtable typeReg = new Hashtable();
  private Schema2Java s2j = new Schema2Java(schemaURI);
  
  public PortTypeCompiler()
  {
    s2j.setWorkingDirectory(workingDirectory);
    s2j.setVerbose(verbose);
    s2j.setOverwrite(overwrite);
  }
  
  /**
   * Defaults to <em>http://www.w3.org/1999/XMLSchema</em>.
   */
  public void setSchemaURI(String schemaURI)
  {
    this.schemaURI = schemaURI;
    this.qElemSchema = new QName(schemaURI, "schema");
    
    s2j = new Schema2Java(schemaURI);
    s2j.setWorkingDirectory(workingDirectory);
    s2j.setVerbose(verbose);
    s2j.setOverwrite(overwrite);
  }
  
  public String getSchemaURI()
  {
    return schemaURI;
  }
  
  /**
   * Working directory for generated files. If the target code is placed
   * in a package, the directories for the package hierarcy go
   * <b>below</b> this directory.  Defaults to ".".
   */
  public void setWorkingDirectory(String workingDirectory)
  {
    this.workingDirectory = workingDirectory;
    s2j.setWorkingDirectory(workingDirectory);
  }
  
  public String getWorkingDirectory()
  {
    return workingDirectory;
  }
  
  public void setVerbose(boolean verbose)
  {
    this.verbose = verbose;
    s2j.setVerbose(verbose);
    Conventions.setVerbose(verbose);
  }
  
  public boolean getVerbose()
  {
    return verbose;
  }
  
  public void setOverwrite(boolean overwrite)
  {
    this.overwrite = overwrite;
    s2j.setOverwrite(overwrite);
  }
  
  public boolean getOverwrite()
  {
    return overwrite;
  }
  
  public void compilePortType(PortType portType,
                              Definition def)
    throws WSIFException
  {
    generateTypes(def);
    
    if (verbose)
    {
      System.out.println(">> Generating stub ..");
    }
    
    String packageName =
      Conventions.namespaceURI2JavaPath(def.getTargetNamespace());
    QName portTypeQName = portType.getQName();
    String iFaceName = portTypeQName.getLocalPart();
    String className = iFaceName + "Stub";
    String javaPathName = Conventions.getJavaPathName(workingDirectory,
                                                      packageName);
    OutputStream iFaceOS = streamFactory.getOutputStream(javaPathName,
                                                         iFaceName + ".java",
                                                         overwrite);
    PrintWriter iFacePW = new PrintWriter(iFaceOS);
    OutputStream os = streamFactory.getOutputStream(javaPathName,
                                                    className + ".java",
                                                    overwrite);
    PrintWriter pw = new PrintWriter(os);
    StringWriter importSW = new StringWriter();
    PrintWriter importPW = new PrintWriter(importSW);
    StringWriter mapSW = new StringWriter();
    PrintWriter mapPW = new PrintWriter(mapSW);
    StringWriter iFaceBodySW = new StringWriter();
    PrintWriter iFaceBodyPW = new PrintWriter(iFaceBodySW);
    StringWriter classBodySW = new StringWriter();
    PrintWriter classBodyPW = new PrintWriter(classBodySW);
    List operations = portType.getOperations();
    
    printMappings(packageName, mapPW, importPW);
    printOperations(operations, iFaceBodyPW, classBodyPW);
    serializeDefinition(def, classBodyPW);
    
    importPW.flush();
    mapPW.flush();
    iFaceBodyPW.flush();
    classBodyPW.flush();
    
    iFacePW.println("package " + packageName + ';' + lineSeparator +
                      lineSeparator +
                      "import java.rmi.Remote;" + lineSeparator +
                      "import java.rmi.RemoteException;" + lineSeparator +
                      importSW + lineSeparator +
                      "/**" + lineSeparator +
                      " * This shows an example of a generated Java " +
                      "interface that" + lineSeparator +
                      " * corresponds to a WSDL port type." + lineSeparator +
                      " */" + lineSeparator +
                      "public interface " + iFaceName + " extends Remote" +
                      lineSeparator +
                      "{" + lineSeparator +
                      iFaceBodySW + '}');
    
    String mapString = mapSW.toString();
    
    pw.println("package " + packageName + ';' + lineSeparator +
                 lineSeparator +
                 "import java.util.*;" + lineSeparator +
                 "import javax.wsdl.*;" + lineSeparator +
                 "import com.ibm.wsdl.xml.*;" + lineSeparator +
                 "import com.ibm.wsif.*;" + lineSeparator +
                 "import com.ibm.wsif.stub.*;" + lineSeparator +
                 importSW + lineSeparator +
                 "/**" + lineSeparator +
                 " * This shows an example of using the JNDI registry at " +
                 "runtime" + lineSeparator +
                 " * to locate a port factory. If the JNDI lookup fails, " +
                 "then the" + lineSeparator +
                 " * WSDL document is used to initialize a dynamic port " +
                 "factory." + lineSeparator +
                 " * The port factory is then consulted for a suitable port " +
                 "for" + lineSeparator +
                 " * this stub to use." + lineSeparator +
                 " */" + lineSeparator +
                 "public class " + className + " extends WSIFStub" +
                 lineSeparator +
                 "  implements " + iFaceName +
                 lineSeparator +
                 "{" + lineSeparator +
                 "  private final String DOCUMENT_BASE =" + lineSeparator +
                 "    \"" +
                 StringUtils.cleanString(def.getDocumentBaseURI()) +
                 "\";" +
                 lineSeparator +
                 "  private final String PORT_TYPE_NS =" + lineSeparator +
                 "    \"" + portTypeQName.getNamespaceURI() + "\";" +
                 lineSeparator +
                 "  private final String PORT_TYPE_NAME = \"" +
                 portTypeQName.getLocalPart() + "\";" + lineSeparator +
                 lineSeparator +
                 "  /**" + lineSeparator +
                 "   * Create a new stub using a port factory obtained via " +
                 "JNDI. If that" + lineSeparator +
                 "   * doesn't work, use the WSDL document provided at " +
                 "compile-time to" + lineSeparator +
                 "   * initialize a dynamic port factory. If serviceNS and " +
                 "serviceName" + lineSeparator +
                 "   * are not null, they will be used in selecting a " +
                 "service." + lineSeparator +
                 "   */" + lineSeparator +
                 "  public " + className + "(String serviceNS, " +
                 "String serviceName) " + lineSeparator +
                 "    throws WSIFException" + lineSeparator +
                 "  {" + lineSeparator +
                 "    locatePortFactory(" + lineSeparator +
                 "      null," + lineSeparator +
                 "      DOCUMENT_BASE," + lineSeparator +
                 "      WSDL_DEFINITION_STR," + lineSeparator +
                 "      serviceNS," + lineSeparator +
                 "      serviceName," + lineSeparator +
                 "      PORT_TYPE_NS," + lineSeparator +
                 "      PORT_TYPE_NAME);" + lineSeparator +
                 "  }" + lineSeparator + lineSeparator +
                 "  /**" + lineSeparator +
                 "   * Create a new stub using a port factory obtained via " +
                 "JNDI. If that" + lineSeparator +
                 "   * doesn't work, use the specified WSDL document to " +
                 "initialize a" + lineSeparator +
                 "   * dynamic port factory. If serviceNS and serviceName " +
                 "are not null," + lineSeparator +
                 "   * they will be used in selecting a service." +
                 lineSeparator +
                 "   */" + lineSeparator +
                 "  public " + className + "(Definition def," +
                 lineSeparator +
                 "    String serviceNS," + lineSeparator +
                 "    String serviceName) " +
                 "throws WSIFException" + lineSeparator +
                 "  {" + lineSeparator +
                 "    locatePortFactory(" + lineSeparator +
                 "      def," + lineSeparator +
                 "      null," + lineSeparator +
                 "      null," + lineSeparator +
                 "      serviceNS," + lineSeparator +
                 "      serviceName," + lineSeparator +
                 "      PORT_TYPE_NS," + lineSeparator +
                 "      PORT_TYPE_NAME);" + lineSeparator +
                 "  }" + lineSeparator + lineSeparator +
                 ((mapString.length() > 0)
                    ? "  /**" + lineSeparator +
                    "   * This overrides default method by adding type " +
                    "mappings." + lineSeparator +
                    "   */" + lineSeparator +
                    "  protected void initializeMappings() throws " +
                    "WSIFException" + lineSeparator +
                    "  {" + lineSeparator +
                    "    if (wpf instanceof WSIFService)" +
                    lineSeparator +
                    "    {" + lineSeparator +
                    "      WSIFService dwpf = " +
                    "(WSIFService)wpf;" + lineSeparator +
                    lineSeparator +
                    mapString +
                    "    }" + lineSeparator +
                    "  }" + lineSeparator + lineSeparator
                    : "") +
                 classBodySW + '}');
    
    try
    {
      iFacePW.flush();
      iFaceOS.close();
      pw.flush();
      os.close();
    }
    catch (IOException e)
    {
      throw new WSIFException("Problem closing streams.", e);
    }
    
    String fileName = javaPathName + FILE_SEPARATOR + iFaceName + ".java";
    
    if (Conventions.JDKcompile(fileName, workingDirectory) && verbose)
    {
      System.out.println("Compiled file '" + fileName + "'.");
    }
    
    fileName = javaPathName + FILE_SEPARATOR + className + ".java";
    
    if (Conventions.JDKcompile(fileName, workingDirectory) && verbose)
    {
      System.out.println("Compiled file '" + fileName + "'.");
    }
  }
  
  private void serializeDefinition(Definition def,
                                   PrintWriter pw) throws WSIFException
  {
    try
    {
      StringWriter sw = new StringWriter();
      
      WSIFUtils.writeWSDL(def, sw);
      
      String defStr = sw.toString();
      
      defStr = Utils.getQuotedString(new StringReader(defStr), 4);
      
      pw.println(lineSeparator +
                   "  private static final String WSDL_DEFINITION_STR =" +
                   lineSeparator +
                   defStr + ';');
    }
    catch (WSDLException e)
    {
      throw new WSIFException("Problem writing definition.", e);
    }
  }
  
  private void printMappings(String classPackageName,
                             PrintWriter mapPW,
                             PrintWriter importPW)
  {
    Iterator typeMappingIterator = typeReg.values().iterator();
    Set importSet = new HashSet();
    
    while (typeMappingIterator.hasNext())
    {
      TypeMapping tm = (TypeMapping)typeMappingIterator.next();
      
      if (tm.elementType != null)
      {
        String namespaceURI = tm.elementType.getNamespaceURI();
        
        if (namespaceURI != null
              && !namespaceURI.equals(schemaURI)
              && tm.javaType != null)
        {
          String packageName = Utils.getPackageName(tm.javaType);
          String className = Utils.getClassName(tm.javaType);
          String itemToImport = packageName + ".*";
          
          if (!packageName.equals("")
                && !packageName.equals("java.lang")
                && !packageName.equals(classPackageName)
                && !importSet.contains(itemToImport))
          {
            importSet.add(itemToImport);
          }
          
          mapPW.println("      dwpf.mapType(" + lineSeparator +
                          "        new QName(\"" + namespaceURI + "\", " +
                          lineSeparator +
                          "          \"" + tm.elementType.getLocalPart() +
                          "\"), " + className + ".class);");
        }
      }
    }
    
    if (importSet.size() > 0)
    {
      Iterator importIterator = importSet.iterator();
      
      while (importIterator.hasNext())
      {
        String itemToImport = (String)importIterator.next();
        
        importPW.println("import " + itemToImport + ';');
      }
    }
  }
  
  private void printOperations(List operations,
                               PrintWriter iFaceBodyPW,
                               PrintWriter classBodyPW)
    throws WSIFException
  {
    if (operations != null)
    {
      Iterator opIterator = operations.iterator();
      int opIndex = 0;
      
      while (opIterator.hasNext())
      {
        StringWriter methodBodySW = new StringWriter();
        PrintWriter methodBodyPW = new PrintWriter(methodBodySW);
        String outputJavaType = "void";
        Operation operation = (Operation)opIterator.next();
        String operationName = operation.getName();
        Input input = operation.getInput();
        Output output = operation.getOutput();
        String inputName = (input==null)?null:input.getName();
        String outputName = (output==null)?null:output.getName();
        Message inputMessage = (input==null)?null:input.getMessage();
        Message outputMessage = (output==null)?null:output.getMessage();
        Part outputPart = null;
        
        if (outputMessage != null)
        {
          List outputParts = outputMessage.getOrderedParts(null);
          
          if (outputParts != null && outputParts.size() > 0)
          {
            outputPart = (Part)outputParts.toArray()[0];
            
            QName typeName = outputPart.getTypeName();
            String fqClassName = Utils.queryJavaTypeName(typeName,
                                                         null,
                                                         typeReg);
            
            outputJavaType = Utils.getClassName(fqClassName);
          }
        }
        
        methodBodyPW.println("    WSIFMessage input = " +
                             "op.createInputMessage(" +
                             (inputName==null ? " " :
                             ("\"" + inputName + "\"")) +
                             ");" + lineSeparator);
        
        String methodDecl = (opIndex > 0 ? lineSeparator : "") +
          "  public " + outputJavaType + ' ' +
          operationName + '(';
        
        iFaceBodyPW.print(methodDecl);
        classBodyPW.print(methodDecl);
        
        if (inputMessage != null)
        {
          List inputParts = inputMessage.getOrderedParts(null);
          Iterator inputPartIterator = inputParts.iterator();
          int argIndex = 0;
          
          while (inputPartIterator.hasNext())
          {
            Part inputPart = (Part)inputPartIterator.next();
            String name = inputPart.getName();
            QName typeName = inputPart.getTypeName();
            String fqClassName = Utils.queryJavaTypeName(typeName,
                                                         null,
                                                         typeReg);
            String className = Utils.getClassName(fqClassName);
            
            methodDecl = (argIndex > 0 ? ", " : "") + className + ' ' + name;
            
            iFaceBodyPW.print(methodDecl);
            classBodyPW.print(methodDecl);
            
            methodBodyPW.println("    input.setObjectPart(\"" + name + "\", " 
            						+ Utils.convertToObject(className, name) +
                                   ");");
            
            argIndex++;
          }
        }
        
        methodDecl = ") throws WSIFException";
        
        //iFaceBodyPW.println(methodDecl + ';');
        iFaceBodyPW.println(") throws RemoteException;");
        classBodyPW.println(methodDecl + lineSeparator +
                              "  {" + lineSeparator +
                              "    WSIFOperation op = wp.createOperation(\"" +
                              operationName + "\"" +
                              (inputName==null ? ", null" : 
                              (", \"" + inputName + "\"")) +
                              (outputName==null ? ", null" : 
                              (", \"" + outputName + "\"")) +
                              ");" + lineSeparator +    
                              methodBodySW);
        if (output==null)
        {
          classBodyPW.println("    op.executeInputOnlyOperation(input);");
        }
        else
        {
          classBodyPW.println("    WSIFMessage output = " +
                              "op.createOutputMessage(" +
                              (outputName==null ? " " : 
                              ("\"" + outputName + "\"")) +
                              ");" + lineSeparator +
                              lineSeparator +
                              "    op.executeRequestResponseOperation(" +
                              "input, output, null);");
        }
        
        if (outputPart != null)
        {
          // MJD - debug
          // Should use cleanString().
          classBodyPW.println(lineSeparator +
                                "    Object part = " +
                                "output.getObjectPart(\"" +
                                outputPart.getName() + "\");" + lineSeparator +
                                lineSeparator +
                                "    return " +
                                Utils.convertFromObject("part",
                                                        outputJavaType) + ";");
          // MJD - debug
        }
        
        classBodyPW.println("  }");
        
        opIndex++;
      }
    }
  }
  
  private void generateTypes(Definition def) throws WSIFException
  {
    List typesElList = Utils.getAllTypesElements(def);
    
    if (typesElList.size() > 0)
    {
      if (verbose)
      {
        System.out.println(">> Generating Schema to Java bindings ..");
      }
      
      Iterator typesElIterator = typesElList.iterator();
      
      try
      {
        while (typesElIterator.hasNext())
        {
          Element typesEl = (Element)typesElIterator.next();
          Element schemaEl = DOMUtils.getFirstChildElement(typesEl);
          
          if (qElemSchema.matches(schemaEl))
          {
            s2j.createJavaMapping(schemaEl, typeReg);
          }
          else
          {
            throw new WSIFException("A 'wsdl:types' element must contain " +
                                      "a '" + qElemSchema + "' element.");
          }
        }
        
        s2j.outputJavaMapping();
      }
      catch (SchemaException se)
      {
        throw new WSIFException("Ouch: Schema->Java problem: " +
                                  se.getMessage(), se);
      }
      catch (IOException ie)
      {
        throw new WSIFException("Ouch: problem while saving code: " +
                                  ie.getMessage(), ie);
      }
    }
    else
    {
      s2j.getRegistry(typeReg);
    }
  }
  
  private static void printUsage()
  {
    System.err.println("Usage:");
    System.err.println("  java " + PortTypeCompiler.class.getName() +
                         " [-xsd:schemaURI] " +
                         "wsdlLocation [portTypeNS portTypeName]");
  }
  
  public static void main(String[] argv) throws WSDLException, WSIFException
  {
  	System.out.println("This tool is deprecated. Please do not use it any more. " +
  	                   "Use the Wsdl2java and Java2Wsdl tools in Axis instead.");
    if (argv.length == 0)
    {
      printUsage();
      System.exit(1);
    }
    
    String schemaURI = null;
    int offset = 0;
    
    if (argv[0].startsWith("-xsd:"))
    {
      schemaURI = argv[0].substring(5);
      offset = 1;
    }
    
    if (argv.length - offset != 1 && argv.length - offset != 3)
    {
      printUsage();
      System.exit(1);
    }
    
    //WSIFServiceImpl.addExtensionRegistry(new JavaExtensionRegistry()) ;    
   	//WSIFServiceImpl.addExtensionRegistry(new FormatExtensionRegistry()) ;
   	//WSIFServiceImpl.addExtensionRegistry(new JmsExtensionRegistry()) ;
    
    Definition def = WSIFUtils.readWSDL(null, argv[offset]);
    String portTypeNS = null;
    String portTypeName = null;
    
    if (argv.length - offset == 3)
    {
      portTypeNS = argv[1 + offset];
      portTypeName = argv[2 + offset];
    }
    
    PortType portType = WSIFUtils.selectPortType(def,
                                                 portTypeNS,
                                                 portTypeName);
    PortTypeCompiler compiler = new PortTypeCompiler();
    
    if (schemaURI != null)
    {
      compiler.setSchemaURI(schemaURI);
    }
    
    compiler.compilePortType(portType, def);
  }
}
