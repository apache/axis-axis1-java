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
import org.apache.axis.types.HexBinary;
import org.apache.axis.schema.SchemaVersion;
import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.Base64DeserializerFactory;
import org.apache.axis.encoding.ser.Base64SerializerFactory;
import org.apache.axis.encoding.ser.DateDeserializerFactory;
import org.apache.axis.encoding.ser.DateSerializerFactory;
import org.apache.axis.encoding.ser.ElementDeserializerFactory;
import org.apache.axis.encoding.ser.ElementSerializerFactory;
import org.apache.axis.encoding.ser.HexDeserializerFactory;
import org.apache.axis.encoding.ser.HexSerializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
import org.apache.axis.encoding.ser.MapDeserializerFactory;
import org.apache.axis.encoding.ser.MapSerializerFactory;
import org.apache.axis.encoding.ser.QNameDeserializerFactory;
import org.apache.axis.encoding.ser.QNameSerializerFactory;
import org.apache.axis.encoding.ser.SimpleDeserializerFactory;
import org.apache.axis.encoding.ser.SimpleSerializerFactory;
import org.apache.axis.encoding.ser.VectorDeserializerFactory;
import org.apache.axis.encoding.ser.VectorSerializerFactory;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.DeserializerFactory;

/**
 * This is the implementation of the axis Default TypeMapping (which extends
 * the JAX-RPC TypeMapping interface) for SOAP 1.1.
 *
 * A TypeMapping contains tuples as follows:
 * {Java type, SerializerFactory, DeserializerFactory, Type QName)
 *
 * In other words, it serves to map Java types to and from XML types using
 * particular Serializers/Deserializers.  Each TypeMapping is associated with
 * one or more encodingStyle URIs.
 *
 * The wsdl in your web service will use a number of types.  The tuple
 * information for each of these will be accessed via the TypeMapping.
 *
 * This TypeMapping is the "default" one, which includes all the standard
 * SOAP and schema XSD types.  Individual TypeMappings (associated with
 * AxisEngines and SOAPServices) will delegate to this one, so if you haven't
 * overriden a default mapping we'll end up getting it from here.
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class DefaultTypeMappingImpl extends TypeMappingImpl {

    private static DefaultTypeMappingImpl tm = null;
    private boolean doneInit = false;   // have we completed initalization

    /**
     * Obtain the singleton default typemapping.
     */
    public static synchronized TypeMapping getSingleton() {
        if (tm == null) {
            tm = new DefaultTypeMappingImpl();
        }
        return tm;
    }

    protected DefaultTypeMappingImpl() {
        super(null);
        delegate = null;

        // Notes:
        // 1) The registration statements are order dependent.  The last one
        //    wins.  So if two javaTypes of String are registered, the
        //    ser factory for the last one registered will be chosen.  Likewise
        //    if two javaTypes for XSD_DATE are registered, the deserializer
        //    factory for the last one registered will be chosen.
        //    Corollary:  Please be very careful with the order.  The
        //                runtime, Java2WSDL and WSDL2Java emitters all
        //                use this code to get type mapping information.
        // 2) Even if the SOAP 1.1 format is used over the wire, an
        //    attempt is made to receive SOAP 1.2 format from the wire.
        //    This is the reason why the soap encoded primitives are
        //    registered without serializers.

        // Since the last-registered type wins, I want to add the mime
        // String FIRST.
        if (JavaUtils.isAttachmentSupported()) {
            myRegister(Constants.MIME_PLAINTEXT, java.lang.String.class,
                    new JAFDataHandlerSerializerFactory(
                            java.lang.String.class,
                            Constants.MIME_PLAINTEXT),
                    new JAFDataHandlerDeserializerFactory(
                            java.lang.String.class,
                            Constants.MIME_PLAINTEXT));
        }

        // SOAP Encoded strings are treated as primitives.
        // Everything else is not.
        myRegisterSimple(Constants.SOAP_STRING, java.lang.String.class);
        myRegisterSimple(Constants.SOAP_BOOLEAN, java.lang.Boolean.class);
        myRegisterSimple(Constants.SOAP_DOUBLE, java.lang.Double.class);
        myRegisterSimple(Constants.SOAP_FLOAT, java.lang.Float.class);
        myRegisterSimple(Constants.SOAP_INT, java.lang.Integer.class);
        myRegisterSimple(Constants.SOAP_INTEGER, java.math.BigInteger.class);
        myRegisterSimple(Constants.SOAP_DECIMAL, java.math.BigDecimal.class);
        myRegisterSimple(Constants.SOAP_LONG, java.lang.Long.class);
        myRegisterSimple(Constants.SOAP_SHORT, java.lang.Short.class);
        myRegisterSimple(Constants.SOAP_BYTE, java.lang.Byte.class);

        // HexBinary binary data needs to use the hex binary serializer/deserializer
        myRegister(Constants.XSD_HEXBIN,     HexBinary.class,
                   new HexSerializerFactory(
                        HexBinary.class, Constants.XSD_HEXBIN),
                   new HexDeserializerFactory(
                        HexBinary.class, Constants.XSD_HEXBIN));
        myRegister(Constants.XSD_HEXBIN,     byte[].class,
                   new HexSerializerFactory(
                        byte[].class, Constants.XSD_HEXBIN),
                   new HexDeserializerFactory(
                        byte[].class, Constants.XSD_HEXBIN));

        // SOAP 1.1
        // byte[] -ser-> XSD_BASE64
        // XSD_BASE64 -deser-> byte[]
        // SOAP_BASE64 -deser->byte[]
        //
        // Special case:
        // If serialization is requested for xsd:byte with byte[],
        // the array serializer is used.  If deserialization
        // is specifically requested for xsd:byte with byte[], the
        // simple deserializer is used.  This is necessary
        // to support the serialization/deserialization
        // of <element name="a" type="xsd:byte" maxOccurs="unbounded" />
        // as discrete bytes without interference with XSD_BASE64.
        myRegister(Constants.XSD_BYTE,       byte[].class,
                   new ArraySerializerFactory(),
                   null
        );

        myRegister(Constants.SOAP_BASE64,     byte[].class,
                   new Base64SerializerFactory(byte[].class,
                                               Constants.SOAP_BASE64 ),
                   new Base64DeserializerFactory(byte[].class,
                                                 Constants.SOAP_BASE64)
        );
        myRegister(Constants.SOAP_BASE64BINARY,     byte[].class,
                   new Base64SerializerFactory(byte[].class,
                                               Constants.SOAP_BASE64 ),
                   new Base64DeserializerFactory(byte[].class,
                                                 Constants.SOAP_BASE64)
        );
        myRegister(Constants.XSD_BASE64,     byte[].class,
                   new Base64SerializerFactory(byte[].class,
                                               Constants.XSD_BASE64 ),
                   new Base64DeserializerFactory(byte[].class,
                                           Constants.XSD_BASE64));
        /*
        // This JSR 101 change occurred right before v1.0.
        // This mapping is not roundtrippable, and breaks the roundtrip
        // testcase, so it is commented out for now.
        // SOAP 1.1
        // byte[] -ser-> XSD_BASE64
        // Byte[] -ser-> XSD_BASE64
        // XSD_BASE64 -deser-> byte[]
        // SOAP_BASE64 -deser->byte[]
        //
        // NOTE: If the following code is enabled, the
        // commented out code "//type == Byte[].class ||"
        // code in org.apache.axis.wsdl.fromJava.Types also needs to be enabled.

        myRegister(Constants.SOAP_BASE64,     byte[].class,
                   new Base64SerializerFactory(byte[].class,
                                               Constants.SOAP_BASE64 ),
                   new Base64DeserializerFactory(byte[].class,
                                                 Constants.SOAP_BASE64),
                   true);
        myRegister(Constants.XSD_BASE64,     Byte[].class,
                   new Base64SerializerFactory(Byte[].class,
                                               Constants.XSD_BASE64 ),
                   new Base64DeserializerFactory(Byte[].class,
                                           Constants.XSD_BASE64),true);
        myRegister(Constants.XSD_BASE64,     byte[].class,
                   new Base64SerializerFactory(byte[].class,
                                               Constants.XSD_BASE64 ),
                   new Base64DeserializerFactory(byte[].class,
                                           Constants.XSD_BASE64),true);
        */

        // If SOAP 1.1 over the wire, map wrapper classes to XSD primitives.
        myRegisterSimple(Constants.XSD_STRING, java.lang.String.class);
        myRegisterSimple(Constants.XSD_BOOLEAN, java.lang.Boolean.class);
        myRegisterSimple(Constants.XSD_DOUBLE, java.lang.Double.class);
        myRegisterSimple(Constants.XSD_FLOAT, java.lang.Float.class);
        myRegisterSimple(Constants.XSD_INT, java.lang.Integer.class);
        myRegisterSimple(Constants.XSD_INTEGER, java.math.BigInteger.class
        );
        myRegisterSimple(Constants.XSD_DECIMAL, java.math.BigDecimal.class
        );
        myRegisterSimple(Constants.XSD_LONG, java.lang.Long.class);
        myRegisterSimple(Constants.XSD_SHORT, java.lang.Short.class);
        myRegisterSimple(Constants.XSD_BYTE, java.lang.Byte.class);

        // The XSD Primitives are mapped to java primitives.
        myRegisterSimple(Constants.XSD_BOOLEAN, boolean.class);
        myRegisterSimple(Constants.XSD_DOUBLE, double.class);
        myRegisterSimple(Constants.XSD_FLOAT, float.class);
        myRegisterSimple(Constants.XSD_INT, int.class);
        myRegisterSimple(Constants.XSD_LONG, long.class);
        myRegisterSimple(Constants.XSD_SHORT, short.class);
        myRegisterSimple(Constants.XSD_BYTE, byte.class);

        // Map QNAME to the jax rpc QName class
        myRegister(Constants.XSD_QNAME,
              javax.xml.namespace.QName.class,
              new QNameSerializerFactory(javax.xml.namespace.QName.class,
                                        Constants.XSD_QNAME),
              new QNameDeserializerFactory(javax.xml.namespace.QName.class,
                                        Constants.XSD_QNAME)
        );

        // The closest match for anytype is Object
        myRegister(Constants.XSD_ANYTYPE,    java.lang.Object.class,
                   null, null);

        // See the SchemaVersion classes for where the registration of
        // dateTime (for 2001) and timeInstant (for 1999 & 2000) happen.
        myRegister(Constants.XSD_DATE,       java.util.Date.class,
                   new DateSerializerFactory(java.util.Date.class,
                                             Constants.XSD_DATE),
                   new DateDeserializerFactory(java.util.Date.class,
                                               Constants.XSD_DATE)
        );

        // Mapping for xsd:time.  Map to Axis type Time
        myRegister(Constants.XSD_TIME,       org.apache.axis.types.Time.class,
                   new SimpleSerializerFactory(org.apache.axis.types.Time.class,
                                             Constants.XSD_TIME),
                   new SimpleDeserializerFactory(org.apache.axis.types.Time.class,
                                               Constants.XSD_TIME)
        );
        // These are the g* types (gYearMonth, etc) which map to Axis types
        myRegister(Constants.XSD_YEARMONTH, org.apache.axis.types.YearMonth.class,
                   new SimpleSerializerFactory(org.apache.axis.types.YearMonth.class,
                                             Constants.XSD_YEARMONTH),
                   new SimpleDeserializerFactory(org.apache.axis.types.YearMonth.class,
                                             Constants.XSD_YEARMONTH)
        );
        myRegister(Constants.XSD_YEAR, org.apache.axis.types.Year.class,
                   new SimpleSerializerFactory(org.apache.axis.types.Year.class,
                                             Constants.XSD_YEAR),
                   new SimpleDeserializerFactory(org.apache.axis.types.Year.class,
                                             Constants.XSD_YEAR)
        );
        myRegister(Constants.XSD_MONTH, org.apache.axis.types.Month.class,
                   new SimpleSerializerFactory(org.apache.axis.types.Month.class,
                                             Constants.XSD_MONTH),
                   new SimpleDeserializerFactory(org.apache.axis.types.Month.class,
                                             Constants.XSD_MONTH)
        );
        myRegister(Constants.XSD_DAY, org.apache.axis.types.Day.class,
                   new SimpleSerializerFactory(org.apache.axis.types.Day.class,
                                             Constants.XSD_DAY),
                   new SimpleDeserializerFactory(org.apache.axis.types.Day.class,
                                             Constants.XSD_DAY)
        );
        myRegister(Constants.XSD_MONTHDAY, org.apache.axis.types.MonthDay.class,
                   new SimpleSerializerFactory(org.apache.axis.types.MonthDay.class,
                                             Constants.XSD_MONTHDAY),
                   new SimpleDeserializerFactory(org.apache.axis.types.MonthDay.class,
                                             Constants.XSD_MONTHDAY)
        );

        // Serialize all extensions of Map to SOAP_MAP
        // The SOAP_MAP will be deserialized into a HashMap by default.
        myRegister(Constants.SOAP_MAP,       java.util.HashMap.class,
                   new MapSerializerFactory(java.util.Map.class,
                                            Constants.SOAP_MAP),
                   new MapDeserializerFactory(java.util.HashMap.class,
                                              Constants.SOAP_MAP)
        );
        myRegister(Constants.SOAP_MAP,       java.util.Hashtable.class,
                   new MapSerializerFactory(java.util.Hashtable.class,
                                            Constants.SOAP_MAP),
                   null  // Make sure not to override the deser mapping
        );
        myRegister(Constants.SOAP_MAP,       java.util.Map.class,
                   new MapSerializerFactory(java.util.Map.class,
                                            Constants.SOAP_MAP),
                   null  // Make sure not to override the deser mapping
        );

        // Use the Element Serializeration for elements
        myRegister(Constants.SOAP_ELEMENT,   org.w3c.dom.Element.class,
                   new ElementSerializerFactory(),
                   new ElementDeserializerFactory());
        myRegister(Constants.SOAP_VECTOR,    java.util.Vector.class,
                   new VectorSerializerFactory(java.util.Vector.class,
                                               Constants.SOAP_VECTOR),
                   new VectorDeserializerFactory(java.util.Vector.class,
                                                 Constants.SOAP_VECTOR)
        );

        // Register all the supported MIME types
        // (note that MIME_PLAINTEXT was registered near the top)
        if (JavaUtils.isAttachmentSupported()) {
            myRegister(Constants.MIME_IMAGE, java.awt.Image.class,
                    new JAFDataHandlerSerializerFactory(
                            java.awt.Image.class,
                            Constants.MIME_IMAGE),
                    new JAFDataHandlerDeserializerFactory(
                            java.awt.Image.class,
                            Constants.MIME_IMAGE));
            myRegister(Constants.MIME_MULTIPART, javax.mail.internet.MimeMultipart.class,
                    new JAFDataHandlerSerializerFactory(
                            javax.mail.internet.MimeMultipart.class,
                            Constants.MIME_MULTIPART),
                    new JAFDataHandlerDeserializerFactory(
                            javax.mail.internet.MimeMultipart.class,
                            Constants.MIME_MULTIPART));
            myRegister(Constants.MIME_SOURCE, javax.xml.transform.Source.class,
                    new JAFDataHandlerSerializerFactory(
                            javax.xml.transform.Source.class,
                            Constants.MIME_SOURCE),
                    new JAFDataHandlerDeserializerFactory(
                            javax.xml.transform.Source.class,
                            Constants.MIME_SOURCE));
            myRegister(Constants.MIME_OCTETSTREAM, javax.activation.DataHandler.class,
                    new JAFDataHandlerSerializerFactory(),
                    new JAFDataHandlerDeserializerFactory());
            myRegister(Constants.MIME_DATA_HANDLER, javax.activation.DataHandler.class,
                    new JAFDataHandlerSerializerFactory(),
                    new JAFDataHandlerDeserializerFactory());
        }

        // xsd:token
        myRegister(Constants.XSD_TOKEN, org.apache.axis.types.Token.class,
                   new SimpleSerializerFactory(org.apache.axis.types.Token.class,
                                             Constants.XSD_TOKEN),
                   new SimpleDeserializerFactory(org.apache.axis.types.Token.class,
                                             Constants.XSD_TOKEN)
        );

        // a xsd:normalizedString
        myRegister(Constants.XSD_NORMALIZEDSTRING, org.apache.axis.types.NormalizedString.class,
                   new SimpleSerializerFactory(org.apache.axis.types.NormalizedString.class,
                                             Constants.XSD_NORMALIZEDSTRING),
                   new SimpleDeserializerFactory(org.apache.axis.types.NormalizedString.class,
                                             Constants.XSD_NORMALIZEDSTRING)
        );

        // a xsd:unsignedLong
        myRegister(Constants.XSD_UNSIGNEDLONG, org.apache.axis.types.UnsignedLong.class,
             new SimpleSerializerFactory(org.apache.axis.types.UnsignedLong.class,
                                                  Constants.XSD_UNSIGNEDLONG),
             new SimpleDeserializerFactory(org.apache.axis.types.UnsignedLong.class,
                                           Constants.XSD_UNSIGNEDLONG)
        );

        // a xsd:unsignedInt
        myRegister(Constants.XSD_UNSIGNEDINT, org.apache.axis.types.UnsignedInt.class,
             new SimpleSerializerFactory(org.apache.axis.types.UnsignedInt.class,
                                                  Constants.XSD_UNSIGNEDINT),
             new SimpleDeserializerFactory(org.apache.axis.types.UnsignedInt.class,
                                           Constants.XSD_UNSIGNEDINT)
        );

        // a xsd:unsignedShort
        myRegister(Constants.XSD_UNSIGNEDSHORT, org.apache.axis.types.UnsignedShort.class,
             new SimpleSerializerFactory(org.apache.axis.types.UnsignedShort.class,
                                                  Constants.XSD_UNSIGNEDSHORT),
             new SimpleDeserializerFactory(org.apache.axis.types.UnsignedShort.class,
                                           Constants.XSD_UNSIGNEDSHORT)
        );

        // a xsd:unsignedByte
        myRegister(Constants.XSD_UNSIGNEDBYTE, org.apache.axis.types.UnsignedByte.class,
                   new SimpleSerializerFactory(org.apache.axis.types.UnsignedByte.class,
                                             Constants.XSD_UNSIGNEDBYTE),
                   new SimpleDeserializerFactory(org.apache.axis.types.UnsignedByte.class,
                                             Constants.XSD_UNSIGNEDBYTE)
        );

        // a xsd:nonNegativeInteger
        myRegister(Constants.XSD_NONNEGATIVEINTEGER, org.apache.axis.types.NonNegativeInteger.class,
             new SimpleSerializerFactory(org.apache.axis.types.NonNegativeInteger.class,
                                                  Constants.XSD_NONNEGATIVEINTEGER),
             new SimpleDeserializerFactory(org.apache.axis.types.NonNegativeInteger.class,
                                           Constants.XSD_NONNEGATIVEINTEGER)
        );

        // a xsd:negativeInteger
        myRegister(Constants.XSD_NEGATIVEINTEGER, org.apache.axis.types.NegativeInteger.class,
             new SimpleSerializerFactory(org.apache.axis.types.NegativeInteger.class,
                                                  Constants.XSD_NEGATIVEINTEGER),
             new SimpleDeserializerFactory(org.apache.axis.types.NegativeInteger.class,
                                           Constants.XSD_NEGATIVEINTEGER)
        );

        // a xsd:positiveInteger
        myRegister(Constants.XSD_POSITIVEINTEGER, org.apache.axis.types.PositiveInteger.class,
             new SimpleSerializerFactory(org.apache.axis.types.PositiveInteger.class,
                                                  Constants.XSD_POSITIVEINTEGER),
             new SimpleDeserializerFactory(org.apache.axis.types.PositiveInteger.class,
                                           Constants.XSD_POSITIVEINTEGER)
        );

        // a xsd:nonPositiveInteger
        myRegister(Constants.XSD_NONPOSITIVEINTEGER, org.apache.axis.types.NonPositiveInteger.class,
             new SimpleSerializerFactory(org.apache.axis.types.NonPositiveInteger.class,
                                                  Constants.XSD_NONPOSITIVEINTEGER),
             new SimpleDeserializerFactory(org.apache.axis.types.NonPositiveInteger.class,
                                           Constants.XSD_NONPOSITIVEINTEGER)
        );

        // a xsd:Name
        myRegister(Constants.XSD_NAME, org.apache.axis.types.Name.class,
                   new SimpleSerializerFactory(org.apache.axis.types.Name.class,
                                             Constants.XSD_NAME),
                   new SimpleDeserializerFactory(org.apache.axis.types.Name.class,
                                             Constants.XSD_NAME)
        );

        // a xsd:NCName
        myRegister(Constants.XSD_NCNAME, org.apache.axis.types.NCName.class,
                   new SimpleSerializerFactory(org.apache.axis.types.NCName.class,
                                             Constants.XSD_NCNAME),
                   new SimpleDeserializerFactory(org.apache.axis.types.NCName.class,
                                             Constants.XSD_NCNAME)
        );

        // a xsd:NmToken
        myRegister(Constants.XSD_NMTOKEN, org.apache.axis.types.NMToken.class,
                   new SimpleSerializerFactory(org.apache.axis.types.NMToken.class,
                                             Constants.XSD_NMTOKEN),
                   new SimpleDeserializerFactory(org.apache.axis.types.NMToken.class,
                                             Constants.XSD_NMTOKEN)
        );

        // a xsd:Duration
        myRegister(Constants.XSD_DURATION, org.apache.axis.types.Duration.class,
                   new SimpleSerializerFactory(org.apache.axis.types.Duration.class,
                                             Constants.XSD_DURATION),
                   new SimpleDeserializerFactory(org.apache.axis.types.Duration.class,
                                             Constants.XSD_DURATION)
        );
        
        // a xsd:anyURI
        myRegister(Constants.XSD_ANYURI, org.apache.axis.types.URI.class,
                   new SimpleSerializerFactory(org.apache.axis.types.URI.class,
                                             Constants.XSD_ANYURI),
                   new SimpleDeserializerFactory(org.apache.axis.types.URI.class,
                                             Constants.XSD_ANYURI)
        );
        
        // All array objects automatically get associated with the SOAP_ARRAY.
        // There is no way to do this with a hash table,
        // so it is done directly in getTypeQName.
        // Internally the runtime uses ArrayList objects to hold arrays...
        // which is the reason that ArrayList is associated with SOAP_ARRAY.
        // In addition, handle all objects that implement the List interface
        // as a SOAP_ARRAY
        myRegister(Constants.SOAP_ARRAY,     java.util.Collection.class,
                   new ArraySerializerFactory(),
                   new ArrayDeserializerFactory()
        );
//        myRegister(Constants.SOAP_ARRAY,     java.util.ArrayList.class,
//                   new ArraySerializerFactory(),
//                   new ArrayDeserializerFactory(),
//                   false);
        myRegister(Constants.SOAP_ARRAY,     Object[].class,
                   new ArraySerializerFactory(),
                   new ArrayDeserializerFactory()
        );

        //
        // Now register the schema specific types
        //
        SchemaVersion.SCHEMA_1999.registerSchemaSpecificTypes(this);
        SchemaVersion.SCHEMA_2000.registerSchemaSpecificTypes(this);
        SchemaVersion.SCHEMA_2001.registerSchemaSpecificTypes(this);
        doneInit = true;
    }

    /**
     * Register a "simple" type mapping - in other words, a
     * @param xmlType
     * @param javaType
     */
    protected void myRegisterSimple(QName xmlType, Class javaType) {
        SerializerFactory sf = new SimpleSerializerFactory(javaType, xmlType);
        DeserializerFactory df = null;
        if (javaType != java.lang.Object.class) {
            df = new SimpleDeserializerFactory(javaType, xmlType);
        }
        
        myRegister(xmlType, javaType, sf, df);        
    }

    /**
     * Construct TypeMapping for all the [xmlType, javaType] for all of the
     * known xmlType namespaces.  This is the shotgun approach, which works
     * in 99% of the cases.  The other cases that are Schema version specific
     * (i.e. timeInstant vs. dateTime) are handled by the SchemaVersion
     * Interface registerSchemaSpecificTypes().
     *
     * @param xmlType is the QName type
     * @param javaType is the java type
     * @param sf is the ser factory (if null, the simple factory is used)
     * @param df is the deser factory (if null, the simple factory is used)
     */
    protected void myRegister(QName xmlType, Class javaType,
                              SerializerFactory sf, DeserializerFactory df) {
        // Register all known flavors of the namespace.
        try {
            if (xmlType.getNamespaceURI().equals(
                    Constants.URI_DEFAULT_SCHEMA_XSD)) {
                for (int i=0; i < Constants.URIS_SCHEMA_XSD.length; i++) {
                    QName qName = new QName(Constants.URIS_SCHEMA_XSD[i],
                                            xmlType.getLocalPart());
                    super.internalRegister(javaType, qName, sf, df);
                }
            }
            else if (xmlType.getNamespaceURI().equals(
                    Constants.URI_DEFAULT_SOAP_ENC)) {
                for (int i=0; i < Constants.URIS_SOAP_ENC.length; i++) {
                    QName qName = new QName(Constants.URIS_SOAP_ENC[i],
                                            xmlType.getLocalPart());
                    super.internalRegister(javaType, qName, sf, df);
                }
            }
            // Register with the specified xmlType.
            // This is the prefered mapping and the last registed one wins
            super.internalRegister(javaType, xmlType, sf, df);
        } catch (JAXRPCException e) { }
    }

    // Don't allow anyone to muck with the default type mapping because
    // it is a singleton used for the whole system.
    public void register(Class javaType, QName xmlType,
                         javax.xml.rpc.encoding.SerializerFactory sf,
                         javax.xml.rpc.encoding.DeserializerFactory dsf)
        throws JAXRPCException {

        // Don't allow anyone but init to modify us.
        if (doneInit) {
            throw new JAXRPCException(Messages.getMessage("fixedTypeMapping"));
        }
        else {
            super.register(javaType, xmlType, sf, dsf);
        }
    }
    public void removeSerializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        throw new JAXRPCException(Messages.getMessage("fixedTypeMapping"));
    }
    public void removeDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        throw new JAXRPCException(Messages.getMessage("fixedTypeMapping"));
    }
    public void setSupportedEncodings(String[] namespaceURIs) {
    }
}
