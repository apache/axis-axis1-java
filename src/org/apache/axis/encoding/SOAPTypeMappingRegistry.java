package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.utils.QName;

public class SOAPTypeMappingRegistry extends TypeMappingRegistry { 
    
    public static final QName XSD_STRING = new QName(Constants.URI_SCHEMA_XSD, "string");
    public static final QName XSD_BOOLEAN = new QName(Constants.URI_SCHEMA_XSD, "boolean");
    public static final QName XSD_DOUBLE = new QName(Constants.URI_SCHEMA_XSD, "double");
    public static final QName XSD_FLOAT = new QName(Constants.URI_SCHEMA_XSD, "float");
    public static final QName XSD_INT = new QName(Constants.URI_SCHEMA_XSD, "int");
    public static final QName XSD_LONG = new QName(Constants.URI_SCHEMA_XSD, "long");
    public static final QName XSD_SHORT = new QName(Constants.URI_SCHEMA_XSD, "short");
    public static final QName SOAP_STRING = new QName(Constants.URI_SOAP_ENC, "string");
    public static final QName SOAP_BOOLEAN = new QName(Constants.URI_SOAP_ENC, "boolean");
    public static final QName SOAP_DOUBLE = new QName(Constants.URI_SOAP_ENC, "double");
    public static final QName SOAP_FLOAT = new QName(Constants.URI_SOAP_ENC, "float");
    public static final QName SOAP_INT = new QName(Constants.URI_SOAP_ENC, "int");
    public static final QName SOAP_LONG = new QName(Constants.URI_SOAP_ENC, "long");
    public static final QName SOAP_SHORT = new QName(Constants.URI_SOAP_ENC, "short");
    
    public SOAPTypeMappingRegistry() {
        SOAPEncoding se = new SOAPEncoding();
        addSerializer(java.lang.String.class, se);
        addSerializer(java.lang.Boolean.class, se);
        addSerializer(java.lang.Double.class, se);
        addSerializer(java.lang.Float.class, se);
        addSerializer(java.lang.Integer.class, se);
        addSerializer(java.lang.Long.class, se);
        addSerializer(java.lang.Short.class, se);
        addDeserializer(XSD_STRING, se);    
        addDeserializer(XSD_BOOLEAN, se);
        addDeserializer(XSD_DOUBLE, se);
        addDeserializer(XSD_FLOAT, se);
        addDeserializer(XSD_INT, se);
        addDeserializer(XSD_LONG, se);
        addDeserializer(XSD_SHORT, se);
        addDeserializer(SOAP_STRING, se);
        addDeserializer(SOAP_BOOLEAN, se);
        addDeserializer(SOAP_DOUBLE, se);
        addDeserializer(SOAP_FLOAT, se);
        addDeserializer(SOAP_INT, se);
        addDeserializer(SOAP_LONG, se);
        addDeserializer(SOAP_SHORT, se);
    }
    
}
