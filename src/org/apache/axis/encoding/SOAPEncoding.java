package org.apache.axis.encoding;

import org.apache.axis.utils.QName;
import org.apache.axis.utils.XMLUtils;
import org.xml.sax.Attributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Hashtable;

public class SOAPEncoding implements Serializer { 
    private Hashtable typemap = new Hashtable();
    private Hashtable namemap = new Hashtable();
    
    public SOAPEncoding() {
        typemap.put(String.class,  SOAPTypeMappingRegistry.XSD_STRING);
        typemap.put(Boolean.class, SOAPTypeMappingRegistry.XSD_BOOLEAN);
        typemap.put(Double.class,  SOAPTypeMappingRegistry.XSD_DOUBLE);
        typemap.put(Float.class,   SOAPTypeMappingRegistry.XSD_FLOAT);
        typemap.put(Integer.class, SOAPTypeMappingRegistry.XSD_INT);
        typemap.put(Long.class,    SOAPTypeMappingRegistry.XSD_LONG);
        typemap.put(Short.class,   SOAPTypeMappingRegistry.XSD_SHORT);
        typemap.put(BigDecimal.class, SOAPTypeMappingRegistry.XSD_DECIMAL);
        typemap.put(Date.class,    SOAPTypeMappingRegistry.XSD_DATE);
        namemap.put(String.class,  SOAPTypeMappingRegistry.SOAP_STRING);
        namemap.put(Boolean.class, SOAPTypeMappingRegistry.SOAP_BOOLEAN);
        namemap.put(Double.class,  SOAPTypeMappingRegistry.SOAP_DOUBLE);
        namemap.put(Float.class,   SOAPTypeMappingRegistry.SOAP_FLOAT);
        namemap.put(Integer.class, SOAPTypeMappingRegistry.SOAP_INT);
        namemap.put(Long.class,    SOAPTypeMappingRegistry.SOAP_LONG);
        namemap.put(Short.class,   SOAPTypeMappingRegistry.SOAP_SHORT);
        namemap.put(BigDecimal.class, SOAPTypeMappingRegistry.XSD_DECIMAL);
        namemap.put(Date.class,    SOAPTypeMappingRegistry.XSD_DATE);
    }
    
    public void serialize(QName qname, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        context.startElement(qname, attributes);
        if (value != null) {
            if (value instanceof String)
                context.writeString(
                                XMLUtils.xmlEncodeString(value.toString()));
            else
                context.writeString(value.toString());
        }
        context.endElement();
    }
}
