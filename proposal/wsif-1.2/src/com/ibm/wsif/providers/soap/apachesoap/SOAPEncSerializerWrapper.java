// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.soap.apachesoap;

import java.io.*;
import org.apache.soap.Utils;
import org.apache.soap.rpc.SOAPContext;
import org.apache.soap.util.Bean;
import org.apache.soap.util.xml.*;
import org.w3c.dom.*;

public class SOAPEncSerializerWrapper implements PartSerializer 
{
	private Serializer targetSerializer = null;
	private Deserializer targetDeserializer = null;
	private Object part = null;
	/*
	 * @see PartSerializer#setPart(Object)
	 */
	public void setPart(Object aPart) {
		this.part = aPart;
	}

	/*
	 * @see PartSerializer#getPart()
	 */
	public Object getPart() {
		return this.part;
	}

	/*
	 * @see PartSerializer#getPart(Class)
	 */
	public Object getPart(Class partClass) {
		return null;
	}

	/*
	 * @see PartSerializer#getPartQName()
	 */
	public javax.wsdl.QName getPartQName() {
		return null;
	}

	/*
	 * @see PartSerializer#setPartQName(QName)
	 */
	public void setPartQName(javax.wsdl.QName qName) {
	}

	/*
	 * @see Serializer#marshall(String, Class, Object, Object, Writer, 
	 * NSStack, XMLJavaMappingRegistry, SOAPContext)
	 */
	public void marshall(String inScopeEncStyle, Class javaType, Object src, 
	                     Object context, Writer sink, NSStack nsStack, 
	                     XMLJavaMappingRegistry xjmr, SOAPContext ctx) 
	  throws IllegalArgumentException, IOException 
	{
		if(this.targetSerializer != null)
			this.targetSerializer.marshall(inScopeEncStyle, javaType, this.part, 
			                               context, sink, nsStack, xjmr, ctx);
	}

	/*
	 * @see Deserializer#unmarshall(String, QName, Node, XMLJavaMappingRegistry, 
	 * SOAPContext)
	 */
	public Bean unmarshall(String inScopeEncStyle, QName elementType, Node src, 
	                       XMLJavaMappingRegistry xjmr, SOAPContext ctx) 
	  throws IllegalArgumentException 
	{
		if(this.targetDeserializer != null)
			return this.targetDeserializer.unmarshall(
			  inScopeEncStyle, elementType, src, xjmr, ctx);
		return null;
	}
	
	

	/**
	 * Gets the targetDeserializer.
	 * @return Returns a Deserializer
	 */
	public Deserializer getTargetDeserializer() {
		return targetDeserializer;
	}

	/**
	 * Sets the targetDeserializer.
	 * @param targetDeserializer The targetDeserializer to set
	 */
	public void setTargetDeserializer(Deserializer targetDeserializer) {
		this.targetDeserializer = targetDeserializer;
	}

	/**
	 * Gets the targetSerializer.
	 * @return Returns a Serializer
	 */
	public Serializer getTargetSerializer() {
		return targetSerializer;
	}

	/**
	 * Sets the targetSerializer.
	 * @param targetSerializer The targetSerializer to set
	 */
	public void setTargetSerializer(Serializer targetSerializer) {
		this.targetSerializer = targetSerializer;
	}

}

