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

package test.message;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;
import org.apache.axis.client.AxisClient;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.EnvelopeBuilder;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.soap.MessageFactoryImpl;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import test.AxisTestBase;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.Text;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.OutputStream;
import java.util.Iterator;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Test {@link MessageElement} class.
 *
 * @author Glyn Normington (glyn@apache.org)
 */
public class TestMessageElement extends AxisTestBase {

    public TestMessageElement(String name) {
        super(name);
    }

    // Test JAXM methods...
    public void testParentage() throws Exception {
        SOAPElement parent = new MessageElement("ns", "parent");
        SOAPElement child = new MessageElement("ns", "child");
        child.setParentElement(parent);
        assertEquals("Parent is not as set", parent, child.getParentElement());
    }

    public void testAddChild() throws Exception {
        SOAPConstants sc = SOAPConstants.SOAP11_CONSTANTS;
        EnvelopeBuilder eb = new EnvelopeBuilder(Message.REQUEST, sc);
        DeserializationContext dc = new DeserializationContext(null, eb);
        MessageElement parent = new MessageElement("parent.names",
                                                "parent",
                                                "parns",
                                                null,
                                                dc);
        Name c1 = new PrefixedQName("child1.names", "child1" ,"c1ns");
        SOAPElement child1 = parent.addChildElement(c1);
        SOAPElement child2 = parent.addChildElement("child2");
        SOAPElement child3 = parent.addChildElement("child3.names", "parns");
        SOAPElement child4 = parent.addChildElement("child4",
                                                    "c4ns",
                                                    "child4.names");
        SOAPElement child5 = new MessageElement("ns", "child5");
        parent.addChildElement(child5);
        SOAPElement c[] = {child1, child2, child3, child4, child5}; 
        
        Iterator children = parent.getChildElements();
        for (int i = 0; i < 5; i++) {
            assertEquals("Child " + (i+1) + " not found",
                         c[i],
                         children.next());
        }
        assertTrue("Unexpected child", !children.hasNext());
       
        Iterator c1only = parent.getChildElements(c1);
        assertEquals("Child 1 not found", child1, c1only.next());
        assertTrue("Unexpected child", !c1only.hasNext());
    }

    public void testDetachNode() throws Exception {
        SOAPConstants sc = SOAPConstants.SOAP11_CONSTANTS;
        EnvelopeBuilder eb = new EnvelopeBuilder(Message.REQUEST, sc);
        DeserializationContext dc = new DeserializationContext(null, eb);
        SOAPElement parent = new MessageElement("parent.names",
                                                "parent",
                                                "parns",
                                                null,
                                                dc);
        SOAPElement child1 = parent.addChildElement("child1");
        SOAPElement child2 = parent.addChildElement("child2");
        SOAPElement child3 = parent.addChildElement("child3");

        child2.detachNode();
        SOAPElement c[] = {child1, child3}; 
        
        Iterator children = parent.getChildElements();
        for (int i = 0; i < 2; i++) {
            assertEquals("Child not found",
                         c[i],
                         children.next());
        }
        assertTrue("Unexpected child", !children.hasNext());
    }

    public void testGetCompleteAttributes() throws Exception {
        MessageElement me = 
            new MessageElement("http://www.wolfram.com","Test");
        me.addNamespaceDeclaration("pre", "http://www.wolfram2.com");
        Attributes attrs = me.getCompleteAttributes();
        assertEquals(attrs.getLength(), 1);
    }
    
    public void testAddNamespaceDeclaration() throws Exception {
        MessageElement me = 
            new MessageElement("http://www.wolfram.com","Test");
        me.addNamespaceDeclaration("pre", "http://www.wolfram2.com");
        me.addAttribute(
            "http://www.w3.org/2001/XMLSchema-instance", 
            "type",
            "pre:test1");
        boolean found = false;
        Iterator it = me.getNamespacePrefixes();
        while(!found && it.hasNext()){ 
            String prefix = (String)it.next();
            if (prefix.equals("pre") && 
                me.getNamespaceURI(prefix).equals("http://www.wolfram2.com")) {
                found = true;
            }
        }
        assertTrue("Did not find namespace declaration \"pre\"", found);
    }
    
