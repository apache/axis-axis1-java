// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.instance;

import javax.wsdl.*;
import javax.wsdl.extensions.*;

/**
 * @author Aleksander Slominski
 * @author Nirmal K. Mukhi (nmukhi@us.ibm.com)
 */
public class InstanceEstablishment
  implements ExtensibilityElement, java.io.Serializable
{
  protected QName elementType = InstanceConstants.Q_ELEM_ESTABLISHMENT;
  // Uses the wrapper type so we can tell if it was set or not.
  protected Boolean required = null;
//  protected String archive;
  protected String operationName;
  
  /**
   * Set the type of this extensibility element.
   *
   * @param elementType the type
   */
  public void setElementType(QName elementType) {
    this.elementType = elementType;
  }
  
  /**
   * Get the type of this extensibility element.
   *
   * @return the extensibility element's type
   */
  public QName getElementType() {
    return elementType;
  }
  
  /**
   * Set whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
  public void setRequired(Boolean required) {
    this.required = required;
  }
  
  /**
   * Get whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
  public Boolean getRequired() {
    return required;
  }

  public String getOperationName() {
    return operationName;
  }
  
  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }
}

