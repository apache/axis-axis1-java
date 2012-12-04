/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import java.util.Collection;

import org.apache.axis.model.wsdd.Fault;
import org.apache.axis.model.wsdd.Operation;
import org.apache.axis.model.wsdd.OperationParameter;
import org.apache.axis.model.wsdd.WSDDPackage;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationImpl#getQname <em>Qname</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationImpl#getReturnQName <em>Return QName</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationImpl#getReturnType <em>Return Type</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationImpl#getReturnItemQName <em>Return Item QName</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationImpl#getReturnItemType <em>Return Item Type</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationImpl#getSoapAction <em>Soap Action</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationImpl#getMep <em>Mep</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationImpl#getReturnHeader <em>Return Header</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationImpl#getFaults <em>Faults</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OperationImpl extends EObjectImpl implements Operation {
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
     * The default value of the '{@link #getQname() <em>Qname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getQname()
     * @generated
     * @ordered
     */
    protected static final Object QNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getQname() <em>Qname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getQname()
     * @generated
     * @ordered
     */
    protected Object qname = QNAME_EDEFAULT;

    /**
     * The default value of the '{@link #getReturnQName() <em>Return QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnQName()
     * @generated
     * @ordered
     */
    protected static final Object RETURN_QNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getReturnQName() <em>Return QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnQName()
     * @generated
     * @ordered
     */
    protected Object returnQName = RETURN_QNAME_EDEFAULT;

    /**
     * The default value of the '{@link #getReturnType() <em>Return Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnType()
     * @generated
     * @ordered
     */
    protected static final Object RETURN_TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getReturnType() <em>Return Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnType()
     * @generated
     * @ordered
     */
    protected Object returnType = RETURN_TYPE_EDEFAULT;

    /**
     * The default value of the '{@link #getReturnItemQName() <em>Return Item QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnItemQName()
     * @generated
     * @ordered
     */
    protected static final Object RETURN_ITEM_QNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getReturnItemQName() <em>Return Item QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnItemQName()
     * @generated
     * @ordered
     */
    protected Object returnItemQName = RETURN_ITEM_QNAME_EDEFAULT;

    /**
     * The default value of the '{@link #getReturnItemType() <em>Return Item Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnItemType()
     * @generated
     * @ordered
     */
    protected static final Object RETURN_ITEM_TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getReturnItemType() <em>Return Item Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnItemType()
     * @generated
     * @ordered
     */
    protected Object returnItemType = RETURN_ITEM_TYPE_EDEFAULT;

    /**
     * The default value of the '{@link #getSoapAction() <em>Soap Action</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSoapAction()
     * @generated
     * @ordered
     */
    protected static final String SOAP_ACTION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getSoapAction() <em>Soap Action</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSoapAction()
     * @generated
     * @ordered
     */
    protected String soapAction = SOAP_ACTION_EDEFAULT;

    /**
     * The default value of the '{@link #getMep() <em>Mep</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMep()
     * @generated
     * @ordered
     */
    protected static final String MEP_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMep() <em>Mep</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMep()
     * @generated
     * @ordered
     */
    protected String mep = MEP_EDEFAULT;

    /**
     * The default value of the '{@link #getReturnHeader() <em>Return Header</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnHeader()
     * @generated
     * @ordered
     */
    protected static final Boolean RETURN_HEADER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getReturnHeader() <em>Return Header</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnHeader()
     * @generated
     * @ordered
     */
    protected Boolean returnHeader = RETURN_HEADER_EDEFAULT;

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
     * The cached value of the '{@link #getFaults() <em>Faults</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFaults()
     * @generated
     * @ordered
     */
    protected EList faults;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected OperationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WSDDPackage.Literals.OPERATION;
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
    public Object getQname() {
        return qname;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setQname(Object newQname) {
        qname = newQname;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getReturnQName() {
        return returnQName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReturnQName(Object newReturnQName) {
        returnQName = newReturnQName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getReturnType() {
        return returnType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReturnType(Object newReturnType) {
        returnType = newReturnType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getReturnItemQName() {
        return returnItemQName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReturnItemQName(Object newReturnItemQName) {
        returnItemQName = newReturnItemQName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getReturnItemType() {
        return returnItemType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReturnItemType(Object newReturnItemType) {
        returnItemType = newReturnItemType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSoapAction(String newSoapAction) {
        soapAction = newSoapAction;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getMep() {
        return mep;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMep(String newMep) {
        mep = newMep;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getReturnHeader() {
        return returnHeader;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReturnHeader(Boolean newReturnHeader) {
        returnHeader = newReturnHeader;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getParameters() {
        if (parameters == null) {
            parameters = new BasicInternalEList(OperationParameter.class);
        }
        return parameters;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getFaults() {
        if (faults == null) {
            faults = new BasicInternalEList(Fault.class);
        }
        return faults;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case WSDDPackage.OPERATION__PARAMETERS:
                return ((InternalEList)getParameters()).basicRemove(otherEnd, msgs);
            case WSDDPackage.OPERATION__FAULTS:
                return ((InternalEList)getFaults()).basicRemove(otherEnd, msgs);
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
            case WSDDPackage.OPERATION__NAME:
                return getName();
            case WSDDPackage.OPERATION__QNAME:
                return getQname();
            case WSDDPackage.OPERATION__RETURN_QNAME:
                return getReturnQName();
            case WSDDPackage.OPERATION__RETURN_TYPE:
                return getReturnType();
            case WSDDPackage.OPERATION__RETURN_ITEM_QNAME:
                return getReturnItemQName();
            case WSDDPackage.OPERATION__RETURN_ITEM_TYPE:
                return getReturnItemType();
            case WSDDPackage.OPERATION__SOAP_ACTION:
                return getSoapAction();
            case WSDDPackage.OPERATION__MEP:
                return getMep();
            case WSDDPackage.OPERATION__RETURN_HEADER:
                return getReturnHeader();
            case WSDDPackage.OPERATION__PARAMETERS:
                return getParameters();
            case WSDDPackage.OPERATION__FAULTS:
                return getFaults();
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
            case WSDDPackage.OPERATION__NAME:
                setName((String)newValue);
                return;
            case WSDDPackage.OPERATION__QNAME:
                setQname(newValue);
                return;
            case WSDDPackage.OPERATION__RETURN_QNAME:
                setReturnQName(newValue);
                return;
            case WSDDPackage.OPERATION__RETURN_TYPE:
                setReturnType(newValue);
                return;
            case WSDDPackage.OPERATION__RETURN_ITEM_QNAME:
                setReturnItemQName(newValue);
                return;
            case WSDDPackage.OPERATION__RETURN_ITEM_TYPE:
                setReturnItemType(newValue);
                return;
            case WSDDPackage.OPERATION__SOAP_ACTION:
                setSoapAction((String)newValue);
                return;
            case WSDDPackage.OPERATION__MEP:
                setMep((String)newValue);
                return;
            case WSDDPackage.OPERATION__RETURN_HEADER:
                setReturnHeader((Boolean)newValue);
                return;
            case WSDDPackage.OPERATION__PARAMETERS:
                getParameters().clear();
                getParameters().addAll((Collection)newValue);
                return;
            case WSDDPackage.OPERATION__FAULTS:
                getFaults().clear();
                getFaults().addAll((Collection)newValue);
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
            case WSDDPackage.OPERATION__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WSDDPackage.OPERATION__QNAME:
                setQname(QNAME_EDEFAULT);
                return;
            case WSDDPackage.OPERATION__RETURN_QNAME:
                setReturnQName(RETURN_QNAME_EDEFAULT);
                return;
            case WSDDPackage.OPERATION__RETURN_TYPE:
                setReturnType(RETURN_TYPE_EDEFAULT);
                return;
            case WSDDPackage.OPERATION__RETURN_ITEM_QNAME:
                setReturnItemQName(RETURN_ITEM_QNAME_EDEFAULT);
                return;
            case WSDDPackage.OPERATION__RETURN_ITEM_TYPE:
                setReturnItemType(RETURN_ITEM_TYPE_EDEFAULT);
                return;
            case WSDDPackage.OPERATION__SOAP_ACTION:
                setSoapAction(SOAP_ACTION_EDEFAULT);
                return;
            case WSDDPackage.OPERATION__MEP:
                setMep(MEP_EDEFAULT);
                return;
            case WSDDPackage.OPERATION__RETURN_HEADER:
                setReturnHeader(RETURN_HEADER_EDEFAULT);
                return;
            case WSDDPackage.OPERATION__PARAMETERS:
                getParameters().clear();
                return;
            case WSDDPackage.OPERATION__FAULTS:
                getFaults().clear();
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
            case WSDDPackage.OPERATION__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WSDDPackage.OPERATION__QNAME:
                return QNAME_EDEFAULT == null ? qname != null : !QNAME_EDEFAULT.equals(qname);
            case WSDDPackage.OPERATION__RETURN_QNAME:
                return RETURN_QNAME_EDEFAULT == null ? returnQName != null : !RETURN_QNAME_EDEFAULT.equals(returnQName);
            case WSDDPackage.OPERATION__RETURN_TYPE:
                return RETURN_TYPE_EDEFAULT == null ? returnType != null : !RETURN_TYPE_EDEFAULT.equals(returnType);
            case WSDDPackage.OPERATION__RETURN_ITEM_QNAME:
                return RETURN_ITEM_QNAME_EDEFAULT == null ? returnItemQName != null : !RETURN_ITEM_QNAME_EDEFAULT.equals(returnItemQName);
            case WSDDPackage.OPERATION__RETURN_ITEM_TYPE:
                return RETURN_ITEM_TYPE_EDEFAULT == null ? returnItemType != null : !RETURN_ITEM_TYPE_EDEFAULT.equals(returnItemType);
            case WSDDPackage.OPERATION__SOAP_ACTION:
                return SOAP_ACTION_EDEFAULT == null ? soapAction != null : !SOAP_ACTION_EDEFAULT.equals(soapAction);
            case WSDDPackage.OPERATION__MEP:
                return MEP_EDEFAULT == null ? mep != null : !MEP_EDEFAULT.equals(mep);
            case WSDDPackage.OPERATION__RETURN_HEADER:
                return RETURN_HEADER_EDEFAULT == null ? returnHeader != null : !RETURN_HEADER_EDEFAULT.equals(returnHeader);
            case WSDDPackage.OPERATION__PARAMETERS:
                return parameters != null && !parameters.isEmpty();
            case WSDDPackage.OPERATION__FAULTS:
                return faults != null && !faults.isEmpty();
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
        result.append(", qname: ");
        result.append(qname);
        result.append(", returnQName: ");
        result.append(returnQName);
        result.append(", returnType: ");
        result.append(returnType);
        result.append(", returnItemQName: ");
        result.append(returnItemQName);
        result.append(", returnItemType: ");
        result.append(returnItemType);
        result.append(", soapAction: ");
        result.append(soapAction);
        result.append(", mep: ");
        result.append(mep);
        result.append(", returnHeader: ");
        result.append(returnHeader);
        result.append(')');
        return result.toString();
    }

} //OperationImpl
