/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import org.apache.axis.model.wsdd.DeployableItem;

import org.apache.axis.model.wsdd.Flow;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Deployable Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.impl.DeployableItemImpl#getRequestFlow <em>Request Flow</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.DeployableItemImpl#getResponseFlow <em>Response Flow</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class DeployableItemImpl extends ParameterizableImpl implements DeployableItem {
    /**
     * The cached value of the '{@link #getRequestFlow() <em>Request Flow</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRequestFlow()
     * @generated
     * @ordered
     */
    protected Flow requestFlow;
    /**
     * The cached value of the '{@link #getResponseFlow() <em>Response Flow</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getResponseFlow()
     * @generated
     * @ordered
     */
    protected Flow responseFlow;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DeployableItemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WSDDPackageImpl.Literals.DEPLOYABLE_ITEM;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Flow getRequestFlow() {
        if (requestFlow != null && ((EObject)requestFlow).eIsProxy()) {
            InternalEObject oldRequestFlow = (InternalEObject)requestFlow;
            requestFlow = (Flow)eResolveProxy(oldRequestFlow);
            if (requestFlow != oldRequestFlow) {
            }
        }
        return requestFlow;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Flow basicGetRequestFlow() {
        return requestFlow;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRequestFlow(Flow newRequestFlow) {
        requestFlow = newRequestFlow;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Flow getResponseFlow() {
        if (responseFlow != null && ((EObject)responseFlow).eIsProxy()) {
            InternalEObject oldResponseFlow = (InternalEObject)responseFlow;
            responseFlow = (Flow)eResolveProxy(oldResponseFlow);
            if (responseFlow != oldResponseFlow) {
            }
        }
        return responseFlow;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Flow basicGetResponseFlow() {
        return responseFlow;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setResponseFlow(Flow newResponseFlow) {
        responseFlow = newResponseFlow;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case WSDDPackageImpl.DEPLOYABLE_ITEM__REQUEST_FLOW:
                if (resolve) return getRequestFlow();
                return basicGetRequestFlow();
            case WSDDPackageImpl.DEPLOYABLE_ITEM__RESPONSE_FLOW:
                if (resolve) return getResponseFlow();
                return basicGetResponseFlow();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case WSDDPackageImpl.DEPLOYABLE_ITEM__REQUEST_FLOW:
                setRequestFlow((Flow)newValue);
                return;
            case WSDDPackageImpl.DEPLOYABLE_ITEM__RESPONSE_FLOW:
                setResponseFlow((Flow)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eUnset(int featureID) {
        switch (featureID) {
            case WSDDPackageImpl.DEPLOYABLE_ITEM__REQUEST_FLOW:
                setRequestFlow((Flow)null);
                return;
            case WSDDPackageImpl.DEPLOYABLE_ITEM__RESPONSE_FLOW:
                setResponseFlow((Flow)null);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case WSDDPackageImpl.DEPLOYABLE_ITEM__REQUEST_FLOW:
                return requestFlow != null;
            case WSDDPackageImpl.DEPLOYABLE_ITEM__RESPONSE_FLOW:
                return responseFlow != null;
        }
        return super.eIsSet(featureID);
    }

} //DeployableItemImpl
