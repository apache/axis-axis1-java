/*
 * XmlBeanSerializer.java
 * 
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
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
 * 
 * Original author: Jonathan Colwell
 */

package org.apache.axis.encoding.ser.xbeans;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3.x2001.xmlSchema.SchemaDocument;
import org.w3.x2001.xmlSchema.TopLevelComplexType;
import org.w3.x2001.xmlSchema.TopLevelElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xmlsoap.schemas.wsdl.DefinitionsDocument;
import org.xmlsoap.schemas.wsdl.TDefinitions;
import org.xmlsoap.schemas.wsdl.TTypes;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Class XmlBeanSerializer
 * @author Jonathan Colwell
 */
public class XmlBeanSerializer implements Serializer {

    /**
     * Serialize an element named name, with the indicated attributes
     * and value.
     *
     * @param name       is the element name
     * @param attributes are the attributes...serialize is free to add more.
     * @param value      is the value
     * @param context    is the SerializationContext
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
            throws IOException {
        if (!(value instanceof XmlObject)) {
            throw new IOException(((value != null) ?
                    value.getClass().getName()
                    : "null")
                    + " is not an "
                    + XmlObject.class.getName());
        } else {
            context.setWriteXMLType(null);
            context.startElement(name, attributes);
            XmlCursor xCur = ((XmlObject) value).newCursor();
            if (xCur.toFirstContentToken() == XmlCursor.TokenType.START) {
                do {
                    Node n = xCur.getDomNode();
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        context.writeDOMElement((Element) n);
                    }
                } while (xCur.toNextSibling());
            }
            context.endElement();
        }
    }

    public String getMechanismType() {
        return Constants.AXIS_SAX;
    }

    /**
     * Return XML schema for the specified type, suitable for insertion into
     * the &lt;types&gt; element of a WSDL document, or underneath an
     * &lt;element&gt; or &lt;attribute&gt; declaration.
     *
     * @param javaType the Java Class we're writing out schema for
     * @param types    the Java2WSDL Types object which holds the context
     *                 for the WSDL being generated.
     * @return a type element containing a schema simpleType/complexType
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public Element writeSchema(Class javaType, Types types) throws Exception {
        if (XmlObject.class.isAssignableFrom(javaType)) {
            SchemaType docType = XmlBeans.typeForClass(javaType);

            /*
             * NOTE jcolwell@bea.com 2004-Oct-18 -- 
             * This is a hack to handle node adoption.
             * I don't like it but I need to avoid a 
             * org.w3c.dom.DOMException: WRONG_DOCUMENT_ERR
             * NOTE jcolwell@bea.com 2004-Oct-21 -- 
             * since I already use the Document I'll use it to check 
             * if a schema for the namspace is already in place.
             */
            
            Document doc = types.createElement("deleteme")
                    .getOwnerDocument();
            XmlOptions opts = new XmlOptions()
                    .setLoadReplaceDocumentElement(null);
            Element root = doc.getDocumentElement();
            String schemaSrc = docType.getSourceName();
            InputStream stream = docType.getTypeSystem()
                    .getSourceAsStream(schemaSrc);
            SchemaDocument.Schema schema = null;
            if (schemaSrc.endsWith(".wsdl") || schemaSrc.endsWith(".WSDL")) {
                DefinitionsDocument defDoc =
                        DefinitionsDocument.Factory.parse(stream);
                TTypes tt = defDoc.getDefinitions().getTypesArray(0);
                XmlObject[] kids = selectChildren
                        (tt, SchemaDocument.Schema.class);
                SchemaDocument.Schema[] schemas =
                        new SchemaDocument.Schema[kids.length];

                // NOTE jcolwell@bea.com 2005-Jan-10 -- this is the part that the
                // fancy generics saves me from having to do after each call to 
                // selectChildren(XmlObject, Class)                

                for (int j = 0; j < kids.length; j++) {
                    schemas[j] = (SchemaDocument.Schema) kids[j];
                }
                if (schemas.length == 1) {
                    schema = schemas[0];
                } else {
                    String stNS = docType.getName().getNamespaceURI();
                    //System.out.println("target NS " + stNS);
                    for (int j = 0; j < schemas.length; j++) {
                        /*System.out.println("comparing schema namespace "
                          + schemas[j].getTargetNamespace());*/
                        if (stNS.equals(schemas[j].getTargetNamespace())) {
                            schema = schemas[j];
                            break;
                        }
                    }
                }
            } else {
                SchemaDocument schemaDoc = SchemaDocument.Factory.parse(stream);
                schema = schemaDoc.getSchema();
            }

            /*
             FIXME jcolwell@bea.com 2004-Oct-21 -- it would be great if
             the Types.loadInputSchema took an input source instead of a 
             String so I could directly pass in the input stream instead of 
             providing the schema elements individually.
            */
            DefinitionsDocument defDoc = DefinitionsDocument.Factory
                    .newInstance();
            TDefinitions definitions = defDoc.addNewDefinitions();
            definitions.addNewService();
            Node defEl = definitions.newDomNode(new XmlOptions()
                    .setSaveOuter());
            Document dDoc = defEl.getOwnerDocument();
            if (null == dDoc.getDocumentElement()) {
                dDoc.appendChild(defEl);
            }
            Set existingNameSpaces = new HashSet();
            if (dDoc != null) {
                types.insertTypesFragment(dDoc);
                Element e = (Element) dDoc.getFirstChild().getFirstChild()
                        .getFirstChild();
                if (e != null) {
                    String tn = e.getAttribute("targetNamespace");
                    existingNameSpaces.add(tn);
                    while (null != (e = (Element) e.getNextSibling())) {
                        tn = e.getAttribute("targetNamespace");
                        existingNameSpaces.add(tn);
                    }
                }
            } else {
                throw new Exception("null document");
            }
            if (schema != null) {
                String targetNamespace = schema.getTargetNamespace();
                if (targetNamespace != null) {
                    if (!existingNameSpaces.contains(targetNamespace)) {
                        TopLevelComplexType[] schemaTypes = schema
                                .getComplexTypeArray();
                        for (int j = 0; j < schemaTypes.length; j++) {
                            types.writeSchemaElement(targetNamespace,
                                    (Element) doc
                                    .importNode(schemaTypes[j].newDomNode()
                                    .getFirstChild(),
                                            true));
                        }
                        TopLevelElement[] elements = schema
                                .getElementArray();
                        for (int j = 0; j < elements.length; j++) {
                            types.writeSchemaElement(targetNamespace,
                                    (Element) doc
                                    .importNode(elements[j].newDomNode()
                                    .getFirstChild(),
                                            true));
                        }
                    }
                    return null;
                }
            }
            throw new Exception(javaType.getName()
                    + "did not specify a target namespace");
        } else {
            throw new Exception(javaType.getName()
                    + " must be a subclass of XmlObject");
        }
    }

    // NOTE jcolwell@bea.com 2004-Nov-15 -- 
    // once the WSDLProcessor is changed to an interface, remove this function
    // and use the one in the upcoming XmlBeanWSDLProcessor.
    private static XmlObject[] selectChildren(XmlObject parent,
                                              Class childClass)
            throws IllegalAccessException, NoSuchFieldException {
        // retrieve the SchemaType from the static type field
        SchemaType st = (SchemaType) childClass.getField("type").get(null);
        return parent.selectChildren(st.getDocumentElementName());
    }
}