    public void testQNameAttrTest() throws Exception {
        MessageElement me = 
            new MessageElement("http://www.wolfram.com","Test");
        me.addAttribute(
            "http://www.w3.org/2001/XMLSchema-instance", 
            "type",
            new QName("http://www.wolfram2.com", "type1"));
        MessageElement me2 = 
            new MessageElement("http://www.wolfram.com", "Child", (Object)"1");
        me2.addAttribute(
            "http://www.w3.org/2001/XMLSchema-instance", 
            "type",
            new QName("http://www.w3.org/2001/XMLSchema", "int"));
        me.addChildElement(me2);
        String s1 = me.toString();
        String s2 = me.toString();
        assertEquals(s1, s2);
    }
    
    public void testMessageElementNullOngetNamespaceURI() throws Exception{
        String data="<anElement xmlns:ns1=\"aNamespace\" href=\"unknownProtocol://data\"/>";
        data="<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body>"+
             data+"</SOAP-ENV:Body></SOAP-ENV:Envelope>";
        MessageContext ctx=new MessageContext(new AxisClient());
        DeserializationContext dser = new DeserializationContext(
                                           new org.xml.sax.InputSource(new StringReader(data)),
                                           ctx,
                                           Message.REQUEST);
        dser.parse();
		MessageElement elem=dser.getEnvelope().getBodyByName("","anElement");
        assertEquals("aNamespace",elem.getNamespaceURI("ns1"));
        assertEquals("ns1",elem.getPrefix("aNamespace"));
    }    
    
    public void testSOAPElementMessageDoesNotHavePrefix() throws Exception {
            String A_TAG = "A";
            String A_PREFIX = "a";
            String A_NAMESPACE_URI = "http://schemas.com/a";
            String AA_TAG = "AA";

            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage message = messageFactory.createMessage();
            SOAPPart part = message.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
            SOAPBody body = envelope.getBody();

            envelope.getHeader().detachNode();

            Name aName = envelope.createName(A_TAG, A_PREFIX, A_NAMESPACE_URI);
            SOAPBodyElement aBodyElement = body.addBodyElement(aName);
            aBodyElement.addChildElement(AA_TAG, A_PREFIX);
            String data = envelope.toString();

            MessageContext ctx = new MessageContext(new AxisClient());
            DeserializationContext dser = new DeserializationContext(
                    new org.xml.sax.InputSource(new StringReader(data)),
                    ctx,
                    Message.REQUEST);
            dser.parse();
            MessageElement elem = dser.getEnvelope().getBodyByName(A_NAMESPACE_URI, A_TAG);
            Iterator iterator = elem.getChildElements();
            while(iterator.hasNext()){
                MessageElement childElem = (MessageElement)iterator.next();
                Name name = childElem.getElementName();
                assertEquals(A_NAMESPACE_URI, name.getURI());
                assertEquals(AA_TAG, name.getLocalName());
            }    
    }

    public void testSerializable() throws Exception
    {
        MessageElement m1 = 
            new MessageElement("http://www.wolfram.com","Test");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(m1);
        oos.flush();
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        MessageElement m2 = (MessageElement)ois.readObject();

        assertEquals("m1 is not the same as m2", m1, m2);
        assertEquals("m2 is not the same as m1", m2, m1);
    }
    
