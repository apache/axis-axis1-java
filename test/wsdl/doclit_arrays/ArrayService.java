package test.wsdl.doclit_arrays;

/**
 * Test doc/lit service taking and returning arrays
 */
public class ArrayService {
    public static class Bean {
        public Bean() {
        }

        public String [] arrayField;

        // Type metadata
        private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(Bean.class, true);

        static {
            typeDesc.setXmlType(new javax.xml.namespace.QName("http://doclit_arrays.wsdl.test/", "Bean"));
            org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
            elemField.setFieldName("arrayField");
            elemField.setXmlName(new javax.xml.namespace.QName("http://doclit_arrays.wsdl.test/", "arrayField"));
            elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
            elemField.setMinOccurs(0);
            elemField.setNillable(false);
            //elemField.setItemQName(new javax.xml.namespace.QName("", "inner"));
            typeDesc.addFieldDesc(elemField);
        }

        /**
         * Return type metadata object
         */
        public static org.apache.axis.description.TypeDesc getTypeDesc() {
            return typeDesc;
        }
    }

    public String [] echoString(String [] input) {
        return input;
    }

    public Bean echoBean(Bean input) {
        return input;
    }
}
