package test.typedesc;

import org.apache.axis.description.AttributeDesc;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SimpleType;

import javax.xml.namespace.QName;

public class ComplexBean3 extends SimpleBean2 implements java.io.Serializable {

    public String c;
    public String d;

    private static TypeDesc typeDesc = new TypeDesc(ComplexBean3.class);
    static {
        FieldDesc fd = new AttributeDesc();
        fd.setFieldName("c");
        fd.setXmlName(new QName("foo", "c"));
        typeDesc.addFieldDesc(fd);

	fd = new AttributeDesc();
        fd.setFieldName("d");
        fd.setXmlName(new QName("foo", "d"));
        typeDesc.addFieldDesc(fd);
    }
    public static TypeDesc getTypeDesc() { return typeDesc; }

    public ComplexBean3() {}

    public void setC(String value) {
    }

    public String getC() {
	return null;
    }

    public void setD(String value) {
    }

    public String getD() {
	return null;
    }

}
