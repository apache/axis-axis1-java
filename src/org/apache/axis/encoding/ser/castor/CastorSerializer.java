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

package org.apache.axis.encoding.ser.castor;

import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Castor serializer
 * 
 * @author Olivier Brand (olivier.brand@vodafone.com)
 * @author Steve Loughran
 * @version 1.0
 */
public class CastorSerializer implements Serializer {

    protected static Log log =
            LogFactory.getLog(CastorSerializer.class.getName());

    /**
     * Serialize a Castor object.
     * 
     * @param name       
     * @param attributes 
     * @param value      this must be a castor object for marshalling
     * @param context    
     * @throws IOException for XML schema noncompliance, bad object type, and any IO
     *                     trouble.
     */
    public void serialize(
            QName name,
            Attributes attributes,
            Object value,
            SerializationContext context)
            throws IOException {
        String fdate;
        StringWriter writer = null;

        try {
            writer = new StringWriter();

            // Create a Castor Marshaller initialized with the output stream
            Marshaller marshaller = new Marshaller(writer);

            // Don't include the DOCTYPE, otherwise an exception occurs due to
            //2 DOCTYPE defined in the document. The XML fragment is included in
            //an XML document containing already a DOCTYPE
            marshaller.setMarshalAsDocument(false);

            // Marshall the Castor object into the stream (sink)
            marshaller.marshal(value);

            context.writeString(writer.toString());
        } catch (MarshalException me) {
            log.error(Messages.getMessage("castorMarshalException00"), me);
            throw new IOException(Messages.getMessage("castorMarshalException00")
                    + me.getLocalizedMessage());
        } catch (ValidationException ve) {
            log.error(Messages.getMessage("castorValidationException00"), ve);
            throw new IOException(Messages.getMessage("castorValidationException00")
                    + ve.getLocalizedMessage());
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
        return null;
    }
}
