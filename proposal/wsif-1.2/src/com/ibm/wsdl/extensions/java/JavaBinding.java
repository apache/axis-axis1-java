// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.java;

import javax.wsdl.*;
import javax.wsdl.extensions.*;

/**
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class JavaBinding implements ExtensibilityElement, java.io.Serializable
{
  protected QName fieldElementType = JavaBindingConstants.Q_ELEM_JAVA_BINDING;
  // Uses the wrapper type so we can tell if it was set or not.
  protected Boolean fieldRequired = null;

  /**
   * Get the type of this extensibility element.
   *
   * @return the extensibility element's type
   */
  public QName getElementType()
  {
    return fieldElementType;
  }
  
  /**
   * Get whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
  public Boolean getRequired()
  {
    return fieldRequired;
  }
  
  /**
   * Set the type of this extensibility element.
   *
   * @param elementType the type
   */
  public void setElementType(QName elementType)
  {
    fieldElementType = elementType;
  }
  
  /**
   * Set whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
  public void setRequired(Boolean required)
  {
    fieldRequired = required;
  }
  
  public String toString()
  {
    StringBuffer strBuf = new StringBuffer(super.toString());

    strBuf.append("\nJavaOperation (" + fieldElementType + "):");
    strBuf.append("\nrequired=" + fieldRequired);

    return strBuf.toString();
  }
}
