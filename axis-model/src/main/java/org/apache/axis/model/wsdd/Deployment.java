/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Deployment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.Deployment#getServices <em>Services</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.apache.axis.model.wsdd.WSDDPackage#getDeployment()
 * @model extendedMetaData="name='deployment' kind='element'"
 * @generated
 */
public interface Deployment extends EObject {
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
     * @see org.apache.axis.model.wsdd.WSDDPackage#getDeployment_Services()
     * @model type="org.apache.axis.model.wsdd.Service" containment="true"
     *        extendedMetaData="name='service' kind='element' namespace='##targetNamespace'"
     * @generated
     */
    EList getServices();

} // Deployment
