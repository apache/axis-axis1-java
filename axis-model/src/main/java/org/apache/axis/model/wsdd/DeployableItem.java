/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Deployable Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.DeployableItem#getRequestFlow <em>Request Flow</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.DeployableItem#getResponseFlow <em>Response Flow</em>}</li>
 * </ul>
 * </p>
 *
 * @model abstract="true"
 * @generated
 */
public interface DeployableItem extends Parameterizable {
    /**
     * Returns the value of the '<em><b>Request Flow</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Request Flow</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Request Flow</em>' containment reference.
     * @see #setRequestFlow(Chain)
     * @model containment="true"
     *        extendedMetaData="kind='element' name='requestFlow' namespace='##targetNamespace'"
     * @generated
     */
    Chain getRequestFlow();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.DeployableItem#getRequestFlow <em>Request Flow</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Request Flow</em>' containment reference.
     * @see #getRequestFlow()
     * @generated
     */
    void setRequestFlow(Chain value);

    /**
     * Returns the value of the '<em><b>Response Flow</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Response Flow</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Response Flow</em>' containment reference.
     * @see #setResponseFlow(Chain)
     * @model containment="true"
     *        extendedMetaData="kind='element' name='responseFlow' namespace='##targetNamespace'"
     * @generated
     */
    Chain getResponseFlow();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.DeployableItem#getResponseFlow <em>Response Flow</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Response Flow</em>' containment reference.
     * @see #getResponseFlow()
     * @generated
     */
    void setResponseFlow(Chain value);

} // DeployableItem
