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
import org.apache.beehive.wsm.wsdl.Schema;
import org.apache.beehive.wsm.wsdl.Utilities;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3.x2001.xmlSchema.LocalElement;
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
import java.lang.reflect.Array;
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
     * Return XML schema for the specified type, suitable for insertion into the
     * &lt;types&gt; element of a WSDL document, or underneath an
     * &lt;element&gt; or &lt;attribute&gt; declaration.
     * 
     * @param javaType
     *            the Java Class we're writing out schema for
     * @param types
     *            the Java2WSDL Types object which holds the context for the
     *            WSDL being generated.
     * @return a type element containing a schema simpleType/complexType
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public Element writeSchema(Class javaType, Types types) throws Exception {
        try {
            if (!XmlObject.class.isAssignableFrom(javaType)) {
                throw new RuntimeException(
                        "Invalid Object type is assigned to the XMLBeanSerialization Type: "
                                + javaType.getCanonicalName());
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

    /**
     * @param types
     * @param docType
     * @return
     * @throws Exception
     */
    private void writeSchemaForDocType(SchemaType docType, Types types)
            throws Exception {
        Schema mySchema = Utilities.findtSchemaDocument(docType);

        QName q = docType.getName();

        XmlObject typeNodeInWSDL = mySchema.getTypeNode(q);

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
        Set<QName> dependentTypes = new HashSet<QName>();
        getAllDependentTypes(typeNodeInWSDL, dependentTypes);
        for (QName nxtType : dependentTypes) {
            Class nxtJavaType = null;
            // add the class if it is an xml bean
            if (null != (nxtJavaType = q2UserClass(nxtType))
                    && XmlObject.class.isAssignableFrom(nxtJavaType)) {
                writeSchema(nxtJavaType, types);
            }
        }
        return;
    }

    /**
     * @param nxtType
     * @return null for classes that are not found, or if they are primitive types
     *     * 
     */
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
        else
            return null; // for classes that are not found, or are build in

    }

    /**
     * @param nodeInWSDL
     * @param dependentTypes
     * @return
     * 
     * Walk all the nodes under the nodeInWSDL if there is an 'element' type the
     * add its types or references to the dependent type.
     */
    private void getAllDependentTypes(XmlObject nodeInWSDL,
            Set<QName> dependentTypes) {
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
        return;
    }

    // NOTE jcolwell@bea.com 2004-Nov-15 --
    // once the WSDLProcessor is changed to an interface, remove this function
    // and use the one in the upcoming XmlBeanWSDLProcessor.
    private static <T extends XmlObject> T[] selectChildren(XmlObject parent,
            Class<T> childClass) throws IllegalAccessException,
            NoSuchFieldException {
        // retrieve the SchemaType from the static type field
        SchemaType st = (SchemaType) childClass.getField("type").get(null);
        XmlObject[] kids = parent.selectChildren(st.getDocumentElementName());
        T[] castKids = (T[]) Array.newInstance(childClass, kids.length);
        for (int j = 0; j < castKids.length; j++) {
            castKids[j] = childClass.cast(kids[j]);
        }
        return castKids;
    }
}
