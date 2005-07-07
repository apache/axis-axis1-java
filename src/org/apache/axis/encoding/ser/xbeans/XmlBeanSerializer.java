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

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalElement;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xmlsoap.schemas.wsdl.DefinitionsDocument;
import org.xmlsoap.schemas.wsdl.TTypes;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Class XmlBeanSerializer
 *
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
     * Return XML schema for the specified type, suitable for insertion into the
     * &lt;types&gt; element of a WSDL document, or underneath an
     * &lt;element&gt; or &lt;attribute&gt; declaration.
     *
     * @param javaType the Java Class we're writing out schema for
     * @param types    the Java2WSDL Types object which holds the context for the
     *                 WSDL being generated.
     * @return a type element containing a schema simpleType/complexType
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public Element writeSchema(Class javaType, Types types) throws Exception {
        try {
            if (!XmlObject.class.isAssignableFrom(javaType)) {
                throw new RuntimeException(
                        "Invalid Object type is assigned to the XMLBeanSerialization Type: "
                                + javaType);
            }
            SchemaType docType = XmlBeans.typeForClass(javaType);
            writeSchemaForDocType(docType, types);
            // assume that the writeSchemaForDocType wrote the schema
            // for the type and all the dependent types.
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void writeSchemaForDocType(SchemaType docType, Types types)
            throws Exception {
        SchemaDocument.Schema[] mySchemas = findtSchemaDocument(docType);
        QName q = docType.getName();
        XmlObject typeNodeInWSDL = getTypeNode(mySchemas, q);
        if (null == typeNodeInWSDL)
            throw new RuntimeException(
                    "Type for object not found in the assigned WSDL file. "
                            + docType.getName() + " schema in: "
                            + docType.getSourceName());
        //        insertDependentTypes(typeNodeInWSDL, types);
        Node n = typeNodeInWSDL.getDomNode();
        Document doc = types.createElement(
                "element_to_get_document_useless_otherwise").getOwnerDocument();
        Element e = (Element) doc.importNode(n, true);
        try {
            types.writeSchemaElementDecl(q, e);
        } catch (AxisFault e1) {
            // this means the types was already in... fine!
            // TBD: make sure there are other types of exceptions are at least
            // reported
        }
        Set dependentTypes = new HashSet();
        getAllDependentTypes(typeNodeInWSDL, dependentTypes);
        for (java.util.Iterator it = dependentTypes.iterator(); it.hasNext();) {
            QName nxtType = (QName) it.next();
            Class nxtJavaType;
            // add the class if it is an xml bean
            if (null != (nxtJavaType = q2UserClass(nxtType))
                    && XmlObject.class.isAssignableFrom(nxtJavaType)) {
                writeSchema(nxtJavaType, types);
            }
        }
    }

    private Class q2UserClass(QName qname) {
        SchemaTypeLoader stl = XmlBeans.getContextTypeLoader();
        SchemaType st = stl.findType(qname);
        if (st == null) {
            SchemaField sf = stl.findElement(qname);
            if (sf != null)
                st = sf.getType();
        }
        if (st != null && !st.isBuiltinType())
            return st.getJavaClass();
        // for classes that are not found, or are built in
        return null;
    }

    /**
     * @param nodeInWSDL
     * @param dependentTypes Walk all the nodes under the nodeInWSDL if there is an 'element' type the
     *                       add its types or references to the dependent type.
     */
    private void getAllDependentTypes(XmlObject nodeInWSDL,
                                      Set dependentTypes) {
        // scan for any node under the type that has "type" or "ref" attribute
        XmlCursor cursor = nodeInWSDL.newCursor();
        if (cursor.toFirstChild()) { // has child
            while (true) {
                getAllDependentTypes(cursor.getObject(), dependentTypes);
                if (!cursor.toNextSibling())
                    break;
            }
        }
        if (nodeInWSDL.schemaType().getName().getLocalPart().equals(
                "localElement")) {
            LocalElement e = (LocalElement) nodeInWSDL;
            if (e.isSetType())
                dependentTypes.add(e.getType());
            else if (e.isSetRef())
                dependentTypes.add(e.getRef());
        }
    }

    public static DefinitionsDocument parseWSDL(String wsdlLocation)
            throws IOException, MalformedURLException, XmlException {
        if (wsdlLocation.indexOf("://") > 2) {
            return parseWSDL(new URL(wsdlLocation));
        } else {
            return parseWSDL(new File(wsdlLocation));
        }
    }

    public static DefinitionsDocument parseWSDL(File wsdlFile)
            throws IOException, XmlException {
        return DefinitionsDocument.Factory.parse(wsdlFile);
    }

    public static DefinitionsDocument parseWSDL(URL wsdlURL)
            throws IOException, XmlException {
        return DefinitionsDocument.Factory.parse(wsdlURL);
    }

    public static DefinitionsDocument parseWSDL(InputStream wsdlStream)
            throws IOException, XmlException {
        return DefinitionsDocument.Factory.parse(wsdlStream);
    }

    public static SchemaDocument parseSchema(InputStream stream)
            throws XmlException, IOException {
        return SchemaDocument.Factory.parse(stream);
    }

    public static SchemaDocument.Schema[] selectChildren(XmlObject parent, Class childClass)
            throws IllegalAccessException, NoSuchFieldException {
        // retrieve the SchemaType from the static type field
        SchemaType st = (SchemaType) childClass.getField("type").get(null);
        XmlObject[] kids = parent.selectChildren(st.getDocumentElementName());
        SchemaDocument.Schema[] castKids = (SchemaDocument.Schema[]) Array.newInstance(childClass, kids.length);
        for (int j = 0; j < castKids.length; j++) {
            castKids[j] = (SchemaDocument.Schema) kids[j];
        }
        return castKids;
    }

    public static SchemaDocument.Schema[] findtSchemaDocument(SchemaType docType)
            throws XmlException, IOException, IllegalAccessException, NoSuchFieldException {
        SchemaDocument.Schema[] schemas = null;
        String schemaSrc = docType.getSourceName();
        InputStream stream = null;
        try {
            stream = docType.getTypeSystem().getSourceAsStream(schemaSrc);
            if (null == stream) {
                throw new RuntimeException("WSDL file not found: " + schemaSrc);
            }
            if (schemaSrc.toLowerCase().endsWith(".wsdl")) {
                TTypes tt = parseWSDL(stream).getDefinitions().getTypesArray(0);
                schemas = selectChildren(tt, SchemaDocument.Schema.class);
            } else {
                SchemaDocument schemaDoc = parseSchema(stream);
                schemas = new SchemaDocument.Schema[1];
                schemas[0] = schemaDoc.getSchema();
            }
        } finally {
            if (null != stream)
                stream.close();
        }
        return schemas;
    }

    public static XmlObject getTypeNode(SchemaDocument.Schema[] schemas, QName q) {
        // first find the schema with matching namespace
        SchemaDocument.Schema schema = null;
        for (int i = 0; i < schemas.length; i++) {
            SchemaDocument.Schema nxtSchema = schemas[i];
            if (nxtSchema.getTargetNamespace() != null
                    && nxtSchema.getTargetNamespace().equals(
                    q.getNamespaceURI())) {
                schema = nxtSchema;
                break;
            }
        }
        if (null == schema)
            return null; // namespace is not found in this schema.
        // look in complex types
        TopLevelComplexType[] tlComplexTypes = schema.getComplexTypeArray();
        for (int i = 0; i < tlComplexTypes.length; i++) {
            TopLevelComplexType nxtComplexType = tlComplexTypes[i];
            if (nxtComplexType.getName().equals(q.getLocalPart())) {
                return nxtComplexType;
            }
        }
        // look in simple types
        TopLevelSimpleType[] tlSimpleTypes = schema.getSimpleTypeArray();
        for (int i = 0; i < tlSimpleTypes.length; i++) {
            TopLevelSimpleType nxtSimpleType = tlSimpleTypes[i];
            if (nxtSimpleType.getName().equals(q.getLocalPart())) {
                return nxtSimpleType;
            }
        }
        // look in element types
        TopLevelElement[] tlElementTypes = schema.getElementArray();
        for (int i = 0; i < tlElementTypes.length; i++) {
            TopLevelElement nxtElement = tlElementTypes[i];
            if (nxtElement.getName().equals(q.getLocalPart())) {
                return nxtElement;
            }
        }
        return null;  // it is not in comlex or simple types!
    }
}
