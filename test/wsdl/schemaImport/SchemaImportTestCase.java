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
