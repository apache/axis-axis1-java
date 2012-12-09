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
 * A representation of the model object '<em><b>Flow</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.Chain#getHandlers <em>Handlers</em>}</li>
 * </ul>
 * </p>
 *
 * @model
 * @generated
 */
public interface Chain {

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
