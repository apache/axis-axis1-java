/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.Operation#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Operation#getQname <em>Qname</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Operation#getReturnQName <em>Return QName</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Operation#getReturnType <em>Return Type</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Operation#getReturnItemQName <em>Return Item QName</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Operation#getReturnItemType <em>Return Item Type</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Operation#getSoapAction <em>Soap Action</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Operation#getMep <em>Mep</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Operation#getReturnHeader <em>Return Header</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Operation#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Operation#getFaults <em>Faults</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation()
 * @model
 * @generated
 */
public interface Operation extends EObject {
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
     * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Operation#getName <em>Name</em>}' attribute.
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
     * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation_Qname()
     * @model dataType="org.eclipse.emf.ecore.xml.type.QName"
     * @generated
     */
    Object getQname();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Operation#getQname <em>Qname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Qname</em>' attribute.
     * @see #getQname()
     * @generated
     */
    void setQname(Object value);

    /**
     * Returns the value of the '<em><b>Return QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Return QName</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Return QName</em>' attribute.
     * @see #setReturnQName(Object)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation_ReturnQName()
     * @model dataType="org.eclipse.emf.ecore.xml.type.QName"
     * @generated
     */
    Object getReturnQName();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Operation#getReturnQName <em>Return QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Return QName</em>' attribute.
     * @see #getReturnQName()
     * @generated
     */
    void setReturnQName(Object value);

    /**
     * Returns the value of the '<em><b>Return Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Return Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Return Type</em>' attribute.
     * @see #setReturnType(Object)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation_ReturnType()
     * @model dataType="org.eclipse.emf.ecore.xml.type.QName"
     * @generated
     */
    Object getReturnType();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Operation#getReturnType <em>Return Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Return Type</em>' attribute.
     * @see #getReturnType()
     * @generated
     */
    void setReturnType(Object value);

    /**
     * Returns the value of the '<em><b>Return Item QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Return Item QName</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Return Item QName</em>' attribute.
     * @see #setReturnItemQName(Object)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation_ReturnItemQName()
     * @model dataType="org.eclipse.emf.ecore.xml.type.QName"
     * @generated
     */
    Object getReturnItemQName();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Operation#getReturnItemQName <em>Return Item QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Return Item QName</em>' attribute.
     * @see #getReturnItemQName()
     * @generated
     */
    void setReturnItemQName(Object value);

    /**
     * Returns the value of the '<em><b>Return Item Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Return Item Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Return Item Type</em>' attribute.
     * @see #setReturnItemType(Object)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation_ReturnItemType()
     * @model dataType="org.eclipse.emf.ecore.xml.type.QName"
     * @generated
     */
    Object getReturnItemType();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Operation#getReturnItemType <em>Return Item Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Return Item Type</em>' attribute.
     * @see #getReturnItemType()
     * @generated
     */
    void setReturnItemType(Object value);

    /**
     * Returns the value of the '<em><b>Soap Action</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Soap Action</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Soap Action</em>' attribute.
     * @see #setSoapAction(String)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation_SoapAction()
     * @model
     * @generated
     */
    String getSoapAction();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Operation#getSoapAction <em>Soap Action</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Soap Action</em>' attribute.
     * @see #getSoapAction()
     * @generated
     */
    void setSoapAction(String value);

    /**
     * Returns the value of the '<em><b>Mep</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mep</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mep</em>' attribute.
     * @see #setMep(String)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation_Mep()
     * @model
     * @generated
     */
    String getMep();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Operation#getMep <em>Mep</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Mep</em>' attribute.
     * @see #getMep()
     * @generated
     */
    void setMep(String value);

    /**
     * Returns the value of the '<em><b>Return Header</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Return Header</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Return Header</em>' attribute.
     * @see #setReturnHeader(Boolean)
     * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation_ReturnHeader()
     * @model
     * @generated
     */
    Boolean getReturnHeader();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Operation#getReturnHeader <em>Return Header</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Return Header</em>' attribute.
     * @see #getReturnHeader()
     * @generated
     */
    void setReturnHeader(Boolean value);

    /**
     * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
     * The list contents are of type {@link org.apache.axis.model.wsdd.OperationParameter}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameters</em>' containment reference list.
     * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation_Parameters()
     * @model type="org.apache.axis.model.wsdd.OperationParameter" containment="true"
     *        extendedMetaData="kind='element' name='parameter' namespace='##targetNamespace'"
     * @generated
     */
    EList getParameters();

    /**
     * Returns the value of the '<em><b>Faults</b></em>' containment reference list.
     * The list contents are of type {@link org.apache.axis.model.wsdd.Fault}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Faults</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Faults</em>' containment reference list.
     * @see org.apache.axis.model.wsdd.WSDDPackage#getOperation_Faults()
     * @model type="org.apache.axis.model.wsdd.Fault" containment="true"
     *        extendedMetaData="kind='element' name='fault' namespace='##targetNamespace'"
     * @generated
     */
    EList getFaults();

} // Operation
