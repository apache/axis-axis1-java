// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
//-------------------------74-columns-wide-------------------------------|
package com.ibm.wsif.providers.soap.apachesoap;

import java.io.*;
import java.util.*; 
import java.net.*;
import org.w3c.dom.*;
import org.apache.soap.util.xml.*;
import org.apache.soap.*;
import org.apache.soap.encoding.*;
import org.apache.soap.encoding.soapenc.*;
import org.apache.soap.rpc.*;
import org.apache.soap.transport.*;
import org.apache.soap.transport.http.*;

import javax.wsdl.*;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.extensions.soap.SOAPOperation;
import com.ibm.wsdl.extensions.jms.*;

import com.ibm.wsif.*;
import com.ibm.wsif.logging.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.providers.*;

/**
 * This is Apache SOAP dynamic WSIF port  that is driven by WSDL.
 *
 * @author Alekander Slominski
 */

public class WSIFPort_ApacheSOAP extends WSIFDefaultPort {
  protected Map operationInstances = new HashMap();

  protected Port port;
  protected Definition definition;
  protected SOAPMappingRegistry smr;
   
  protected JROMSOAPMappingRegistry jromSMR;  

  protected SOAPTransport st;
  protected URL url;
  protected String style = "document";
  protected String partSerializerName = null;
  private static final boolean DEBUG = false; //for internal use only
  private WSIFDynamicTypeMap wsifTypeMap = null;

  /**
   * Create dynamic port instance from WDL model defintion and port.
   * <p><b>NOTE:</b> this constructor is doing full initialization
   *  therefore after dynamic port is created overhead of executing
   *  operation should be as small as possible for dynamic case...
   */
  public WSIFPort_ApacheSOAP(
    Definition def,
    Service service,
    Port port,
    WSIFDynamicTypeMap typeMap,
    String partSerName)
    throws WSIFException {
    TraceLogger.getGeneralTraceLogger().entry(
      new Object[] {
        def.getQName(),
        service.getQName(),
        port.getName(),
        typeMap,
        partSerName });

    setPartSerializerName(partSerName);
    setDefinition(def);
    setPort(port);
    wsifTypeMap = typeMap;

    smr = createSOAPMappingRegistry();        
 
    JmsAddress ja = (JmsAddress) 
      getExtElem(port, JmsAddress.class, port.getExtensibilityElements());
    SOAPAddress sa = (SOAPAddress) 
      getExtElem(port, SOAPAddress.class, port.getExtensibilityElements());

    if (sa != null && ja != null)
      throw new WSIFException(
        "Both soap:address and jms:address cannot be specified for port " + port);

    if (sa == null && ja == null)
      throw new WSIFException(
        "Either soap:address or jms:address must be specified for port " + port);

    if (ja != null) {
      // Port jms:address binding element
      st = new SOAPJMSConnection(ja);
    } else {
      // Port soap:address bindng element
      st = new SOAPHTTPConnection(); // call.getSOAPTransport() is null...

      String s = sa.getLocationURI();
      try {
        url = new URL(s);
      } catch (MalformedURLException meu) {
        throw new WSIFException("could not set SOAP address to " + s, meu);
      }
      if (url == null) {
        throw new WSIFException(
          "soap:address with location URI is required for " + port);
      }
    }

    // check soap:binding element
    Binding binding = port.getBinding();
    SOAPBinding soapBinding =
      (SOAPBinding) getExtElem(binding,
        SOAPBinding.class,
        binding.getExtensibilityElements());
    if (soapBinding != null) {
      style = soapBinding.getStyle();
      if (style == null)
        style = "document"; //it is default style value as of WSDL 1.1

      String transport = soapBinding.getTransportURI();
      if ((ja != null && !"http://schemas.xmlsoap.org/soap/jms".equals(transport))
        || (sa != null && !"http://schemas.xmlsoap.org/soap/http".equals(transport))) {
        throw new WSIFException(
          "unsupported transport " + transport + " for " + soapBinding);
      }
    }

    TraceLogger.getGeneralTraceLogger().exit();
  }

