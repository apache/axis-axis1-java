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
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.model.wsdd.ArrayMapping;
import org.apache.axis.model.wsdd.BeanMapping;
import org.apache.axis.model.wsdd.Operation;
import org.apache.axis.model.wsdd.Service;
import org.apache.axis.model.wsdd.TypeMapping;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Service</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ServiceImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ServiceImpl#getProvider <em>Provider</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ServiceImpl#getUse <em>Use</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ServiceImpl#getStyle <em>Style</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ServiceImpl#getNamespaces <em>Namespaces</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ServiceImpl#getOperations <em>Operations</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ServiceImpl#getTypeMappings <em>Type Mappings</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ServiceImpl#getBeanMappings <em>Bean Mappings</em>}</li>
 *   <li>{@link org.apache.axis.model.wsdd.impl.ServiceImpl#getArrayMappings <em>Array Mappings</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ServiceImpl extends DeployableItemImpl implements Service {
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
     * The default value of the '{@link #getProvider() <em>Provider</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProvider()
     * @generated
     * @ordered
     */
    protected static final QName PROVIDER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getProvider() <em>Provider</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProvider()
     * @generated
     * @ordered
     */
    protected QName provider = PROVIDER_EDEFAULT;

    /**
     * The default value of the '{@link #getUse() <em>Use</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUse()
     * @generated
     * @ordered
     */
    protected static final Use USE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUse() <em>Use</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUse()
     * @generated
     * @ordered
     */
    protected Use use = USE_EDEFAULT;

    /**
     * The default value of the '{@link #getStyle() <em>Style</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStyle()
     * @generated
     * @ordered
     */
    protected static final Style STYLE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getStyle() <em>Style</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStyle()
     * @generated
     * @ordered
     */
    protected Style style = STYLE_EDEFAULT;

    /**
     * The cached value of the '{@link #getNamespaces() <em>Namespaces</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNamespaces()
     * @generated
     * @ordered
     */
    protected EList namespaces;

    /**
     * The cached value of the '{@link #getOperations() <em>Operations</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOperations()
     * @generated
     * @ordered
     */
    protected EList operations;

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
    protected ServiceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WSDDPackageImpl.Literals.SERVICE;
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
    public QName getProvider() {
        return provider;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setProvider(QName newProvider) {
        provider = newProvider;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Use getUse() {
        return use;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUse(Use newUse) {
        use = newUse;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Style getStyle() {
        return style;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setStyle(Style newStyle) {
        style = newStyle;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getNamespaces() {
        if (namespaces == null) {
            namespaces = new BasicInternalEList(String.class);
        }
        return namespaces;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getOperations() {
        if (operations == null) {
            operations = new BasicInternalEList(Operation.class);
        }
        return operations;
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
            case WSDDPackageImpl.SERVICE__OPERATIONS:
                return ((InternalEList)getOperations()).basicRemove(otherEnd, msgs);
            case WSDDPackageImpl.SERVICE__TYPE_MAPPINGS:
                return ((InternalEList)getTypeMappings()).basicRemove(otherEnd, msgs);
            case WSDDPackageImpl.SERVICE__BEAN_MAPPINGS:
                return ((InternalEList)getBeanMappings()).basicRemove(otherEnd, msgs);
            case WSDDPackageImpl.SERVICE__ARRAY_MAPPINGS:
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
            case WSDDPackageImpl.SERVICE__NAME:
                return getName();
            case WSDDPackageImpl.SERVICE__PROVIDER:
                return getProvider();
            case WSDDPackageImpl.SERVICE__USE:
                return getUse();
            case WSDDPackageImpl.SERVICE__STYLE:
                return getStyle();
            case WSDDPackageImpl.SERVICE__NAMESPACES:
                return getNamespaces();
            case WSDDPackageImpl.SERVICE__OPERATIONS:
                return getOperations();
            case WSDDPackageImpl.SERVICE__TYPE_MAPPINGS:
                return getTypeMappings();
            case WSDDPackageImpl.SERVICE__BEAN_MAPPINGS:
                return getBeanMappings();
            case WSDDPackageImpl.SERVICE__ARRAY_MAPPINGS:
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
            case WSDDPackageImpl.SERVICE__NAME:
                setName((String)newValue);
                return;
            case WSDDPackageImpl.SERVICE__PROVIDER:
                setProvider((QName)newValue);
                return;
            case WSDDPackageImpl.SERVICE__USE:
                setUse((Use)newValue);
                return;
            case WSDDPackageImpl.SERVICE__STYLE:
                setStyle((Style)newValue);
                return;
            case WSDDPackageImpl.SERVICE__NAMESPACES:
                getNamespaces().clear();
                getNamespaces().addAll((Collection)newValue);
                return;
            case WSDDPackageImpl.SERVICE__OPERATIONS:
                getOperations().clear();
                getOperations().addAll((Collection)newValue);
                return;
            case WSDDPackageImpl.SERVICE__TYPE_MAPPINGS:
                getTypeMappings().clear();
                getTypeMappings().addAll((Collection)newValue);
                return;
            case WSDDPackageImpl.SERVICE__BEAN_MAPPINGS:
                getBeanMappings().clear();
                getBeanMappings().addAll((Collection)newValue);
                return;
            case WSDDPackageImpl.SERVICE__ARRAY_MAPPINGS:
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
            case WSDDPackageImpl.SERVICE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WSDDPackageImpl.SERVICE__PROVIDER:
                setProvider(PROVIDER_EDEFAULT);
                return;
            case WSDDPackageImpl.SERVICE__USE:
                setUse(USE_EDEFAULT);
                return;
            case WSDDPackageImpl.SERVICE__STYLE:
                setStyle(STYLE_EDEFAULT);
                return;
            case WSDDPackageImpl.SERVICE__NAMESPACES:
                getNamespaces().clear();
                return;
            case WSDDPackageImpl.SERVICE__OPERATIONS:
                getOperations().clear();
                return;
            case WSDDPackageImpl.SERVICE__TYPE_MAPPINGS:
                getTypeMappings().clear();
                return;
            case WSDDPackageImpl.SERVICE__BEAN_MAPPINGS:
                getBeanMappings().clear();
                return;
            case WSDDPackageImpl.SERVICE__ARRAY_MAPPINGS:
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
            case WSDDPackageImpl.SERVICE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WSDDPackageImpl.SERVICE__PROVIDER:
                return PROVIDER_EDEFAULT == null ? provider != null : !PROVIDER_EDEFAULT.equals(provider);
            case WSDDPackageImpl.SERVICE__USE:
                return USE_EDEFAULT == null ? use != null : !USE_EDEFAULT.equals(use);
            case WSDDPackageImpl.SERVICE__STYLE:
                return STYLE_EDEFAULT == null ? style != null : !STYLE_EDEFAULT.equals(style);
            case WSDDPackageImpl.SERVICE__NAMESPACES:
                return namespaces != null && !namespaces.isEmpty();
            case WSDDPackageImpl.SERVICE__OPERATIONS:
                return operations != null && !operations.isEmpty();
            case WSDDPackageImpl.SERVICE__TYPE_MAPPINGS:
                return typeMappings != null && !typeMappings.isEmpty();
            case WSDDPackageImpl.SERVICE__BEAN_MAPPINGS:
                return beanMappings != null && !beanMappings.isEmpty();
            case WSDDPackageImpl.SERVICE__ARRAY_MAPPINGS:
                return arrayMappings != null && !arrayMappings.isEmpty();
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
        result.append(", provider: ");
        result.append(provider);
        result.append(", use: ");
        result.append(use);
        result.append(", style: ");
        result.append(style);
        result.append(", namespaces: ");
        result.append(namespaces);
        result.append(')');
        return result.toString();
    }

} //ServiceImpl
