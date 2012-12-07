/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import javax.xml.namespace.QName;
import org.apache.axis.model.wsdd.ArrayMapping;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Array Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ArrayMappingImpl#getInnerType <em>Inner Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ArrayMappingImpl extends MappingImpl implements ArrayMapping {
    /**
     * The default value of the '{@link #getInnerType() <em>Inner Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInnerType()
     * @generated
     * @ordered
     */
    protected static final QName INNER_TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getInnerType() <em>Inner Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInnerType()
     * @generated
     * @ordered
     */
    protected QName innerType = INNER_TYPE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ArrayMappingImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WSDDPackageImpl.Literals.ARRAY_MAPPING;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public QName getInnerType() {
        return innerType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInnerType(QName newInnerType) {
        innerType = newInnerType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case WSDDPackageImpl.ARRAY_MAPPING__INNER_TYPE:
                return getInnerType();
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
            case WSDDPackageImpl.ARRAY_MAPPING__INNER_TYPE:
                setInnerType((QName)newValue);
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
            case WSDDPackageImpl.ARRAY_MAPPING__INNER_TYPE:
                setInnerType(INNER_TYPE_EDEFAULT);
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
            case WSDDPackageImpl.ARRAY_MAPPING__INNER_TYPE:
                return INNER_TYPE_EDEFAULT == null ? innerType != null : !INNER_TYPE_EDEFAULT.equals(innerType);
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
        result.append(" (innerType: ");
        result.append(innerType);
        result.append(')');
        return result.toString();
    }

} //ArrayMappingImpl
