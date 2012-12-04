/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.apache.axis.model.wsdd.WSDDFactory
 * @model kind="package"
 * @generated
 */
public interface WSDDPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "wsdd";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://xml.apache.org/axis/wsdd/";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "wsdd";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    WSDDPackage eINSTANCE = org.apache.axis.model.wsdd.impl.WSDDPackageImpl.init();

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.ParameterImpl <em>Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.ParameterImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getParameter()
     * @generated
     */
    int PARAMETER = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARAMETER__NAME = 0;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARAMETER__VALUE = 1;

    /**
     * The number of structural features of the '<em>Parameter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARAMETER_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.MappingImpl <em>Mapping</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.MappingImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getMapping()
     * @generated
     */
    int MAPPING = 1;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING__QNAME = 0;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING__TYPE = 1;

    /**
     * The feature id for the '<em><b>Encoding Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING__ENCODING_STYLE = 2;

    /**
     * The number of structural features of the '<em>Mapping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.TypeMappingImpl <em>Type Mapping</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.TypeMappingImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getTypeMapping()
     * @generated
     */
    int TYPE_MAPPING = 2;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPE_MAPPING__QNAME = MAPPING__QNAME;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPE_MAPPING__TYPE = MAPPING__TYPE;

    /**
     * The feature id for the '<em><b>Encoding Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPE_MAPPING__ENCODING_STYLE = MAPPING__ENCODING_STYLE;

    /**
     * The feature id for the '<em><b>Serializer</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPE_MAPPING__SERIALIZER = MAPPING_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Deserializer</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPE_MAPPING__DESERIALIZER = MAPPING_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Type Mapping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPE_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.ArrayMappingImpl <em>Array Mapping</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.ArrayMappingImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getArrayMapping()
     * @generated
     */
    int ARRAY_MAPPING = 3;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ARRAY_MAPPING__QNAME = MAPPING__QNAME;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ARRAY_MAPPING__TYPE = MAPPING__TYPE;

    /**
     * The feature id for the '<em><b>Encoding Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ARRAY_MAPPING__ENCODING_STYLE = MAPPING__ENCODING_STYLE;

    /**
     * The feature id for the '<em><b>Inner Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ARRAY_MAPPING__INNER_TYPE = MAPPING_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Array Mapping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ARRAY_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.BeanMappingImpl <em>Bean Mapping</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.BeanMappingImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getBeanMapping()
     * @generated
     */
    int BEAN_MAPPING = 4;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_MAPPING__QNAME = MAPPING__QNAME;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_MAPPING__TYPE = MAPPING__TYPE;

    /**
     * The feature id for the '<em><b>Encoding Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_MAPPING__ENCODING_STYLE = MAPPING__ENCODING_STYLE;

    /**
     * The number of structural features of the '<em>Bean Mapping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.OperationParameterImpl <em>Operation Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.OperationParameterImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getOperationParameter()
     * @generated
     */
    int OPERATION_PARAMETER = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_PARAMETER__NAME = 0;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_PARAMETER__QNAME = 1;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_PARAMETER__TYPE = 2;

    /**
     * The feature id for the '<em><b>Mode</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_PARAMETER__MODE = 3;

    /**
     * The feature id for the '<em><b>In Header</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_PARAMETER__IN_HEADER = 4;

    /**
     * The feature id for the '<em><b>Out Header</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_PARAMETER__OUT_HEADER = 5;

    /**
     * The feature id for the '<em><b>Item QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_PARAMETER__ITEM_QNAME = 6;

    /**
     * The number of structural features of the '<em>Operation Parameter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_PARAMETER_FEATURE_COUNT = 7;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.FaultImpl <em>Fault</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.FaultImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getFault()
     * @generated
     */
    int FAULT = 6;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FAULT__NAME = 0;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FAULT__QNAME = 1;

    /**
     * The feature id for the '<em><b>Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FAULT__CLASS = 2;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FAULT__TYPE = 3;

    /**
     * The number of structural features of the '<em>Fault</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FAULT_FEATURE_COUNT = 4;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.OperationImpl <em>Operation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.OperationImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getOperation()
     * @generated
     */
    int OPERATION = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__NAME = 0;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__QNAME = 1;

    /**
     * The feature id for the '<em><b>Return QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__RETURN_QNAME = 2;

    /**
     * The feature id for the '<em><b>Return Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__RETURN_TYPE = 3;

    /**
     * The feature id for the '<em><b>Return Item QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__RETURN_ITEM_QNAME = 4;

    /**
     * The feature id for the '<em><b>Return Item Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__RETURN_ITEM_TYPE = 5;

    /**
     * The feature id for the '<em><b>Soap Action</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__SOAP_ACTION = 6;

    /**
     * The feature id for the '<em><b>Mep</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__MEP = 7;

    /**
     * The feature id for the '<em><b>Return Header</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__RETURN_HEADER = 8;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__PARAMETERS = 9;

    /**
     * The feature id for the '<em><b>Faults</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__FAULTS = 10;

    /**
     * The number of structural features of the '<em>Operation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_FEATURE_COUNT = 11;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.DeployableItemImpl <em>Deployable Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.DeployableItemImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getDeployableItem()
     * @generated
     */
    int DEPLOYABLE_ITEM = 8;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYABLE_ITEM__NAME = 0;

    /**
     * The number of structural features of the '<em>Deployable Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYABLE_ITEM_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.ServiceImpl <em>Service</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.ServiceImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getService()
     * @generated
     */
    int SERVICE = 9;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__NAME = DEPLOYABLE_ITEM__NAME;

    /**
     * The feature id for the '<em><b>Provider</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__PROVIDER = DEPLOYABLE_ITEM_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Use</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__USE = DEPLOYABLE_ITEM_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__STYLE = DEPLOYABLE_ITEM_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__PARAMETERS = DEPLOYABLE_ITEM_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Operations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__OPERATIONS = DEPLOYABLE_ITEM_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Type Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__TYPE_MAPPINGS = DEPLOYABLE_ITEM_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Bean Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__BEAN_MAPPINGS = DEPLOYABLE_ITEM_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Array Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__ARRAY_MAPPINGS = DEPLOYABLE_ITEM_FEATURE_COUNT + 7;

    /**
     * The number of structural features of the '<em>Service</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_FEATURE_COUNT = DEPLOYABLE_ITEM_FEATURE_COUNT + 8;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.DeploymentImpl <em>Deployment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.DeploymentImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getDeployment()
     * @generated
     */
    int DEPLOYMENT = 10;

    /**
     * The feature id for the '<em><b>Services</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYMENT__SERVICES = 0;

    /**
     * The number of structural features of the '<em>Deployment</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYMENT_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.Use <em>Use</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.Use
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getUse()
     * @generated
     */
    int USE = 11;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.Style <em>Style</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.Style
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getStyle()
     * @generated
     */
    int STYLE = 12;


    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.ParameterMode <em>Parameter Mode</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.ParameterMode
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getParameterMode()
     * @generated
     */
    int PARAMETER_MODE = 13;


    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Parameter <em>Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Parameter</em>'.
     * @see org.apache.axis.model.wsdd.Parameter
     * @generated
     */
    EClass getParameter();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Parameter#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.Parameter#getName()
     * @see #getParameter()
     * @generated
     */
    EAttribute getParameter_Name();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Parameter#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see org.apache.axis.model.wsdd.Parameter#getValue()
     * @see #getParameter()
     * @generated
     */
    EAttribute getParameter_Value();

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Mapping <em>Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mapping</em>'.
     * @see org.apache.axis.model.wsdd.Mapping
     * @generated
     */
    EClass getMapping();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Mapping#getQname <em>Qname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Qname</em>'.
     * @see org.apache.axis.model.wsdd.Mapping#getQname()
     * @see #getMapping()
     * @generated
     */
    EAttribute getMapping_Qname();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Mapping#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.apache.axis.model.wsdd.Mapping#getType()
     * @see #getMapping()
     * @generated
     */
    EAttribute getMapping_Type();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Mapping#getEncodingStyle <em>Encoding Style</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Encoding Style</em>'.
     * @see org.apache.axis.model.wsdd.Mapping#getEncodingStyle()
     * @see #getMapping()
     * @generated
     */
    EAttribute getMapping_EncodingStyle();

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.TypeMapping <em>Type Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Type Mapping</em>'.
     * @see org.apache.axis.model.wsdd.TypeMapping
     * @generated
     */
    EClass getTypeMapping();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.TypeMapping#getSerializer <em>Serializer</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Serializer</em>'.
     * @see org.apache.axis.model.wsdd.TypeMapping#getSerializer()
     * @see #getTypeMapping()
     * @generated
     */
    EAttribute getTypeMapping_Serializer();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.TypeMapping#getDeserializer <em>Deserializer</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Deserializer</em>'.
     * @see org.apache.axis.model.wsdd.TypeMapping#getDeserializer()
     * @see #getTypeMapping()
     * @generated
     */
    EAttribute getTypeMapping_Deserializer();

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.ArrayMapping <em>Array Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Array Mapping</em>'.
     * @see org.apache.axis.model.wsdd.ArrayMapping
     * @generated
     */
    EClass getArrayMapping();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.ArrayMapping#getInnerType <em>Inner Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Inner Type</em>'.
     * @see org.apache.axis.model.wsdd.ArrayMapping#getInnerType()
     * @see #getArrayMapping()
     * @generated
     */
    EAttribute getArrayMapping_InnerType();

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.BeanMapping <em>Bean Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Bean Mapping</em>'.
     * @see org.apache.axis.model.wsdd.BeanMapping
     * @generated
     */
    EClass getBeanMapping();

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.OperationParameter <em>Operation Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Operation Parameter</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter
     * @generated
     */
    EClass getOperationParameter();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getName()
     * @see #getOperationParameter()
     * @generated
     */
    EAttribute getOperationParameter_Name();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getQname <em>Qname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Qname</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getQname()
     * @see #getOperationParameter()
     * @generated
     */
    EAttribute getOperationParameter_Qname();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getType()
     * @see #getOperationParameter()
     * @generated
     */
    EAttribute getOperationParameter_Type();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getMode <em>Mode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Mode</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getMode()
     * @see #getOperationParameter()
     * @generated
     */
    EAttribute getOperationParameter_Mode();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getInHeader <em>In Header</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>In Header</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getInHeader()
     * @see #getOperationParameter()
     * @generated
     */
    EAttribute getOperationParameter_InHeader();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getOutHeader <em>Out Header</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Out Header</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getOutHeader()
     * @see #getOperationParameter()
     * @generated
     */
    EAttribute getOperationParameter_OutHeader();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getItemQName <em>Item QName</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Item QName</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getItemQName()
     * @see #getOperationParameter()
     * @generated
     */
    EAttribute getOperationParameter_ItemQName();

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Fault <em>Fault</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Fault</em>'.
     * @see org.apache.axis.model.wsdd.Fault
     * @generated
     */
    EClass getFault();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Fault#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.Fault#getName()
     * @see #getFault()
     * @generated
     */
    EAttribute getFault_Name();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Fault#getQname <em>Qname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Qname</em>'.
     * @see org.apache.axis.model.wsdd.Fault#getQname()
     * @see #getFault()
     * @generated
     */
    EAttribute getFault_Qname();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Fault#getClass_ <em>Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Class</em>'.
     * @see org.apache.axis.model.wsdd.Fault#getClass_()
     * @see #getFault()
     * @generated
     */
    EAttribute getFault_Class();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Fault#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.apache.axis.model.wsdd.Fault#getType()
     * @see #getFault()
     * @generated
     */
    EAttribute getFault_Type();

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Operation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Operation</em>'.
     * @see org.apache.axis.model.wsdd.Operation
     * @generated
     */
    EClass getOperation();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getName()
     * @see #getOperation()
     * @generated
     */
    EAttribute getOperation_Name();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getQname <em>Qname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Qname</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getQname()
     * @see #getOperation()
     * @generated
     */
    EAttribute getOperation_Qname();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getReturnQName <em>Return QName</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Return QName</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getReturnQName()
     * @see #getOperation()
     * @generated
     */
    EAttribute getOperation_ReturnQName();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getReturnType <em>Return Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Return Type</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getReturnType()
     * @see #getOperation()
     * @generated
     */
    EAttribute getOperation_ReturnType();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getReturnItemQName <em>Return Item QName</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Return Item QName</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getReturnItemQName()
     * @see #getOperation()
     * @generated
     */
    EAttribute getOperation_ReturnItemQName();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getReturnItemType <em>Return Item Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Return Item Type</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getReturnItemType()
     * @see #getOperation()
     * @generated
     */
    EAttribute getOperation_ReturnItemType();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getSoapAction <em>Soap Action</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Soap Action</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getSoapAction()
     * @see #getOperation()
     * @generated
     */
    EAttribute getOperation_SoapAction();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getMep <em>Mep</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Mep</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getMep()
     * @see #getOperation()
     * @generated
     */
    EAttribute getOperation_Mep();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getReturnHeader <em>Return Header</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Return Header</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getReturnHeader()
     * @see #getOperation()
     * @generated
     */
    EAttribute getOperation_ReturnHeader();

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Operation#getParameters <em>Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Parameters</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getParameters()
     * @see #getOperation()
     * @generated
     */
    EReference getOperation_Parameters();

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Operation#getFaults <em>Faults</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Faults</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getFaults()
     * @see #getOperation()
     * @generated
     */
    EReference getOperation_Faults();

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.DeployableItem <em>Deployable Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Deployable Item</em>'.
     * @see org.apache.axis.model.wsdd.DeployableItem
     * @generated
     */
    EClass getDeployableItem();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.DeployableItem#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.DeployableItem#getName()
     * @see #getDeployableItem()
     * @generated
     */
    EAttribute getDeployableItem_Name();

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Service <em>Service</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Service</em>'.
     * @see org.apache.axis.model.wsdd.Service
     * @generated
     */
    EClass getService();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Service#getProvider <em>Provider</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Provider</em>'.
     * @see org.apache.axis.model.wsdd.Service#getProvider()
     * @see #getService()
     * @generated
     */
    EAttribute getService_Provider();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Service#getUse <em>Use</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Use</em>'.
     * @see org.apache.axis.model.wsdd.Service#getUse()
     * @see #getService()
     * @generated
     */
    EAttribute getService_Use();

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Service#getStyle <em>Style</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Style</em>'.
     * @see org.apache.axis.model.wsdd.Service#getStyle()
     * @see #getService()
     * @generated
     */
    EAttribute getService_Style();

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Service#getParameters <em>Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Parameters</em>'.
     * @see org.apache.axis.model.wsdd.Service#getParameters()
     * @see #getService()
     * @generated
     */
    EReference getService_Parameters();

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Service#getOperations <em>Operations</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Operations</em>'.
     * @see org.apache.axis.model.wsdd.Service#getOperations()
     * @see #getService()
     * @generated
     */
    EReference getService_Operations();

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Service#getTypeMappings <em>Type Mappings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Type Mappings</em>'.
     * @see org.apache.axis.model.wsdd.Service#getTypeMappings()
     * @see #getService()
     * @generated
     */
    EReference getService_TypeMappings();

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Service#getBeanMappings <em>Bean Mappings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Bean Mappings</em>'.
     * @see org.apache.axis.model.wsdd.Service#getBeanMappings()
     * @see #getService()
     * @generated
     */
    EReference getService_BeanMappings();

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Service#getArrayMappings <em>Array Mappings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Array Mappings</em>'.
     * @see org.apache.axis.model.wsdd.Service#getArrayMappings()
     * @see #getService()
     * @generated
     */
    EReference getService_ArrayMappings();

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Deployment <em>Deployment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Deployment</em>'.
     * @see org.apache.axis.model.wsdd.Deployment
     * @generated
     */
    EClass getDeployment();

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Deployment#getServices <em>Services</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Services</em>'.
     * @see org.apache.axis.model.wsdd.Deployment#getServices()
     * @see #getDeployment()
     * @generated
     */
    EReference getDeployment_Services();

    /**
     * Returns the meta object for enum '{@link org.apache.axis.model.wsdd.Use <em>Use</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Use</em>'.
     * @see org.apache.axis.model.wsdd.Use
     * @generated
     */
    EEnum getUse();

    /**
     * Returns the meta object for enum '{@link org.apache.axis.model.wsdd.Style <em>Style</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Style</em>'.
     * @see org.apache.axis.model.wsdd.Style
     * @generated
     */
    EEnum getStyle();

    /**
     * Returns the meta object for enum '{@link org.apache.axis.model.wsdd.ParameterMode <em>Parameter Mode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Parameter Mode</em>'.
     * @see org.apache.axis.model.wsdd.ParameterMode
     * @generated
     */
    EEnum getParameterMode();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    WSDDFactory getWSDDFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.ParameterImpl <em>Parameter</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.ParameterImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getParameter()
         * @generated
         */
        EClass PARAMETER = eINSTANCE.getParameter();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PARAMETER__NAME = eINSTANCE.getParameter_Name();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PARAMETER__VALUE = eINSTANCE.getParameter_Value();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.MappingImpl <em>Mapping</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.MappingImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getMapping()
         * @generated
         */
        EClass MAPPING = eINSTANCE.getMapping();

        /**
         * The meta object literal for the '<em><b>Qname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MAPPING__QNAME = eINSTANCE.getMapping_Qname();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MAPPING__TYPE = eINSTANCE.getMapping_Type();

        /**
         * The meta object literal for the '<em><b>Encoding Style</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MAPPING__ENCODING_STYLE = eINSTANCE.getMapping_EncodingStyle();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.TypeMappingImpl <em>Type Mapping</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.TypeMappingImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getTypeMapping()
         * @generated
         */
        EClass TYPE_MAPPING = eINSTANCE.getTypeMapping();

        /**
         * The meta object literal for the '<em><b>Serializer</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TYPE_MAPPING__SERIALIZER = eINSTANCE.getTypeMapping_Serializer();

        /**
         * The meta object literal for the '<em><b>Deserializer</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TYPE_MAPPING__DESERIALIZER = eINSTANCE.getTypeMapping_Deserializer();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.ArrayMappingImpl <em>Array Mapping</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.ArrayMappingImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getArrayMapping()
         * @generated
         */
        EClass ARRAY_MAPPING = eINSTANCE.getArrayMapping();

        /**
         * The meta object literal for the '<em><b>Inner Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ARRAY_MAPPING__INNER_TYPE = eINSTANCE.getArrayMapping_InnerType();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.BeanMappingImpl <em>Bean Mapping</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.BeanMappingImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getBeanMapping()
         * @generated
         */
        EClass BEAN_MAPPING = eINSTANCE.getBeanMapping();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.OperationParameterImpl <em>Operation Parameter</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.OperationParameterImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getOperationParameter()
         * @generated
         */
        EClass OPERATION_PARAMETER = eINSTANCE.getOperationParameter();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION_PARAMETER__NAME = eINSTANCE.getOperationParameter_Name();

        /**
         * The meta object literal for the '<em><b>Qname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION_PARAMETER__QNAME = eINSTANCE.getOperationParameter_Qname();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION_PARAMETER__TYPE = eINSTANCE.getOperationParameter_Type();

        /**
         * The meta object literal for the '<em><b>Mode</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION_PARAMETER__MODE = eINSTANCE.getOperationParameter_Mode();

        /**
         * The meta object literal for the '<em><b>In Header</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION_PARAMETER__IN_HEADER = eINSTANCE.getOperationParameter_InHeader();

        /**
         * The meta object literal for the '<em><b>Out Header</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION_PARAMETER__OUT_HEADER = eINSTANCE.getOperationParameter_OutHeader();

        /**
         * The meta object literal for the '<em><b>Item QName</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION_PARAMETER__ITEM_QNAME = eINSTANCE.getOperationParameter_ItemQName();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.FaultImpl <em>Fault</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.FaultImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getFault()
         * @generated
         */
        EClass FAULT = eINSTANCE.getFault();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute FAULT__NAME = eINSTANCE.getFault_Name();

        /**
         * The meta object literal for the '<em><b>Qname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute FAULT__QNAME = eINSTANCE.getFault_Qname();

        /**
         * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute FAULT__CLASS = eINSTANCE.getFault_Class();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute FAULT__TYPE = eINSTANCE.getFault_Type();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.OperationImpl <em>Operation</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.OperationImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getOperation()
         * @generated
         */
        EClass OPERATION = eINSTANCE.getOperation();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION__NAME = eINSTANCE.getOperation_Name();

        /**
         * The meta object literal for the '<em><b>Qname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION__QNAME = eINSTANCE.getOperation_Qname();

        /**
         * The meta object literal for the '<em><b>Return QName</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION__RETURN_QNAME = eINSTANCE.getOperation_ReturnQName();

        /**
         * The meta object literal for the '<em><b>Return Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION__RETURN_TYPE = eINSTANCE.getOperation_ReturnType();

        /**
         * The meta object literal for the '<em><b>Return Item QName</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION__RETURN_ITEM_QNAME = eINSTANCE.getOperation_ReturnItemQName();

        /**
         * The meta object literal for the '<em><b>Return Item Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION__RETURN_ITEM_TYPE = eINSTANCE.getOperation_ReturnItemType();

        /**
         * The meta object literal for the '<em><b>Soap Action</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION__SOAP_ACTION = eINSTANCE.getOperation_SoapAction();

        /**
         * The meta object literal for the '<em><b>Mep</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION__MEP = eINSTANCE.getOperation_Mep();

        /**
         * The meta object literal for the '<em><b>Return Header</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute OPERATION__RETURN_HEADER = eINSTANCE.getOperation_ReturnHeader();

        /**
         * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference OPERATION__PARAMETERS = eINSTANCE.getOperation_Parameters();

        /**
         * The meta object literal for the '<em><b>Faults</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference OPERATION__FAULTS = eINSTANCE.getOperation_Faults();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.DeployableItemImpl <em>Deployable Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.DeployableItemImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getDeployableItem()
         * @generated
         */
        EClass DEPLOYABLE_ITEM = eINSTANCE.getDeployableItem();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEPLOYABLE_ITEM__NAME = eINSTANCE.getDeployableItem_Name();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.ServiceImpl <em>Service</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.ServiceImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getService()
         * @generated
         */
        EClass SERVICE = eINSTANCE.getService();

        /**
         * The meta object literal for the '<em><b>Provider</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute SERVICE__PROVIDER = eINSTANCE.getService_Provider();

        /**
         * The meta object literal for the '<em><b>Use</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute SERVICE__USE = eINSTANCE.getService_Use();

        /**
         * The meta object literal for the '<em><b>Style</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute SERVICE__STYLE = eINSTANCE.getService_Style();

        /**
         * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SERVICE__PARAMETERS = eINSTANCE.getService_Parameters();

        /**
         * The meta object literal for the '<em><b>Operations</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SERVICE__OPERATIONS = eINSTANCE.getService_Operations();

        /**
         * The meta object literal for the '<em><b>Type Mappings</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SERVICE__TYPE_MAPPINGS = eINSTANCE.getService_TypeMappings();

        /**
         * The meta object literal for the '<em><b>Bean Mappings</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SERVICE__BEAN_MAPPINGS = eINSTANCE.getService_BeanMappings();

        /**
         * The meta object literal for the '<em><b>Array Mappings</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SERVICE__ARRAY_MAPPINGS = eINSTANCE.getService_ArrayMappings();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.DeploymentImpl <em>Deployment</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.DeploymentImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getDeployment()
         * @generated
         */
        EClass DEPLOYMENT = eINSTANCE.getDeployment();

        /**
         * The meta object literal for the '<em><b>Services</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DEPLOYMENT__SERVICES = eINSTANCE.getDeployment_Services();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.Use <em>Use</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.Use
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getUse()
         * @generated
         */
        EEnum USE = eINSTANCE.getUse();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.Style <em>Style</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.Style
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getStyle()
         * @generated
         */
        EEnum STYLE = eINSTANCE.getStyle();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.ParameterMode <em>Parameter Mode</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.ParameterMode
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getParameterMode()
         * @generated
         */
        EEnum PARAMETER_MODE = eINSTANCE.getParameterMode();

    }

} //WSDDPackage
