// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.ejb;

import javax.wsdl.*;
import javax.wsdl.extensions.*;
import java.util.List;
import com.ibm.wsdl.util.StringUtils;

/**
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class EJBOperation
    implements ExtensibilityElement, java.io.Serializable {
    protected QName fieldElementType = EJBBindingConstants.Q_ELEM_EJB_OPERATION;
    // Uses the wrapper type so we can tell if it was set or not.
    protected Boolean fieldRequired = null;
    protected java.lang.String fieldMethodName;
    protected java.lang.String fieldEjbInterface;
    protected List fieldParameterOrder;
    protected String fieldReturnPart; 
    
    public java.lang.String getEjbInterface() {
        return fieldEjbInterface;
    }
    /**
     * Get the type of this extensibility element.
     *
     * @return the extensibility element's type
     */
    public QName getElementType() {
        return fieldElementType;
    }
    public java.lang.String getMethodName() {
        return fieldMethodName;
    }
    /**
     * Get whether or not the semantics of this extension
     * are required. Relates to the wsdl:required attribute.
     */
    public Boolean getRequired() {
        return fieldRequired;
    }
    public void setEjbInterface(java.lang.String ejbInterface) {
        fieldEjbInterface = ejbInterface;
    }
    /**
     * Set the type of this extensibility element.
     *
     * @param elementType the type
     */
    public void setElementType(QName elementType) {
        fieldElementType = elementType;
    }
    public void setMethodName(java.lang.String newMethodName) {
        fieldMethodName = newMethodName;
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

        strBuf.append("\nEJBOperation (" + fieldElementType + "):");
        strBuf.append("\nrequired=" + fieldRequired);
        strBuf.append("\nmethodName=" + fieldMethodName);
        strBuf.append("\nejbInterface=" + fieldEjbInterface);
        strBuf.append("\nparameterOrder=" + fieldParameterOrder);
        strBuf.append("\nreturnPart=" + fieldReturnPart);

        return strBuf.toString();
    }
	/**
	 * Gets the fieldReturnPart
	 * @return Returns a String
	 */
	public String getReturnPart() {
		return fieldReturnPart;
	}
    /**
     * Sets the fieldReturnPart
     * @param fieldReturnPart The fieldReturnPart to set
     */
    public void setReturnPart(String fieldReturnPart) {
        this.fieldReturnPart = fieldReturnPart;
    }

	/**
	 * Gets the fieldParameterOrder
	 * @return Returns a List
	 */
	public List getParameterOrder() {
		return fieldParameterOrder;
	}
	
    public void setParameterOrder(String newParameterOrderStr) {
        if (newParameterOrderStr != null) {
            fieldParameterOrder = StringUtils.parseNMTokens(newParameterOrderStr);
        }
    }

}