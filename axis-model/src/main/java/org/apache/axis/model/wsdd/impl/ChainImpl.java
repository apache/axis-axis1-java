/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import java.util.Collection;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.model.wsdd.Chain;

import org.apache.axis.model.wsdd.Handler;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Flow</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ChainImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ChainImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ChainImpl#getHandlers <em>Handlers</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ChainImpl extends EObjectImpl implements Chain {
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
     * The default value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected static final QName TYPE_EDEFAULT = null;
    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected QName type = TYPE_EDEFAULT;
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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ChainImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WSDDPackageImpl.Literals.CHAIN;
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
    public QName getType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setType(QName newType) {
        type = newType;
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
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case WSDDPackageImpl.CHAIN__HANDLERS:
                return ((InternalEList)getHandlers()).basicRemove(otherEnd, msgs);
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
            case WSDDPackageImpl.CHAIN__NAME:
                return getName();
            case WSDDPackageImpl.CHAIN__TYPE:
                return getType();
            case WSDDPackageImpl.CHAIN__HANDLERS:
                return getHandlers();
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
            case WSDDPackageImpl.CHAIN__NAME:
                setName((String)newValue);
                return;
            case WSDDPackageImpl.CHAIN__TYPE:
                setType((QName)newValue);
                return;
            case WSDDPackageImpl.CHAIN__HANDLERS:
                getHandlers().clear();
                getHandlers().addAll((Collection)newValue);
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
            case WSDDPackageImpl.CHAIN__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WSDDPackageImpl.CHAIN__TYPE:
                setType(TYPE_EDEFAULT);
                return;
            case WSDDPackageImpl.CHAIN__HANDLERS:
                getHandlers().clear();
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
            case WSDDPackageImpl.CHAIN__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WSDDPackageImpl.CHAIN__TYPE:
                return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
            case WSDDPackageImpl.CHAIN__HANDLERS:
                return handlers != null && !handlers.isEmpty();
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
        result.append(", type: ");
        result.append(type);
        result.append(')');
        return result.toString();
    }

} //FlowImpl
