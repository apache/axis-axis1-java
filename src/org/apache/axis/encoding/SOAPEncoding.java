package org.apache.axis.encoding;

import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.axis.utils.QName;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.Constants;
import java.util.Hashtable;
import java.io.IOException;

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
        namemap.put(String.class,  SOAPTypeMappingRegistry.SOAP_STRING);
        namemap.put(Boolean.class, SOAPTypeMappingRegistry.SOAP_BOOLEAN);
        namemap.put(Double.class,  SOAPTypeMappingRegistry.SOAP_DOUBLE);
        namemap.put(Float.class,   SOAPTypeMappingRegistry.SOAP_FLOAT);
        namemap.put(Integer.class, SOAPTypeMappingRegistry.SOAP_INT);
        namemap.put(Long.class,    SOAPTypeMappingRegistry.SOAP_LONG);
        namemap.put(Short.class,   SOAPTypeMappingRegistry.SOAP_SHORT);
    }
    
    public void serialize(QName qname, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        QName xsitype = null;
        Class type = null;
        
        if (value != null) {
            type = value.getClass();
        } else {
            // !!! add xsi:null attribute
        }
        
        if (type != null) {
            if (qname == null) {
                qname = (QName)namemap.get(type);
            }
            xsitype = (QName)typemap.get(type);
        }
        
        Attributes attrs = attributes;
        String str = context.qName2String(xsitype);
        
        if (xsitype != null) {
            // !!! should check if we're writing types or not?
            AttributesImpl impl = new AttributesImpl();
            boolean gotType = false;
            
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    impl.addAttribute(attributes.getURI(i),
                                      attributes.getLocalName(i),
                                      attributes.getQName(i), "CDATA",
                                      attributes.getValue(i));
                    if (attributes.getURI(i).equals(Constants.URI_CURRENT_SCHEMA_XSI) &&
                        attributes.getLocalName(i).equals("type"))
                        gotType = true;
                }
            }
            
            if (!gotType) {
                impl.addAttribute(Constants.URI_CURRENT_SCHEMA_XSI,
                               "type", "xsi:type",
                               "CDATA", str);
            }
            
            attrs = impl;
        }

        context.startElement(qname, attrs);
        if (value != null)
            context.writeString(value.toString());
        context.endElement();
    }
}
