// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.java;

import com.ibm.wsif.util.*;
import com.ibm.wsdl.extensions.format.*;
import com.ibm.wsdl.extensions.java.*;
import javax.wsdl.extensions.*;
import java.util.*;
import com.ibm.wsif.*; 
import com.ibm.wsif.logging.*;
import com.ibm.wsif.providers.*;
import javax.wsdl.*;
/**
 * Java WSIF Port.
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 * Partially based on WSIFPort_ApacheSOAP from Alekander Slominski, 
 * Paul Fremantle, Sanjiva Weerawarana and Matthew J. Duftler
 */
public class WSIFPort_Java extends WSIFDefaultPort implements java.io.Serializable	// is this the right place for caching?
{
  private javax.wsdl.Definition fieldDefinition = null;
  private javax.wsdl.Port fieldPortModel = null;

  private java.lang.Object fieldObjectReference = null; // 'physical connection'

  protected Map operationInstances = new HashMap();

  public WSIFPort_Java(Definition def, Port port, WSIFDynamicTypeMap typeMap)
  {
    TraceLogger.getGeneralTraceLogger().entry(
      new Object[] {def.getQName(),port.getName(),typeMap});
                                    
    fieldDefinition = def;
    fieldPortModel = port;
    // System.out.println(def.getQName());
    TraceLogger.getGeneralTraceLogger().exit();
  }
  /**
    * Execute a request-response operation. The signature allows for
    * input, output and fault messages. WSDL in fact allows one to
    * describe the set of possible faults an operation may result
    * in, however, only one fault can occur at any one time.
    *
    * @param op name of operation to execute
    * @param input input message to send to the operation
    * @param output an empty message which will be filled in if
    *        the operation invocation succeeds. If it does not
    *        succeed, the contents of this message are undefined.
    *        (This is a return value of this method.)
    * @param fault an empty message which will be filled in if
    *        the operation invocation fails. If it succeeds, the
    *        contents of this message are undefined. (This is a
    *        return value of this method.)
    *
    * @return true or false indicating whether a fault message was
    *         generated or not. The truth value indicates whether
    *         the output or fault message has useful information.
    *
    * @exception WSIFException if something goes wrong.
    * @deprecated
    *
    * NOTE: fault processing is not yet implemented.
    */
  public boolean executeRequestResponseOperation(
    String operationName,
    WSIFMessage input,
    WSIFMessage output,
    WSIFMessage fault)
    throws WSIFException
  {
    // find correct operation
    WSIFOperation_Java operation = getDynamicWSIFOperation(operationName, input.getName(), output.getName());
    if (operation == null)
    {
      throw new WSIFException("No operation named '" + operationName + "' found in port " + fieldPortModel.getName());
    }

    // and invoke it
    return operation.executeRequestResponseOperation(input, output, fault);
  }
  
    /**
    * Execute an input only operation. The signature allows for
    * only an input message. 
    * 
    * @param op name of operation to execute
    * @param input input message to send to the operation
    * @exception WSIFException if something goes wrong.
    * @deprecated
    */
  public void executeInputOnlyOperation(
    String operationName,
    WSIFMessage input)
    throws WSIFException
  {
    // find correct operation
    WSIFOperation_Java operation = getDynamicWSIFOperation(operationName, input.getName(), null);
    if (operation == null)
    {
      throw new WSIFException("No operation named '" + operationName + "' found in port " + fieldPortModel.getName());
    }

    // and invoke it
    operation.executeInputOnlyOperation(input);
  }

  public Definition getDefinition()
  {
    return fieldDefinition;
  }

  public WSIFOperation createOperation(String operationName) throws WSIFException {
   	return createOperation(operationName, null, null);
  }
  
  public WSIFOperation createOperation(String operationName,
                                       String inputName,
                                       String outputName) throws WSIFException {
    WSIFOperation_Java op=getDynamicWSIFOperation(
      operationName,inputName,outputName);
    if (op == null) {
      throw new WSIFException("Could not create operation: " + operationName
                              + ":" + inputName + ":" + outputName);
  	}
    return op.copy();
  }  

  public WSIFOperation_Java getDynamicWSIFOperation(String name, String inputName, String outputName)
    throws WSIFException
  {
    WSIFOperation_Java operation = (WSIFOperation_Java) operationInstances.get(getKey(name, inputName, outputName)); 

    if (operation == null)
    {
      BindingOperation bindingOperationModel = 
      	fieldPortModel.getBinding().getBindingOperation(name, inputName, outputName) ;
      	
	    if(bindingOperationModel != null)
	    {
     		operation = new WSIFOperation_Java(fieldPortModel, bindingOperationModel, this);
	      setDynamicWSIFOperation(name, inputName, outputName, operation);
	    }
    }

    return operation;
  }
  public java.lang.Object getObjectReference() throws WSIFException
  {
    if (fieldObjectReference == null)
    {
      JavaAddress address = null;

      try
      {
        ExtensibilityElement portExtension = (ExtensibilityElement) fieldPortModel.getExtensibilityElements().get(0);

        if (portExtension == null)
        {
          throw new WSIFException("missing port extension");
        }

        address = (JavaAddress) portExtension;

        fieldObjectReference = Class.forName(address.getClassName(), true, Thread.currentThread().getContextClassLoader()).newInstance();
      }
      catch (Exception ex)
      {
        throw new WSIFException("Could not create object of class '" + address.getClassName() + "'", ex);
      }
    }
    return fieldObjectReference;
  }
  public Port getPortModel()
  {
    return fieldPortModel;
  }
  public void setDefinition(Definition value)
  {
    fieldDefinition = value;
  }
  // WSIF: keep list of operations available in this port
  public void setDynamicWSIFOperation(String name, String inputName, String outputName, WSIFOperation_Java value)
  {
    operationInstances.put(getKey(name, inputName, outputName), value);
  }
  public void setObjectReference(java.lang.Object newObjectReference)
  {
    fieldObjectReference = newObjectReference;
  }
  public void setPortModel(Port value)
  {
    fieldPortModel = value;
  }
  
  public String toString() {
 	String buff=new String(super.toString()+":\n");
   	 
  	buff += "definition:"+fieldDefinition.getQName();
  	buff += "\nportModel:"+fieldPortModel.getName();
  	buff += "\nobjectReference:"+fieldObjectReference;
  	  
  	buff += " operationInstances: size:"+operationInstances.size();
  	Iterator it=operationInstances.keySet().iterator();
  	int i=0;
  	while (it.hasNext()) {
  	  String key=(String)it.next();
  	  WSIFOperation_Java woj=(WSIFOperation_Java)operationInstances.get(key);
  	  buff += "\noperationInstances["+i+"]:"+key+" "+woj.toShallowString()+" ";
  	  i++;
  	}
  	
  	return buff;
  }
  
  public String toShallowString() { return super.toString(); }
}
