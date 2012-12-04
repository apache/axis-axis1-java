/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Array Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.ArrayMapping#getInnerType <em>Inner Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.apache.axis.model.wsdd.WSDDPackage#getArrayMapping()
 * @model
 * @generated
 */
public interface ArrayMapping extends Mapping {
    /**
     * Returns the value of the '<em><b>Inner Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Inner Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Inner Type</em>' attribute.
     * @see #setInnerType(Object)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getArrayMapping_InnerType()
     * @model dataType="org.eclipse.emf.ecore.xml.type.QName"
     * @generated
     */
    Object getInnerType();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.ArrayMapping#getInnerType <em>Inner Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Inner Type</em>' attribute.
     * @see #getInnerType()
     * @generated
     */
    void setInnerType(Object value);

} // ArrayMapping