    public void testElementConstructor() throws Exception {
        String xmlIn = "<h:html xmlns:xdc=\"http://www.xml.com/books\"\n" +
                       "        xmlns:h=\"http://www.w3.org/HTML/1998/html4\">\n" +
                       " <h:head><h:title>Book Review</h:title></h:head>\n" +
                       " <h:body>\n" +
                       "  <xdc:bookreview>\n" +
                       "   <xdc:title>XML: A Primer</xdc:title>\n" +
                       "   <h:table>\n" +
                       "    <h:tr align=\"center\">\n" +
                       "     <h:td>Author</h:td><h:td>Price</h:td>\n" +
                       "     <h:td>Pages</h:td><h:td>Date</h:td></h:tr>\n" +
                       "     <!-- here is a comment -->\n" +
                       "    <h:tr align=\"left\">\n" +
                       "     <h:td><xdc:author>Simon St. Laurent</xdc:author></h:td>\n" +
                       "     <h:td><xdc:price>31.98</xdc:price></h:td>\n" +
                       "     <h:td><xdc:pages>352</xdc:pages></h:td>\n" +
                       "     <h:td><xdc:date>1998/01</xdc:date></h:td>\n" +
                       "     <h:td><![CDATA[text content]]></h:td>\n" +
                       "    </h:tr>\n" +
                       "   </h:table>\n" +
                       "  </xdc:bookreview>\n" +
                       " </h:body>\n" +
                       "</h:html>";
        
        Document doc = XMLUtils.newDocument(new ByteArrayInputStream(xmlIn.getBytes()));
        MessageElement me = new MessageElement(doc.getDocumentElement());
        String xmlOut = me.getAsString();
        this.assertXMLEqual(xmlIn,xmlOut);
    }
    
    public void testElementConstructor2() throws Exception {
        String xmlIn = "<!-- This file can be used to deploy the echoAttachments sample -->\n" +
                "<!-- using this command: java org.apache.axis.client.AdminClient attachdeploy.wsdd -->\n" +
                "\n" +
                "<!-- This deploys the echo attachment service.  -->\n" +
                "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\" xmlns:ns1=\"urn:EchoAttachmentsService\" >\n" +
                "  <service name=\"urn:EchoAttachmentsService\" provider=\"java:RPC\" >\n" +
                "    <parameter name=\"className\" value=\"samples.attachments.EchoAttachmentsService\"/>\n" +
                "    <parameter name=\"allowedMethods\" value=\"echo echoDir\"/>\n" +
                "    <operation name=\"echo\" returnQName=\"returnqname\" returnType=\"ns1:DataHandler\" >\n" +
                "        <parameter name=\"dh\" type=\"ns1:DataHandler\"/>\n" +
                "      </operation>\n" +
                "\n" +
                " <typeMapping deserializer=\"org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory\"\n" +
                "   languageSpecificType=\"java:javax.activation.DataHandler\" qname=\"ns1:DataHandler\"\n" +
                "    serializer=\"org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory\"\n" +
                "    encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                "     />\n" +
                "  </service>\n" +
                "\n" +
                "</deployment>";
        
        Document doc = XMLUtils.newDocument(new ByteArrayInputStream(xmlIn.getBytes()));
        MessageElement me = new MessageElement(doc.getDocumentElement());
        String xmlOut = me.getAsString();
        System.out.println(xmlOut);
        this.assertXMLEqual(xmlIn,xmlOut);
    }

