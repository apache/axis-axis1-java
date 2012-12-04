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
 * A representation of the model object '<em><b>Fault</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.Fault#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Fault#getQname <em>Qname</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Fault#getClass_ <em>Class</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Fault#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.apache.axis.model.wsdd.WSDDPackage#getFault()
 * @model
 * @generated
 */
public interface Fault extends EObject {
    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getFault_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Fault#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

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
     * @see org.apache.axis.model.wsdd.WSDDPackage#getFault_Qname()
     * @model dataType="org.eclipse.emf.ecore.xml.type.QName"
     * @generated
     */
    Object getQname();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Fault#getQname <em>Qname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Qname</em>' attribute.
     * @see #getQname()
     * @generated
     */
    void setQname(Object value);

    /**
     * Returns the value of the '<em><b>Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Class</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Class</em>' attribute.
     * @see #setClass(String)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getFault_Class()
     * @model
     * @generated
     */
    String getClass_();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Fault#getClass_ <em>Class</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Class</em>' attribute.
     * @see #getClass_()
     * @generated
     */
    void setClass(String value);

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
     * @see org.apache.axis.model.wsdd.WSDDPackage#getFault_Type()
     * @model dataType="org.eclipse.emf.ecore.xml.type.QName"
     * @generated
     */
    Object getType();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Fault#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType(Object value);

} // Fault
