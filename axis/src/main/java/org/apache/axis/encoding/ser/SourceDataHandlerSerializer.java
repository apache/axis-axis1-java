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

package org.apache.axis.encoding.ser;

import org.apache.axis.attachments.SourceDataSource;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

/**
 * SourceDataHandler Serializer
 * @author Russell Butek (butek@us.ibm.com)
 */
public class SourceDataHandlerSerializer extends JAFDataHandlerSerializer {

    protected static Log log =
        LogFactory.getLog(SourceDataHandlerSerializer.class.getName());

    /**
     * Serialize a Source DataHandler quantity.
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (value != null) {
            if (!(value instanceof StreamSource)) {
                throw new IOException(Messages.getMessage("badSource",
                        value.getClass().getName()));
            }
            DataHandler dh = new DataHandler(new SourceDataSource("source",
                    "text/xml", (StreamSource) value));
            super.serialize(name, attributes, dh, context);
        }
    } // serialize
} // class SourceDataHandlerSerializer
