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

package samples.echo;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;

import org.xml.sax.*;

/**
 * Test serializer/deserializerFactory for the SOAPStruct used by the
 * echo interop test.
 *
 * @author Glen Daniels <gdaniels@macromedia.com>
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class SOAPStructSer extends DeserializerBase implements Serializer
{
    public static final String INTEGERMEMBER = "varInt";
    public static final String STRINGMEMBER  = "varString";
    public static final String FLOATMEMBER   = "varFloat";
    public static final QName myTypeQName = new QName("typeNS", "SOAPStruct");
    
    public static class SOAPStructSerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() {
            return new SOAPStructSer();
        }
    }

    public static DeserializerFactory getFactory() {
        return new SOAPStructSerFactory();
    }
    
    private Hashtable typesByMemberName = new Hashtable();  
    
    public SOAPStructSer() {
        typesByMemberName.put(INTEGERMEMBER, SOAPTypeMappingRegistry.XSD_INT);
        typesByMemberName.put(STRINGMEMBER, SOAPTypeMappingRegistry.XSD_STRING);
        typesByMemberName.put(FLOATMEMBER, SOAPTypeMappingRegistry.XSD_FLOAT);
        value = new SOAPStruct();
    }
    
    /** DESERIALIZER STUFF - event handlers
     */
    
    public void onStartChild(String namespace, String localName,
                             String qName, Attributes attributes)
        throws SAXException
    {
        QName typeQName = (QName)typesByMemberName.get(localName);
        if (typeQName == null)
            throw new SAXException("Invalid element in SOAPStruct struct - " + localName);
        
        // These can come in either order.
        DeserializerBase dSer = context.getDeserializer(typeQName);
        dSer.registerValueTarget(value, localName);
        
        if (dSer == null)
            throw new SAXException("No deserializer for a " + typeQName + "???");
        
        context.pushElementHandler(dSer);
    }
    
    /** SERIALIZER STUFF
     */
    
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (!(value instanceof SOAPStruct))
            throw new IOException("Can't serialize a " + value.getClass().getName() + " with a SOAPStructSerializer.");
        SOAPStruct data = (SOAPStruct)value;
        
        context.startElement(name, attributes);
        context.serialize(new QName("", INTEGERMEMBER), null, data.varInt);
        context.serialize(new QName("", STRINGMEMBER), null, data.varString);
        context.serialize(new QName("", FLOATMEMBER), null, data.varFloat);
        context.endElement();
    }
}
