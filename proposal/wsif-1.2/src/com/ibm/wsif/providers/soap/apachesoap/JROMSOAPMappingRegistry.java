// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.soap.apachesoap;

import org.apache.soap.encoding.SOAPMappingRegistry;
import org.apache.soap.util.xml.*;
import org.apache.soap.Constants;
import com.ibm.jrom.*;

/**
 * A SOAP Mapping Registry which is specialized for serializing
 * and deserializing to JROM objects
 * @author Owen Burroughs
 */

public class JROMSOAPMappingRegistry extends SOAPMappingRegistry {

    /**
     * Constructor for JROMSOAPMappingRegistry
     */
    public JROMSOAPMappingRegistry() {
        super();
    }

    /**
     * Constructor for JROMSOAPMappingRegistry
     * @param registry The parent SOAP mapping registry      
     */
    public JROMSOAPMappingRegistry(SOAPMappingRegistry registry) {
        super(registry);
    }

    /**
     * Constructor for JROMSOAPMappingRegistry
     * @param registry The parent SOAP mapping registry
     * @param schemaURI The namespace URI of XSD to be used for serializers.
     */
    public JROMSOAPMappingRegistry(SOAPMappingRegistry registry, String schemaURI) {
        super(registry, schemaURI);
    }

	/**
	 * Get the serializer for the specified class and encoding
	 * @param javaType The class to serialize
	 * @param encodingStyleURI The namespace of the encoding 
	 * @return The serializer if found or null otherwise 
	 */	
    public Serializer querySerializer(Class javaType, String encodingStyleURI) {
		if (JROMValue.class.isAssignableFrom(javaType)) return new JROMSerializer();
		Serializer ser = super.querySerializer(javaType, encodingStyleURI);
        return ser;
    }

	/**
	 * Get the deserializer for the specified element and encoding 
	 * @param elementType The element to deserialize
	 * @param encodingStyleURI The namespace of the encoding
	 * @return The deserializer if found or null otherwise 
	 */	
    public Deserializer queryDeserializer(
        	QName elementType,
        	String encodingStyleURI) {
        Deserializer deser = super.queryDeserializer(elementType, encodingStyleURI);
        if (deser != null) {
        	String ns = elementType.getNamespaceURI();        	
        	if (ns.equals(Constants.NS_URI_1999_SCHEMA_XSD)
        	  || ns.equals(Constants.NS_URI_2000_SCHEMA_XSD)
        	  || ns.equals(Constants.NS_URI_2001_SCHEMA_XSD))
        	{
        		return new JROMSerializer();
        	}
        }
        return deser;
    }
}