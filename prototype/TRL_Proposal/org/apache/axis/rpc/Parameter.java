package org.apache.axis.rpc;

final public class Parameter {
    Class type;
    String name;
    Object value;
    public Parameter(Class type, String name, Object value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Class getType() { return type; }
    public String getName() { return name; }
    public Object getValue() { return value; }
}