  public static SOAPMappingRegistry createSOAPMappingRegistry() {

    Call call = new Call();
    SOAPMappingRegistry smr = call.getSOAPMappingRegistry();

	// Add mapping registry entry for dateTime
	DateSerializer dateSer = new DateSerializer();
	// 1999 deserializer
	smr.mapTypes(Constants.NS_URI_SOAP_ENC, new org.apache.soap.util.xml.QName(Constants.NS_URI_1999_SCHEMA_XSD, "dateTime"), java.util.Date.class, null, dateSer);
	// 2000 deserializer
	smr.mapTypes(Constants.NS_URI_SOAP_ENC, new org.apache.soap.util.xml.QName(Constants.NS_URI_2000_SCHEMA_XSD, "dateTime"), java.util.Date.class, null, dateSer);
	// 2001 deserializer
	smr.mapTypes(Constants.NS_URI_SOAP_ENC, new org.apache.soap.util.xml.QName(Constants.NS_URI_2001_SCHEMA_XSD, "dateTime"), java.util.Date.class, null, dateSer);
	// Use current serializer for serialization
	smr.mapTypes(Constants.NS_URI_SOAP_ENC, new org.apache.soap.util.xml.QName(Constants.NS_URI_CURRENT_SCHEMA_XSD, "dateTime"), java.util.Date.class, dateSer, null);

	// Add mapping registry entry for base64
    Base64Serializer base64Ser = new Base64Serializer();
	// 1999 deserializer
	smr.mapTypes(Constants.NS_URI_SOAP_ENC, new org.apache.soap.util.xml.QName(Constants.NS_URI_1999_SCHEMA_XSD, "base64Binary"), byte[].class, null, base64Ser);
	// 2000 deserializer
	smr.mapTypes(Constants.NS_URI_SOAP_ENC, new org.apache.soap.util.xml.QName(Constants.NS_URI_2000_SCHEMA_XSD, "base64Binary"), byte[].class, null, base64Ser);
	// 2001 deserializer
	smr.mapTypes(Constants.NS_URI_SOAP_ENC, new org.apache.soap.util.xml.QName(Constants.NS_URI_2001_SCHEMA_XSD, "base64Binary"), byte[].class, null, base64Ser);
	// Use current serializer for serialization
	smr.mapTypes(Constants.NS_URI_SOAP_ENC, new org.apache.soap.util.xml.QName(Constants.NS_URI_CURRENT_SCHEMA_XSD, "base64Binary"), byte[].class, base64Ser, null);

  	return smr;
  }
  /**
   * Invoke RPC operation on port instance.
   * NOTE: fault processing is not yet implemented.
   * @deprecated
   */
  public boolean executeRequestResponseOperation(
    String operationName,
    WSIFMessage input,
    WSIFMessage output,
    WSIFMessage fault)
    throws WSIFException {
    // find correct operation
    WSIFOperation_ApacheSOAP op =
      getDynamicWSIFOperation(operationName, input.getName(), output.getName());
    if (op == null) {
      throw new WSIFException(
        "no operation named " + operationName + " is available in port " + port);
    }

    // and invoke it
    return op.invokeRequestResponseOperation(input, output, fault);
  }

  public SOAPMappingRegistry getSOAPMappingRegistry() {
    return smr;
  }

  public void setSOAPMappingRegistry(SOAPMappingRegistry smr) {
    this.smr = smr;
  }

  public JROMSOAPMappingRegistry getJROMSOAPMappingRegistry() {
	if (jromSMR == null) 
		jromSMR = new JROMSOAPMappingRegistry(smr);
    return jromSMR;
  }

  public void setJROMSOAPMappingRegistry(JROMSOAPMappingRegistry jsmr) {
    jromSMR = jsmr;
  }  

  public URL getEndPoint() {
    return url;
  }

  public void setEndPoint(URL url) {
    this.url = url;
  }

  public SOAPTransport getSOAPTransport() {
    return st;
  }

  public void setSOAPTransport(SOAPTransport st) {
    this.st = st;
  }

