package org.apache.axis.rpc.encoding;

public interface JavaTypeMapper {
    public void mapType(Class type, String xsdType);
    public void unmapType(Class type);
    public void unmapType(String xsdType);
    public String toXSDType(Class type) throws NoSuchTypeMappingException;
    public Class toJavaType(String xsdType) throws NoSuchTypeMappingException;
}
