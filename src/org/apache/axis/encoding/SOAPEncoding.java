package org.apache.axis.encoding;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Attr;
import org.apache.axis.utils.QName;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.Constants;
import java.util.Hashtable;

public class SOAPEncoding implements Serializer, Deserializer { 

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
    
    public Element serialize(QName qname, Object value, TypeMappingRegistry tmr, Document doc) {
        Element e;
        String name = null;
        String ns = null;
        QName xsitype = null;
        Class type = null;
        
        if (value != null) type = value.getClass();
        if (qname != null) {
            name = qname.getLocalPart();
            ns = qname.getNamespaceURI();
            xsitype = (QName)typemap.get(type);
        } else {
            if (type != null) {
                QName q = (QName)namemap.get(type);
                if (q != null) {
                    name = q.getLocalPart();
                    ns = q.getNamespaceURI();
                }
            }
        }
        
        if (name != null) {
            if (ns != null) {
                e = doc.createElementNS(ns, name);
            } else {
                e = doc.createElement(name);
            }
            if (xsitype != null) {
                e.setAttributeNS(Constants.URI_SCHEMA_XSI, "type", Constants.NSPREFIX_SCHEMA_XSD + ":" + xsitype.getLocalPart());
            }
            if (value != null) {
                e.appendChild(doc.createTextNode(value.toString()));
            }
            return e;
        } 
        return null;
    }

    public Object deserialize(Element element, TypeMappingRegistry tmr) {
        String xsdprefix = XMLUtils.getPrefix(Constants.URI_SCHEMA_XSD, element);
        String xsitype = element.getAttributeNS(Constants.URI_SCHEMA_XSI, "type");
        QName name = new QName(element.getNamespaceURI(), element.getLocalName());
        String value = element.getFirstChild().getNodeValue();
        
        if (xsitype != null) {
            if (xsitype.startsWith(xsdprefix + ":")) {
                String stype = xsitype.substring(xsdprefix.length() + 1);
                QName type = new QName(Constants.URI_SCHEMA_XSD, stype);
                if (type.getNamespaceURI().equals(SOAPTypeMappingRegistry.XSD_STRING.getNamespaceURI()) &&
                    type.getLocalPart().equals(SOAPTypeMappingRegistry.XSD_STRING.getLocalPart())) {
                    return new String(value);
                }
                if (type.getNamespaceURI().equals(SOAPTypeMappingRegistry.XSD_BOOLEAN.getNamespaceURI()) &&
                    type.getLocalPart().equals(SOAPTypeMappingRegistry.XSD_BOOLEAN.getLocalPart())) {
                    return new Boolean(value);
                }
                if (type.getNamespaceURI().equals(SOAPTypeMappingRegistry.XSD_DOUBLE.getNamespaceURI()) &&
                    type.getLocalPart().equals(SOAPTypeMappingRegistry.XSD_DOUBLE.getLocalPart())) {
                    return new Double(value);
                }
                if (type.getNamespaceURI().equals(SOAPTypeMappingRegistry.XSD_FLOAT.getNamespaceURI()) &&
                    type.getLocalPart().equals(SOAPTypeMappingRegistry.XSD_FLOAT.getLocalPart())) {
                    return new Float(value);
                }
                if (type.getNamespaceURI().equals(SOAPTypeMappingRegistry.XSD_INT.getNamespaceURI()) &&
                    type.getLocalPart().equals(SOAPTypeMappingRegistry.XSD_INT.getLocalPart())) {
                    return new Integer(value);
                }
                if (type.getNamespaceURI().equals(SOAPTypeMappingRegistry.XSD_LONG.getNamespaceURI()) &&
                    type.getLocalPart().equals(SOAPTypeMappingRegistry.XSD_LONG.getLocalPart())) {
                    return new Long(value);
                }
                if (type.getNamespaceURI().equals(SOAPTypeMappingRegistry.XSD_SHORT.getNamespaceURI()) &&
                    type.getLocalPart().equals(SOAPTypeMappingRegistry.XSD_SHORT.getLocalPart())) {
                    return new Short(value);
                }
            }
        }
        
        if (name.getNamespaceURI().equals(SOAPTypeMappingRegistry.SOAP_STRING.getNamespaceURI()) &&
            name.getLocalPart().equals(SOAPTypeMappingRegistry.SOAP_STRING.getLocalPart())) {
            return new String(value);
        }
        if (name.getNamespaceURI().equals(SOAPTypeMappingRegistry.SOAP_BOOLEAN.getNamespaceURI()) &&
            name.getLocalPart().equals(SOAPTypeMappingRegistry.SOAP_BOOLEAN.getLocalPart())) {
            return new Boolean(value);
        }
        if (name.getNamespaceURI().equals(SOAPTypeMappingRegistry.SOAP_DOUBLE.getNamespaceURI()) &&
            name.getLocalPart().equals(SOAPTypeMappingRegistry.SOAP_DOUBLE.getLocalPart())) {
            return new Double(value);
        }
        if (name.getNamespaceURI().equals(SOAPTypeMappingRegistry.SOAP_FLOAT.getNamespaceURI()) &&
            name.getLocalPart().equals(SOAPTypeMappingRegistry.SOAP_FLOAT.getLocalPart())) {
            return new Float(value);
        }
        if (name.getNamespaceURI().equals(SOAPTypeMappingRegistry.SOAP_INT.getNamespaceURI()) &&
            name.getLocalPart().equals(SOAPTypeMappingRegistry.SOAP_INT.getLocalPart())) {
            return new Integer(value);
        }
        if (name.getNamespaceURI().equals(SOAPTypeMappingRegistry.SOAP_LONG.getNamespaceURI()) &&
            name.getLocalPart().equals(SOAPTypeMappingRegistry.SOAP_LONG.getLocalPart())) {
            return new Long(value);
        }
        if (name.getNamespaceURI().equals(SOAPTypeMappingRegistry.SOAP_SHORT.getNamespaceURI()) &&
            name.getLocalPart().equals(SOAPTypeMappingRegistry.SOAP_SHORT.getLocalPart())) {
            return new Short(value);
        }
        
        return null;
    }
}