  // where is WSDL defining this abstract mesage
  public Definition getDefinition() {
    return definition;
  }
  public void setDefinition(Definition value) {
    definition = value;
  }
  public Port getPort() {
    return port;
  }
  public void setPort(Port value) {
    port = value;
  }

  // WSIF: keep list of operations available in this port
  public void setDynamicWSIFOperation(
    String name,
    String inputName,
    String outputName,
    WSIFOperation_ApacheSOAP value) {
    operationInstances.put(getKey(name, inputName, outputName), value);
  }

  public WSIFOperation createOperation(String operationName)
    throws WSIFException {
    return createOperation(operationName, null, null);
  }

  public WSIFOperation createOperation(
    String operationName,
    String inputName,
    String outputName)
    throws WSIFException {
    WSIFOperation_ApacheSOAP op =
      getDynamicWSIFOperation(operationName, inputName, outputName);
    if (op == null) {
      throw new WSIFException(
        "Could not create operation: "
          + operationName
          + ":"
          + inputName
          + ":"
          + outputName);
    }
    return op.copy();
  }

  public WSIFOperation_ApacheSOAP getDynamicWSIFOperation(
    String name,
    String inputName,
    String outputName)
    throws WSIFException {

    WSIFOperation_ApacheSOAP operation =
      (WSIFOperation_ApacheSOAP) operationInstances.get(
        getKey(name, inputName, outputName));

    if (operation == null) {
      BindingOperation bop =
        port.getBinding().getBindingOperation(name, inputName, outputName);

      if (bop != null) {
        operation = new WSIFOperation_ApacheSOAP(this, bop.getOperation(), wsifTypeMap);
        if (operation == null) {
          throw new WSIFException(
            "Operation not found from binding operation: " + bop.getName());
        }

        operation.setStyle(this.style);
        operation.setPartSerializerName(this.partSerializerName);

        // get soapActionURI and style from soap:operation
        SOAPOperation soapOperation =
          (SOAPOperation) getExtElem(bop,
            SOAPOperation.class,
            bop.getExtensibilityElements());
        if (soapOperation == null) {
          throw new WSIFException(
            "soapAction must be specified in "
              + " required by WSDL 1.1 soap:operation binding for "
              + bop);
        }
        String soapActionURI = soapOperation.getSoapActionURI();
        operation.setSoapActionURI(soapActionURI);
        if (DEBUG)
          System.err.println(
            "setting actionURI " + soapActionURI + " for op " + operation.getName());
        String opStyle = soapOperation.getStyle();

        // try to get soap:body for input message
        BindingInput binpt = bop.getBindingInput();
        SOAPBody soapInputBody =
          (SOAPBody) getExtElem(binpt, SOAPBody.class, binpt.getExtensibilityElements());
        if (soapInputBody != null) {
          String namespaceURI = soapInputBody.getNamespaceURI();
          if (DEBUG)
            System.err.println(
              "setting namespace " + namespaceURI + " for op " + operation.getName());
          operation.setInputNamespace(namespaceURI);
          String use = soapInputBody.getUse();
          operation.setInputUse(use);

          List encodingStyles = soapInputBody.getEncodingStyles();
          if (encodingStyles != null) {
            if (encodingStyles.size() == 0) {
            }
            operation.setInputEncodingStyle((String) encodingStyles.get(0));
            // quietly ignore if encodingStyles.size() > 1 ...
          }
          List parts = soapInputBody.getParts();
          if (parts != null)
            operation.setPartNames(parts);
        }

        SOAPHeader soapHeader =
          (SOAPHeader) getExtElem(binpt,
            SOAPHeader.class,
            binpt.getExtensibilityElements());
        if (soapHeader != null) {
          throw new WSIFException("not supported input soap:header " + soapHeader);
        }

        List inJmsAttrs = getExtElems(binpt, JmsAttribute.class, binpt.getExtensibilityElements());
        if (inJmsAttrs!=null && inJmsAttrs.size()>0)
        {
          if (st instanceof SOAPJMSConnection) operation.setInputJmsAttributes(inJmsAttrs);
          else throw new WSIFException("jms:attributes found in non-jms binding");
        }

        JmsHeader jmsHeader = (JmsHeader)getExtElem(binpt,JmsHeader.class,binpt.getExtensibilityElements());
        if (jmsHeader!=null)
        {
          if (st instanceof SOAPJMSConnection) operation.setJmsHeader(jmsHeader.getValue());
          else throw new WSIFException("jms:header found in non-jms binding");
        }

        // try to get soap:body for output message
        BindingOutput boutpt = bop.getBindingOutput();
        if (boutpt != null)
        {
          SOAPBody soapOutputBody =
            (SOAPBody) getExtElem(boutpt,
              SOAPBody.class,
              boutpt.getExtensibilityElements());
          if (soapOutputBody != null) {
            // NOTE: element ignored
            //String namespaceURI = soapOutputBody.getNamespaceURI();
            String use = soapOutputBody.getUse();
            operation.setOutputUse(use);

            // NOTE: element ignored
            //List encodingStyles = soapInputBody.getEncodingStyles();
            List parts = soapOutputBody.getParts();
            if (parts != null && parts.size() > 0) {
              operation.setReturnName((String) parts.get(0));
            }
          }
          soapHeader =
            (SOAPHeader) getExtElem(boutpt,
              SOAPHeader.class,
              boutpt.getExtensibilityElements());
          if (soapHeader != null) {
            throw new WSIFException("not supported output soap:header " + soapHeader);
          }

          List outJmsAttrs = getExtElems(boutpt, JmsAttribute.class, boutpt.getExtensibilityElements());
          if (outJmsAttrs!=null && outJmsAttrs.size()>0)
          {
            if (st instanceof SOAPJMSConnection) operation.setOutputJmsAttributes(outJmsAttrs);
            else throw new WSIFException("jms:attributes found in non-jms binding");
          }
        }

        // Only now the operation has enough information to initialize itself
//        operation.prepare();

        // make this operation instance accessible
        setDynamicWSIFOperation(name, inputName, outputName, operation);
      }
    }

    return operation;
  }

