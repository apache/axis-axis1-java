// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.jms;

import javax.wsdl.extensions.ExtensionRegistry;
/**
 * WSDL Jms extension
 * 
 * @author <a href="mailto:ake@de.ibm.com">Hermann Akermann</a>
 */
public class JmsExtensionRegistry extends ExtensionRegistry {

	public JmsExtensionRegistry() {
		super();
		JmsBindingSerializer jmsBindingSerializer = new JmsBindingSerializer();
		jmsBindingSerializer.registerSerializer(this);
	}
}