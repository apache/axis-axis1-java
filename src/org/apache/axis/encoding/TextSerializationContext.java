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

package org.apache.axis.encoding;

import java.io.IOException;
import java.io.Writer;

import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.apache.axis.utils.Messages;

import org.w3c.dom.Element;

import org.xml.sax.Attributes;

/**
 * For internal use only. Used to get the first text node of an element.
 *
 * @author Jarek Gawor (gawor@apache.org)
 */
public class TextSerializationContext extends SerializationContext {

    private boolean ignore = false;
    private int depth = 0;

    public TextSerializationContext(Writer writer)
    {
        super(writer);
        startOfDocument = false;    // prevent XML decl for text
    }

    public TextSerializationContext(Writer writer, MessageContext msgContext)
    {
        super(writer, msgContext);
        startOfDocument = false;    // prevent XML decl for text
    }

    public void serialize(QName elemQName,
                          Attributes attributes,
                          Object value,
                          QName xmlType,
                          Boolean sendNull,
                          Boolean sendType)
        throws IOException
    {
        throw new IOException(Messages.getMessage("notImplemented00",
                                                  "serialize"));
    }

    public void writeDOMElement(Element el)
        throws IOException
    {
        throw new IOException(Messages.getMessage("notImplemented00",
                                                  "writeDOMElement"));
    }

    public void startElement(QName qName, Attributes attributes)
        throws IOException
    {
        depth++;
        if (depth == 2) {
            this.ignore = true;
        }
    }

    public void endElement()
        throws IOException
    {
        depth--;
        ignore = true;
    }

    public void writeChars(char [] p1, int p2, int p3)
        throws IOException
    {
        if (!this.ignore) {
            super.writeChars(p1, p2, p3);
        }
    }

    public void writeString(String string)
        throws IOException
    {
        if (!this.ignore) {
            super.writeString(string);
        }
    }

}
