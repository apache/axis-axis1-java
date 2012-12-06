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
 * A representation of the model object '<em><b>Parameterizable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.Parameterizable#getParameters <em>Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @model
 * @generated
 */
public interface Parameterizable {
    /**
     * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
     * The list contents are of type {@link org.apache.axis.model.wsdd.Parameter}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameters</em>' containment reference list.
     * @model type="org.apache.axis.model.wsdd.Parameter" containment="true"
     *        extendedMetaData="kind='element' name='parameter' namespace='##targetNamespace'"
     * @generated
     */
    List getParameters();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model nameRequired="true" valueRequired="true"
     * @generated
     */
    void setParameter(String name, String value);

} // Parameterizable
