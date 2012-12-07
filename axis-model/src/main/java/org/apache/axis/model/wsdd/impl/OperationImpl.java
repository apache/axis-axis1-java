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
import org.apache.axis.model.wsdd.Fault;
import org.apache.axis.model.wsdd.Operation;
import org.apache.axis.model.wsdd.OperationParameter;

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
    protected static final QName QNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getQname() <em>Qname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getQname()
     * @generated
     * @ordered
     */
    protected QName qname = QNAME_EDEFAULT;

    /**
     * The default value of the '{@link #getReturnQName() <em>Return QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnQName()
     * @generated
     * @ordered
     */
    protected static final QName RETURN_QNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getReturnQName() <em>Return QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnQName()
     * @generated
     * @ordered
     */
    protected QName returnQName = RETURN_QNAME_EDEFAULT;

    /**
     * The default value of the '{@link #getReturnType() <em>Return Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnType()
     * @generated
     * @ordered
     */
    protected static final QName RETURN_TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getReturnType() <em>Return Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnType()
     * @generated
     * @ordered
     */
    protected QName returnType = RETURN_TYPE_EDEFAULT;

    /**
     * The default value of the '{@link #getReturnItemQName() <em>Return Item QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnItemQName()
     * @generated
     * @ordered
     */
    protected static final QName RETURN_ITEM_QNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getReturnItemQName() <em>Return Item QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnItemQName()
     * @generated
     * @ordered
     */
    protected QName returnItemQName = RETURN_ITEM_QNAME_EDEFAULT;

    /**
     * The default value of the '{@link #getReturnItemType() <em>Return Item Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnItemType()
     * @generated
     * @ordered
     */
    protected static final QName RETURN_ITEM_TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getReturnItemType() <em>Return Item Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnItemType()
     * @generated
     * @ordered
     */
    protected QName returnItemType = RETURN_ITEM_TYPE_EDEFAULT;

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
        return WSDDPackageImpl.Literals.OPERATION;
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
    public QName getQname() {
        return qname;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setQname(QName newQname) {
        qname = newQname;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public QName getReturnQName() {
        return returnQName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReturnQName(QName newReturnQName) {
        returnQName = newReturnQName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public QName getReturnType() {
        return returnType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReturnType(QName newReturnType) {
        returnType = newReturnType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public QName getReturnItemQName() {
        return returnItemQName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReturnItemQName(QName newReturnItemQName) {
        returnItemQName = newReturnItemQName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public QName getReturnItemType() {
        return returnItemType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReturnItemType(QName newReturnItemType) {
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
    public List getParameters() {
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
    public List getFaults() {
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
            case WSDDPackageImpl.OPERATION__PARAMETERS:
                return ((InternalEList)getParameters()).basicRemove(otherEnd, msgs);
            case WSDDPackageImpl.OPERATION__FAULTS:
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
            case WSDDPackageImpl.OPERATION__NAME:
                return getName();
            case WSDDPackageImpl.OPERATION__QNAME:
                return getQname();
            case WSDDPackageImpl.OPERATION__RETURN_QNAME:
                return getReturnQName();
            case WSDDPackageImpl.OPERATION__RETURN_TYPE:
                return getReturnType();
            case WSDDPackageImpl.OPERATION__RETURN_ITEM_QNAME:
                return getReturnItemQName();
            case WSDDPackageImpl.OPERATION__RETURN_ITEM_TYPE:
                return getReturnItemType();
            case WSDDPackageImpl.OPERATION__SOAP_ACTION:
                return getSoapAction();
            case WSDDPackageImpl.OPERATION__MEP:
                return getMep();
            case WSDDPackageImpl.OPERATION__RETURN_HEADER:
                return getReturnHeader();
            case WSDDPackageImpl.OPERATION__PARAMETERS:
                return getParameters();
            case WSDDPackageImpl.OPERATION__FAULTS:
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
            case WSDDPackageImpl.OPERATION__NAME:
                setName((String)newValue);
                return;
            case WSDDPackageImpl.OPERATION__QNAME:
                setQname((QName)newValue);
                return;
            case WSDDPackageImpl.OPERATION__RETURN_QNAME:
                setReturnQName((QName)newValue);
                return;
            case WSDDPackageImpl.OPERATION__RETURN_TYPE:
                setReturnType((QName)newValue);
                return;
            case WSDDPackageImpl.OPERATION__RETURN_ITEM_QNAME:
                setReturnItemQName((QName)newValue);
                return;
            case WSDDPackageImpl.OPERATION__RETURN_ITEM_TYPE:
                setReturnItemType((QName)newValue);
                return;
            case WSDDPackageImpl.OPERATION__SOAP_ACTION:
                setSoapAction((String)newValue);
                return;
            case WSDDPackageImpl.OPERATION__MEP:
                setMep((String)newValue);
                return;
            case WSDDPackageImpl.OPERATION__RETURN_HEADER:
                setReturnHeader((Boolean)newValue);
                return;
            case WSDDPackageImpl.OPERATION__PARAMETERS:
                getParameters().clear();
                getParameters().addAll((Collection)newValue);
                return;
            case WSDDPackageImpl.OPERATION__FAULTS:
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
            case WSDDPackageImpl.OPERATION__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION__QNAME:
                setQname(QNAME_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION__RETURN_QNAME:
                setReturnQName(RETURN_QNAME_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION__RETURN_TYPE:
                setReturnType(RETURN_TYPE_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION__RETURN_ITEM_QNAME:
                setReturnItemQName(RETURN_ITEM_QNAME_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION__RETURN_ITEM_TYPE:
                setReturnItemType(RETURN_ITEM_TYPE_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION__SOAP_ACTION:
                setSoapAction(SOAP_ACTION_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION__MEP:
                setMep(MEP_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION__RETURN_HEADER:
                setReturnHeader(RETURN_HEADER_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION__PARAMETERS:
                getParameters().clear();
                return;
            case WSDDPackageImpl.OPERATION__FAULTS:
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
            case WSDDPackageImpl.OPERATION__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WSDDPackageImpl.OPERATION__QNAME:
                return QNAME_EDEFAULT == null ? qname != null : !QNAME_EDEFAULT.equals(qname);
            case WSDDPackageImpl.OPERATION__RETURN_QNAME:
                return RETURN_QNAME_EDEFAULT == null ? returnQName != null : !RETURN_QNAME_EDEFAULT.equals(returnQName);
            case WSDDPackageImpl.OPERATION__RETURN_TYPE:
                return RETURN_TYPE_EDEFAULT == null ? returnType != null : !RETURN_TYPE_EDEFAULT.equals(returnType);
            case WSDDPackageImpl.OPERATION__RETURN_ITEM_QNAME:
                return RETURN_ITEM_QNAME_EDEFAULT == null ? returnItemQName != null : !RETURN_ITEM_QNAME_EDEFAULT.equals(returnItemQName);
            case WSDDPackageImpl.OPERATION__RETURN_ITEM_TYPE:
                return RETURN_ITEM_TYPE_EDEFAULT == null ? returnItemType != null : !RETURN_ITEM_TYPE_EDEFAULT.equals(returnItemType);
            case WSDDPackageImpl.OPERATION__SOAP_ACTION:
                return SOAP_ACTION_EDEFAULT == null ? soapAction != null : !SOAP_ACTION_EDEFAULT.equals(soapAction);
            case WSDDPackageImpl.OPERATION__MEP:
                return MEP_EDEFAULT == null ? mep != null : !MEP_EDEFAULT.equals(mep);
            case WSDDPackageImpl.OPERATION__RETURN_HEADER:
                return RETURN_HEADER_EDEFAULT == null ? returnHeader != null : !RETURN_HEADER_EDEFAULT.equals(returnHeader);
            case WSDDPackageImpl.OPERATION__PARAMETERS:
                return parameters != null && !parameters.isEmpty();
            case WSDDPackageImpl.OPERATION__FAULTS:
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
