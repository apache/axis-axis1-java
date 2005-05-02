/**
 * StringArrayTestResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Apr 29, 2005 (10:05:23 EDT) WSDL2Java emitter.
 */
package test.wsdl.marshall3.types;

import javax.xml.namespace.QName;

public class QNameArrayTestResponse  implements java.io.Serializable {
    private QName[] qnameArray;

    public QNameArrayTestResponse() {
    }

    public QNameArrayTestResponse(
            QName[] qnameArray) {
        this.qnameArray = qnameArray;
    }

    /**
     * Gets the stringArray value for this StringArrayTestResponse.
     *
     * @return stringArray
     */
    public QName[] getQnameArray() {
        return qnameArray;
    }

    /**
     * Sets the stringArray value for this StringArrayTestResponse.
     *
     * @param stringArray
     */
    public void setQnameArray(QName[] qnameArray) {
        this.qnameArray = qnameArray;
    }
}
