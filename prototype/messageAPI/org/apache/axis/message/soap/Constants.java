package org.apache.axis.message.soap;

import org.apache.axis.util.xml.QName;

public class Constants
{
    public static final String NSPREFIX_SOAP_ENV = "SOAP" ;
    public static final String NSPREFIX_SOAP_ENC = "SOAP-ENC" ;
    public static final String NSPREFIX_SCHEMA_XSI = "xsi" ;
    public static final String NSPREFIX_SCHEMA_XSD = "xsd" ;

    public static final String URI_SOAP_ENV   = "http://schemas.xmlsoap.org/soap/envelope/" ;
    public static final String URI_SOAP_ENC   = "http://schemas.xmlsoap.org/soap/encoding/" ;
    public static final String URI_SCHEMA_XSI = "http://www.w3.org/1999/XMLSchema/instance/" ;
    public static final String URI_SCHEMA_XSD = "http://www.w3.org/1999/XMLSchema/" ;
    public static final String URI_NEXT_ACTOR = "http://schemas.xmlsoap.org/soap/actor/next" ;

    public static final String ELEM_ENVELOPE = "Envelope" ;
    public static final String ELEM_HEADER = "Header" ;
    public static final String ELEM_BODY = "Body" ;
    public static final String ELEM_FAULT = "Fault" ;

    public static final String ATTR_MUST_UNDERSTAND = "mustUnderstand" ;
    public static final String ATTR_ENCODING_STYLE = "encodingStyle" ;
    public static final String ATTR_ACTOR = "actor" ;
    public static final String ATTR_ROOT = "root" ;
    public static final String ATTR_ID = "id" ;
    public static final String ATTR_HREF = "href" ;
	public static final String ATTR_TYPE = "type" ;
	
	public static final QName stringQName = new QName(URI_SCHEMA_XSD, "string");
	public static final QName booleanQName = new QName(URI_SCHEMA_XSD, "boolean");
	public static final QName doubleQName = new QName(URI_SCHEMA_XSD, "double");
	public static final QName floatQName = new QName(URI_SCHEMA_XSD, "float");
	public static final QName longQName = new QName(URI_SCHEMA_XSD, "long");
	public static final QName intQName = new QName(URI_SCHEMA_XSD, "int");
	public static final QName shortQName = new QName(URI_SCHEMA_XSD, "short");
	public static final QName byteQName = new QName(URI_SCHEMA_XSD, "byte");
	public static final QName qNameQName = new QName(URI_SCHEMA_XSD, "QName");
	public static final QName arrayQName = new QName(URI_SOAP_ENC, "Array");
	public static final QName objectQName = new QName(URI_SCHEMA_XSD, "ur-type");
}
