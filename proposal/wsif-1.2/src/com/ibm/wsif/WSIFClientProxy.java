// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

/**
 * @author Mark Whitlock
 */

import java.lang.reflect.*;
import java.util.*;
import javax.wsdl.*;
import com.ibm.wsdl.xml.*;
import com.ibm.wsif.stub.*;
import com.ibm.wsif.providers.*;
import com.ibm.wsif.compiler.schema.tools.*;
import com.ibm.wsif.compiler.util.*;
import com.ibm.wsif.logging.*;
import org.apache.soap.Constants;

public class WSIFClientProxy implements InvocationHandler 
{
    protected Class              iface         = null;
    protected Definition         def           = null;
    protected String             serviceNS     = null;
    protected String             serviceName   = null;
    protected String             portTypeNS    = null;
    protected String             portTypeName  = null;
    protected WSIFDynamicTypeMap typeMap       = null;
    protected Hashtable          simpleTypeReg = null;
    protected PortType           portType      = null;
    protected WSIFPort           wsifport      = null;
    protected Object             proxy         = null;

	/**
	 * Factory method to create a new dynamic proxy using a port factory 
	 * obtained via JNDI. If that doesn't work, use the specified WSDL 
	 * document to initialize a dynamic port factory. If serviceNS and 
	 * serviceName are not null, they will be used in selecting a service.
	 */
    public static WSIFClientProxy newInstance(Class              iface,
                                              Definition         def,
                                              String             serviceNS,
                                              String             serviceName,
                                              String             portTypeNS,
                                              String             portTypeName,
                                              WSIFDynamicTypeMap typeMap)
      throws WSIFException
    {
      TraceLogger.getGeneralTraceLogger().entry(
        new Object[] {iface,def.getQName(),serviceNS,serviceName,
        	          portTypeNS,portTypeName,typeMap});
        	          
      if (!iface.isInterface()) throw new WSIFException(
          "Cannot get a stub for "+iface+" because it is not an interface");
        
	  WSIFClientProxy clientProxy = new WSIFClientProxy(iface,def,
	    serviceNS,serviceName,portTypeNS,portTypeName,typeMap);
    	
	  Object proxy = Proxy.newProxyInstance(
	    iface.getClassLoader(), new Class[]{iface}, clientProxy);
	    
      clientProxy.setProxy(proxy);
      
      TraceLogger.getGeneralTraceLogger().exit(clientProxy);
      return clientProxy;  	    
    }

    /**
     * Private constructor because newInstance() should be used instead.
     * 
     * This constructs a cache of simple types (int, float, string, etc) using
     * the Schema2Java class. This is so there is only one list of the simple
     * type mappings.
     */    
    private WSIFClientProxy(Class              iface,
                            Definition         def,
                            String             serviceNS,
                            String             serviceName,
                            String             portTypeNS,
                            String             portTypeName,
                            WSIFDynamicTypeMap typeMap)
      throws WSIFException
    {
      TraceLogger.getGeneralTraceLogger().entry(
        new Object[] {iface,def.getQName(),serviceNS,serviceName,
        	          portTypeNS,portTypeName,typeMap});
      this.iface = iface;
      this.def = def; 
      this.serviceNS = serviceNS;
      this.serviceName = serviceName;
      this.portTypeNS = portTypeNS;
      this.portTypeName = portTypeName;
      this.typeMap = typeMap;
      this.portType = WSIFUtils.selectPortType(def, portTypeNS, portTypeName);
      
      simpleTypeReg = new Hashtable();
      new Schema2Java(Constants.NS_URI_1999_SCHEMA_XSD).getRegistry(simpleTypeReg);
      new Schema2Java(Constants.NS_URI_2000_SCHEMA_XSD).getRegistry(simpleTypeReg);
      new Schema2Java(Constants.NS_URI_2001_SCHEMA_XSD).getRegistry(simpleTypeReg);

      TraceLogger.getGeneralTraceLogger().exit();
    }

    private void setProxy(Object proxy) { this.proxy=proxy; }
    Object getProxy() { return this.proxy; }
    
    /**
     * Select which port to use for this proxy.
     */
    public void setPort(WSIFPort wsifport) { this.wsifport = wsifport; }
    	
