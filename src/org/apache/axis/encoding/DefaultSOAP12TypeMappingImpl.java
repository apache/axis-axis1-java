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

package org.apache.axis.encoding;

import org.apache.axis.Constants;

import javax.xml.rpc.namespace.QName;
import javax.xml.rpc.JAXRPCException;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.DateSerializerFactory;
import org.apache.axis.encoding.ser.DateDeserializerFactory;
import org.apache.axis.encoding.ser.Base64SerializerFactory;
import org.apache.axis.encoding.ser.Base64DeserializerFactory;
import org.apache.axis.encoding.ser.MapSerializerFactory;
import org.apache.axis.encoding.ser.MapDeserializerFactory;
import org.apache.axis.encoding.ser.HexSerializerFactory;
import org.apache.axis.encoding.ser.HexDeserializerFactory;
import org.apache.axis.encoding.ser.ElementSerializerFactory;
import org.apache.axis.encoding.ser.ElementDeserializerFactory;
import org.apache.axis.encoding.ser.VectorDeserializerFactory;
import org.apache.axis.encoding.ser.SimpleDeserializerFactory;
import org.apache.axis.encoding.ser.SimplePrimitiveSerializerFactory;
import org.apache.axis.encoding.ser.SimpleNonPrimitiveSerializerFactory;
import java.util.Vector;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Rich Scheuerle (scheu@us.ibm.com)
 * 
 * This is the implementation of the axis Default JAX-RPC SOAP 1.2 TypeMapping
 * See DefaultTypeMapping for more information.
 * 
 */
public class DefaultSOAP12TypeMappingImpl extends DefaultTypeMappingImpl { 
    
    private static DefaultSOAP12TypeMappingImpl tm = null;
    /**
     * Construct TypeMapping
     */
    public static TypeMapping create() {
        if (tm == null) {
            tm = new DefaultSOAP12TypeMappingImpl();
        }
        return tm;
    }

    protected DefaultSOAP12TypeMappingImpl() {
        super();
        // This default type mapping only contains the SOAP 1.2 differences.
        // delegate to the DefaultTypeMapping as necessary.
        delegate = DefaultTypeMappingImpl.getSingleton();

        // Notes:
        // 1) The registration statements are order dependent.  The last one
        //    wins.  So if two javaTypes of String are registered, the 
        //    ser factory for the last one registered will be chosen.  Likewise
        //    if two javaTypes for XSD_DATE are registered, the deserializer 
        //    factory for the last one registered will be chosen.
        // 2) Even if the SOAP 1.1 format is used over the wire, an 
        //    attempt is made to receive SOAP 1.2 format from the wire.
        //    This is the reason why the soap encoded primitives are 
        //    registered without serializers.

        // SOAP Encoded strings are treated as primitives.
        // Everything else is not.
        myRegister(Constants.SOAP_STRING,     java.lang.String.class,  
                   null, null, true); 
        myRegister(Constants.SOAP_BOOLEAN,    java.lang.Boolean.class, 
                   null, null, false);
        myRegister(Constants.SOAP_DOUBLE,     java.lang.Double.class,
                   null, null, false);
        myRegister(Constants.SOAP_FLOAT,      java.lang.Float.class,
                   null, null, false);
        myRegister(Constants.SOAP_INT,        java.lang.Integer.class,
                   null, null, false);
        myRegister(Constants.SOAP_INTEGER,    java.math.BigInteger.class,
                   null, null, false);
        myRegister(Constants.SOAP_DECIMAL,    java.math.BigDecimal.class,
                   null, null, false);
        myRegister(Constants.SOAP_LONG,       java.lang.Long.class,     
                   null, null, false);
        myRegister(Constants.SOAP_SHORT,      java.lang.Short.class,    
                   null, null, false);
        myRegister(Constants.SOAP_BYTE,       java.lang.Byte.class,     
                   null, null, false);

        // SOAP 1.2
        // byte[] -ser-> XSD_BASE64
        // Byte[] -ser-> SOAP_BASE64
        // XSD_BASE64 -deser-> byte[]
        // SOAP_BASE64 -deser->Byte[]
        myRegister(Constants.SOAP_BASE64,     java.lang.Byte[].class,     
                   new Base64SerializerFactory(java.lang.Byte[].class,
                                               Constants.SOAP_BASE64 ),
                   new Base64DeserializerFactory(java.lang.Byte[].class, 
                                                 Constants.SOAP_BASE64),
                   true);
    }
}
