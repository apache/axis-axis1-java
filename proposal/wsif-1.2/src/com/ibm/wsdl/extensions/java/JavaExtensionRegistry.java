// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.java;

/**
 * Extension registry for Java binding. 
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class JavaExtensionRegistry extends javax.wsdl.extensions.ExtensionRegistry
{
  public JavaExtensionRegistry()
  {
    super();
    JavaBindingSerializer javaBindingSerializer = new JavaBindingSerializer();
    javaBindingSerializer.registerSerializer(this);
  }
}