    /**
     * Invoke a user method. The java proxy support calls this method.
     * 
     * The fault from the fault message is not passed back to caller 
     * (but it should be). However none of the existing providers set 
     * the fault message. I'm not sure what to do with the fault message
     * anyhow. I guess raise a WSIFException which is what the current
     * providers do with faults already.
     */
    public Object invoke(Object proxy, Method method, Object[] args)
	  throws WSIFException
    {
      TraceLogger.getGeneralTraceLogger().entry (
        new Object[] {method,args}); // Tracing proxy cause a hang
        
      Operation operation = findMatchingOperation(method);
      
      // Now set up the input and output messages.      
      Input input = operation.getInput();
      Output output = operation.getOutput();
      String inputName = (input==null)?null:input.getName();
      String outputName = (output==null)?null:output.getName();
      Message inputMessage = (input==null)?null:input.getMessage();
      Message outputMessage = (output==null)?null:output.getMessage();

      WSIFOperation wsifoperation = 
        wsifport.createOperation(method.getName(),inputName,outputName);

      // make the msg names for compiled msgs xxxAnt ask Mark why diff from inputName 
      String inputMsgName = "";
      if ( input != null ) {
      	 Message m = input.getMessage();
         if ( m != null ) {
         	QName qn = m.getQName();
         	inputMsgName = (qn == null) ? "" : qn.getLocalPart();
         }
      }

      String outputMsgName = "";
      if ( output != null ) {
      	 Message m = output.getMessage();
         if ( m != null ) {
         	QName qn = m.getQName();
         	outputMsgName = (qn == null) ? "" : qn.getLocalPart();
         }
      }

      // There must be an inputMessage.
      WSIFMessage wsifInputMessage = wsifoperation.createInputMessage(inputMsgName);
      	
      // There may not be an output message.
      WSIFMessage wsifOutputMessage = null;
      if (output!=null)
        wsifOutputMessage = wsifoperation.createOutputMessage(outputMsgName);
      	
      Iterator partIt = inputMessage.getOrderedParts(null).iterator();	
      int argIndex;
      for (argIndex=0; partIt.hasNext(); argIndex++)
      {
        Part part = (Part)partIt.next();
        String partName = part.getName();
        wsifInputMessage.setObjectPart(partName, args[argIndex]);
      }
      
      // Ought to check the return code from executeRequestResponseOperation here,
      // and use the fault message. However the portTypeCompiler doesn't.
      if (output==null)
      	wsifoperation.executeInputOnlyOperation(wsifInputMessage);
      else
      	wsifoperation.executeRequestResponseOperation(
      	  wsifInputMessage,wsifOutputMessage,null);

      // Copy the output part out of the message. 
      Object result=null;
      if (outputMessage != null)
      {
        List outputParts = outputMessage.getOrderedParts(null);
        if (outputParts != null && outputParts.size() > 0)
        {
          // The return value is always the first output part
          Iterator outPartIt = outputParts.iterator();
          Part returnPart = (Part)outPartIt.next();
          result = wsifOutputMessage.getObjectPart(returnPart.getName());
          
          // Are there any inout parts? Multiple output-only parts
          // are not allowed in java. Skip over input-only parts in the message.
          if (outPartIt.hasNext())
          {
          	Object[] inPartArr = inputMessage.getOrderedParts(null).toArray();
            Part nextOutPart = (Part)outPartIt.next();
          	
          	for (argIndex=0; argIndex<args.length; argIndex++)
            {              
          	  if (((Part)(inPartArr[argIndex])).getName().
          	         equals(nextOutPart.getName()))
          	  {
          	  	args[argIndex] = 
          	  	  wsifOutputMessage.getObjectPart(nextOutPart.getName());
                if (outPartIt.hasNext()) nextOutPart = (Part)outPartIt.next();
                else break;  // No more output parameters
          	  }
          	} // end for
          }
        }
      }
      
      TraceLogger.getGeneralTraceLogger().exit(result);
	  return result;
    }
    
