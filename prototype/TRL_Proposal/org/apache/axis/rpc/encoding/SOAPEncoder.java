package org.apache.axis.rpc.encoding;

import java.io.Serializable;
import java.io.NotSerializableException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.apache.axis.message.impl.Constants;
import org.apache.axis.rpc.util.JavaType;

final public class SOAPEncoder implements Encoder, SOAPEncoding {
    private final Hashtable objectTable;
    private final Vector independentElements;
    private final JavaTypeMapper typeMapper;
    private final Document factory;
    private int nextID;

    public SOAPEncoder(Document factory, JavaTypeMapper typeMapper) {
        this.factory = factory;
        this.typeMapper = typeMapper;
        this.objectTable = new Hashtable();
        independentElements = new Vector();
        nextID = 0;
    }

    public void init() {
        objectTable.clear();
        independentElements.removeAllElements();
        nextID = 0;
    }

    public Element[] getEncodedObjects() {
        Object[] objects = independentElements.toArray();
        Element[] elems = new Element[objects.length];
        System.arraycopy(objects, 0, elems, 0, elems.length);
        return elems;
    }

    private Element getEncodedObject(Object object) {
        return (Element)objectTable.get(object);
    }

    private void putEncodedObject(Object object, Element elem) {
        objectTable.put(object, elem);
    }

    private synchronized String getID(Element elem) {
        String value;
        if ("".equals(value = elem.getAttribute(Constants.ATTR_ID)))
            elem.setAttribute(Constants.ATTR_ID, value = "obj-"+nextID++);
        return value;
    }

    public Element encodeRoot(Class type, String name, Object value)
        throws NotSerializableException, NoSuchTypeMappingException
    {
        Element elem = encode(type, name, value);
        elem.setAttributeNS(Constants.URI_SOAP_ENV, Constants.NSPREFIX_SOAP_ENV+':'+Constants.ATTR_ROOT, "1");
        if (!independentElements.contains(elem))
            independentElements.addElement(elem);
        return elem;
    }

    public Element encode(Class type, String name, Object value)
        throws NotSerializableException, NoSuchTypeMappingException
    {
        // null
        if (value == null)
            return encodeNull(type, name);

        // Primitive
        if (type.isPrimitive())
            return encodePrimitive(type, name, value);

        // Reference
        // if found in encoded object table
        Element encodedObject;
        if ((encodedObject = getEncodedObject(value)) != null)
            return encodeReference(name, encodedObject);

        // String
        if (String.class.equals(type))
            return encodeString(name, (String)value);

        // Array
        if (type.isArray())
            return encodeArray(type, name, value);

        // Object
        return encodeObject(type, name, value);
    }

    private Element encodePrimitive(Class type, String name, Object value)
        throws NoSuchTypeMappingException
    {
        String stringValue;
        int typeCode;
        switch (typeCode = JavaType.getTypeCode(type)) {
        case JavaType._boolean:
            stringValue = ""+((Boolean)value).booleanValue();
            break;
        case JavaType._byte:
            stringValue = ""+((Byte)value).byteValue();
            break;
        case JavaType._short:
            stringValue = ""+((Short)value).shortValue();
            break;
        case JavaType._char:
            stringValue = ""+((Character)value).charValue();
            break;
        case JavaType._int:
            stringValue = ""+((Integer)value).intValue();
            break;
        case JavaType._long:
            stringValue = ""+((Long)value).longValue();
            break;
        case JavaType._float:
            stringValue = ""+((Float)value).floatValue();
            break;
        case JavaType._double:
            stringValue = ""+((Double)value).doubleValue();
            break;
        case JavaType._string:
            stringValue = (String)value;
            break;
        default:
            throw new UnknownError("Unknown type code: " + typeCode);
        }

        Element elem = factory.createElement(name);
        setXSDType(elem, typeMapper.toXSDType(type));
        elem.appendChild(factory.createTextNode(stringValue));
        return elem;
    }

    private Element encodeString(String name, String value)
        throws NoSuchTypeMappingException
    {
        Element elem = encodePrimitive(String.class, name, value);
        putEncodedObject(value, elem);
        return elem;
    }

