package org.apache.axis.rpc.encoding;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.axis.message.impl.Constants;
import org.apache.axis.rpc.util.JavaType;

final public class SOAPDecoder implements Decoder, SOAPEncoding {
    private final JavaTypeMapper typeMapper;
    private final Hashtable elemTable;
    private final Hashtable objectTable;

    public SOAPDecoder(JavaTypeMapper typeMapper) {
        this.typeMapper = typeMapper;
        this.elemTable = new Hashtable();
        this.objectTable = new Hashtable();
    }

    public void init() {
        elemTable.clear();
        objectTable.clear();
    }

    private Element getEncodedElement(String id) {
        return (Element)elemTable.get(id);
    }

    private void putEncodedElement(String id, Element elem) {
        elemTable.put(id, elem);
    }

    private void putObject(String id, Object object) {
        objectTable.put(id, object);
    }

    private Object getObject(String id) { return objectTable.get(id); }

    public Object[] decodeRoot(Class[] types, Element[] elems)
        throws IllegalAccessException,
               InstantiationException,
               NoSuchFieldException,
               NoSuchTypeMappingException,
               MalformedEncodingException
    {
        String id;
        int index = 0;
        Element[] roots = new Element[types.length];
        for (int i = 0; i < elems.length; i++) {
            if ("1".equals(elems[i].getAttributeNS(Constants.URI_SOAP_ENV, Constants.ATTR_ROOT)))
                roots[index++] = elems[i];
            if ((id = getID(elems[i])) != null)
                putEncodedElement(id, elems[i]);
        }

        Object[] values = new Object[types.length];
        for (int i = 0; i < values.length; i++)
            values[i] = decode(types[i], roots[i]);
        return values;
    }

    public Object decode(Class type, Element elem)
        throws IllegalAccessException,
               InstantiationException,
               NoSuchFieldException,
               NoSuchTypeMappingException,
               MalformedEncodingException
    {
        // Primitive
        if (type.isPrimitive()) {
            String xsdType;
            if ((xsdType = getXSDType(elem)) == null ||
                !typeMapper.toJavaType(xsdType).equals(type))
                throw new MalformedEncodingException("Java type schema is inconsistent with XSD data type");
            return decodePrimitive(type, elem.getFirstChild().getNodeValue());
        }

        // Reference
        String href;
        if ((href = getHref(elem)) != null) {
            if (!href.startsWith("#") || href.length() < 2)
                throw new MalformedEncodingException("The value of 'href' attribute is invalid: " + href);
            return decodeReference(type, href.substring(1));
        }

        // null
        if (!elem.hasChildNodes())
            return null;

        // String
        if (String.class.equals(type)) {
            String xsdType;
            if ((xsdType = getXSDType(elem)) == null ||
                !typeMapper.toJavaType(xsdType).equals(type))
                throw new MalformedEncodingException("Java type schema is inconsistent with XSD data type");

            String value = elem.getFirstChild().getNodeValue();
            String id;
            if ((id = getID(elem)) != null)
                putObject(id, value);

            return decodePrimitive(type, value);
        }

        // Array
        if (type.isArray()) {
            String arrayTypeValue;
            if ((arrayTypeValue = getArrayType(elem)) == null)
                throw new MalformedEncodingException("Java type schema is inconsistent with XSD data type");
            ArrayType arrayType = decodeArrayType(arrayTypeValue);
            Object array = Array.newInstance(arrayType.atype, arrayType.asize);
            String id;
            if ((id = getID(elem)) != null)
                putObject(id, array);

            NodeList list = elem.getChildNodes();
            int length = list.getLength();
            Node node;
            int index = 0;
            for (int i = 0; i < length; i++)
                if ((node = list.item(i)).getNodeType() == Node.ELEMENT_NODE)
                    Array.set(array, index++, decode(arrayType.atype, (Element)node));
            return array;
        }

        // Object
        Object object = type.newInstance();
        String id;
        if ((id = getID(elem)) != null)
            putObject(id, object);

        NodeList list = elem.getChildNodes();
        int length = list.getLength();
        Node node;
        for (int i = 0; i < length; i++)
            if ((node = list.item(i)).getNodeType() == Node.ELEMENT_NODE) {
                elem = (Element)node;
                Field field = type.getDeclaredField(elem.getNodeName());
                Object value = decode(field.getType(), elem);
                try {
                    setValue(field, object, value);
                } catch (IllegalAccessException e) {
                }
            }
        return object;
    }

