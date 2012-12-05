/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.Mapping#getQname <em>Qname</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Mapping#getType <em>Type</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Mapping#getEncodingStyle <em>Encoding Style</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.apache.axis.model.wsdd.WSDDPackage#getMapping()
 * @model abstract="true"
 * @generated
 */
public interface Mapping extends EObject {
    /**
     * Returns the value of the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Qname</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Qname</em>' attribute.
     * @see #setQname(Object)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getMapping_Qname()
     * @model dataType="org.eclipse.emf.ecore.xml.type.QName"
     * @generated
     */
    Object getQname();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Mapping#getQname <em>Qname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Qname</em>' attribute.
     * @see #getQname()
     * @generated
     */
    void setQname(Object value);

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see #setType(Object)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getMapping_Type()
     * @model dataType="org.eclipse.emf.ecore.xml.type.QName"
     * @generated
     */
    Object getType();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Mapping#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType(Object value);

    /**
     * Returns the value of the '<em><b>Encoding Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Encoding Style</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Encoding Style</em>' attribute.
     * @see #setEncodingStyle(String)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getMapping_EncodingStyle()
     * @model
     * @generated
     */
    String getEncodingStyle();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Mapping#getEncodingStyle <em>Encoding Style</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Encoding Style</em>' attribute.
     * @see #getEncodingStyle()
     * @generated
     */
    void setEncodingStyle(String value);

} // Mapping
