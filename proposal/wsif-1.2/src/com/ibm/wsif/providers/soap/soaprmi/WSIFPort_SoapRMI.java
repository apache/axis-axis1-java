// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
//-------------------------74-columns-wide-------------------------------|
package com.ibm.wsif.providers.soap.soaprmi;

import java.io.*;
import java.util.*;
import java.net.*;
import org.w3c.dom.*;

import javax.wsdl.*;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.extensions.soap.SOAPOperation;

import com.ibm.wsif.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.providers.*;

/**
 * This is SoapRMI dynamic WSIF port that is driven by WSDL.
 *
 * @author Alekander Slominski
 */

public class WSIFPort_SoapRMI extends WSIFDefaultPort
{
  protected Map operationInstances = new HashMap();
  
  protected Port port;
  protected Definition definition;
  protected String location;
  private static final boolean DEBUG = false; //for internal use only
  
  /**
   * Create dynamic port instance from WDL model defintion and port.
   * <p><b>NOTE:</b> this constructor is doing full initialization
   *  therefore after dynamic port is created overhead of executing
   *  operation should be as small as possible for dynamic case...
   */
  public WSIFPort_SoapRMI(Definition def, Service service, Port port,
                          WSIFDynamicTypeMap typeMap)
    throws WSIFException
  {
    setDefinition(def);
    setPort(port);
        
    // find Port soap:address bindng element
    SOAPAddress sa = (SOAPAddress) getExtElem(
      port, SOAPAddress.class, port.getExtensibilityElements());
    if(sa != null) {
      location = sa.getLocationURI();
    }
    if(location == null) {
      throw new WSIFException(
        "soap:address with location URI is required for "+port);
    }
    
    // check soap:binding element
    String style = null;
    Binding binding = port.getBinding();
    SOAPBinding soapBinding = (SOAPBinding) getExtElem(
      binding, SOAPBinding.class, binding.getExtensibilityElements());
    if(soapBinding != null) {
      style = soapBinding.getStyle();
      if(! "rpc".equals(style) ) {
        throw new WSIFException("unsupported style "+style
                                  +" for "+soapBinding);
      }
      String transport = soapBinding.getTransportURI();
      if(! "http://schemas.xmlsoap.org/soap/http".equals(transport) ) {
        throw new WSIFException("unsupported transport "+transport
                                  +" for "+soapBinding);
      }
    }
    if(style == null) {
      style = "document"; //it is default style value as of WSDL 1.1
    }
    
    // check required SOAP bindings for all portType operations
    PortType portType = binding.getPortType();
    
    List operationList = portType.getOperations();
    
    // process each operation to create dynamic operation instance
    for(Iterator i = operationList.iterator(); i.hasNext(); ) {
      Operation op = (Operation) i.next();
      String name = op.getName();
      //System.err.println("op = "+op);
      Input input = op.getInput();
      Output output = op.getOutput();
      if(input == null) {
        throw new WSIFException(
          "missing input message for operation "+name);
        
      }
      if(output == null) {
        throw new WSIFException(
          "missing output message for operation "+name);
      }
      
      WSIFOperation_SoapRMI opInst =
        new WSIFOperation_SoapRMI(this, op, typeMap);
      
      BindingOperation bop = binding.getBindingOperation(
        name, input.getName(), output.getName());
      
      if(bop == null) {
        throw new WSIFException(
          "mising required in WSDL 1.1 binding operation for "+name);
      }
      
      // get soapActionURI and style from soap:operation
      SOAPOperation soapOperation = (SOAPOperation) getExtElem(
        bop, SOAPOperation.class, bop.getExtensibilityElements());
      if(soapOperation == null) {
        throw new WSIFException(
          "soapAction must be specified in "+
            " required by WSDL 1.1 soap:operation binding for "+bop);
      }
      String soapActionURI = soapOperation.getSoapActionURI();
      opInst.setSoapActionURI(soapActionURI);
      if(DEBUG) System.err.println(
          "setting actionURI "+soapActionURI+" for op "+opInst.getName());
      String opStyle = soapOperation.getStyle();
      if(opStyle != null && ! "rpc".equals(opStyle)) {
        throw new WSIFException(
          "unsupported style "+style+" for operation "+name);
      } else if(!"rpc".equals(style)) {
        throw new WSIFException(
          "default soap style must be rpc if operation "+name
            +" binding has not style attribute");
      }
      
      // try to get soap:body for input message
      BindingInput binpt = bop.getBindingInput();
      SOAPBody soapInputBody = (SOAPBody) getExtElem(
        binpt, SOAPBody.class, binpt.getExtensibilityElements());
      if(soapInputBody != null) {
        String namespaceURI = soapInputBody.getNamespaceURI();
        if(DEBUG) System.err.println(
            "setting namespace "+namespaceURI+" for op "+opInst.getName());
        opInst.setInputNamespace(namespaceURI);
        String use = soapInputBody.getUse();
        if(! "encoded".equals(use)) {
          throw new WSIFException(
            "unsupported use "+use+" in "+soapOperation);
        }
        List encodingStyles = soapInputBody.getEncodingStyles();
        if(encodingStyles != null) {
          if(encodingStyles.size() == 0) {
          }
          opInst.setInputEncodingStyle((String)encodingStyles.get(0));
          // quietly ignore if encodingStyles.size() > 1 ...
        }
        List parts = soapInputBody.getParts();
        if(parts != null) {
          opInst.setPartNames(parts);
        }
      }
      SOAPHeader soapHeader = (SOAPHeader) getExtElem(
        binpt, SOAPHeader.class, binpt.getExtensibilityElements());
      if(soapHeader != null) {
        throw new WSIFException(
          "not supported input soap:header "+soapHeader);
      }
      
      // try to get soap:body for output message
      BindingOutput boutpt = bop.getBindingOutput();
      SOAPBody soapOutputBody = (SOAPBody) getExtElem(
        boutpt, SOAPBody.class, boutpt.getExtensibilityElements());
      if(soapOutputBody != null) {
        // NOTE: element ignored
        //String namespaceURI = soapOutputBody.getNamespaceURI();
        String use = soapInputBody.getUse();
        if(! "encoded".equals(use)) {
          throw new WSIFException(
            "unsupported use "+use+" in "+soapOperation);
        }
        // NOTE: element ignored
        //List encodingStyles = soapInputBody.getEncodingStyles();
        List parts = soapInputBody.getParts();
        if(parts != null && parts.size() > 0) {
          opInst.setReturnName((String) parts.get(0));
        }
      }
      soapHeader = (SOAPHeader) getExtElem(
        boutpt, SOAPHeader.class, boutpt.getExtensibilityElements());
      if(soapHeader != null) {
        throw new WSIFException(
          "not supported output soap:header "+soapHeader);
      }
      
      
      for(Iterator bfaults = bop.getBindingFaults().values().iterator();
            bfaults.hasNext(); )
      {
        BindingFault bfault = (BindingFault) bfaults.next();
        SOAPFault soapFault = (SOAPFault) getExtElem(
          bfault, SOAPFault.class, bfault.getExtensibilityElements());
        // NOTE: element ignored
        //if(soapFault != null) {
        //  throw new WSIFException(
        //   "soap:fault not supported in "+bfault);
        //}
      }
      
      // make this operation instance accessible
      setDynamicWSIFOperation(
        op.getName(), op.getInput().getName(), op.getOutput().getName(), opInst);
      
    } // for
  }
  
  
  /**
   * Invoke RPC operation on port instance.
   * NOTE: fault processing is not yet implemented.
   * @deprecated
   */
  public boolean executeRequestResponseOperation(String operationName,
                                                 WSIFMessage input,
                                                 WSIFMessage output,
                                                 WSIFMessage fault)
    throws WSIFException
  {
    // find correct operation
    WSIFOperation_SoapRMI op = getDynamicWSIFOperation(
      operationName,
      input.getName(), output.getName());
    if(op == null) {
      throw new WSIFException("no operation named "+operationName
                                +" is available in port "+port);
    }
    
    // and invoke it
    return op.invokeRequestResponseOperation(input, output, fault);
  }
  
