package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.utils.QName;
import org.xml.sax.*;

public class SOAPTypeMappingRegistry extends TypeMappingRegistry { 
    
    public static final QName XSD_STRING = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "string");
    public static final QName XSD_BOOLEAN = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "boolean");
    public static final QName XSD_DOUBLE = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "double");
    public static final QName XSD_FLOAT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "float");
    public static final QName XSD_INT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "int");
    public static final QName XSD_LONG = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "long");
    public static final QName XSD_SHORT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "short");
    public static final QName SOAP_STRING = new QName(Constants.URI_SOAP_ENC, "string");
    public static final QName SOAP_BOOLEAN = new QName(Constants.URI_SOAP_ENC, "boolean");
    public static final QName SOAP_DOUBLE = new QName(Constants.URI_SOAP_ENC, "double");
    public static final QName SOAP_FLOAT = new QName(Constants.URI_SOAP_ENC, "float");
    public static final QName SOAP_INT = new QName(Constants.URI_SOAP_ENC, "int");
    public static final QName SOAP_LONG = new QName(Constants.URI_SOAP_ENC, "long");
    public static final QName SOAP_SHORT = new QName(Constants.URI_SOAP_ENC, "short");
    public static final QName SOAP_ARRAY = new QName(Constants.URI_SOAP_ENC, "Array");
    
    abstract class BasicDeser extends DeserializerBase {
        public void characters(char [] chars, int start, int end)
            throws SAXException
        {
            value = makeValue(new String(chars, start, end));
            valueComplete();
        }
        abstract Object makeValue(String source);
    }
    class IntDeser extends BasicDeser {
        Object makeValue(String source) { return new Integer(source); }
    }
    class IntDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new IntDeser(); }
    }
    class FloatDeser extends BasicDeser {
        Object makeValue(String source) { return new Float(source); }
    }
    class FloatDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new FloatDeser(); }
    }
    class LongDeser extends BasicDeser {
        Object makeValue(String source) { return new Long(source); }
    }
    class LongDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new LongDeser(); }
    }
    class StringDeser extends BasicDeser {
        public void characters(char [] chars, int start, int end) {
            String work = new String(chars, start, end);
            if (value == null)
                value = work;
            else
                value = (String)value + work;
        }
        Object makeValue(String source) { return null; }
    }
    class StringDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new StringDeser(); }
    }
    class BooleanDeser extends BasicDeser {
        Object makeValue(String source) { return new Boolean(source); }
    }
    class BooleanDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new BooleanDeser(); }
    }
    class DoubleDeser extends BasicDeser {
        Object makeValue(String source) { return new Double(source); }
    }
    class DoubleDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new DoubleDeser(); }
    }
    class ShortDeser extends BasicDeser {
        Object makeValue(String source) { return new Short(source); }
    }
    class ShortDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new ShortDeser(); }
    }
    
    private ArraySerializer arraySer = new ArraySerializer();

    /**
     * Alias common DeserializerFactories across the various popular schemas
     * @param base QName based on the current Schema namespace
     * @param factory common factory to be used across all schemas
     */
    private void addDeserializersFor(QName base, Class cls, DeserializerFactory factory) {
        addDeserializerFactory(base, cls, factory);
        String localPart = base.getLocalPart();
        for (int i=0; i<Constants.URIS_SCHEMA_XSD.length; i++) {
            if (!Constants.URIS_SCHEMA_XSD[i].equals(base.getNamespaceURI())) {
               QName qname = new QName(Constants.URIS_SCHEMA_XSD[i], localPart);
               addDeserializerFactory(qname, cls, factory);
            }
        }
    }

    public Serializer getSerializer(Class _class) {
        Serializer ser = super.getSerializer(_class);
        
        if ((ser == null) && (_class != null) &&
            (_class.isArray())) {
            ser = arraySer;
        }
        
        return ser;
    }

    public QName getTypeQName(Class _class) {
        QName qName = super.getTypeQName(_class);
        if ((qName == null) && (_class != null)) {
            if (_class.isArray()) qName = SOAP_ARRAY;
            if (_class == boolean.class) qName = XSD_BOOLEAN;
            if (_class == double.class)  qName = XSD_DOUBLE;
            if (_class == float.class)   qName = XSD_FLOAT;
            if (_class == int.class)     qName = XSD_INT;
            if (_class == long.class)    qName = XSD_LONG;
            if (_class == short.class)   qName = XSD_SHORT;
        }
        return qName;
    }
    
    public SOAPTypeMappingRegistry() {
        SOAPEncoding se = new SOAPEncoding();
        addSerializer(java.lang.String.class, XSD_STRING, se);
        addSerializer(java.lang.Boolean.class, XSD_BOOLEAN, se);
        addSerializer(java.lang.Double.class, XSD_DOUBLE, se);
        addSerializer(java.lang.Float.class, XSD_FLOAT, se);
        addSerializer(java.lang.Integer.class, XSD_INT, se);
        addSerializer(java.lang.Long.class, XSD_LONG, se);
        addSerializer(java.lang.Short.class, XSD_SHORT, se);
        
        addDeserializersFor(XSD_STRING, java.lang.String.class, new StringDeserializerFactory());    
        addDeserializersFor(XSD_BOOLEAN, java.lang.Boolean.class, new BooleanDeserializerFactory());
        addDeserializersFor(XSD_DOUBLE, java.lang.Double.class, new DoubleDeserializerFactory());
        addDeserializersFor(XSD_FLOAT, java.lang.Float.class, new FloatDeserializerFactory());
        addDeserializersFor(XSD_INT, java.lang.Integer.class, new IntDeserializerFactory());
        addDeserializersFor(XSD_LONG, java.lang.Long.class, new LongDeserializerFactory());
        addDeserializersFor(XSD_SHORT, java.lang.Short.class, new ShortDeserializerFactory());

        // !!! Seems a little weird to pass a null class here...?
        addDeserializerFactory(SOAP_ARRAY, null, ArraySerializer.factory);
        /*
        addDeserializerFactory(SOAP_STRING, se);
        addDeserializerFactory(SOAP_BOOLEAN, se);
        addDeserializerFactory(SOAP_DOUBLE, se);
        addDeserializerFactory(SOAP_FLOAT, se);
        addDeserializerFactory(SOAP_INT, se);
        addDeserializerFactory(SOAP_LONG, se);
        addDeserializerFactory(SOAP_SHORT, se);
        */
    }
    
}
