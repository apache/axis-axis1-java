/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Type Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.TypeMapping#getSerializer <em>Serializer</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.TypeMapping#getDeserializer <em>Deserializer</em>}</li>
 * </ul>
 * </p>
 *
 * @model
 * @generated
 */
public interface TypeMapping extends Mapping {
    /**
     * Returns the value of the '<em><b>Serializer</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Serializer</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Serializer</em>' attribute.
     * @see #setSerializer(String)
     * @model
     * @generated
     */
    String getSerializer();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.TypeMapping#getSerializer <em>Serializer</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Serializer</em>' attribute.
     * @see #getSerializer()
     * @generated
     */
    void setSerializer(String value);

    /**
     * Returns the value of the '<em><b>Deserializer</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Deserializer</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Deserializer</em>' attribute.
     * @see #setDeserializer(String)
     * @model
     * @generated
     */
    String getDeserializer();

    /**
     * Sets the value of the '{@link org.apache.axis.model.wsdd.TypeMapping#getDeserializer <em>Deserializer</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Deserializer</em>' attribute.
     * @see #getDeserializer()
     * @generated
     */
    void setDeserializer(String value);

} // TypeMapping
