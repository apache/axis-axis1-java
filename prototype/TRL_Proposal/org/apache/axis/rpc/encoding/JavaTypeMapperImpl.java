package org.apache.axis.rpc.encoding;

import java.util.Hashtable;
import org.apache.axis.message.impl.Constants;

final public class JavaTypeMapperImpl implements JavaTypeMapper {
    final Hashtable java2xsd;
    final Hashtable xsd2java;

    public JavaTypeMapperImpl() {
        java2xsd = new Hashtable();
        xsd2java = new Hashtable();
        defaultMap();
    }

    private void defaultMap() {
        mapType(boolean.class, Constants.NSPREFIX_SCHEMA_XSD+":boolean");
        mapType(byte.class, Constants.NSPREFIX_SCHEMA_XSD+":byte");
        mapType(short.class, Constants.NSPREFIX_SCHEMA_XSD+":short");
        mapType(char.class, Constants.NSPREFIX_SCHEMA_XSD+":CDATA");
        mapType(int.class, Constants.NSPREFIX_SCHEMA_XSD+":int");
        mapType(long.class, Constants.NSPREFIX_SCHEMA_XSD+":long");
        mapType(float.class, Constants.NSPREFIX_SCHEMA_XSD+":float");
        mapType(double.class, Constants.NSPREFIX_SCHEMA_XSD+":double");
        mapType(String.class, Constants.NSPREFIX_SCHEMA_XSD+":string");
    }

    public void mapType(Class type, String xsdType) {
        java2xsd.put(type, xsdType);
        xsd2java.put(xsdType, type);
    }

    public void unmapType(Class type) {
        xsd2java.remove(java2xsd.get(type));
        java2xsd.remove(type);
    }

    public void unmapType(String xsdType) {
        java2xsd.remove(xsd2java.get(xsdType));
        xsd2java.remove(xsdType);
    }

    public String toXSDType(Class type) throws NoSuchTypeMappingException {
        String xsdType;
        if ((xsdType = (String)java2xsd.get(type)) == null)
            throw new NoSuchTypeMappingException(type.getName());
        return xsdType;
    }

    public Class toJavaType(String xsdType) throws NoSuchTypeMappingException {
        Class type;
        if ((type = (Class)xsd2java.get(xsdType)) == null)
            throw new NoSuchTypeMappingException(xsdType);
        return type;
    }
}
