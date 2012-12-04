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
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.DeployableItemImpl <em>Deployable Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.DeployableItemImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getDeployableItem()
     * @generated
     */
    int DEPLOYABLE_ITEM = 5;

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
    int SERVICE = 6;

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
     * The feature id for the '<em><b>Type Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__TYPE_MAPPINGS = DEPLOYABLE_ITEM_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Bean Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__BEAN_MAPPINGS = DEPLOYABLE_ITEM_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Array Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE__ARRAY_MAPPINGS = DEPLOYABLE_ITEM_FEATURE_COUNT + 6;

    /**
     * The number of structural features of the '<em>Service</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_FEATURE_COUNT = DEPLOYABLE_ITEM_FEATURE_COUNT + 7;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.DeploymentImpl <em>Deployment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.DeploymentImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getDeployment()
     * @generated
     */
    int DEPLOYMENT = 7;

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
    int USE = 8;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.Style <em>Style</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.Style
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getStyle()
     * @generated
     */
    int STYLE = 9;


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

    }

} //WSDDPackage
