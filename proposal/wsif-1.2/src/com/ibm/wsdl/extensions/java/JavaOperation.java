// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.java;

import javax.wsdl.*;
import javax.wsdl.extensions.*;
import java.util.List;
import com.ibm.wsdl.util.StringUtils;

/**
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class JavaOperation
    implements ExtensibilityElement, java.io.Serializable {
    protected QName fieldElementType = JavaBindingConstants.Q_ELEM_JAVA_OPERATION;
    // Uses the wrapper type so we can tell if it was set or not.
    protected Boolean fieldRequired = null;

    protected String fieldMethodName;
    protected String fieldMethodType;
    protected List fieldParameterOrder;
    protected String fieldReturnPart;    

    /**
     * Get the type of this extensibility element.
     *
     * @return the extensibility element's type
     */
    public QName getElementType() {
        return fieldElementType;
    }
    public String getMethodName() {
        return fieldMethodName;
    }
    public String getMethodType() {
        return fieldMethodType;
    }
    public List getParameterOrder() {
        return fieldParameterOrder;
    }
    public String getReturnPart() {
        return fieldReturnPart;
    }

    /**
     * Get whether or not the semantics of this extension
     * are required. Relates to the wsdl:required attribute.
     */
    public Boolean getRequired() {
        return fieldRequired;
    }
    /**
     * Set the type of this extensibility element.
     *
     * @param elementType the type
     */
    public void setElementType(QName elementType) {
        fieldElementType = elementType;
    }
    public void setMethodName(String newMethodName) {
        fieldMethodName = newMethodName;
    }
    public void setMethodType(String newMethodType) {
        fieldMethodType = newMethodType;
    }
    public void setParameterOrder(String newParameterOrderStr) {
        if (newParameterOrderStr != null) {
            fieldParameterOrder = StringUtils.parseNMTokens(newParameterOrderStr);
        }
    }
    public void setReturnPart(String newReturnPart) {
        fieldReturnPart = newReturnPart;
    }

    /**
     * Set whether or not the semantics of this extension
     * are required. Relates to the wsdl:required attribute.
     */
    public void setRequired(Boolean required) {
        fieldRequired = required;
    }
    public String toString() {
        StringBuffer strBuf = new StringBuffer(super.toString());

        strBuf.append("\nJavaOperation (" + fieldElementType + "):");
        strBuf.append("\nrequired=" + fieldRequired);

        strBuf.append("\nmethodName=" + fieldMethodName);
        strBuf.append("\nmethodType=" + fieldMethodType);
        strBuf.append("\nparameterOrder=" + fieldParameterOrder);
        strBuf.append("\nreturnPart=" + fieldReturnPart);

        return strBuf.toString();
    }
}