    /**
     * Find an operation in the list that matches the method and arguments.
     * 
     * Java only allows one output-only parameter and that is the return 
     * value. Java also does not allow overloading based on the return value.
     * Consequently we only look at the input parameters when deciding 
     * which overloaded operation to pick.
     * 
     * If the user invoked an overloaded method, MyMethod(null) seems to be 
     * ambiguous. However it is not since java forces the user to cast the 
     * null to one of the types that are valid on the method. So the invoke
     * method on our client proxy gets passed args[0]==null which is not typed.
     * However method.getParameterTypes()[0] is the type that java picked to 
     * invoke. So we use getParameterTypes() to choose the operation, not 
     * args[i].getClass().
     * 
     * The typeMap only contains complexTypes, so this class also uses the 
     * simpleTypeReg for simple types (int, string, etc).
     * 
     * We compare the class in the mapping with the one from types using
     * isAssignableFrom() not equals() because we allow the user to pass
     * a subclass.
     *
     * If there are two methods MyMethod(Address) and MyMethod(SubAddress) 
     * then MyMethod(new SubAddress()) would match both methods. So if we 
     * find an operation which exactly matches the method we return it. 
     * But if we find an operation whose types are assignable from the 
     * method's types, we carry on searching for an exact match. If we fail 
     * to find an exact match then we return the "assignable" match. There 
     * is a problem if there are multiple "assignable" matches and no exact 
     * match as would happen if MyMethod(SubSubAddress) where SubSubAddress
     * extends Address. This code does not cope with that case and it is a 
     * known restriction (bug).
     * 
     * If the WSDL is correct, we do not expect that there will be multiple 
     * exact matches, so we do not test for this.
     */
    private Operation findMatchingOperation(Method method)
      throws WSIFException
    {
      // Check here that the method is in the interface iface
      Method[] allMethods = iface.getMethods();
      int i;
      for (i=0; i<allMethods.length; i++)
        if (allMethods[i].equals(method)) break;
      if (i>=allMethods.length || !method.equals(allMethods[i])) 
        throw new WSIFException("Method "+method.getName()+
          " is not in interface "+iface.getName());

      String methodName = method.getName();
      Class[] types = method.getParameterTypes();
      List opList = portType.getOperations();
      Iterator opIt = opList.iterator();
      Operation matchingOperation=null;

      // First try to find this method in the portType's list of operations.
      // Be careful of overloaded operations.
      while (opIt.hasNext())
      {
      	Operation operation = (Operation)opIt.next();
      	// If the method name doesn't match the operation name this isn't the operation
      	if (!methodName.equals(operation.getName())) continue;

        Input input = operation.getInput();
        Message inputMessage = (input==null)?null:input.getMessage();
        int numInputParts = inputMessage==null?0:inputMessage.getParts().size();
        
        // Check for a match if neither args nor the operation has any parameters
        if (numInputParts==0 && types.length==0) return operation;

        // No match if there are different numbers of parameters
        if (numInputParts!=types.length) continue;
          
        // Go through all the parameters making sure all their datatypes match
        Iterator partIt = inputMessage.getOrderedParts(null).iterator();	
        boolean foundAllArgs = true;
        boolean exactMatchAllArgs = true;
        for (int argIndex=0; partIt.hasNext() && foundAllArgs; argIndex++)
        {
          Part part = (Part)partIt.next();
          QName partTypeName = part.getTypeName();
          boolean foundThisArg = false;
          boolean exactMatchThisArg = false;

          // Look this parameter up in the typeMap.
          for (Iterator mapIt=typeMap.iterator(); 
               mapIt.hasNext() && !foundThisArg; )
          {
            WSIFDynamicTypeMapping mapping=(WSIFDynamicTypeMapping)mapIt.next();

            if (mapping.getXmlType().equals(partTypeName))
            {
              if (mapping.getJavaType().isAssignableFrom(types[argIndex]))
              {
                foundThisArg = true;
                if (mapping.getJavaType().equals(types[argIndex])) 
                  exactMatchThisArg = true;
              }
              else break;
            }
          }
          
          // Look for a simple type that matches
          TypeMapping tm = (TypeMapping)(simpleTypeReg.get(partTypeName)); 
          if (!foundThisArg && tm!=null) 
          {
            String simpleType = tm.javaType;
          	if (types[argIndex].toString().equals(simpleType))
          	{  // this works for simple types (float, int)
          	  foundThisArg = true;
              exactMatchThisArg = true;
          	}
          	else try // this works for String, Date
            { 
              Class simpleClass = Class.forName(simpleType); 
              if (simpleClass.isAssignableFrom(types[argIndex]))
              {
                foundThisArg = true;
                if (simpleClass.equals(types[argIndex]))
                  exactMatchThisArg = true;
              }
            }
            catch (ClassNotFoundException ignored) {}
          }
              
          if (!foundThisArg) foundAllArgs = false;
          if (!exactMatchThisArg) exactMatchAllArgs = false;
        }

        if (foundAllArgs)
        {
          if (exactMatchAllArgs) return operation;
          
          // if matchingOperation!=null then write trace statement.
          matchingOperation = operation;
        }
      } // end while

      if (matchingOperation!=null) return matchingOperation;

      // if we get here then we haven't found a matching operation       
      String argString = new String();
      if (types!=null) for (i=0; i<types.length; i++)
      {
        if (i!=0) argString += ", ";
        argString += types[i];
      }
      	
      throw new WSIFException("Method " + methodName + "(" + argString + 
        ") was not found in portType " + portType.getQName());
    }    

    public String toString() 
    {
      String buff = new String(super.toString() + ":\n");
      buff += "iface: " + iface;
      buff += " def: " + def==null?"null":def.getQName().toString();
      buff += " serviceNS: " + serviceNS;
      buff += " serviceName: " + serviceName;
      buff += " portTypeNS: " + portTypeNS;
      buff += " portTypeName: " + portTypeName;
      buff += " typeMap: " + typeMap;
      buff += " simpleTypeReg: " + simpleTypeReg;
      buff += " portType: " + portType;
      buff += " wsifport: " + wsifport;
      
      // Can't trace proxy here because it causes a hang.
      //buff += "proxy: " + proxy;
      return buff;
    }

    public String toShallowString() 
    {
      return super.toString();
    }
}
