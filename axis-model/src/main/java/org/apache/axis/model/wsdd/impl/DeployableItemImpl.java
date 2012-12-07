/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import org.apache.axis.model.wsdd.DeployableItem;

import org.apache.axis.model.wsdd.Flow;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;

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
     * The cached value of the '{@link #getRequestFlow() <em>Request Flow</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRequestFlow()
     * @generated
     * @ordered
     */
    protected Flow requestFlow;
    /**
     * The cached value of the '{@link #getResponseFlow() <em>Response Flow</em>}' containment reference.
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
        return requestFlow;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetRequestFlow(Flow newRequestFlow, NotificationChain msgs) {
        Flow oldRequestFlow = requestFlow;
        requestFlow = newRequestFlow;
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRequestFlow(Flow newRequestFlow) {
        if (newRequestFlow != requestFlow) {
            NotificationChain msgs = null;
            if (requestFlow != null)
                msgs = ((InternalEObject)requestFlow).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WSDDPackageImpl.DEPLOYABLE_ITEM__REQUEST_FLOW, null, msgs);
            if (newRequestFlow != null)
                msgs = ((InternalEObject)newRequestFlow).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WSDDPackageImpl.DEPLOYABLE_ITEM__REQUEST_FLOW, null, msgs);
            msgs = basicSetRequestFlow(newRequestFlow, msgs);
            if (msgs != null) msgs.dispatch();
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Flow getResponseFlow() {
        return responseFlow;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetResponseFlow(Flow newResponseFlow, NotificationChain msgs) {
        Flow oldResponseFlow = responseFlow;
        responseFlow = newResponseFlow;
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setResponseFlow(Flow newResponseFlow) {
        if (newResponseFlow != responseFlow) {
            NotificationChain msgs = null;
            if (responseFlow != null)
                msgs = ((InternalEObject)responseFlow).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WSDDPackageImpl.DEPLOYABLE_ITEM__RESPONSE_FLOW, null, msgs);
            if (newResponseFlow != null)
                msgs = ((InternalEObject)newResponseFlow).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WSDDPackageImpl.DEPLOYABLE_ITEM__RESPONSE_FLOW, null, msgs);
            msgs = basicSetResponseFlow(newResponseFlow, msgs);
            if (msgs != null) msgs.dispatch();
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case WSDDPackageImpl.DEPLOYABLE_ITEM__REQUEST_FLOW:
                return basicSetRequestFlow(null, msgs);
            case WSDDPackageImpl.DEPLOYABLE_ITEM__RESPONSE_FLOW:
                return basicSetResponseFlow(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case WSDDPackageImpl.DEPLOYABLE_ITEM__REQUEST_FLOW:
                return getRequestFlow();
            case WSDDPackageImpl.DEPLOYABLE_ITEM__RESPONSE_FLOW:
                return getResponseFlow();
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