  public String getLocation () {
    return location;
  }
  
  public void setLocation (String location) {
    this.location = location;
  }
  
    
  // where is WSDL defining this abstract mesage
  public Definition getDefinition() { return definition; }
  public void setDefinition(Definition value) { definition = value; }
  public Port getPort() { return port; }
  public void setPort(Port value) { port = value; }
  
  
  // WSIF: keep list of operations available in this port
  public void setDynamicWSIFOperation(
    String name, String inputName, String outputName,
    WSIFOperation_SoapRMI value)
  {
    operationInstances.put(getKey(name, inputName, outputName), value);
  }
  
  public WSIFOperation createOperation(String operationName) throws WSIFException {
   	return createOperation(operationName, null, null);
  }
  
  public WSIFOperation createOperation(String operationName,
                                       String inputName,
                                       String outputName) throws WSIFException {
    WSIFOperation_SoapRMI op=getDynamicWSIFOperation(
      operationName,inputName,outputName);
    if (op == null) {
      throw new WSIFException("Could not create operation: " + operationName
                              + ":" + inputName + ":" + outputName);
  	}
    return op.copy();
  }
  
  public WSIFOperation_SoapRMI getDynamicWSIFOperation(
    String name, String inputName,  String outputName)
  {
    WSIFOperation_SoapRMI operation =
      (WSIFOperation_SoapRMI) operationInstances.get(
        getKey(name, inputName, outputName));
    if (operation == null)
    {
      BindingOperation bindingOperationModel =
        port.getBinding().getBindingOperation(name, inputName, outputName);

      if (bindingOperationModel != null)
      {
        // Only one operation matched in binding so find it in porttype
        // from all the information that is available to us
        Iterator i = operationInstances.keySet().iterator();
        while (i.hasNext())
        {
          String key = (String) i.next();
          if ((outputName != null && key.endsWith(outputName)) || outputName == null)
          {
            String start = (inputName == null) ? name : name + ":" + inputName;
            if (key.startsWith(start))
            {
              if (operation != null)
              {
                // Duplicate operation found based on names!
                operation = null;
                break;
              }
              operation = (WSIFOperation_SoapRMI) operationInstances.get(key);
            }
          }
        }
      }
    }

    return operation;
  }
}

