/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import org.apache.axis.model.wsdd.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class WSDDFactoryImpl extends EFactoryImpl implements WSDDFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final WSDDFactoryImpl eINSTANCE = init();

    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static WSDDFactoryImpl init() {
        try {
            WSDDFactoryImpl theWSDDFactory = (WSDDFactoryImpl)EPackage.Registry.INSTANCE.getEFactory("http://xml.apache.org/axis/wsdd/"); 
            if (theWSDDFactory != null) {
                return theWSDDFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new WSDDFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public WSDDFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case WSDDPackageImpl.PARAMETER: return (EObject)createParameter();
            case WSDDPackageImpl.TYPE_MAPPING: return (EObject)createTypeMapping();
            case WSDDPackageImpl.ARRAY_MAPPING: return (EObject)createArrayMapping();
            case WSDDPackageImpl.BEAN_MAPPING: return (EObject)createBeanMapping();
            case WSDDPackageImpl.OPERATION_PARAMETER: return (EObject)createOperationParameter();
            case WSDDPackageImpl.FAULT: return (EObject)createFault();
            case WSDDPackageImpl.OPERATION: return (EObject)createOperation();
            case WSDDPackageImpl.SERVICE: return (EObject)createService();
            case WSDDPackageImpl.DEPLOYMENT: return (EObject)createDeployment();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
            case WSDDPackageImpl.USE:
                return createUseFromString(eDataType, initialValue);
            case WSDDPackageImpl.STYLE:
                return createStyleFromString(eDataType, initialValue);
            case WSDDPackageImpl.PARAMETER_MODE:
                return createParameterModeFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
            case WSDDPackageImpl.USE:
                return convertUseToString(eDataType, instanceValue);
            case WSDDPackageImpl.STYLE:
                return convertStyleToString(eDataType, instanceValue);
            case WSDDPackageImpl.PARAMETER_MODE:
                return convertParameterModeToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Parameter createParameter() {
        ParameterImpl parameter = new ParameterImpl();
        return parameter;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TypeMapping createTypeMapping() {
        TypeMappingImpl typeMapping = new TypeMappingImpl();
        return typeMapping;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ArrayMapping createArrayMapping() {
        ArrayMappingImpl arrayMapping = new ArrayMappingImpl();
        return arrayMapping;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BeanMapping createBeanMapping() {
        BeanMappingImpl beanMapping = new BeanMappingImpl();
        return beanMapping;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public OperationParameter createOperationParameter() {
        OperationParameterImpl operationParameter = new OperationParameterImpl();
        return operationParameter;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Fault createFault() {
        FaultImpl fault = new FaultImpl();
        return fault;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Operation createOperation() {
        OperationImpl operation = new OperationImpl();
        return operation;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Service createService() {
        ServiceImpl service = new ServiceImpl();
        return service;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Deployment createDeployment() {
        DeploymentImpl deployment = new DeploymentImpl();
        return deployment;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Use createUseFromString(EDataType eDataType, String initialValue) {
        Use result = Use.get(initialValue);
        if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertUseToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Style createStyleFromString(EDataType eDataType, String initialValue) {
        Style result = Style.get(initialValue);
        if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertStyleToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ParameterMode createParameterModeFromString(EDataType eDataType, String initialValue) {
        ParameterMode result = ParameterMode.get(initialValue);
        if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertParameterModeToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public WSDDPackageImpl getWSDDPackage() {
        return (WSDDPackageImpl)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    public static WSDDPackageImpl getPackage() {
        return WSDDPackageImpl.eINSTANCE;
    }

} //WSDDFactoryImpl
