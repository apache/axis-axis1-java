/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

package test.wsdl.inheritance;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.rpc.ServiceException;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * This class contains the methods necessary for testing that the use inherited methods
 * function in the Java2WSDL tool works as specified.
 * 
 * When using the Java2WSDL tool with the use inherited methods switch on, the tool
 * should generate the appropriate classes to include all of the inherited methods
 * of the specified interface (in addition to the actual methods in the interface).  
 *
 * @version   1.00  21 Jan 2002
 * @author    Brent Ulbricht
 */
public class InheritanceTestCase extends junit.framework.TestCase {

    /**
     *  Constructor used in all tests utilizing the Junit Framework.
     */
    public InheritanceTestCase(String name) {
        super(name);
    } // Constructor

    /**
     *  This method insures that two methods (getLastTradePrice and getRealtimeLastTradePrice)
     *  can be called, and they return the expected stock values.  The main goal is to verify
     *  that the getLastTradePrice method does not cause any compile errors and returns the
     *  expected stock value.  
     *
     *  The getLastTradePrice method originates from the test/wsdl/inheritance/StockQuoteProvider
     *  interface.  The InheritancePortType interface extends the StockQuoteProvider interface.
     *
     *  When the WSDL is generated for the InheritancePortType interface and the use inherited
     *  methods switch is used, all methods from the StockQuoteProvider and InheritancePortType
     *  interfaces should be available for service.
     */
    public void testInheritanceTest() {
        test.wsdl.inheritance.InheritancePortType binding;
        try {
            binding = new InheritancePortTypeServiceLocator().getInheritanceTest();
        } catch (ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // The getLastTradePrice method should return a value of 20.25 when sent the tickerSymbol
        // "SOAP".
        try {
            float expected = 20.25F;
            float actual = binding.getLastTradePrice(new java.lang.String("SOAP"));
            float delta = 0.0F;
            assertEquals("The actual and expected values did not match.", expected, actual, delta);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }

        // The getRealtimeLastTradePrice method should return a value of 21.75 when sent the 
        // tickerSymbol "AXIS".
        try {
            float expected = 21.75F;
            float actual = binding.getRealtimeLastTradePrice(new java.lang.String("AXIS"));
            float delta = 0.0F;
            assertEquals("The actual and expected values did not match.", expected, actual, delta);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }

    } // testInheritanceTest

    /**
     * This test validates the WSDL generated by Java2WSDL 
     */
    public void testStopClasses() {
        String path = "build" + File.separator + "work" + File.separator +
                "test" + File.separator + "wsdl" + File.separator +
                "inheritance" + File.separator + "StopExclude.wsdl";
        Document doc = null;
        Definition def = null;
        try {
            doc = XMLUtils.newDocument(path);
            assertNotNull("Unable to locate WSDL file: " + path, doc);
            WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
            //reader.setFeature("javax.wsdl.verbose", true);
            def = reader.readWSDL(path, doc);
            assertNotNull("unable to generate WSDL definition from document: " + path, def);
        } catch (WSDLException e) {
            throw new junit.framework.AssertionFailedError("WSDL Exception caught: " + e);
        }
        
        // Now check parts of the definition
        
        // types
        // The complex types Baby_bean and Child_bean should exist
        // The type Parent_bean should NOT exist
        NodeList typeList = doc.getElementsByTagName("wsdl:types");
        Node typeNode = typeList.item(0);
        assertNotNull("types section of the WSDL document", typeNode);
        Element typeElem = (Element) typeNode;

        boolean babyFound = false;
        boolean childFound = false;
        NodeList nodeList = typeElem.getElementsByTagName("complexType");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            String name = nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue();
            if (name.equals("Baby_bean"))
                babyFound = true;
            else if (name.equals("Child_bean"))
                childFound = true;
            else if (name.equals("Parent_bean"))
                assertTrue("Parent_bean found in WSDL types section", false);
            else
                assertTrue("Unknown node found in types section: " + name, false);
        }
        assertTrue("Baby_bean not found in WSDL types section", babyFound);
        assertTrue("Child_bean not found in WSDL types section", childFound);
            
        // operations
        // The only ones we shold find are baby_method and child_method
        boolean babyOpFound = false;
        boolean childOpFound = false;
        
        // we iterate the portTypes, but we check to make sure there is only one
        Iterator ip = def.getPortTypes().values().iterator();
        PortType portType = (PortType) ip.next();
        List operationList = portType.getOperations();
        for (int i = 0; i < operationList.size(); ++i) {
            String opName = ((Operation) operationList.get(i)).getName();
            if (opName.equals("baby_method"))
                babyOpFound = true;
            else if (opName.equals("child_method"))
                childOpFound = true;
            else if (opName.equals("parent_method"))
                assertTrue("parent_method operation found in WSDL", false);
            else
                assertTrue("Invalid operation found in WSDL: " + opName, false);
        }
        assertTrue("WSDL has more than one portType", !ip.hasNext());
        assertTrue("baby_method operation not found in WSDL", babyOpFound);
        assertTrue("child_method operation not found in WSDL ", childOpFound);

    } // testStopClasses

} // InheritanceTestCase

