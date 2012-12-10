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
 * A representation of the model object '<em><b>Mapping Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.MappingContainer#getTypeMappings <em>Type Mappings</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.MappingContainer#getBeanMappings <em>Bean Mappings</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.MappingContainer#getArrayMappings <em>Array Mappings</em>}</li>
 * </ul>
 * </p>
 *
 * @model
 * @generated
 */
public interface MappingContainer {
    /**
     * Returns the value of the '<em><b>Type Mappings</b></em>' containment reference list.
     * The list contents are of type {@link org.apache.axis.model.wsdd.TypeMapping}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type Mappings</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type Mappings</em>' containment reference list.
     * @model type="org.apache.axis.model.wsdd.TypeMapping" containment="true"
     *        extendedMetaData="kind='element' name='typeMapping' namespace='##targetNamespace'"
     * @generated
     */
    List getTypeMappings();

    /**
     * Returns the value of the '<em><b>Bean Mappings</b></em>' containment reference list.
     * The list contents are of type {@link org.apache.axis.model.wsdd.BeanMapping}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Bean Mappings</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Bean Mappings</em>' containment reference list.
     * @model type="org.apache.axis.model.wsdd.BeanMapping" containment="true"
     *        extendedMetaData="kind='element' name='beanMapping' namespace='##targetNamespace'"
     * @generated
     */
    List getBeanMappings();

    /**
     * Returns the value of the '<em><b>Array Mappings</b></em>' containment reference list.
     * The list contents are of type {@link org.apache.axis.model.wsdd.ArrayMapping}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Array Mappings</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Array Mappings</em>' containment reference list.
     * @model type="org.apache.axis.model.wsdd.ArrayMapping" containment="true"
     *        extendedMetaData="kind='element' name='arrayMapping' namespace='##targetNamespace'"
     * @generated
     */
    List getArrayMappings();

} // MappingContainer
