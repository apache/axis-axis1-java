/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.model.wsdd.Parameter;
import org.apache.axis.model.wsdd.Parameterizable;
import org.apache.axis.model.wsdd.WSDDFactory;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parameterizable</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ParameterizableImpl#getParameters <em>Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ParameterizableImpl extends EObjectImpl implements Parameterizable {
    /**
     * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getParameters()
     * @generated
     * @ordered
     */
    protected EList parameters;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ParameterizableImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WSDDPackageImpl.Literals.PARAMETERIZABLE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getParameters() {
        if (parameters == null) {
            parameters = new BasicInternalEList(Parameter.class);
        }
        return parameters;
    }

    public void setParameter(String name, String value) {
        for (Iterator it = getParameters().iterator(); it.hasNext(); ) {
            Parameter param = (Parameter)it.next();
            if (name.equals(param.getName())) {
                param.setValue(value);
                return;
            }
        }
        Parameter param = WSDDFactory.INSTANCE.createParameter();
        param.setName(name);
        param.setValue(value);
        getParameters().add(param);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case WSDDPackageImpl.PARAMETERIZABLE__PARAMETERS:
                return ((InternalEList)getParameters()).basicRemove(otherEnd, msgs);
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
            case WSDDPackageImpl.PARAMETERIZABLE__PARAMETERS:
                return getParameters();
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
            case WSDDPackageImpl.PARAMETERIZABLE__PARAMETERS:
                getParameters().clear();
                getParameters().addAll((Collection)newValue);
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
            case WSDDPackageImpl.PARAMETERIZABLE__PARAMETERS:
                getParameters().clear();
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
            case WSDDPackageImpl.PARAMETERIZABLE__PARAMETERS:
                return parameters != null && !parameters.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //ParameterizableImpl
