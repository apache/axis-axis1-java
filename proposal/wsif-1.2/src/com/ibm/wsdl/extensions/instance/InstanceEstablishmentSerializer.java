// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.instance;

import java.io.*;
import org.w3c.dom.*;
import javax.wsdl.*;
import javax.wsdl.extensions.*;

import com.ibm.wsdl.*;
import com.ibm.wsdl.util.xml.*;

/**
 * @author Aleksander Slominski
 * @author Nirmal K. Mukhi (nmukhi@us.ibm.com)
 */
public class InstanceEstablishmentSerializer
  implements ExtensionSerializer,  ExtensionDeserializer,  Serializable
{
  public void marshall(Class parentType,
                       QName elementType,
                       ExtensibilityElement extension,
                       PrintWriter pw,
                       Definition def,
                       ExtensionRegistry extReg)
    throws WSDLException
  {
    InstanceEstablishment instanceEstablishment =
      (InstanceEstablishment) extension;
    
    if (instanceEstablishment != null) {
      pw.print("      <instance:establishment");
      
      DOMUtils.printAttribute(InstanceConstants.ATTR_OPERATION,
                              instanceEstablishment.getOperationName(),
                              pw);
      
      
      Boolean required = instanceEstablishment.getRequired();
      
      if (required != null) {
        DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED,
                                         required.toString(),
                                         def,
                                         pw);
      }
      
      pw.println("/>");
    }
  }
  
  public ExtensibilityElement unmarshall(Class parentType,
                                         QName elementType,
                                         Element el,
                                         Definition def,
                                         ExtensionRegistry extReg)
    throws WSDLException
  {
    InstanceEstablishment instanceEstablishment =
      new InstanceEstablishment();
    String operationName = DOMUtils.getAttribute(
      el,
      InstanceConstants.ATTR_OPERATION);

    String requiredStr = DOMUtils.getAttributeNS(el,
                                                 Constants.NS_URI_WSDL,
                                                 Constants.ATTR_REQUIRED);
    
    if (operationName != null) {
      instanceEstablishment.setOperationName(operationName);
    }
    
    //    if (archive != null) {
    //      javaAddress.setArchive(archive);
    //    }
    
    if (requiredStr != null) {
      instanceEstablishment .setRequired(new Boolean(requiredStr));
    }
    
    return instanceEstablishment ;
  }
  
}

