/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package test.encoding;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializer;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.server.AxisServer;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A little testcase for validating the serialization of inherited type.
 */
public class TestDerivatedBeanSerializer extends TestCase {
	
	QName superTypeQName = new QName("typeNS", "SuperBean");
	QName inheritedTypeQName = new QName("typeNS", "DerivatedBean");


	StringWriter stringWriter;
	SerializationContext context;

	/**
	 * Constructor for DerivatedBeanSerializerTest.
	 * @param arg0
	 */
	public TestDerivatedBeanSerializer(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		// Initialisation of attribute used in the testMethods.
		stringWriter = new StringWriter();
		MessageContext msgContext = new MessageContext(new AxisServer());
		context = new SerializationContextImpl(stringWriter, msgContext);

		// Create a TypeMapping and register the specialized Type Mapping
		TypeMappingRegistry reg = context.getTypeMappingRegistry();
		TypeMapping tm = (TypeMapping) reg.createTypeMapping();
		tm.setSupportedEncodings(new String[] {Constants.URI_DEFAULT_SOAP_ENC});
		reg.register(Constants.URI_DEFAULT_SOAP_ENC, tm);

		tm.register(SuperBean.class, superTypeQName, new BeanSerializerFactory(SuperBean.class,superTypeQName), new BeanDeserializerFactory(SuperBean.class,superTypeQName));
		tm.register(DerivatedBean.class, inheritedTypeQName, new BeanSerializerFactory(DerivatedBean.class,inheritedTypeQName), new BeanDeserializerFactory(DerivatedBean.class,inheritedTypeQName));
	}


	/**
	 * Test the serialization of an simple sequence. The bean contains three
	 * elements (zero, one, two). The excepted result is something like:
	 * <BR>
	 * <PRE>
	 * &lt;SuperBean&gt;
	 *     &lt;zero/&gt;
	 *     &lt;one/&gt;
	 *     &lt;two/&gt;
	 * &lt;/SuperBean&gt;
	 * </PRE>
	 */
    /*
	public void testSuperBeanSerialize() throws Exception {
		BeanSerializer ser = new BeanSerializer(SuperBean.class, superTypeQName);

		Object object = new SuperBean();
		ser.serialize(superTypeQName,null,object,context);
		
		// Check the result
		String msgString = stringWriter.toString();
		StringReader reader = new StringReader(msgString);
		DOMParser parser = new DOMParser();
		parser.parse(new InputSource(reader));
		Document doc = parser.getDocument();
		
		// We only test the order of the attributes.
		NodeList nodes = doc.getFirstChild().getChildNodes();
		assertEquals("1st Attribute", "zero", nodes.item(0).getLocalName());
		assertEquals("2nd Attribute", "one", nodes.item(1).getLocalName());
		assertEquals("3rd Attribute", "two", nodes.item(2).getLocalName());
	}
    */
    
	/**
	 * Test the serialization of an derivated sequence. The derivated bean contains two elements
	 * (three, four) and the super class has three elements (zero, one, two). The excepted
	 * result is something like: <BR>
	 * <PRE>
	 * &lt;DerivatedBean&gt;
	 *     &lt;zero/&gt;
	 *     &lt;one/&gt;
	 *     &lt;two/&gt;
	 *     &lt;three/&gt;
	 *     &lt;four/&gt;
	 * &lt;/DerivatedBean&gt;
	 * </PRE>
	 */
	public void testDerivatedBeanSerialize() throws Exception {
		BeanSerializer ser = new BeanSerializer(DerivatedBean.class, inheritedTypeQName);

		Object object = new DerivatedBean();
		ser.serialize(inheritedTypeQName,null,object,context);
		

		// Check the result
		String msgString = stringWriter.toString();
		StringReader reader = new StringReader(msgString);
		DOMParser parser = new DOMParser();
		parser.parse(new InputSource(reader));
		Document doc = parser.getDocument();
		
		NodeList nodes = doc.getFirstChild().getChildNodes();
		assertEquals("1st Attribute", "zero", nodes.item(0).getLocalName());
		assertEquals("2nd Attribute", "one", nodes.item(1).getLocalName());
		assertEquals("3rd Attribute", "two", nodes.item(2).getLocalName());
		assertEquals("4th Attribute", "three", nodes.item(3).getLocalName());
		assertEquals("First Attribute", "four", nodes.item(4).getLocalName());
	}


}