    /**
     * Test setting the text value on a MessageElement in various ways.
     *
     * @throws Exception on error
     */ 
    public void testSetValue() throws Exception
    {
        MessageElement me;
        final QName name = new QName( "urn:xyz", "foo", "xyz" );
        final String value = "java";
        // Test #1: set value via MessageElement(name, value) constructor
        me = new MessageElement( name, value );
        assertEquals( value, me.getValue() );
        assertEquals( value, me.getObjectValue() );
        // Test #2a: set value via setValue(value)
        me = new MessageElement( name );
        me.setValue( value );
        assertEquals( value, me.getValue() );
        assertEquals( value, me.getObjectValue() );
        // Test #2b: call setValue(value) on SOAPElement w/ more than one child (illegal)
        me = new MessageElement( name );
        me.addTextNode("cobol");
        me.addTextNode("fortran");
        try
        {
            me.setValue( value );
            fail("setValue() should throw an IllegalStateException when called on a SOAPElement with more than one child");
        }
        catch ( RuntimeException re )
        {
            assertTrue(re instanceof IllegalStateException);
        }
        // Test #2c: call setValue(value) on SOAPElement w/ a non-Text child (illegal)
        me = new MessageElement( name );
        me.addChildElement("pascal");
        try
        {
            me.setValue( value );
            fail("setValue() should throw an IllegalStateException when called on a SOAPElement with a non-Text child");
        }
        catch ( RuntimeException re )
        {
            assertTrue(re instanceof IllegalStateException);
        }
        // Test #2d: set value via setValue(value) on Text child
        me = new MessageElement( name );
        me.addTextNode( "c++" );
        Object child = me.getChildren().get(0);
        assertTrue( child instanceof Text );
        ((Text)child).setValue( value );
        assertEquals( value, me.getValue());
        assertEquals( null, me.getObjectValue());
        // Test #3: set value via setObjectValue(value)
        me = new MessageElement( name );
        me.setObjectValue( value );
        assertEquals( value, me.getValue());
        assertEquals( value, me.getObjectValue());
        // Test #4: set value via addTextNode(value)
        me = new MessageElement( name );
        me.addTextNode( value );
        assertEquals( value, me.getValue());
        assertEquals( null, me.getObjectValue());
        // Test #5: set value via dser.parse()
        SOAPMessage msg = MessageFactoryImpl.newInstance().createMessage();
        msg.getSOAPBody().addChildElement(new org.apache.axis.message.SOAPBodyElement(name, value));
        OutputStream os = new ByteArrayOutputStream( );
        msg.writeTo(os);
        DeserializationContext dser = new DeserializationContext(new InputSource(new StringReader(os.toString())), new MessageContext(new AxisServer()),Message.REQUEST);
        dser.parse();
        me = (MessageElement) dser.getEnvelope().getBodyElements().get( 0 );
        assertEquals( value, me.getValue());
        assertEquals( value, me.getObjectValue());
    }

    public void testGetNodeValue() throws Exception {
        String data = null;

        data = "<anElement xmlns:ns1=\"aNamespace\"/>";
        
        assertTrue(getNodeValueContext(data) == null);
        assertTrue(getNodeValueDOM(data) == null);

        data = "<anElement xmlns:ns1=\"aNamespace\">FooBar</anElement>";
        
        assertEquals("FooBar", getNodeValueContext(data));
        assertEquals("FooBar", getNodeValueDOM(data));

        data = "<anElement xmlns:ns1=\"aNamespace\"><bElement>FooBar</bElement></anElement>";
        
        assertTrue(getNodeValueContext(data) == null);
        assertTrue(getNodeValueDOM(data) == null);

        data = "<anElement xmlns:ns1=\"aNamespace\"><bElement>FooBar</bElement>Mixed</anElement>";
        
        assertTrue(getNodeValueContext(data) == null);
        assertTrue(getNodeValueDOM(data) == null);

        data = "<anElement xmlns:ns1=\"aNamespace\">Foo<bElement>FooBar</bElement>Mixed</anElement>";
        
        assertEquals("Foo", getNodeValueContext(data));
        assertEquals("Foo", getNodeValueDOM(data));
    }

    private String getNodeValueDOM(String data) throws Exception {
        Document doc = 
            XMLUtils.newDocument(new org.xml.sax.InputSource(new StringReader(data)));
        MessageElement elem = new MessageElement(doc.getDocumentElement());

        assertTrue(elem.getDeserializationContext() == null);
        return elem.getValue();
    }

    private String getNodeValueContext(String data) throws Exception {
        String env = 
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body>" +
            data +
            "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
        
        MessageContext ctx = new MessageContext(new AxisClient());
        DeserializationContext dser = new DeserializationContext(
                           new org.xml.sax.InputSource(new StringReader(env)),
                           ctx,
                           Message.REQUEST);
        dser.parse();

        MessageElement elem = dser.getEnvelope().getFirstBody();
        assertTrue(elem.getDeserializationContext() != null);
        return elem.getValue();
    }


    public static void main(String[] args) throws Exception {
        new TestRunner().doRun( new TestMessageElement( "TestMessageElement" ) );
    }
}
