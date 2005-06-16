/*
 * This is a ColdFusion QueryBean object, including meta data.
 */
package test.wsdl.query;

import java.io.Serializable;

/**
 * Representation of a ColdFusion qeury object for web services.
 */
public class QueryBean implements Serializable
{
    public void setColumnList(String[] column_list)
    {
        this.column_list = column_list;
    }

    public String[] getColumnList()
    {
        return column_list;
    }

    public void setData(Object[][] data)
    {
        this.data = data;
    }

    public Object[][] getData()
    {
        return data;
    }

    private String[] column_list;
    private Object[][] data;

    // Type metadatae
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(QueryBean.class, true);

    // Axis 1.2.1 with a document/literal WSDL
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://rpc.xml.coldfusion", "QueryBean"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("columnList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://rpc.xml.coldfusion", "columnList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("urn:QueryTest", "item"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("data");
        elemField.setXmlName(new javax.xml.namespace.QName("http://rpc.xml.coldfusion", "data"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:QueryTest", "ArrayOf_xsd_anyType"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("urn:QueryTest", "item"));
        typeDesc.addFieldDesc(elemField);
    }


    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc()
    {
        return typeDesc;
    }

}