  /**
   * Gets the partSerializerName.
   * @return Returns a String
   */
  public String getPartSerializerName() {
    return partSerializerName;
  }

  /**
   * Sets the partSerializerName.
   * @param partSerializerName The partSerializerName to set
   */
  public void setPartSerializerName(String partSerializerName) {
    this.partSerializerName = partSerializerName;
  }
  
  /**
   * Closes the port. All methods are invalid after calling this method.
   */
  public void close() throws WSIFException 
  {
  	if (st!=null && st instanceof SOAPJMSConnection)
  	  ((SOAPJMSConnection)st).close();
  }
  	  

  public String toString() {
    String buff = new String(super.toString() + ":\n");
    buff += "url:" + url;
    if (operationInstances == null) {
        buff += " operationInstances: null";
    } else {
      buff += " operationInstances: size:" + operationInstances.size();
      Iterator it = operationInstances.keySet().iterator();
      int i = 0;
      while (it.hasNext()) {
        String key = (String) it.next();
        WSIFOperation_ApacheSOAP woas =
          (WSIFOperation_ApacheSOAP) operationInstances.get(key);
        buff += "\noperationInstances[" + i + "]:" + key + " " + woas.getName() + " ";
        i++;
      }
    }

  	buff += "\nport:" + (port == null ? "null" : port.getName());
    buff += " soapMappingRegistry:" + smr;
    buff += " jromSoapMappingRegistry:" + jromSMR;
    buff += " soapTransport:" + st;
    return buff;
  }

  /**
   * Tests if this port supports asynchronous calls to operations.
   * 
   * @return true if the port is using a JMS transport, otherwise false
   */
  public boolean supportsAsync() {
     if ( st instanceof SOAPJMSConnection ) {
        return true;
     } else {
     	return false;
     }
  }
  
  public String toShallowString() {
    return super.toString();
  }
}
