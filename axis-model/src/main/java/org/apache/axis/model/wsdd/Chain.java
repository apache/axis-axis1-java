/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;

import java.util.List;
import javax.xml.namespace.QName;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Flow</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.Chain#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Chain#getType <em>Type</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Chain#getHandlers <em>Handlers</em>}</li>
 * </ul>
 * </p>
 *
 * @model
 * @generated
 */
public interface Chain {

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
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Chain#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

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
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Chain#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType(QName value);

    /**
     * Returns the value of the '<em><b>Handlers</b></em>' containment reference list.
     * The list contents are of type {@link org.apache.axis.model.wsdd.Handler}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Handlers</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Handlers</em>' containment reference list.
     * @model type="org.apache.axis.model.wsdd.Handler" containment="true"
     *        extendedMetaData="name='handler' kind='element' namespace='##targetNamespace'"
     * @generated
     */
    List getHandlers();
} // Flow
