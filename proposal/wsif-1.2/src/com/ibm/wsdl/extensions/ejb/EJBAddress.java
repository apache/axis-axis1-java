// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.ejb;

import javax.wsdl.*;
import javax.wsdl.extensions.*;

/**
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class EJBAddress implements ExtensibilityElement, java.io.Serializable
{
  protected QName fieldElementType = EJBBindingConstants.Q_ELEM_EJB_ADDRESS;
  // Uses the wrapper type so we can tell if it was set or not.
  protected Boolean fieldRequired = null;

  protected java.lang.String fieldClassName;
  protected java.lang.String fieldArchive;
  protected java.lang.String fieldClassLoader;

	protected java.lang.String fieldJndiName;
  public java.lang.String getClassLoader()
  {
    return fieldClassLoader;
  }
  public java.lang.String getClassName()
  {
    return fieldClassName;
  }
  public java.lang.String getArchive()
  {
    return fieldArchive;
  }
  /**
   * Get the type of this extensibility element.
   *
   * @return the extensibility element's type
   */
  public QName getElementType()
  {
    return fieldElementType;
  }
	public java.lang.String getJndiName() 
	{
		return fieldJndiName;
	}
  /**
   * Get whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
  public Boolean getRequired()
  {
    return fieldRequired;
  }
  public void setClassLoader(java.lang.String newClassLoader)
  {
    fieldClassLoader = newClassLoader;
  }
  public void setClassName(java.lang.String newClassName)
  {
    fieldClassName = newClassName;
  }
  public void setArchive(java.lang.String newArchive)
  {
    fieldArchive = newArchive;
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
	public void setJndiName(java.lang.String newJndiName) 
	{
		fieldJndiName = newJndiName;
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

    strBuf.append("\nEJBAddress (" + fieldElementType + "):");
    strBuf.append("\nrequired=" + fieldRequired);

    strBuf.append("\nclassName=" + fieldClassName);
    strBuf.append("\narchive=" + fieldArchive);
    strBuf.append("\nclassLoader=" + fieldClassLoader);
    strBuf.append("\njndiName=" + fieldJndiName);

    return strBuf.toString();
  }
}
