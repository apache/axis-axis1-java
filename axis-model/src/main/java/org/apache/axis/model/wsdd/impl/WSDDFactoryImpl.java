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
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static WSDDFactory init() {
        try {
            WSDDFactory theWSDDFactory = (WSDDFactory)EPackage.Registry.INSTANCE.getEFactory("http://xml.apache.org/axis/wsdd/"); 
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
            case WSDDPackage.PARAMETER: return createParameter();
            case WSDDPackage.TYPE_MAPPING: return createTypeMapping();
            case WSDDPackage.ARRAY_MAPPING: return createArrayMapping();
            case WSDDPackage.BEAN_MAPPING: return createBeanMapping();
            case WSDDPackage.OPERATION_PARAMETER: return createOperationParameter();
            case WSDDPackage.FAULT: return createFault();
            case WSDDPackage.OPERATION: return createOperation();
            case WSDDPackage.SERVICE: return createService();
            case WSDDPackage.DEPLOYMENT: return createDeployment();
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
            case WSDDPackage.USE:
                return createUseFromString(eDataType, initialValue);
            case WSDDPackage.STYLE:
                return createStyleFromString(eDataType, initialValue);
            case WSDDPackage.PARAMETER_MODE:
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
            case WSDDPackage.USE:
                return convertUseToString(eDataType, instanceValue);
            case WSDDPackage.STYLE:
                return convertStyleToString(eDataType, instanceValue);
            case WSDDPackage.PARAMETER_MODE:
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
    public WSDDPackage getWSDDPackage() {
        return (WSDDPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    public static WSDDPackage getPackage() {
        return WSDDPackage.eINSTANCE;
    }

} //WSDDFactoryImpl
