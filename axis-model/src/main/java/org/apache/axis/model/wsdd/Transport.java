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
 * A representation of the model object '<em><b>Transport</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.Transport#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Transport#getPivot <em>Pivot</em>}</li>
 * </ul>
 * </p>
 *
 * @model
 * @generated
 */
public interface Transport extends DeployableItem {
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
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Transport#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Pivot</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Pivot</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Pivot</em>' attribute.
     * @see #setPivot(QName)
     * @model dataType="org.apache.axis.model.xml.QName"
     * @generated
     */
    QName getPivot();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Transport#getPivot <em>Pivot</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Pivot</em>' attribute.
     * @see #getPivot()
     * @generated
     */
    void setPivot(QName value);

} // Transport
