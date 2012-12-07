/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import javax.xml.namespace.QName;
import org.apache.axis.model.wsdd.OperationParameter;
import org.apache.axis.model.wsdd.ParameterMode;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Operation Parameter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationParameterImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationParameterImpl#getQname <em>Qname</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationParameterImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationParameterImpl#getMode <em>Mode</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationParameterImpl#getInHeader <em>In Header</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationParameterImpl#getOutHeader <em>Out Header</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.OperationParameterImpl#getItemQName <em>Item QName</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OperationParameterImpl extends EObjectImpl implements OperationParameter {
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
     * The default value of the '{@link #getMode() <em>Mode</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMode()
     * @generated
     * @ordered
     */
    protected static final ParameterMode MODE_EDEFAULT = ParameterMode.IN_LITERAL;

    /**
     * The cached value of the '{@link #getMode() <em>Mode</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMode()
     * @generated
     * @ordered
     */
    protected ParameterMode mode = MODE_EDEFAULT;

    /**
     * The default value of the '{@link #getInHeader() <em>In Header</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInHeader()
     * @generated
     * @ordered
     */
    protected static final Boolean IN_HEADER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getInHeader() <em>In Header</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInHeader()
     * @generated
     * @ordered
     */
    protected Boolean inHeader = IN_HEADER_EDEFAULT;

    /**
     * The default value of the '{@link #getOutHeader() <em>Out Header</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOutHeader()
     * @generated
     * @ordered
     */
    protected static final Boolean OUT_HEADER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getOutHeader() <em>Out Header</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOutHeader()
     * @generated
     * @ordered
     */
    protected Boolean outHeader = OUT_HEADER_EDEFAULT;

    /**
     * The default value of the '{@link #getItemQName() <em>Item QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getItemQName()
     * @generated
     * @ordered
     */
    protected static final QName ITEM_QNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getItemQName() <em>Item QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getItemQName()
     * @generated
     * @ordered
     */
    protected QName itemQName = ITEM_QNAME_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected OperationParameterImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WSDDPackageImpl.Literals.OPERATION_PARAMETER;
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
    public ParameterMode getMode() {
        return mode;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMode(ParameterMode newMode) {
        mode = newMode == null ? MODE_EDEFAULT : newMode;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getInHeader() {
        return inHeader;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInHeader(Boolean newInHeader) {
        inHeader = newInHeader;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getOutHeader() {
        return outHeader;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOutHeader(Boolean newOutHeader) {
        outHeader = newOutHeader;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public QName getItemQName() {
        return itemQName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setItemQName(QName newItemQName) {
        itemQName = newItemQName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case WSDDPackageImpl.OPERATION_PARAMETER__NAME:
                return getName();
            case WSDDPackageImpl.OPERATION_PARAMETER__QNAME:
                return getQname();
            case WSDDPackageImpl.OPERATION_PARAMETER__TYPE:
                return getType();
            case WSDDPackageImpl.OPERATION_PARAMETER__MODE:
                return getMode();
            case WSDDPackageImpl.OPERATION_PARAMETER__IN_HEADER:
                return getInHeader();
            case WSDDPackageImpl.OPERATION_PARAMETER__OUT_HEADER:
                return getOutHeader();
            case WSDDPackageImpl.OPERATION_PARAMETER__ITEM_QNAME:
                return getItemQName();
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
            case WSDDPackageImpl.OPERATION_PARAMETER__NAME:
                setName((String)newValue);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__QNAME:
                setQname((QName)newValue);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__TYPE:
                setType((QName)newValue);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__MODE:
                setMode((ParameterMode)newValue);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__IN_HEADER:
                setInHeader((Boolean)newValue);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__OUT_HEADER:
                setOutHeader((Boolean)newValue);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__ITEM_QNAME:
                setItemQName((QName)newValue);
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
            case WSDDPackageImpl.OPERATION_PARAMETER__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__QNAME:
                setQname(QNAME_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__TYPE:
                setType(TYPE_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__MODE:
                setMode(MODE_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__IN_HEADER:
                setInHeader(IN_HEADER_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__OUT_HEADER:
                setOutHeader(OUT_HEADER_EDEFAULT);
                return;
            case WSDDPackageImpl.OPERATION_PARAMETER__ITEM_QNAME:
                setItemQName(ITEM_QNAME_EDEFAULT);
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
            case WSDDPackageImpl.OPERATION_PARAMETER__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WSDDPackageImpl.OPERATION_PARAMETER__QNAME:
                return QNAME_EDEFAULT == null ? qname != null : !QNAME_EDEFAULT.equals(qname);
            case WSDDPackageImpl.OPERATION_PARAMETER__TYPE:
                return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
            case WSDDPackageImpl.OPERATION_PARAMETER__MODE:
                return mode != MODE_EDEFAULT;
            case WSDDPackageImpl.OPERATION_PARAMETER__IN_HEADER:
                return IN_HEADER_EDEFAULT == null ? inHeader != null : !IN_HEADER_EDEFAULT.equals(inHeader);
            case WSDDPackageImpl.OPERATION_PARAMETER__OUT_HEADER:
                return OUT_HEADER_EDEFAULT == null ? outHeader != null : !OUT_HEADER_EDEFAULT.equals(outHeader);
            case WSDDPackageImpl.OPERATION_PARAMETER__ITEM_QNAME:
                return ITEM_QNAME_EDEFAULT == null ? itemQName != null : !ITEM_QNAME_EDEFAULT.equals(itemQName);
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
        result.append(", type: ");
        result.append(type);
        result.append(", mode: ");
        result.append(mode);
        result.append(", inHeader: ");
        result.append(inHeader);
        result.append(", outHeader: ");
        result.append(outHeader);
        result.append(", itemQName: ");
        result.append(itemQName);
        result.append(')');
        return result.toString();
    }

} //OperationParameterImpl
