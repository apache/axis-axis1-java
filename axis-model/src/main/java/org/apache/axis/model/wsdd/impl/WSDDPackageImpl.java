/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import org.apache.axis.model.wsdd.ArrayMapping;
import org.apache.axis.model.wsdd.BeanMapping;
import org.apache.axis.model.wsdd.DeployableItem;
import org.apache.axis.model.wsdd.Deployment;
import org.apache.axis.model.wsdd.Fault;
import org.apache.axis.model.wsdd.Mapping;
import org.apache.axis.model.wsdd.Operation;
import org.apache.axis.model.wsdd.OperationParameter;
import org.apache.axis.model.wsdd.Parameter;
import org.apache.axis.model.wsdd.ParameterMode;
import org.apache.axis.model.wsdd.Service;
import org.apache.axis.model.wsdd.Style;
import org.apache.axis.model.wsdd.TypeMapping;
import org.apache.axis.model.wsdd.Use;
import org.apache.axis.model.wsdd.WSDDFactory;
import org.apache.axis.model.wsdd.WSDDPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class WSDDPackageImpl extends EPackageImpl implements WSDDPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass parameterEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass mappingEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass typeMappingEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass arrayMappingEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass beanMappingEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass operationParameterEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass faultEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass operationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass deployableItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass serviceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass deploymentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum useEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum styleEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum parameterModeEEnum = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.apache.axis.model.wsdd.WSDDPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private WSDDPackageImpl() {
        super(eNS_URI, WSDDFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link WSDDPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static WSDDPackage init() {
        if (isInited) return (WSDDPackage)EPackage.Registry.INSTANCE.getEPackage(WSDDPackage.eNS_URI);

        // Obtain or create and register package
        WSDDPackageImpl theWSDDPackage = (WSDDPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof WSDDPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new WSDDPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        XMLTypePackage.eINSTANCE.eClass();

        // Create package meta-data objects
        theWSDDPackage.createPackageContents();

        // Initialize created meta-data
        theWSDDPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theWSDDPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(WSDDPackage.eNS_URI, theWSDDPackage);
        return theWSDDPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getParameter() {
        return parameterEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getParameter_Name() {
        return (EAttribute)parameterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getParameter_Value() {
        return (EAttribute)parameterEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getMapping() {
        return mappingEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMapping_Qname() {
        return (EAttribute)mappingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMapping_Type() {
        return (EAttribute)mappingEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMapping_EncodingStyle() {
        return (EAttribute)mappingEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTypeMapping() {
        return typeMappingEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTypeMapping_Serializer() {
        return (EAttribute)typeMappingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTypeMapping_Deserializer() {
        return (EAttribute)typeMappingEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getArrayMapping() {
        return arrayMappingEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getArrayMapping_InnerType() {
        return (EAttribute)arrayMappingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getBeanMapping() {
        return beanMappingEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getOperationParameter() {
        return operationParameterEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperationParameter_Name() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperationParameter_Qname() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperationParameter_Type() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperationParameter_Mode() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperationParameter_InHeader() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperationParameter_OutHeader() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperationParameter_ItemQName() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getFault() {
        return faultEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFault_Name() {
        return (EAttribute)faultEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFault_Qname() {
        return (EAttribute)faultEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFault_Class() {
        return (EAttribute)faultEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFault_Type() {
        return (EAttribute)faultEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getOperation() {
        return operationEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperation_Name() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperation_Qname() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperation_ReturnQName() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperation_ReturnType() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperation_ReturnItemQName() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperation_ReturnItemType() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperation_SoapAction() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperation_Mep() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperation_ReturnHeader() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getOperation_Parameters() {
        return (EReference)operationEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getOperation_Faults() {
        return (EReference)operationEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getDeployableItem() {
        return deployableItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDeployableItem_Name() {
        return (EAttribute)deployableItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getService() {
        return serviceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getService_Provider() {
        return (EAttribute)serviceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getService_Use() {
        return (EAttribute)serviceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getService_Style() {
        return (EAttribute)serviceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getService_Parameters() {
        return (EReference)serviceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getService_Operations() {
        return (EReference)serviceEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getService_TypeMappings() {
        return (EReference)serviceEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getService_BeanMappings() {
        return (EReference)serviceEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getService_ArrayMappings() {
        return (EReference)serviceEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getDeployment() {
        return deploymentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getDeployment_Services() {
        return (EReference)deploymentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getUse() {
        return useEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getStyle() {
        return styleEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getParameterMode() {
        return parameterModeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public WSDDFactory getWSDDFactory() {
        return (WSDDFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        parameterEClass = createEClass(PARAMETER);
        createEAttribute(parameterEClass, PARAMETER__NAME);
        createEAttribute(parameterEClass, PARAMETER__VALUE);

        mappingEClass = createEClass(MAPPING);
        createEAttribute(mappingEClass, MAPPING__QNAME);
        createEAttribute(mappingEClass, MAPPING__TYPE);
        createEAttribute(mappingEClass, MAPPING__ENCODING_STYLE);

        typeMappingEClass = createEClass(TYPE_MAPPING);
        createEAttribute(typeMappingEClass, TYPE_MAPPING__SERIALIZER);
        createEAttribute(typeMappingEClass, TYPE_MAPPING__DESERIALIZER);

        arrayMappingEClass = createEClass(ARRAY_MAPPING);
        createEAttribute(arrayMappingEClass, ARRAY_MAPPING__INNER_TYPE);

        beanMappingEClass = createEClass(BEAN_MAPPING);

        operationParameterEClass = createEClass(OPERATION_PARAMETER);
        createEAttribute(operationParameterEClass, OPERATION_PARAMETER__NAME);
        createEAttribute(operationParameterEClass, OPERATION_PARAMETER__QNAME);
        createEAttribute(operationParameterEClass, OPERATION_PARAMETER__TYPE);
        createEAttribute(operationParameterEClass, OPERATION_PARAMETER__MODE);
        createEAttribute(operationParameterEClass, OPERATION_PARAMETER__IN_HEADER);
        createEAttribute(operationParameterEClass, OPERATION_PARAMETER__OUT_HEADER);
        createEAttribute(operationParameterEClass, OPERATION_PARAMETER__ITEM_QNAME);

        faultEClass = createEClass(FAULT);
        createEAttribute(faultEClass, FAULT__NAME);
        createEAttribute(faultEClass, FAULT__QNAME);
        createEAttribute(faultEClass, FAULT__CLASS);
        createEAttribute(faultEClass, FAULT__TYPE);

        operationEClass = createEClass(OPERATION);
        createEAttribute(operationEClass, OPERATION__NAME);
        createEAttribute(operationEClass, OPERATION__QNAME);
        createEAttribute(operationEClass, OPERATION__RETURN_QNAME);
        createEAttribute(operationEClass, OPERATION__RETURN_TYPE);
        createEAttribute(operationEClass, OPERATION__RETURN_ITEM_QNAME);
        createEAttribute(operationEClass, OPERATION__RETURN_ITEM_TYPE);
        createEAttribute(operationEClass, OPERATION__SOAP_ACTION);
        createEAttribute(operationEClass, OPERATION__MEP);
        createEAttribute(operationEClass, OPERATION__RETURN_HEADER);
        createEReference(operationEClass, OPERATION__PARAMETERS);
        createEReference(operationEClass, OPERATION__FAULTS);

        deployableItemEClass = createEClass(DEPLOYABLE_ITEM);
        createEAttribute(deployableItemEClass, DEPLOYABLE_ITEM__NAME);

        serviceEClass = createEClass(SERVICE);
        createEAttribute(serviceEClass, SERVICE__PROVIDER);
        createEAttribute(serviceEClass, SERVICE__USE);
        createEAttribute(serviceEClass, SERVICE__STYLE);
        createEReference(serviceEClass, SERVICE__PARAMETERS);
        createEReference(serviceEClass, SERVICE__OPERATIONS);
        createEReference(serviceEClass, SERVICE__TYPE_MAPPINGS);
        createEReference(serviceEClass, SERVICE__BEAN_MAPPINGS);
        createEReference(serviceEClass, SERVICE__ARRAY_MAPPINGS);

        deploymentEClass = createEClass(DEPLOYMENT);
        createEReference(deploymentEClass, DEPLOYMENT__SERVICES);

        // Create enums
        useEEnum = createEEnum(USE);
        styleEEnum = createEEnum(STYLE);
        parameterModeEEnum = createEEnum(PARAMETER_MODE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

        // Add supertypes to classes
        typeMappingEClass.getESuperTypes().add(this.getMapping());
        arrayMappingEClass.getESuperTypes().add(this.getMapping());
        beanMappingEClass.getESuperTypes().add(this.getMapping());
        serviceEClass.getESuperTypes().add(this.getDeployableItem());

        // Initialize classes and features; add operations and parameters
        initEClass(parameterEClass, Parameter.class, "Parameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getParameter_Name(), ecorePackage.getEString(), "name", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getParameter_Value(), ecorePackage.getEString(), "value", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(mappingEClass, Mapping.class, "Mapping", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getMapping_Qname(), theXMLTypePackage.getQName(), "qname", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMapping_Type(), theXMLTypePackage.getQName(), "type", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMapping_EncodingStyle(), ecorePackage.getEString(), "encodingStyle", "", 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(typeMappingEClass, TypeMapping.class, "TypeMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getTypeMapping_Serializer(), ecorePackage.getEString(), "serializer", null, 0, 1, TypeMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTypeMapping_Deserializer(), ecorePackage.getEString(), "deserializer", null, 0, 1, TypeMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(arrayMappingEClass, ArrayMapping.class, "ArrayMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getArrayMapping_InnerType(), theXMLTypePackage.getQName(), "innerType", null, 0, 1, ArrayMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(beanMappingEClass, BeanMapping.class, "BeanMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(operationParameterEClass, OperationParameter.class, "OperationParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getOperationParameter_Name(), ecorePackage.getEString(), "name", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_Qname(), theXMLTypePackage.getQName(), "qname", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_Type(), theXMLTypePackage.getQName(), "type", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_Mode(), this.getParameterMode(), "mode", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_InHeader(), ecorePackage.getEBooleanObject(), "inHeader", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_OutHeader(), ecorePackage.getEBooleanObject(), "outHeader", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_ItemQName(), theXMLTypePackage.getQName(), "itemQName", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(faultEClass, Fault.class, "Fault", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getFault_Name(), ecorePackage.getEString(), "name", null, 0, 1, Fault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFault_Qname(), theXMLTypePackage.getQName(), "qname", null, 0, 1, Fault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFault_Class(), ecorePackage.getEString(), "class", null, 0, 1, Fault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFault_Type(), theXMLTypePackage.getQName(), "type", null, 0, 1, Fault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(operationEClass, Operation.class, "Operation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getOperation_Name(), ecorePackage.getEString(), "name", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_Qname(), theXMLTypePackage.getQName(), "qname", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_ReturnQName(), theXMLTypePackage.getQName(), "returnQName", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_ReturnType(), theXMLTypePackage.getQName(), "returnType", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_ReturnItemQName(), theXMLTypePackage.getQName(), "returnItemQName", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_ReturnItemType(), theXMLTypePackage.getQName(), "returnItemType", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_SoapAction(), ecorePackage.getEString(), "soapAction", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_Mep(), ecorePackage.getEString(), "mep", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_ReturnHeader(), ecorePackage.getEBooleanObject(), "returnHeader", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getOperation_Parameters(), this.getOperationParameter(), null, "parameters", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getOperation_Faults(), this.getFault(), null, "faults", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(deployableItemEClass, DeployableItem.class, "DeployableItem", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDeployableItem_Name(), ecorePackage.getEString(), "name", null, 0, 1, DeployableItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(serviceEClass, Service.class, "Service", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getService_Provider(), theXMLTypePackage.getQName(), "provider", null, 0, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getService_Use(), this.getUse(), "use", null, 0, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getService_Style(), this.getStyle(), "style", null, 0, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getService_Parameters(), this.getParameter(), null, "parameters", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getService_Operations(), this.getOperation(), null, "operations", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getService_TypeMappings(), this.getTypeMapping(), null, "typeMappings", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getService_BeanMappings(), this.getBeanMapping(), null, "beanMappings", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getService_ArrayMappings(), this.getArrayMapping(), null, "arrayMappings", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        EOperation op = addEOperation(serviceEClass, null, "setParameter");
        addEParameter(op, ecorePackage.getEString(), "name", 1, 1);
        addEParameter(op, ecorePackage.getEString(), "value", 1, 1);

        initEClass(deploymentEClass, Deployment.class, "Deployment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getDeployment_Services(), this.getService(), null, "services", null, 0, -1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize enums and add enum literals
        initEEnum(useEEnum, Use.class, "Use");
        addEEnumLiteral(useEEnum, Use.LITERAL_LITERAL);
        addEEnumLiteral(useEEnum, Use.ENCODED_LITERAL);

        initEEnum(styleEEnum, Style.class, "Style");
        addEEnumLiteral(styleEEnum, Style.RPC_LITERAL);
        addEEnumLiteral(styleEEnum, Style.DOCUMENT_LITERAL);
        addEEnumLiteral(styleEEnum, Style.WRAPPED_LITERAL);
        addEEnumLiteral(styleEEnum, Style.MESSAGE_LITERAL);

        initEEnum(parameterModeEEnum, ParameterMode.class, "ParameterMode");
        addEEnumLiteral(parameterModeEEnum, ParameterMode.IN_LITERAL);
        addEEnumLiteral(parameterModeEEnum, ParameterMode.OUT_LITERAL);
        addEEnumLiteral(parameterModeEEnum, ParameterMode.INOUT_LITERAL);

        // Create resource
        createResource(eNS_URI);

        // Create annotations
        // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
        createExtendedMetaDataAnnotations();
    }

    /**
     * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void createExtendedMetaDataAnnotations() {
        String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";		
        addAnnotation
          (getOperation_Parameters(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "parameter",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getOperation_Faults(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "fault",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getService_Parameters(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "parameter",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getService_Operations(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "operation",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getService_TypeMappings(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "typeMapping",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getService_BeanMappings(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "beanMapping",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getService_ArrayMappings(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "arrayMapping",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (deploymentEClass, 
           source, 
           new String[] {
             "name", "deployment",
             "kind", "element"
           });		
        addAnnotation
          (getDeployment_Services(), 
           source, 
           new String[] {
             "name", "service",
             "kind", "element",
             "namespace", "##targetNamespace"
           });
    }

} //WSDDPackageImpl
