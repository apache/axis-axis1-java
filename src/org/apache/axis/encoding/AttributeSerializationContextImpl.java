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

import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.Writer;


/** Used to suppress element tag serialization when serializing simple
 * types into attributes.
 *
 * @author Thomas Sandholm (sandholm@mcs.anl.gov)
 */
public class AttributeSerializationContextImpl extends SerializationContextImpl
{
   SerializationContext parent;
   public AttributeSerializationContextImpl(Writer writer, SerializationContext parent)
   {
        super(writer);
        this.parent = parent;
   }

   public void startElement(QName qName, Attributes attributes)
        throws IOException
   {
        // suppressed
   }

   public void endElement()
        throws IOException
   {
        // suppressed
   }

   public String qName2String(QName qname)
   {
       return parent.qName2String(qname);
   }
}
