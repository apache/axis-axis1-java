/*
 * QueryBean object used in ColdFusion MX web services.
 */
package test.wsdl.query;

import java.io.Serializable;

/**
 * Representation of a ColdFusion qeury object for web services.
 * @author Tom Jordahl (tomj@apache.org)
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

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(QueryBean.class, true);

    static
    {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://rpc.xml.coldfusion/", "QueryBean"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("columnList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://rpc.xml.coldfusion/", "columnList"));
        elemField.setArrayType(org.apache.axis.Constants.XSD_STRING);
        elemField.setJavaType(String[].class);
        elemField.setXmlType(org.apache.axis.Constants.SOAP_ARRAY);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("data");
        elemField.setXmlName(new javax.xml.namespace.QName("http://rpc.xml.coldfusion/", "data"));
        elemField.setXmlType(org.apache.axis.Constants.SOAP_ARRAY);
        elemField.setJavaType(Object[][].class);
        elemField.setNillable(true);
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
