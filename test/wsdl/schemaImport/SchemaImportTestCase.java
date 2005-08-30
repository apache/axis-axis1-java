/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package test.wsdl.schemaImport;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import java.io.File;

/**
 * This class contains the methods necessary for testing that the XML Schema
 * import support is functional within WSDL2Java.  This test will generate a
 * WSDL file and then validate that the schema has been appropriate imported
 * into said WSDL.
 *
 * @version   1.00  01 Nov 2002
 * @author    Doug Bitting (douglas.bitting@agile.com)
 */
public class SchemaImportTestCase extends junit.framework.TestCase {
    public SchemaImportTestCase(String name) {
        super(name);
    }

    public void testSchemaImport() {
        String path = "build" + File.separator + "work" + File.separator +
                "test" + File.separator + "wsdl" + File.separator +
                "schemaImport" + File.separator + "foo.wsdl";
        Document doc = null;
        Definition def = null;

        // Make sure that the WSDL appears to be valid
        try {
            doc = XMLUtils.newDocument(path);
            assertNotNull("Unable to locate WSDL file: " + path, doc);
            WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
            def = reader.readWSDL(path, doc);
            assertNotNull("unable to generate WSDL definition from document: " 
                          + path, def);
            assertEquals("Expected xmlns:foo to be set to urn:ImportSchemaTest",
                         "urn:ImportSchemaTest", def.getNamespace("foo"));
        } catch (Exception e) {
            throw new junit.framework.AssertionFailedError("Exception caught: "
                                                           + e);
        }

        // Now check that the schema was properly imported.  It is assumed
        // that WSDL generation is validated in other unit tests.  Here, we
        // are only interested in the schema importing functionality.
        NodeList typeList = doc.getElementsByTagName("wsdl:types");
        Node typeNode = typeList.item(0);
        assertNotNull("types section of the WSDL document", typeNode);
        Element typeElem = (Element) typeNode;

        NodeList nodeList = typeElem.getElementsByTagName("xs:complexType");
        assertTrue("Could not located imported schema", 
                   nodeList.getLength() > 0);
        Element elt = (Element) nodeList.item(0);
        assertEquals("Unexpected complexType", "foo", elt.getAttribute("name"));
        nodeList = elt.getElementsByTagName("xs:documentation");
        assertTrue("Could not find schema documentation",
                   nodeList.getLength() > 0);
    }
}
// End of SchemaImportTestCase.java
