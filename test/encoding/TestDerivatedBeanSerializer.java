/*
 * Copyright 2002-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.encoding;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializer;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.server.AxisServer;
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
        Document doc = XMLUtils.newDocument(new InputSource(reader));
		
		NodeList nodes = doc.getFirstChild().getChildNodes();
		assertEquals("1st Attribute", "zero", nodes.item(0).getLocalName());
		assertEquals("2nd Attribute", "one", nodes.item(1).getLocalName());
		assertEquals("3rd Attribute", "two", nodes.item(2).getLocalName());
		assertEquals("4th Attribute", "three", nodes.item(3).getLocalName());
		assertEquals("First Attribute", "four", nodes.item(4).getLocalName());
	}


}




