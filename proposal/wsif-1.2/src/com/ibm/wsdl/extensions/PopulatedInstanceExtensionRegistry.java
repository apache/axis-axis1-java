// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions;

import javax.wsdl.*;
import javax.wsdl.extensions.*;

import com.ibm.wsdl.*;
import com.ibm.wsdl.extensions.instance.*;

/**
 * This class extends ExtensionRegistry that pre-registers
 * serializers/deserializers for the Instance WSDL extensions:<ul>
 *
 * @see javax.wsdl.extensions.ExtensionRegistry
 * @author Aleksander Slominski
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public class PopulatedInstanceExtensionRegistry extends ExtensionRegistry
{
  public PopulatedInstanceExtensionRegistry ()
  {
    super();
    
    InstanceEstablishmentSerializer instanceEstablishmentSerializer =
      new InstanceEstablishmentSerializer();
    
    registerSerializer(Port.class,
                       InstanceConstants.Q_ELEM_ESTABLISHMENT,
                       instanceEstablishmentSerializer);
    registerDeserializer(Port.class,
                         InstanceConstants.Q_ELEM_ESTABLISHMENT,
                         instanceEstablishmentSerializer);
    
  }
  
}

