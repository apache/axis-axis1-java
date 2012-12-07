/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;

import javax.xml.namespace.QName;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operation Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.OperationParameter#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.OperationParameter#getQname <em>Qname</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.OperationParameter#getType <em>Type</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.OperationParameter#getMode <em>Mode</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.OperationParameter#getInHeader <em>In Header</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.OperationParameter#getOutHeader <em>Out Header</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.OperationParameter#getItemQName <em>Item QName</em>}</li>
 * </ul>
 * </p>
 *
 * @model
 * @generated
 */
public interface OperationParameter {
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
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.OperationParameter#getName <em>Name</em>}' attribute.
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
     * @see #setQname(QName)
     * @model dataType="org.apache.axis.model.xml.QName"
     * @generated
     */
    QName getQname();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.OperationParameter#getQname <em>Qname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Qname</em>' attribute.
     * @see #getQname()
     * @generated
     */
    void setQname(QName value);

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see #setType(QName)
     * @model dataType="org.apache.axis.model.xml.QName"
     * @generated
     */
    QName getType();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.OperationParameter#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType(QName value);

    /**
     * Returns the value of the '<em><b>Mode</b></em>' attribute.
     * The literals are from the enumeration {@link org.apache.axis.model.wsdd.ParameterMode}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mode</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mode</em>' attribute.
     * @see org.apache.axis.model.wsdd.ParameterMode
     * @see #setMode(ParameterMode)
     * @model
     * @generated
     */
    ParameterMode getMode();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.OperationParameter#getMode <em>Mode</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Mode</em>' attribute.
     * @see org.apache.axis.model.wsdd.ParameterMode
     * @see #getMode()
     * @generated
     */
    void setMode(ParameterMode value);

    /**
     * Returns the value of the '<em><b>In Header</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>In Header</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>In Header</em>' attribute.
     * @see #setInHeader(Boolean)
     * @model
     * @generated
     */
    Boolean getInHeader();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.OperationParameter#getInHeader <em>In Header</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>In Header</em>' attribute.
     * @see #getInHeader()
     * @generated
     */
    void setInHeader(Boolean value);

    /**
     * Returns the value of the '<em><b>Out Header</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Out Header</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Out Header</em>' attribute.
     * @see #setOutHeader(Boolean)
     * @model
     * @generated
     */
    Boolean getOutHeader();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.OperationParameter#getOutHeader <em>Out Header</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Out Header</em>' attribute.
     * @see #getOutHeader()
     * @generated
     */
    void setOutHeader(Boolean value);

    /**
     * Returns the value of the '<em><b>Item QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Item QName</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Item QName</em>' attribute.
     * @see #setItemQName(QName)
     * @model dataType="org.apache.axis.model.xml.QName"
     * @generated
     */
    QName getItemQName();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.OperationParameter#getItemQName <em>Item QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Item QName</em>' attribute.
     * @see #getItemQName()
     * @generated
     */
    void setItemQName(QName value);

} // OperationParameter
