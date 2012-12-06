/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Deployment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.Deployment#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Deployment#getGlobalConfiguration <em>Global Configuration</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Deployment#getHandlers <em>Handlers</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Deployment#getTransports <em>Transports</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Deployment#getServices <em>Services</em>}</li>
 * </ul>
 * </p>
 *
 * @model extendedMetaData="name='deployment' kind='element'"
 * @generated
 */
public interface Deployment {
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
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Deployment#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Global Configuration</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Global Configuration</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Global Configuration</em>' containment reference.
     * @see #setGlobalConfiguration(GlobalConfiguration)
     * @model containment="true"
     *        extendedMetaData="kind='element' name='globalConfiguration' namespace='##targetNamespace'"
     * @generated
     */
    GlobalConfiguration getGlobalConfiguration();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Deployment#getGlobalConfiguration <em>Global Configuration</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Global Configuration</em>' containment reference.
     * @see #getGlobalConfiguration()
     * @generated
     */
    void setGlobalConfiguration(GlobalConfiguration value);

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

    /**
     * Returns the value of the '<em><b>Transports</b></em>' containment reference list.
     * The list contents are of type {@link org.apache.axis.model.wsdd.Transport}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Transports</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Transports</em>' containment reference list.
     * @model type="org.apache.axis.model.wsdd.Transport" containment="true"
     *        extendedMetaData="name='transport' kind='element' namespace='##targetNamespace'"
     * @generated
     */
    List getTransports();

    /**
     * Returns the value of the '<em><b>Services</b></em>' containment reference list.
     * The list contents are of type {@link org.apache.axis.model.wsdd.Service}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Services</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Services</em>' containment reference list.
     * @model type="org.apache.axis.model.wsdd.Service" containment="true"
     *        extendedMetaData="name='service' kind='element' namespace='##targetNamespace'"
     * @generated
     */
    List getServices();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model otherRequired="true"
     * @generated
     */
    void merge(Deployment other);

} // Deployment
