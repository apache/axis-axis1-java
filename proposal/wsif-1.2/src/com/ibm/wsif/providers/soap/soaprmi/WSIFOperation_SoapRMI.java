// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
//-------------------------74-columns-wide-------------------------------|
package com.ibm.wsif.providers.soap.soaprmi;

import java.io.*;
import java.util.*;
import java.net.*;

import javax.wsdl.*;

import com.ibm.wsif.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.providers.*;

import soaprmi.mapping.*;
import soaprmi.soap.Soap;
import soaprmi.soaprpc.MethodInvoker;
import soaprmi.util.HTTPUtils;

/**
 * Provide concrete implementation of WSDL operation with SoapRMI
 * RPC method invocation.
 *
 * @author Alekander Slominski
 */

public class WSIFOperation_SoapRMI
  extends WSIFDefaultOperation 
  implements WSIFOperation
{
  private final static boolean DEBUG = false;
  
  protected WSIFPort_SoapRMI portInstance;
  protected Operation operation;
  protected Definition definition;
  
  // cached information to allow efficinet operation calls
  protected List partNames;
  protected String[] names;
  protected Class[] types;
  protected String inputEncodingStyle = Soap.SOAP_ENC_NS;
  protected String inputNamespace;
  
  protected String returnName;
  protected Class returnType;
  protected String outputEncodingStyle = Soap.SOAP_ENC_NS;
  protected String actionUri;
  protected WSIFDynamicTypeMap typeMap;
  protected XmlJavaMapping soaprmiMapping;
  
  /**
   * Create Apache SOAP operation instance that encapsultes all necessary
   * information required to create and execute Apache SOAP Call.
   */
  public WSIFOperation_SoapRMI(WSIFPort_SoapRMI pi,
                               Operation op,
                               WSIFDynamicTypeMap typeMap)
    throws WSIFException
  {
    this.typeMap = typeMap;
    setDynamicWSIFPort(pi);
    setOperation(op);
    setDefintion(pi.getDefinition());
    prepare();
  }
  
  /**
   * Create a new copy of this object. This is not a clone, since 
   * it does not copy the referenced objects as well.
   */
  public WSIFOperation_SoapRMI copy() throws WSIFException {
  	
  	 WSIFOperation_SoapRMI op = 
  	   new WSIFOperation_SoapRMI(portInstance,operation,typeMap);
  	   
  	 op.setSoapActionURI      (getSoapActionURI      ());
  	 op.setInputNamespace     (getInputNamespace     ());
  	 op.setInputEncodingStyle (getInputEncodingStyle ());
  	 op.setOutputEncodingStyle(getOutputEncodingStyle());
  	 op.setPartNames          (getPartNames          ());
  	 op.setReturnName         (getReturnName         ());
  	 
  	 return op;
  }
  
  /**
   * This is utility method that when called initializes operation
   * (including reconstruction of method signature).
   */
  private void prepare() throws WSIFException {
    soaprmiMapping = new XmlJavaMapping();
    try {
      XmlJavaMapping defaultMapping =
        soaprmi.soap.Soap.getDefault().getMapping();
      soaprmiMapping.connectTo(defaultMapping);
      // disable SoapRMI auto mapping
      soaprmiMapping.setDefaultStructNsPrefix(null);
      
      for(Iterator i = typeMap.iterator(); i.hasNext(); ) {
        WSIFDynamicTypeMapping mapping = (WSIFDynamicTypeMapping) i.next();
        
        // map SOAPStruct into namespace:http://soapinterop.org/ : SOAPStruct
        soaprmiMapping.mapStruct(
          "http://schemas.xmlsoap.org/soap/encoding/",
          mapping.getXmlType().getNamespaceURI(),
          mapping.getXmlType().getLocalPart(),
          mapping.getJavaType()
        );
        
      }
    } catch(XmlMapException ex) {
      throw new WSIFException("Could not initialize mapping.", ex);
    }
    
    // first determine list of arguments
    Input input = operation.getInput();
    if(input != null) {
      List parts;
      if(partNames != null) {
        parts = new Vector();
        for(Iterator i = partNames.iterator(); i.hasNext(); ) {
          String partName = (String) i.next();
          Part part = input.getMessage().getPart(partName);
          if(part == null) {
            throw new WSIFException("no input part named "+partName
                                      +" for bining operation "+getName());
          }
          parts.add(part);
        }
      } else {
        parts = input.getMessage().getOrderedParts(null);
      }
      int count = parts.size();
      names = new String[count];
      types = new Class[count];
      
      // get parts in correct order
      for(int i = 0; i <count; ++i) {
        Part part = (Part) parts.get(i);
        names[i] = part.getName();
        QName partType = part.getTypeName();
        if(partType == null) {
          throw new WSIFException(
            "part "+names[i]+" must have type name declared");
        }
        try {
          XmlJavaTypeMap typeMap = soaprmiMapping.queryTypeMap(
            inputEncodingStyle,
            partType.getNamespaceURI(),
            partType.getLocalPart()
          );
          types[i] = typeMap.javaClass();
        } catch(XmlMapException ex) {
          throw new WSIFException(
            "Could not determine local java type for "+
              partType.getNamespaceURI()+":"+
              partType.getLocalPart(),
            ex);
        }
        
      }
    } else {
      names = new String[0];
      types = new Class[0];
    }
    
    // now prepare return value
    Output output = operation.getOutput();
    if(output != null) {
      Part returnPart = null;
      if(returnName != null) {
        returnPart = output.getMessage().getPart(returnName);
        if(returnPart == null) {
          throw new WSIFException("no output part named "+returnName
                                    +" for bining operation "+getName());
        }
      } else {
        List parts = output.getMessage().getOrderedParts(null);
        if(parts.size() > 0) {
          returnPart = (Part) parts.get(0);
          returnName = returnPart.getName();
        }
      }
      if(returnPart != null) {
        QName partType = returnPart.getTypeName();
        try {
          XmlJavaTypeMap typeMap = soaprmiMapping.queryTypeMap(
            outputEncodingStyle,
            partType.getNamespaceURI(),
            partType.getLocalPart()
          );
          returnType = typeMap.javaClass();
        } catch(XmlMapException ex) {
          throw new WSIFException(
            "Could not determine local java type for "+
              partType.getNamespaceURI()+":"+
              partType.getLocalPart(),
            ex);
        }
      } else {
        returnType = Void.TYPE;
      }
    }
    
  }
  
  public boolean executeRequestResponseOperation(WSIFMessage input,
                                                 WSIFMessage output,
                                                 WSIFMessage fault)
    throws WSIFException {
    return invokeRequestResponseOperation(input,output,fault);
  }
  
  public void executeInputOnlyOperation(WSIFMessage input) throws WSIFException {
    invokeInputOnlyOperation(input);
  }
    
  /**
   * Invoke RPC operation using ApacheSOAP
   */
  public boolean invokeRequestResponseOperation(WSIFMessage input,
                                                WSIFMessage output,
                                                WSIFMessage fault)
    throws WSIFException
  {
    if(names == null) prepare();
    
    try {
      
      // prepare parameters
      
      Object[] params = new Object[types.length];
      
      Object partInst;
      for(int i = 0; i < names.length; ++i) {
        partInst = input.getObjectPart(names[i]);
        if(partInst == null)
      	{
      	  boolean foundInputParameter = false;
      	  String paramName = names[i]; 
    	  Iterator partsIterator = input.getPartNames();
    	  while (partsIterator.hasNext()) {
    		String partName = (String) partsIterator.next();
    		if (partName == null || paramName == null) break;
    		if (partName.equals(paramName)) {
    			foundInputParameter = true;
    		}
    	  }
    	  if (!foundInputParameter)
       		throw new WSIFException(
        		"expected input message to have part with name '"+names[i]+"'");
      	}
        Object value = partInst;
        // some runtime param validity check
        if(value != null
             && ! types[i].isPrimitive()
             && ! (types[i].isAssignableFrom(value.getClass()))
          )
        {
          throw new WSIFException("value "+value
                                    +" has unexpected type "+value.getClass()
                                    +" instead of "+types[i]);
        }
        params[i] = value;
      }
      
      MethodInvoker mi = null; //newCall(m);
      // prepare this method invoker
//      try {
        mi = MethodInvoker.makeMethodInvoker(
          getInputNamespace(),
          returnType,
          getName(),
          types,
          names,
          getSoapActionURI(),
          soaprmiMapping
        );
//      } catch(soaprmi.RemoteException ex) {
//      throw new WSIFException(
//        "Could not prepare method invoker for operation "+getName(), ex);
//      }
      
      
      Map requestHeaders = new HashMap();
      
      requestHeaders.put("SOAPAction", getSoapActionURI());
      
      StringWriter sw = new StringWriter();
      Writer writer = new BufferedWriter(sw);
      
      String locationUri = portInstance.getLocation();
      
      if(DEBUG) System.err.println(
          "invoking SoapRMI operation "+getName()+" on "+locationUri);
      
      mi.sendRequest(params, writer);
      
      String requestContent = sw.toString();
      
      String httpProxyHost = null;
      int httpProxyPort = -1;
      
      URL url = new URL(locationUri);
      
      Reader reader = HTTPUtils.post(url,
                                     requestContent,
                                     requestHeaders,
                                     "text/xml; charset=utf-8",
                                     //NOTE putting "" around utf-8 is crashing Tomcat 3.2.1
                                     60 * 1000, //timeout,
                                     httpProxyHost,
                                     httpProxyPort
                                    );
      
      
      
      Object result =  mi.receiveResponse(reader);
      
      if(returnType != null) {
        if(result != null
             && ! returnType.isPrimitive()
             && ! (returnType.isAssignableFrom(result.getClass())) )
        {
          throw new WSIFException(
            "return value "+result
              +" has unexpected type "+result.getClass()
              +" instead of "+returnType);
        }
        output.setObjectPart(returnName, result);
      }
      
      // TOOD keep pool of method invokers - good for performance
      //returnCallToPool(mi);
      
    } catch(IOException ex) {
      //ex.printStackTrace();
      throw new WSIFException("IO Exception", ex);
    } catch(soaprmi.RemoteException ex) {
      ex.printStackTrace();
      throw new WSIFException("SoapRMI exception", ex);
    } catch(soaprmi.soap.SoapException ex) {
      throw new WSIFException("SOAP exception", ex);
    } catch(xpp.XmlPullParserException ex) {
      throw new WSIFException("SOAP exception", ex);
    }
    
    return true;
  }
  
  /**
   * Invoke only operation are not yet supported.
   */
  public void invokeInputOnlyOperation (WSIFMessage input)
    throws WSIFException
  {
    throw new WSIFException("not implemented");
  }
  
  /**
   * Return name of operation.
   */
  public String getName() { return operation.getName(); }
  
  public String getSoapActionURI() { return actionUri; }
  public void setSoapActionURI(String value) { actionUri = value; }
  
  public String getInputNamespace() { return inputNamespace; }
  public void setInputNamespace(String value) { inputNamespace = value; }
  
  public String getInputEncodingStyle() { return inputEncodingStyle; }
  public void setInputEncodingStyle(String value) {
    inputEncodingStyle = value; }
  public String getOutputEncodingStyle() { return outputEncodingStyle; }
  public void setOutputEncodingStyle(String value) {
    outputEncodingStyle = value; }
  
  
  public List getPartNames() { return partNames; }
  public void setPartNames(List value) { partNames = value; }
  public String getReturnName() { return returnName; }
  public void setReturnName(String value) { returnName = value; }
  
  // where is WSDL defining this abstract mesage
  public Operation getOperation() { return operation; }
  public void setOperation(Operation value) { operation = value; }
  public Definition getDefinition() { return definition; }
  public void setDefintion(Definition value) { definition = value; }
  
  // WSIF related
  public WSIFPort_SoapRMI getDynamicWSIFPort() { return portInstance; }
  public void setDynamicWSIFPort(WSIFPort_SoapRMI value)
  {
    portInstance = value;
  }
  
}

