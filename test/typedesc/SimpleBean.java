package test.typedesc;

import org.apache.axis.description.AttributeDesc;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SimpleType;

import javax.xml.namespace.QName;

public class SimpleBean implements java.io.Serializable {

    public String a;
    public String b;

    private static TypeDesc typeDesc = new TypeDesc(SimpleBean.class);
    static {
        FieldDesc fd = new AttributeDesc();
        fd.setFieldName("a");
        fd.setXmlName(new QName("foo", "a"));
        typeDesc.addFieldDesc(fd);

	fd = new AttributeDesc();
        fd.setFieldName("b");
        fd.setXmlName(new QName("foo", "b"));
        typeDesc.addFieldDesc(fd);
    }
    public static TypeDesc getTypeDesc() { return typeDesc; }

    public SimpleBean() {}

    public void setA(String value) {
    }
    
    public String getA() {
	return null;
    }

    public void setB(String value) {
    }

    public String getB() {
	return null;
    }
}
