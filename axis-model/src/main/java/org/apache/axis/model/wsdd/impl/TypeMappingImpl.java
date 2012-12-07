/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import org.apache.axis.model.wsdd.TypeMapping;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Type Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.impl.TypeMappingImpl#getSerializer <em>Serializer</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.TypeMappingImpl#getDeserializer <em>Deserializer</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TypeMappingImpl extends MappingImpl implements TypeMapping {
    /**
     * The default value of the '{@link #getSerializer() <em>Serializer</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSerializer()
     * @generated
     * @ordered
     */
    protected static final String SERIALIZER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getSerializer() <em>Serializer</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSerializer()
     * @generated
     * @ordered
     */
    protected String serializer = SERIALIZER_EDEFAULT;

    /**
     * The default value of the '{@link #getDeserializer() <em>Deserializer</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDeserializer()
     * @generated
     * @ordered
     */
    protected static final String DESERIALIZER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDeserializer() <em>Deserializer</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDeserializer()
     * @generated
     * @ordered
     */
    protected String deserializer = DESERIALIZER_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TypeMappingImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WSDDPackageImpl.Literals.TYPE_MAPPING;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getSerializer() {
        return serializer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSerializer(String newSerializer) {
        serializer = newSerializer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDeserializer() {
        return deserializer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDeserializer(String newDeserializer) {
        deserializer = newDeserializer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case WSDDPackageImpl.TYPE_MAPPING__SERIALIZER:
                return getSerializer();
            case WSDDPackageImpl.TYPE_MAPPING__DESERIALIZER:
                return getDeserializer();
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
            case WSDDPackageImpl.TYPE_MAPPING__SERIALIZER:
                setSerializer((String)newValue);
                return;
            case WSDDPackageImpl.TYPE_MAPPING__DESERIALIZER:
                setDeserializer((String)newValue);
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
            case WSDDPackageImpl.TYPE_MAPPING__SERIALIZER:
                setSerializer(SERIALIZER_EDEFAULT);
                return;
            case WSDDPackageImpl.TYPE_MAPPING__DESERIALIZER:
                setDeserializer(DESERIALIZER_EDEFAULT);
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
            case WSDDPackageImpl.TYPE_MAPPING__SERIALIZER:
                return SERIALIZER_EDEFAULT == null ? serializer != null : !SERIALIZER_EDEFAULT.equals(serializer);
            case WSDDPackageImpl.TYPE_MAPPING__DESERIALIZER:
                return DESERIALIZER_EDEFAULT == null ? deserializer != null : !DESERIALIZER_EDEFAULT.equals(deserializer);
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
        result.append(" (serializer: ");
        result.append(serializer);
        result.append(", deserializer: ");
        result.append(deserializer);
        result.append(')');
        return result.toString();
    }

} //TypeMappingImpl