    private Element encodeArray(Class type, String name, Object value)
        throws NotSerializableException, NoSuchTypeMappingException
    {
        Element elem = factory.createElement(name);
        putEncodedObject(value, elem);

        String atype = type.getName().substring(1);
        int asize = Array.getLength(value);
        setArrayType(elem, encodeArrayType(atype, asize));
        for (int i = 0; i < asize; i++) {
            Object item;
            Class primitiveType;
            if ((item = Array.get(value, i)) != null &&
                (primitiveType = JavaType.toPrimitiveType(type = item.getClass())) != null)
                type = primitiveType;
            elem.appendChild(encode(type, "item", item));
        }
        return elem;
    }

    private Element encodeObject(Class type, String name, Object value)
        throws NotSerializableException, NoSuchTypeMappingException
    {
        Element elem = factory.createElement(name);
        putEncodedObject(value, elem);

        if (!(value instanceof Serializable))
            throw new NotSerializableException(type.getName());

        Field[] fields = type.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (!Modifier.isStatic(fields[i].getModifiers())) {
                try {
                    elem.appendChild(encode(fields[i].getType(),
                                            fields[i].getName(),
                                            getValue(fields[i], value)));
                } catch (IllegalAccessException e) {
                }
            }
        }
        return elem;
    }

    private Element encodeReference(String name, Element encodedObject) {
        Element elem = factory.createElement(name);
        setHref(elem, encodedObject);

        // if not externalized
        if (!independentElements.contains(encodedObject)) {
            independentElements.addElement(encodedObject);
            Node parent;
            // externalizes the encoded object
            if ((parent = encodedObject.getParentNode()) != null) {
                Element hrefElem = factory.createElement(encodedObject.getNodeName());
                setHref(hrefElem, encodedObject);
                parent.replaceChild(hrefElem, encodedObject);
            }
        }
        return elem;
    }

    private Element encodeNull(Class type, String name) {
        return factory.createElement(name);
    }

    private String encodeArrayType(String atype, int asize)
        throws NoSuchTypeMappingException
    {
        String encodedType;
        int length = atype.length();
        int index = 0;
        if (atype.charAt(index) == '[') {
            encodedType = "[";
            index++;
        } else {
            encodedType = "";
            while (index < length && atype.charAt(index) == '[') {
                encodedType += ',';
                index++;
            }
        }
        if (index > 0)
            encodedType += ']';

        if (atype.charAt(index) == 'L') { // Objects
            String className = atype.substring(index+1, atype.indexOf(';'));
            Class type = null;
            try {
                type = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new NoClassDefFoundError(e.getMessage());
            }
            encodedType = (typeMapper.toXSDType(type) +
                           encodedType  + '['+asize+']');
        } else // Primitive types
            encodedType = (typeMapper.toXSDType(JavaType.toPrimitiveType(""+atype.charAt(index))) + encodedType + '['+asize+']');
        return encodedType;
    }

    private void setHref(Element elem, Element encodedObject) {
        elem.setAttribute(Constants.ATTR_HREF, "#"+getID(encodedObject));
    }

    private void setXSDType(Element elem, String xsdType) {
        elem.setAttributeNS(Constants.URI_SCHEMA_XSI,
                            Constants.NSPREFIX_SCHEMA_XSI+':'+ATTR_TYPE,
                            xsdType);
    }

    private void setArrayType(Element elem, String arrayType) {
        elem.setAttributeNS(Constants.URI_SOAP_ENC,
                            Constants.NSPREFIX_SOAP_ENC+":arrayType",
                            arrayType);
    }

    private Object getValue(Field field, Object object)
        throws IllegalAccessException
    {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
        } catch (SecurityException e) {
        } catch (IllegalArgumentException e) {
        }
        return getBeanValue(field, object);
    }

    private static final Class[] _NON_PARAM_TYPE = new Class[]{};
    private static final Object[] _NON_PARAM = new Object[]{};
    private Object getBeanValue(Field field, Object object)
        throws IllegalAccessException
    {
        try {
            String methodName = "get" + field.getName();
            Method getValue = object.getClass().getMethod(methodName,
                                                          _NON_PARAM_TYPE);
            return getValue.invoke(object, _NON_PARAM);
        } catch (NoSuchMethodException e) { // thrown by getMethod()
        } catch (InvocationTargetException e) { // thrown by invoke()
        }
        throw new IllegalAccessException();
    }
}
