package org.apache.axis.utils;

public class Mapping {
    public String namespaceURI;
    public String prefix;
    public Mapping(String namespaceURI, String prefix)
    {
        this.namespaceURI = namespaceURI;
        this.prefix = prefix;
    }
    
    public String getNamespaceURI()
    {
        return namespaceURI;
    }
    public String getPrefix()
    {
        return prefix;
    }
}
