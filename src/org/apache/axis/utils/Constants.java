package org.apache.axis.utils ;

public class Constants {
  public static String NSPREFIX_SOAP_ENV   = "SOAP-ENV" ;
  public static String NSPREFIX_SOAP_ENC   = "SOAP-ENC" ;
  public static String NSPREFIX_SCHEMA_XSI = "xsi" ;
  public static String NSPREFIX_SCHEMA_XSD = "xsd" ;

  public static String URI_SOAP_ENV =
                               "http://schemas.xmlsoap.org/soap/envelope/" ;
  public static String URI_SOAP_ENC =
                               "http://schemas.xmlsoap.org/soap/encoding/" ;
  public static String URI_SCHEMA_XSI =
                               "http://www.w3.org/1999/XMLSchema/instance/" ;
  public static String URI_SCHEMA_XSD =
                               "http://www.w3.org/1999/XMLSchema/" ;
  public static String URI_NEXT_ACTOR = 
                               "http://schemas.xmlsoap.org/soap/actor/next" ;

  public static String ELEM_ENVELOPE = "Envelope" ;
  public static String ELEM_HEADER   = "Header" ;
  public static String ELEM_BODY     = "Body" ;
  public static String ELEM_FAULT    = "Fault" ;

  public static String ATTR_MUST_UNDERSTAND = "mustUnderstand" ;
  public static String ATTR_ENCODING_STYLE  = "encodingStyle" ;
  public static String ATTR_ACTOR           = "actor" ;
  public static String ATTR_ROOT            = "root" ;
  public static String ATTR_ID              = "id" ;
  public static String ATTR_HREF            = "href" ;
}
