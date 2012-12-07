/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import javax.xml.namespace.QName;
import org.apache.axis.model.wsdd.Mapping;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.impl.MappingImpl#getQname <em>Qname</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.MappingImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.MappingImpl#getEncodingStyle <em>Encoding Style</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class MappingImpl extends EObjectImpl implements Mapping {
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
     * The default value of the '{@link #getEncodingStyle() <em>Encoding Style</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEncodingStyle()
     * @generated
     * @ordered
     */
    protected static final String ENCODING_STYLE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEncodingStyle() <em>Encoding Style</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEncodingStyle()
     * @generated
     * @ordered
     */
    protected String encodingStyle = ENCODING_STYLE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MappingImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WSDDPackageImpl.Literals.MAPPING;
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
    public String getEncodingStyle() {
        return encodingStyle;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEncodingStyle(String newEncodingStyle) {
        encodingStyle = newEncodingStyle;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case WSDDPackageImpl.MAPPING__QNAME:
                return getQname();
            case WSDDPackageImpl.MAPPING__TYPE:
                return getType();
            case WSDDPackageImpl.MAPPING__ENCODING_STYLE:
                return getEncodingStyle();
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
            case WSDDPackageImpl.MAPPING__QNAME:
                setQname((QName)newValue);
                return;
            case WSDDPackageImpl.MAPPING__TYPE:
                setType((QName)newValue);
                return;
            case WSDDPackageImpl.MAPPING__ENCODING_STYLE:
                setEncodingStyle((String)newValue);
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
            case WSDDPackageImpl.MAPPING__QNAME:
                setQname(QNAME_EDEFAULT);
                return;
            case WSDDPackageImpl.MAPPING__TYPE:
                setType(TYPE_EDEFAULT);
                return;
            case WSDDPackageImpl.MAPPING__ENCODING_STYLE:
                setEncodingStyle(ENCODING_STYLE_EDEFAULT);
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
            case WSDDPackageImpl.MAPPING__QNAME:
                return QNAME_EDEFAULT == null ? qname != null : !QNAME_EDEFAULT.equals(qname);
            case WSDDPackageImpl.MAPPING__TYPE:
                return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
            case WSDDPackageImpl.MAPPING__ENCODING_STYLE:
                return ENCODING_STYLE_EDEFAULT == null ? encodingStyle != null : !ENCODING_STYLE_EDEFAULT.equals(encodingStyle);
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
        result.append(" (qname: ");
        result.append(qname);
        result.append(", type: ");
        result.append(type);
        result.append(", encodingStyle: ");
        result.append(encodingStyle);
        result.append(')');
        return result.toString();
    }

} //MappingImpl
