/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import java.util.Collection;

import java.util.List;
import org.apache.axis.model.wsdd.Deployment;
import org.apache.axis.model.wsdd.GlobalConfiguration;
import org.apache.axis.model.wsdd.Handler;
import org.apache.axis.model.wsdd.Service;

import org.apache.axis.model.wsdd.Transport;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Deployment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.impl.DeploymentImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.DeploymentImpl#getGlobalConfiguration <em>Global Configuration</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.DeploymentImpl#getHandlers <em>Handlers</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.DeploymentImpl#getTransports <em>Transports</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.DeploymentImpl#getServices <em>Services</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DeploymentImpl extends EObjectImpl implements Deployment {
    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;
    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;
    /**
     * The cached value of the '{@link #getGlobalConfiguration() <em>Global Configuration</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getGlobalConfiguration()
     * @generated
     * @ordered
     */
    protected GlobalConfiguration globalConfiguration;
    /**
     * The cached value of the '{@link #getHandlers() <em>Handlers</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getHandlers()
     * @generated
     * @ordered
     */
    protected EList handlers;
    /**
     * The cached value of the '{@link #getTransports() <em>Transports</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTransports()
     * @generated
     * @ordered
     */
    protected EList transports;
    /**
     * The cached value of the '{@link #getServices() <em>Services</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getServices()
     * @generated
     * @ordered
     */
    protected EList services;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DeploymentImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WSDDPackageImpl.Literals.DEPLOYMENT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public GlobalConfiguration getGlobalConfiguration() {
        return globalConfiguration;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetGlobalConfiguration(GlobalConfiguration newGlobalConfiguration, NotificationChain msgs) {
        GlobalConfiguration oldGlobalConfiguration = globalConfiguration;
        globalConfiguration = newGlobalConfiguration;
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setGlobalConfiguration(GlobalConfiguration newGlobalConfiguration) {
        if (newGlobalConfiguration != globalConfiguration) {
            NotificationChain msgs = null;
            if (globalConfiguration != null)
                msgs = ((InternalEObject)globalConfiguration).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WSDDPackageImpl.DEPLOYMENT__GLOBAL_CONFIGURATION, null, msgs);
            if (newGlobalConfiguration != null)
                msgs = ((InternalEObject)newGlobalConfiguration).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WSDDPackageImpl.DEPLOYMENT__GLOBAL_CONFIGURATION, null, msgs);
            msgs = basicSetGlobalConfiguration(newGlobalConfiguration, msgs);
            if (msgs != null) msgs.dispatch();
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getHandlers() {
        if (handlers == null) {
            handlers = new BasicInternalEList(Handler.class);
        }
        return handlers;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getTransports() {
        if (transports == null) {
            transports = new BasicInternalEList(Transport.class);
        }
        return transports;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getServices() {
        if (services == null) {
            services = new BasicInternalEList(Service.class);
        }
        return services;
    }

    public void merge(Deployment other) {
        // TODO: very naive implementation; need more fine grained merging
        GlobalConfiguration otherGlobalConfiguration = other.getGlobalConfiguration();
        if (otherGlobalConfiguration != null) {
            setGlobalConfiguration(otherGlobalConfiguration);
        }
        getHandlers().addAll(other.getHandlers());
        getTransports().addAll(other.getTransports());
        getServices().addAll(other.getServices());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case WSDDPackageImpl.DEPLOYMENT__GLOBAL_CONFIGURATION:
                return basicSetGlobalConfiguration(null, msgs);
            case WSDDPackageImpl.DEPLOYMENT__HANDLERS:
                return ((InternalEList)getHandlers()).basicRemove(otherEnd, msgs);
            case WSDDPackageImpl.DEPLOYMENT__TRANSPORTS:
                return ((InternalEList)getTransports()).basicRemove(otherEnd, msgs);
            case WSDDPackageImpl.DEPLOYMENT__SERVICES:
                return ((InternalEList)getServices()).basicRemove(otherEnd, msgs);
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
            case WSDDPackageImpl.DEPLOYMENT__NAME:
                return getName();
            case WSDDPackageImpl.DEPLOYMENT__GLOBAL_CONFIGURATION:
                return getGlobalConfiguration();
            case WSDDPackageImpl.DEPLOYMENT__HANDLERS:
                return getHandlers();
            case WSDDPackageImpl.DEPLOYMENT__TRANSPORTS:
                return getTransports();
            case WSDDPackageImpl.DEPLOYMENT__SERVICES:
                return getServices();
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
            case WSDDPackageImpl.DEPLOYMENT__NAME:
                setName((String)newValue);
                return;
            case WSDDPackageImpl.DEPLOYMENT__GLOBAL_CONFIGURATION:
                setGlobalConfiguration((GlobalConfiguration)newValue);
                return;
            case WSDDPackageImpl.DEPLOYMENT__HANDLERS:
                getHandlers().clear();
                getHandlers().addAll((Collection)newValue);
                return;
            case WSDDPackageImpl.DEPLOYMENT__TRANSPORTS:
                getTransports().clear();
                getTransports().addAll((Collection)newValue);
                return;
            case WSDDPackageImpl.DEPLOYMENT__SERVICES:
                getServices().clear();
                getServices().addAll((Collection)newValue);
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
            case WSDDPackageImpl.DEPLOYMENT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WSDDPackageImpl.DEPLOYMENT__GLOBAL_CONFIGURATION:
                setGlobalConfiguration((GlobalConfiguration)null);
                return;
            case WSDDPackageImpl.DEPLOYMENT__HANDLERS:
                getHandlers().clear();
                return;
            case WSDDPackageImpl.DEPLOYMENT__TRANSPORTS:
                getTransports().clear();
                return;
            case WSDDPackageImpl.DEPLOYMENT__SERVICES:
                getServices().clear();
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
            case WSDDPackageImpl.DEPLOYMENT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WSDDPackageImpl.DEPLOYMENT__GLOBAL_CONFIGURATION:
                return globalConfiguration != null;
            case WSDDPackageImpl.DEPLOYMENT__HANDLERS:
                return handlers != null && !handlers.isEmpty();
            case WSDDPackageImpl.DEPLOYMENT__TRANSPORTS:
                return transports != null && !transports.isEmpty();
            case WSDDPackageImpl.DEPLOYMENT__SERVICES:
                return services != null && !services.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: ");
        result.append(name);
        result.append(')');
        return result.toString();
    }

} //DeploymentImpl