    private Object decodePrimitive(Class type, String value) {
        int typeCode;
        switch (typeCode = JavaType.getTypeCode(type)) {
        case JavaType._boolean:
            return new Boolean(value);
        case JavaType._byte:
            return new Byte(value);
        case JavaType._short:
            return new Short(value);
        case JavaType._char:
            return new Character(value.charAt(0));
        case JavaType._int:
            return new Integer(value);
        case JavaType._long:
            return new Long(value);
        case JavaType._float:
            return new Float(value);
        case JavaType._double:
            return new Double(value);
        case JavaType._string:
            return value;
        default:
            throw new UnknownError("Unknown Java type code: " + typeCode);
        }
    }

    private void setValue(Field field, Object object, Object value)
        throws IllegalAccessException
    {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            setBeanValue(field, object, value);
        }
    }

    private void setBeanValue(Field field, Object object, Object value)
        throws IllegalAccessException
    {
        try {
            String methodName = "set" + field.getName();
            Method setValue = object.getClass().getMethod(methodName, new Class[]{ field.getType(), });
            setValue.invoke(object, new Object[]{ value, });
        } catch (NoSuchMethodException e) { // thrown by getMethod()
        } catch (InvocationTargetException e) { // thrown by invoke()
        }
        throw new IllegalAccessException();
    }

    private String getXSDType(Element elem) {
        String xsdType = elem.getAttributeNS(Constants.URI_SCHEMA_XSI, ATTR_TYPE);
        return "".equals(xsdType) ? null : xsdType;
    }

    private String getHref(Element elem) {
        String href = elem.getAttribute(Constants.ATTR_HREF);
        return "".equals(href) ? null : href;
    }

    private String getID(Element elem) {
        String id = elem.getAttribute(Constants.ATTR_ID);
        return "".equals(id) ? null : id;
    }

    private String getArrayType(Element elem) {
        String arrayType = elem.getAttributeNS(Constants.URI_SOAP_ENC, "arrayType");
        return "".equals(arrayType) ? null : arrayType;
    }

    private Object decodeReference(Class type, String id)
        throws IllegalAccessException,
               InstantiationException,
               NoSuchFieldException,
               NoSuchTypeMappingException,
               MalformedEncodingException
    {
        // if the object indicated by the id has not been decoded yet.
        Object object;
        if ((object = getObject(id)) == null)
            putObject(id, object = decode(type, getEncodedElement(id)));
        return object;
    }

    class ArrayType {
        final Class atype;
        final int asize;
        ArrayType(Class atype, int asize) {
            this.atype = atype;
            this.asize = asize;
        }
    }

    private ArrayType decodeArrayType(String arrayType)
        throws MalformedEncodingException, NoSuchTypeMappingException
    {
        try {
            Reader in = new StringReader(arrayType);
            int c;
            String qname = "";
            while (true) {
                if ((c = in.read()) == -1)
                    throw new MalformedEncodingException("Malformed arrayType: " + arrayType);
                if (c == '[')
                    break;
                qname += (char)c;
            }
            Class type = typeMapper.toJavaType(qname);

            int rank = 0;
            while (true) {
                if ((c = in.read()) == -1)
                    throw new MalformedEncodingException("Malformed arrayType: " + arrayType);
                if (Character.isDigit((char)c) || c == '[')
                    break;
                if (c == ',' || c == ']') {
                    rank++;
                    continue;
                }
                throw new MalformedEncodingException("Malformed arrayType: " + arrayType);
            }

            while (!Character.isDigit((char)c))
                if ((c = in.read()) == -1)
                    throw new MalformedEncodingException("Malformed arrayType: " + arrayType);

            String asizeString = ""+(char)c;
            while (true) {
                if ((c = in.read()) == -1)
                    throw new MalformedEncodingException("Malformed arrayType: " + arrayType);
                if (c == ']')
                    break;
                if (!Character.isDigit((char)c))
                    throw new MalformedEncodingException("Malformed arrayType: " + arrayType);
                asizeString += (char)c;
            }
            int asize = Integer.parseInt(asizeString);

            Class atype = type;
            for (int i = 0; i < rank; i++)
                atype = Array.newInstance(atype, 0).getClass();

            return new ArrayType(atype, asize);
        } catch (IOException e) {
            throw new MalformedEncodingException("Malformed arrayType: " + arrayType);
        }
    }
}
