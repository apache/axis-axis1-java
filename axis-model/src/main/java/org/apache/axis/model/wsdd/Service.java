/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Service</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.Service#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Service#getProvider <em>Provider</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Service#getUse <em>Use</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Service#getStyle <em>Style</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Service#getNamespaces <em>Namespaces</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.Service#getOperations <em>Operations</em>}</li>
 * </ul>
 * </p>
 *
 * @model
 * @generated
 */
public interface Service extends DeployableItem, MappingContainer {
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
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Service#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Provider</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Provider</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Provider</em>' attribute.
     * @see #setProvider(QName)
     * @model dataType="org.apache.axis.model.xml.QName"
     * @generated
     */
    QName getProvider();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Service#getProvider <em>Provider</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Provider</em>' attribute.
     * @see #getProvider()
     * @generated
     */
    void setProvider(QName value);

    /**
     * Returns the value of the '<em><b>Use</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Use</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Use</em>' attribute.
     * @see #setUse(Use)
     * @model dataType="org.apache.axis.model.soap.Use"
     * @generated
     */
    Use getUse();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Service#getUse <em>Use</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Use</em>' attribute.
     * @see #getUse()
     * @generated
     */
    void setUse(Use value);

    /**
     * Returns the value of the '<em><b>Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Style</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Style</em>' attribute.
     * @see #setStyle(Style)
     * @model dataType="org.apache.axis.model.soap.Style"
     * @generated
     */
    Style getStyle();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.Service#getStyle <em>Style</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Style</em>' attribute.
     * @see #getStyle()
     * @generated
     */
    void setStyle(Style value);

    /**
     * Returns the value of the '<em><b>Namespaces</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Namespaces</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Namespaces</em>' attribute list.
     * @model extendedMetaData="kind='element' name='namespace' namespace='##targetNamespace'"
     * @generated
     */
    List getNamespaces();

    /**
     * Returns the value of the '<em><b>Operations</b></em>' containment reference list.
     * The list contents are of type {@link org.apache.axis.model.wsdd.Operation}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Operations</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Operations</em>' containment reference list.
     * @model type="org.apache.axis.model.wsdd.Operation" containment="true"
     *        extendedMetaData="kind='element' name='operation' namespace='##targetNamespace'"
     * @generated
     */
    List getOperations();

} // Service
