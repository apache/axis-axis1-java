/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import java.util.Collection;
import java.util.List;

import org.apache.axis.model.wsdd.ArrayMapping;
import org.apache.axis.model.wsdd.BeanMapping;
import org.apache.axis.model.wsdd.MappingContainer;
import org.apache.axis.model.wsdd.TypeMapping;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapping Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.impl.MappingContainerImpl#getTypeMappings <em>Type Mappings</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.MappingContainerImpl#getBeanMappings <em>Bean Mappings</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.MappingContainerImpl#getArrayMappings <em>Array Mappings</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MappingContainerImpl extends EObjectImpl implements MappingContainer {
    /**
     * The cached value of the '{@link #getTypeMappings() <em>Type Mappings</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTypeMappings()
     * @generated
     * @ordered
     */
    protected EList typeMappings;

    /**
     * The cached value of the '{@link #getBeanMappings() <em>Bean Mappings</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBeanMappings()
     * @generated
     * @ordered
     */
    protected EList beanMappings;

    /**
     * The cached value of the '{@link #getArrayMappings() <em>Array Mappings</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getArrayMappings()
     * @generated
     * @ordered
     */
    protected EList arrayMappings;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MappingContainerImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WSDDPackageImpl.Literals.MAPPING_CONTAINER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getTypeMappings() {
        if (typeMappings == null) {
            typeMappings = new BasicInternalEList(TypeMapping.class);
        }
        return typeMappings;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getBeanMappings() {
        if (beanMappings == null) {
            beanMappings = new BasicInternalEList(BeanMapping.class);
        }
        return beanMappings;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getArrayMappings() {
        if (arrayMappings == null) {
            arrayMappings = new BasicInternalEList(ArrayMapping.class);
        }
        return arrayMappings;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case WSDDPackageImpl.MAPPING_CONTAINER__TYPE_MAPPINGS:
                return ((InternalEList)getTypeMappings()).basicRemove(otherEnd, msgs);
            case WSDDPackageImpl.MAPPING_CONTAINER__BEAN_MAPPINGS:
                return ((InternalEList)getBeanMappings()).basicRemove(otherEnd, msgs);
            case WSDDPackageImpl.MAPPING_CONTAINER__ARRAY_MAPPINGS:
                return ((InternalEList)getArrayMappings()).basicRemove(otherEnd, msgs);
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
            case WSDDPackageImpl.MAPPING_CONTAINER__TYPE_MAPPINGS:
                return getTypeMappings();
            case WSDDPackageImpl.MAPPING_CONTAINER__BEAN_MAPPINGS:
                return getBeanMappings();
            case WSDDPackageImpl.MAPPING_CONTAINER__ARRAY_MAPPINGS:
                return getArrayMappings();
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
            case WSDDPackageImpl.MAPPING_CONTAINER__TYPE_MAPPINGS:
                getTypeMappings().clear();
                getTypeMappings().addAll((Collection)newValue);
                return;
            case WSDDPackageImpl.MAPPING_CONTAINER__BEAN_MAPPINGS:
                getBeanMappings().clear();
                getBeanMappings().addAll((Collection)newValue);
                return;
            case WSDDPackageImpl.MAPPING_CONTAINER__ARRAY_MAPPINGS:
                getArrayMappings().clear();
                getArrayMappings().addAll((Collection)newValue);
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
            case WSDDPackageImpl.MAPPING_CONTAINER__TYPE_MAPPINGS:
                getTypeMappings().clear();
                return;
            case WSDDPackageImpl.MAPPING_CONTAINER__BEAN_MAPPINGS:
                getBeanMappings().clear();
                return;
            case WSDDPackageImpl.MAPPING_CONTAINER__ARRAY_MAPPINGS:
                getArrayMappings().clear();
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
            case WSDDPackageImpl.MAPPING_CONTAINER__TYPE_MAPPINGS:
                return typeMappings != null && !typeMappings.isEmpty();
            case WSDDPackageImpl.MAPPING_CONTAINER__BEAN_MAPPINGS:
                return beanMappings != null && !beanMappings.isEmpty();
            case WSDDPackageImpl.MAPPING_CONTAINER__ARRAY_MAPPINGS:
                return arrayMappings != null && !arrayMappings.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //MappingContainerImpl
