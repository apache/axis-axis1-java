// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.ejb;

/**
 * Extension registry for EJB binding. 
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class EJBExtensionRegistry extends javax.wsdl.extensions.ExtensionRegistry
{
  public EJBExtensionRegistry()
  {
    super();
    EJBBindingSerializer ejbBindingSerializer = new EJBBindingSerializer();
    ejbBindingSerializer.registerSerializer(this);
  }
}
