// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.stub;

import javax.wsdl.*;
import javax.wsdl.xml.*;
import javax.wsdl.extensions.*;
import javax.wsdl.factory.*;
import com.ibm.wsdl.*;
import com.ibm.wsdl.xml.*;
import com.ibm.wsif.WSIFServiceImpl;

/**
 * WSIF specific implementation of javax.wsdl.factory.WSDLFactory
 * @author Owen Burroughs
 */
public class WSIFPrivateWSDLFactoryImpl extends WSDLFactory
{
  public WSIFPrivateWSDLFactoryImpl()
  {
  }
  
  public Definition newDefinition()
  {
    Definition def = new DefinitionImpl();
    def.setExtensionRegistry(newPopulatedExtensionRegistry());    
    return def;
  }
  
  public javax.wsdl.xml.WSDLReader newWSDLReader()
  {
  	WSDLReaderImpl reader = new WSDLReaderImpl();
  	reader.setFactoryImplName(this.getClass().getName());
  	reader.setExtensionRegistry(newPopulatedExtensionRegistry());
  	return reader;
  }
  
  public javax.wsdl.xml.WSDLWriter newWSDLWriter()
  {
  	WSDLWriterImpl writer = new WSDLWriterImpl();
  	return writer;
  }
  
  public ExtensionRegistry newPopulatedExtensionRegistry()
  {
  	ExtensionRegistry extReg =
      WSIFServiceImpl.getCompositeExtensionRegistry();     
    if (extReg == null)
    {
    	extReg = new com.ibm.wsdl.extensions.PopulatedExtensionRegistry();
    }
    return extReg;
  }
}

