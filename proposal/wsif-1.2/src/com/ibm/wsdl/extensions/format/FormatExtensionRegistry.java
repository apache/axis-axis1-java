// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.format;

/**
 * Extension registry for format binding. 
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class FormatExtensionRegistry extends javax.wsdl.extensions.ExtensionRegistry
{
  public FormatExtensionRegistry()
  {
	super();
	FormatBindingSerializer formatSerializer = new FormatBindingSerializer();
	formatSerializer.registerSerializer(this);
  }  
}
