/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd.impl;

import org.apache.axis.model.soap.impl.SOAPPackageImpl;
import org.apache.axis.model.wsdd.ArrayMapping;
import org.apache.axis.model.wsdd.BeanMapping;
import org.apache.axis.model.wsdd.DeployableItem;
import org.apache.axis.model.wsdd.Deployment;
import org.apache.axis.model.wsdd.Fault;
import org.apache.axis.model.wsdd.Chain;
import org.apache.axis.model.wsdd.GlobalConfiguration;
import org.apache.axis.model.wsdd.Handler;
import org.apache.axis.model.wsdd.Mapping;
import org.apache.axis.model.wsdd.MappingContainer;
import org.apache.axis.model.wsdd.Operation;
import org.apache.axis.model.wsdd.OperationParameter;
import org.apache.axis.model.wsdd.Parameter;
import org.apache.axis.model.wsdd.ParameterMode;
import org.apache.axis.model.wsdd.Parameterizable;
import org.apache.axis.model.wsdd.Service;
import org.apache.axis.model.wsdd.Transport;
import org.apache.axis.model.wsdd.TypeMapping;
import org.apache.axis.model.wsdd.WSDDFactory;

import org.apache.axis.model.xml.impl.XmlPackageImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @see org.apache.axis.model.wsdd.WSDDFactory
 * @model kind="package"
 * @generated
 */
public class WSDDPackageImpl extends EPackageImpl {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String eNAME = "wsdd";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String eNS_URI = "http://xml.apache.org/axis/wsdd/";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String eNS_PREFIX = "wsdd";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final WSDDPackageImpl eINSTANCE = org.apache.axis.model.wsdd.impl.WSDDPackageImpl.init();

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.ParameterImpl <em>Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.ParameterImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getParameter()
     * @generated
     */
    public static final int PARAMETER = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int PARAMETER__NAME = 0;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int PARAMETER__VALUE = 1;

    /**
     * The number of structural features of the '<em>Parameter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int PARAMETER_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.MappingImpl <em>Mapping</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.MappingImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getMapping()
     * @generated
     */
    public static final int MAPPING = 1;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int MAPPING__QNAME = 0;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int MAPPING__TYPE = 1;

    /**
     * The feature id for the '<em><b>Encoding Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int MAPPING__ENCODING_STYLE = 2;

    /**
     * The number of structural features of the '<em>Mapping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int MAPPING_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.TypeMappingImpl <em>Type Mapping</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.TypeMappingImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getTypeMapping()
     * @generated
     */
    public static final int TYPE_MAPPING = 2;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TYPE_MAPPING__QNAME = MAPPING__QNAME;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TYPE_MAPPING__TYPE = MAPPING__TYPE;

    /**
     * The feature id for the '<em><b>Encoding Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TYPE_MAPPING__ENCODING_STYLE = MAPPING__ENCODING_STYLE;

    /**
     * The feature id for the '<em><b>Serializer</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TYPE_MAPPING__SERIALIZER = MAPPING_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Deserializer</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TYPE_MAPPING__DESERIALIZER = MAPPING_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Type Mapping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TYPE_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.ArrayMappingImpl <em>Array Mapping</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.ArrayMappingImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getArrayMapping()
     * @generated
     */
    public static final int ARRAY_MAPPING = 3;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int ARRAY_MAPPING__QNAME = MAPPING__QNAME;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int ARRAY_MAPPING__TYPE = MAPPING__TYPE;

    /**
     * The feature id for the '<em><b>Encoding Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int ARRAY_MAPPING__ENCODING_STYLE = MAPPING__ENCODING_STYLE;

    /**
     * The feature id for the '<em><b>Inner Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int ARRAY_MAPPING__INNER_TYPE = MAPPING_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Array Mapping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int ARRAY_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.BeanMappingImpl <em>Bean Mapping</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.BeanMappingImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getBeanMapping()
     * @generated
     */
    public static final int BEAN_MAPPING = 4;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int BEAN_MAPPING__QNAME = MAPPING__QNAME;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int BEAN_MAPPING__TYPE = MAPPING__TYPE;

    /**
     * The feature id for the '<em><b>Encoding Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int BEAN_MAPPING__ENCODING_STYLE = MAPPING__ENCODING_STYLE;

    /**
     * The number of structural features of the '<em>Bean Mapping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int BEAN_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.MappingContainerImpl <em>Mapping Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.MappingContainerImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getMappingContainer()
     * @generated
     */
    public static final int MAPPING_CONTAINER = 5;

    /**
     * The feature id for the '<em><b>Type Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int MAPPING_CONTAINER__TYPE_MAPPINGS = 0;

    /**
     * The feature id for the '<em><b>Bean Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int MAPPING_CONTAINER__BEAN_MAPPINGS = 1;

    /**
     * The feature id for the '<em><b>Array Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int MAPPING_CONTAINER__ARRAY_MAPPINGS = 2;

    /**
     * The number of structural features of the '<em>Mapping Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int MAPPING_CONTAINER_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.OperationParameterImpl <em>Operation Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.OperationParameterImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getOperationParameter()
     * @generated
     */
    public static final int OPERATION_PARAMETER = 6;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION_PARAMETER__NAME = 0;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION_PARAMETER__QNAME = 1;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION_PARAMETER__TYPE = 2;

    /**
     * The feature id for the '<em><b>Mode</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION_PARAMETER__MODE = 3;

    /**
     * The feature id for the '<em><b>In Header</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION_PARAMETER__IN_HEADER = 4;

    /**
     * The feature id for the '<em><b>Out Header</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION_PARAMETER__OUT_HEADER = 5;

    /**
     * The feature id for the '<em><b>Item QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION_PARAMETER__ITEM_QNAME = 6;

    /**
     * The number of structural features of the '<em>Operation Parameter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION_PARAMETER_FEATURE_COUNT = 7;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.FaultImpl <em>Fault</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.FaultImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getFault()
     * @generated
     */
    public static final int FAULT = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int FAULT__NAME = 0;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int FAULT__QNAME = 1;

    /**
     * The feature id for the '<em><b>Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int FAULT__CLASS = 2;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int FAULT__TYPE = 3;

    /**
     * The number of structural features of the '<em>Fault</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int FAULT_FEATURE_COUNT = 4;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.OperationImpl <em>Operation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.OperationImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getOperation()
     * @generated
     */
    public static final int OPERATION = 8;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION__NAME = 0;

    /**
     * The feature id for the '<em><b>Qname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION__QNAME = 1;

    /**
     * The feature id for the '<em><b>Return QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION__RETURN_QNAME = 2;

    /**
     * The feature id for the '<em><b>Return Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION__RETURN_TYPE = 3;

    /**
     * The feature id for the '<em><b>Return Item QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION__RETURN_ITEM_QNAME = 4;

    /**
     * The feature id for the '<em><b>Return Item Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION__RETURN_ITEM_TYPE = 5;

    /**
     * The feature id for the '<em><b>Soap Action</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION__SOAP_ACTION = 6;

    /**
     * The feature id for the '<em><b>Mep</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION__MEP = 7;

    /**
     * The feature id for the '<em><b>Return Header</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION__RETURN_HEADER = 8;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION__PARAMETERS = 9;

    /**
     * The feature id for the '<em><b>Faults</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION__FAULTS = 10;

    /**
     * The number of structural features of the '<em>Operation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int OPERATION_FEATURE_COUNT = 11;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.ParameterizableImpl <em>Parameterizable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.ParameterizableImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getParameterizable()
     * @generated
     */
    public static final int PARAMETERIZABLE = 9;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int PARAMETERIZABLE__PARAMETERS = 0;

    /**
     * The number of structural features of the '<em>Parameterizable</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int PARAMETERIZABLE_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.HandlerImpl <em>Handler</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.HandlerImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getHandler()
     * @generated
     */
    public static final int HANDLER = 10;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int HANDLER__PARAMETERS = PARAMETERIZABLE__PARAMETERS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int HANDLER__NAME = PARAMETERIZABLE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int HANDLER__TYPE = PARAMETERIZABLE_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Handler</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int HANDLER_FEATURE_COUNT = PARAMETERIZABLE_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.ChainImpl <em>Chain</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.ChainImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getChain()
     * @generated
     */
    public static final int CHAIN = 11;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int CHAIN__NAME = 0;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int CHAIN__TYPE = 1;

    /**
     * The feature id for the '<em><b>Handlers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int CHAIN__HANDLERS = 2;

    /**
     * The number of structural features of the '<em>Chain</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int CHAIN_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.DeployableItemImpl <em>Deployable Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.DeployableItemImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getDeployableItem()
     * @generated
     */
    public static final int DEPLOYABLE_ITEM = 12;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYABLE_ITEM__PARAMETERS = PARAMETERIZABLE__PARAMETERS;

    /**
     * The feature id for the '<em><b>Request Flow</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYABLE_ITEM__REQUEST_FLOW = PARAMETERIZABLE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Response Flow</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYABLE_ITEM__RESPONSE_FLOW = PARAMETERIZABLE_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Deployable Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYABLE_ITEM_FEATURE_COUNT = PARAMETERIZABLE_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.GlobalConfigurationImpl <em>Global Configuration</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.GlobalConfigurationImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getGlobalConfiguration()
     * @generated
     */
    public static final int GLOBAL_CONFIGURATION = 13;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int GLOBAL_CONFIGURATION__PARAMETERS = DEPLOYABLE_ITEM__PARAMETERS;

    /**
     * The feature id for the '<em><b>Request Flow</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int GLOBAL_CONFIGURATION__REQUEST_FLOW = DEPLOYABLE_ITEM__REQUEST_FLOW;

    /**
     * The feature id for the '<em><b>Response Flow</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int GLOBAL_CONFIGURATION__RESPONSE_FLOW = DEPLOYABLE_ITEM__RESPONSE_FLOW;

    /**
     * The number of structural features of the '<em>Global Configuration</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int GLOBAL_CONFIGURATION_FEATURE_COUNT = DEPLOYABLE_ITEM_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.TransportImpl <em>Transport</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.TransportImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getTransport()
     * @generated
     */
    public static final int TRANSPORT = 14;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TRANSPORT__PARAMETERS = DEPLOYABLE_ITEM__PARAMETERS;

    /**
     * The feature id for the '<em><b>Request Flow</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TRANSPORT__REQUEST_FLOW = DEPLOYABLE_ITEM__REQUEST_FLOW;

    /**
     * The feature id for the '<em><b>Response Flow</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TRANSPORT__RESPONSE_FLOW = DEPLOYABLE_ITEM__RESPONSE_FLOW;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TRANSPORT__NAME = DEPLOYABLE_ITEM_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Pivot</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TRANSPORT__PIVOT = DEPLOYABLE_ITEM_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Transport</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int TRANSPORT_FEATURE_COUNT = DEPLOYABLE_ITEM_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.ServiceImpl <em>Service</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.ServiceImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getService()
     * @generated
     */
    public static final int SERVICE = 15;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__PARAMETERS = DEPLOYABLE_ITEM__PARAMETERS;

    /**
     * The feature id for the '<em><b>Request Flow</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__REQUEST_FLOW = DEPLOYABLE_ITEM__REQUEST_FLOW;

    /**
     * The feature id for the '<em><b>Response Flow</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__RESPONSE_FLOW = DEPLOYABLE_ITEM__RESPONSE_FLOW;

    /**
     * The feature id for the '<em><b>Type Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__TYPE_MAPPINGS = DEPLOYABLE_ITEM_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Bean Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__BEAN_MAPPINGS = DEPLOYABLE_ITEM_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Array Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__ARRAY_MAPPINGS = DEPLOYABLE_ITEM_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__NAME = DEPLOYABLE_ITEM_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Provider</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__PROVIDER = DEPLOYABLE_ITEM_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Use</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__USE = DEPLOYABLE_ITEM_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Style</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__STYLE = DEPLOYABLE_ITEM_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Namespaces</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__NAMESPACES = DEPLOYABLE_ITEM_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Operations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE__OPERATIONS = DEPLOYABLE_ITEM_FEATURE_COUNT + 8;

    /**
     * The number of structural features of the '<em>Service</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int SERVICE_FEATURE_COUNT = DEPLOYABLE_ITEM_FEATURE_COUNT + 9;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.impl.DeploymentImpl <em>Deployment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.impl.DeploymentImpl
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getDeployment()
     * @generated
     */
    public static final int DEPLOYMENT = 16;

    /**
     * The feature id for the '<em><b>Type Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYMENT__TYPE_MAPPINGS = MAPPING_CONTAINER__TYPE_MAPPINGS;

    /**
     * The feature id for the '<em><b>Bean Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYMENT__BEAN_MAPPINGS = MAPPING_CONTAINER__BEAN_MAPPINGS;

    /**
     * The feature id for the '<em><b>Array Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYMENT__ARRAY_MAPPINGS = MAPPING_CONTAINER__ARRAY_MAPPINGS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYMENT__NAME = MAPPING_CONTAINER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Global Configuration</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYMENT__GLOBAL_CONFIGURATION = MAPPING_CONTAINER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Handlers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYMENT__HANDLERS = MAPPING_CONTAINER_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Transports</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYMENT__TRANSPORTS = MAPPING_CONTAINER_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Services</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYMENT__SERVICES = MAPPING_CONTAINER_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the '<em>Deployment</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    public static final int DEPLOYMENT_FEATURE_COUNT = MAPPING_CONTAINER_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link org.apache.axis.model.wsdd.ParameterMode <em>Parameter Mode</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.model.wsdd.ParameterMode
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getParameterMode()
     * @generated
     */
    public static final int PARAMETER_MODE = 17;

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
    private EClass mappingContainerEClass = null;

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
    private EClass parameterizableEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass handlerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass chainEClass = null;

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
    private EClass globalConfigurationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass transportEClass = null;

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
     * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#eNS_URI
     * @see #init()
     * @generated
     */
    private WSDDPackageImpl() {
        super(eNS_URI, ((EFactory)WSDDFactory.INSTANCE));
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
     * <p>This method is used to initialize {@link WSDDPackageImpl#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static WSDDPackageImpl init() {
        if (isInited) return (WSDDPackageImpl)EPackage.Registry.INSTANCE.getEPackage(WSDDPackageImpl.eNS_URI);

        // Obtain or create and register package
        WSDDPackageImpl theWSDDPackage = (WSDDPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof WSDDPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new WSDDPackageImpl());

        isInited = true;

        // Obtain or create and register interdependencies
        XmlPackageImpl theXmlPackage = (XmlPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(XmlPackageImpl.eNS_URI) instanceof XmlPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(XmlPackageImpl.eNS_URI) : XmlPackageImpl.eINSTANCE);
        SOAPPackageImpl theSOAPPackage = (SOAPPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SOAPPackageImpl.eNS_URI) instanceof SOAPPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SOAPPackageImpl.eNS_URI) : SOAPPackageImpl.eINSTANCE);

        // Create package meta-data objects
        theWSDDPackage.createPackageContents();
        theXmlPackage.createPackageContents();
        theSOAPPackage.createPackageContents();

        // Initialize created meta-data
        theWSDDPackage.initializePackageContents();
        theXmlPackage.initializePackageContents();
        theSOAPPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theWSDDPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(WSDDPackageImpl.eNS_URI, theWSDDPackage);
        return theWSDDPackage;
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Parameter <em>Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Parameter</em>'.
     * @see org.apache.axis.model.wsdd.Parameter
     * @generated
     */
    public EClass getParameter() {
        return parameterEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Parameter#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.Parameter#getName()
     * @see #getParameter()
     * @generated
     */
    public EAttribute getParameter_Name() {
        return (EAttribute)parameterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Parameter#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see org.apache.axis.model.wsdd.Parameter#getValue()
     * @see #getParameter()
     * @generated
     */
    public EAttribute getParameter_Value() {
        return (EAttribute)parameterEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Mapping <em>Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mapping</em>'.
     * @see org.apache.axis.model.wsdd.Mapping
     * @generated
     */
    public EClass getMapping() {
        return mappingEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Mapping#getQname <em>Qname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Qname</em>'.
     * @see org.apache.axis.model.wsdd.Mapping#getQname()
     * @see #getMapping()
     * @generated
     */
    public EAttribute getMapping_Qname() {
        return (EAttribute)mappingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Mapping#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.apache.axis.model.wsdd.Mapping#getType()
     * @see #getMapping()
     * @generated
     */
    public EAttribute getMapping_Type() {
        return (EAttribute)mappingEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Mapping#getEncodingStyle <em>Encoding Style</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Encoding Style</em>'.
     * @see org.apache.axis.model.wsdd.Mapping#getEncodingStyle()
     * @see #getMapping()
     * @generated
     */
    public EAttribute getMapping_EncodingStyle() {
        return (EAttribute)mappingEClass.getEStructuralFeatures().get(2);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.TypeMapping <em>Type Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Type Mapping</em>'.
     * @see org.apache.axis.model.wsdd.TypeMapping
     * @generated
     */
    public EClass getTypeMapping() {
        return typeMappingEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.TypeMapping#getSerializer <em>Serializer</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Serializer</em>'.
     * @see org.apache.axis.model.wsdd.TypeMapping#getSerializer()
     * @see #getTypeMapping()
     * @generated
     */
    public EAttribute getTypeMapping_Serializer() {
        return (EAttribute)typeMappingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.TypeMapping#getDeserializer <em>Deserializer</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Deserializer</em>'.
     * @see org.apache.axis.model.wsdd.TypeMapping#getDeserializer()
     * @see #getTypeMapping()
     * @generated
     */
    public EAttribute getTypeMapping_Deserializer() {
        return (EAttribute)typeMappingEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.ArrayMapping <em>Array Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Array Mapping</em>'.
     * @see org.apache.axis.model.wsdd.ArrayMapping
     * @generated
     */
    public EClass getArrayMapping() {
        return arrayMappingEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.ArrayMapping#getInnerType <em>Inner Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Inner Type</em>'.
     * @see org.apache.axis.model.wsdd.ArrayMapping#getInnerType()
     * @see #getArrayMapping()
     * @generated
     */
    public EAttribute getArrayMapping_InnerType() {
        return (EAttribute)arrayMappingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.BeanMapping <em>Bean Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Bean Mapping</em>'.
     * @see org.apache.axis.model.wsdd.BeanMapping
     * @generated
     */
    public EClass getBeanMapping() {
        return beanMappingEClass;
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.MappingContainer <em>Mapping Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mapping Container</em>'.
     * @see org.apache.axis.model.wsdd.MappingContainer
     * @generated
     */
    public EClass getMappingContainer() {
        return mappingContainerEClass;
    }

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.MappingContainer#getTypeMappings <em>Type Mappings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Type Mappings</em>'.
     * @see org.apache.axis.model.wsdd.MappingContainer#getTypeMappings()
     * @see #getMappingContainer()
     * @generated
     */
    public EReference getMappingContainer_TypeMappings() {
        return (EReference)mappingContainerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.MappingContainer#getBeanMappings <em>Bean Mappings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Bean Mappings</em>'.
     * @see org.apache.axis.model.wsdd.MappingContainer#getBeanMappings()
     * @see #getMappingContainer()
     * @generated
     */
    public EReference getMappingContainer_BeanMappings() {
        return (EReference)mappingContainerEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.MappingContainer#getArrayMappings <em>Array Mappings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Array Mappings</em>'.
     * @see org.apache.axis.model.wsdd.MappingContainer#getArrayMappings()
     * @see #getMappingContainer()
     * @generated
     */
    public EReference getMappingContainer_ArrayMappings() {
        return (EReference)mappingContainerEClass.getEStructuralFeatures().get(2);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.OperationParameter <em>Operation Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Operation Parameter</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter
     * @generated
     */
    public EClass getOperationParameter() {
        return operationParameterEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getName()
     * @see #getOperationParameter()
     * @generated
     */
    public EAttribute getOperationParameter_Name() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getQname <em>Qname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Qname</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getQname()
     * @see #getOperationParameter()
     * @generated
     */
    public EAttribute getOperationParameter_Qname() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getType()
     * @see #getOperationParameter()
     * @generated
     */
    public EAttribute getOperationParameter_Type() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(2);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getMode <em>Mode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Mode</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getMode()
     * @see #getOperationParameter()
     * @generated
     */
    public EAttribute getOperationParameter_Mode() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(3);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getInHeader <em>In Header</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>In Header</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getInHeader()
     * @see #getOperationParameter()
     * @generated
     */
    public EAttribute getOperationParameter_InHeader() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(4);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getOutHeader <em>Out Header</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Out Header</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getOutHeader()
     * @see #getOperationParameter()
     * @generated
     */
    public EAttribute getOperationParameter_OutHeader() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(5);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.OperationParameter#getItemQName <em>Item QName</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Item QName</em>'.
     * @see org.apache.axis.model.wsdd.OperationParameter#getItemQName()
     * @see #getOperationParameter()
     * @generated
     */
    public EAttribute getOperationParameter_ItemQName() {
        return (EAttribute)operationParameterEClass.getEStructuralFeatures().get(6);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Fault <em>Fault</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Fault</em>'.
     * @see org.apache.axis.model.wsdd.Fault
     * @generated
     */
    public EClass getFault() {
        return faultEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Fault#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.Fault#getName()
     * @see #getFault()
     * @generated
     */
    public EAttribute getFault_Name() {
        return (EAttribute)faultEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Fault#getQname <em>Qname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Qname</em>'.
     * @see org.apache.axis.model.wsdd.Fault#getQname()
     * @see #getFault()
     * @generated
     */
    public EAttribute getFault_Qname() {
        return (EAttribute)faultEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Fault#getClass_ <em>Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Class</em>'.
     * @see org.apache.axis.model.wsdd.Fault#getClass_()
     * @see #getFault()
     * @generated
     */
    public EAttribute getFault_Class() {
        return (EAttribute)faultEClass.getEStructuralFeatures().get(2);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Fault#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.apache.axis.model.wsdd.Fault#getType()
     * @see #getFault()
     * @generated
     */
    public EAttribute getFault_Type() {
        return (EAttribute)faultEClass.getEStructuralFeatures().get(3);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Operation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Operation</em>'.
     * @see org.apache.axis.model.wsdd.Operation
     * @generated
     */
    public EClass getOperation() {
        return operationEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getName()
     * @see #getOperation()
     * @generated
     */
    public EAttribute getOperation_Name() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getQname <em>Qname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Qname</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getQname()
     * @see #getOperation()
     * @generated
     */
    public EAttribute getOperation_Qname() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getReturnQName <em>Return QName</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Return QName</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getReturnQName()
     * @see #getOperation()
     * @generated
     */
    public EAttribute getOperation_ReturnQName() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getReturnType <em>Return Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Return Type</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getReturnType()
     * @see #getOperation()
     * @generated
     */
    public EAttribute getOperation_ReturnType() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(3);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getReturnItemQName <em>Return Item QName</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Return Item QName</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getReturnItemQName()
     * @see #getOperation()
     * @generated
     */
    public EAttribute getOperation_ReturnItemQName() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(4);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getReturnItemType <em>Return Item Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Return Item Type</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getReturnItemType()
     * @see #getOperation()
     * @generated
     */
    public EAttribute getOperation_ReturnItemType() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(5);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getSoapAction <em>Soap Action</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Soap Action</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getSoapAction()
     * @see #getOperation()
     * @generated
     */
    public EAttribute getOperation_SoapAction() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(6);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getMep <em>Mep</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Mep</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getMep()
     * @see #getOperation()
     * @generated
     */
    public EAttribute getOperation_Mep() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(7);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Operation#getReturnHeader <em>Return Header</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Return Header</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getReturnHeader()
     * @see #getOperation()
     * @generated
     */
    public EAttribute getOperation_ReturnHeader() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(8);
    }

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Operation#getParameters <em>Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Parameters</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getParameters()
     * @see #getOperation()
     * @generated
     */
    public EReference getOperation_Parameters() {
        return (EReference)operationEClass.getEStructuralFeatures().get(9);
    }

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Operation#getFaults <em>Faults</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Faults</em>'.
     * @see org.apache.axis.model.wsdd.Operation#getFaults()
     * @see #getOperation()
     * @generated
     */
    public EReference getOperation_Faults() {
        return (EReference)operationEClass.getEStructuralFeatures().get(10);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Parameterizable <em>Parameterizable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Parameterizable</em>'.
     * @see org.apache.axis.model.wsdd.Parameterizable
     * @generated
     */
    public EClass getParameterizable() {
        return parameterizableEClass;
    }

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Parameterizable#getParameters <em>Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Parameters</em>'.
     * @see org.apache.axis.model.wsdd.Parameterizable#getParameters()
     * @see #getParameterizable()
     * @generated
     */
    public EReference getParameterizable_Parameters() {
        return (EReference)parameterizableEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Handler <em>Handler</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Handler</em>'.
     * @see org.apache.axis.model.wsdd.Handler
     * @generated
     */
    public EClass getHandler() {
        return handlerEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Handler#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.Handler#getName()
     * @see #getHandler()
     * @generated
     */
    public EAttribute getHandler_Name() {
        return (EAttribute)handlerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Handler#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.apache.axis.model.wsdd.Handler#getType()
     * @see #getHandler()
     * @generated
     */
    public EAttribute getHandler_Type() {
        return (EAttribute)handlerEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Chain <em>Chain</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Chain</em>'.
     * @see org.apache.axis.model.wsdd.Chain
     * @generated
     */
    public EClass getChain() {
        return chainEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Chain#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.Chain#getName()
     * @see #getChain()
     * @generated
     */
    public EAttribute getChain_Name() {
        return (EAttribute)chainEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Chain#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.apache.axis.model.wsdd.Chain#getType()
     * @see #getChain()
     * @generated
     */
    public EAttribute getChain_Type() {
        return (EAttribute)chainEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Chain#getHandlers <em>Handlers</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Handlers</em>'.
     * @see org.apache.axis.model.wsdd.Chain#getHandlers()
     * @see #getChain()
     * @generated
     */
    public EReference getChain_Handlers() {
        return (EReference)chainEClass.getEStructuralFeatures().get(2);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.DeployableItem <em>Deployable Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Deployable Item</em>'.
     * @see org.apache.axis.model.wsdd.DeployableItem
     * @generated
     */
    public EClass getDeployableItem() {
        return deployableItemEClass;
    }

    /**
     * Returns the meta object for the containment reference '{@link org.apache.axis.model.wsdd.DeployableItem#getRequestFlow <em>Request Flow</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Request Flow</em>'.
     * @see org.apache.axis.model.wsdd.DeployableItem#getRequestFlow()
     * @see #getDeployableItem()
     * @generated
     */
    public EReference getDeployableItem_RequestFlow() {
        return (EReference)deployableItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the containment reference '{@link org.apache.axis.model.wsdd.DeployableItem#getResponseFlow <em>Response Flow</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Response Flow</em>'.
     * @see org.apache.axis.model.wsdd.DeployableItem#getResponseFlow()
     * @see #getDeployableItem()
     * @generated
     */
    public EReference getDeployableItem_ResponseFlow() {
        return (EReference)deployableItemEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.GlobalConfiguration <em>Global Configuration</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Global Configuration</em>'.
     * @see org.apache.axis.model.wsdd.GlobalConfiguration
     * @generated
     */
    public EClass getGlobalConfiguration() {
        return globalConfigurationEClass;
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Transport <em>Transport</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Transport</em>'.
     * @see org.apache.axis.model.wsdd.Transport
     * @generated
     */
    public EClass getTransport() {
        return transportEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Transport#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.Transport#getName()
     * @see #getTransport()
     * @generated
     */
    public EAttribute getTransport_Name() {
        return (EAttribute)transportEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Transport#getPivot <em>Pivot</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Pivot</em>'.
     * @see org.apache.axis.model.wsdd.Transport#getPivot()
     * @see #getTransport()
     * @generated
     */
    public EAttribute getTransport_Pivot() {
        return (EAttribute)transportEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Service <em>Service</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Service</em>'.
     * @see org.apache.axis.model.wsdd.Service
     * @generated
     */
    public EClass getService() {
        return serviceEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Service#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.Service#getName()
     * @see #getService()
     * @generated
     */
    public EAttribute getService_Name() {
        return (EAttribute)serviceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Service#getProvider <em>Provider</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Provider</em>'.
     * @see org.apache.axis.model.wsdd.Service#getProvider()
     * @see #getService()
     * @generated
     */
    public EAttribute getService_Provider() {
        return (EAttribute)serviceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Service#getUse <em>Use</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Use</em>'.
     * @see org.apache.axis.model.wsdd.Service#getUse()
     * @see #getService()
     * @generated
     */
    public EAttribute getService_Use() {
        return (EAttribute)serviceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Service#getStyle <em>Style</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Style</em>'.
     * @see org.apache.axis.model.wsdd.Service#getStyle()
     * @see #getService()
     * @generated
     */
    public EAttribute getService_Style() {
        return (EAttribute)serviceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * Returns the meta object for the attribute list '{@link org.apache.axis.model.wsdd.Service#getNamespaces <em>Namespaces</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Namespaces</em>'.
     * @see org.apache.axis.model.wsdd.Service#getNamespaces()
     * @see #getService()
     * @generated
     */
    public EAttribute getService_Namespaces() {
        return (EAttribute)serviceEClass.getEStructuralFeatures().get(4);
    }

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Service#getOperations <em>Operations</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Operations</em>'.
     * @see org.apache.axis.model.wsdd.Service#getOperations()
     * @see #getService()
     * @generated
     */
    public EReference getService_Operations() {
        return (EReference)serviceEClass.getEStructuralFeatures().get(5);
    }

    /**
     * Returns the meta object for class '{@link org.apache.axis.model.wsdd.Deployment <em>Deployment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Deployment</em>'.
     * @see org.apache.axis.model.wsdd.Deployment
     * @generated
     */
    public EClass getDeployment() {
        return deploymentEClass;
    }

    /**
     * Returns the meta object for the attribute '{@link org.apache.axis.model.wsdd.Deployment#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.apache.axis.model.wsdd.Deployment#getName()
     * @see #getDeployment()
     * @generated
     */
    public EAttribute getDeployment_Name() {
        return (EAttribute)deploymentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Returns the meta object for the containment reference '{@link org.apache.axis.model.wsdd.Deployment#getGlobalConfiguration <em>Global Configuration</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Global Configuration</em>'.
     * @see org.apache.axis.model.wsdd.Deployment#getGlobalConfiguration()
     * @see #getDeployment()
     * @generated
     */
    public EReference getDeployment_GlobalConfiguration() {
        return (EReference)deploymentEClass.getEStructuralFeatures().get(1);
    }

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Deployment#getHandlers <em>Handlers</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Handlers</em>'.
     * @see org.apache.axis.model.wsdd.Deployment#getHandlers()
     * @see #getDeployment()
     * @generated
     */
    public EReference getDeployment_Handlers() {
        return (EReference)deploymentEClass.getEStructuralFeatures().get(2);
    }

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Deployment#getTransports <em>Transports</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Transports</em>'.
     * @see org.apache.axis.model.wsdd.Deployment#getTransports()
     * @see #getDeployment()
     * @generated
     */
    public EReference getDeployment_Transports() {
        return (EReference)deploymentEClass.getEStructuralFeatures().get(3);
    }

    /**
     * Returns the meta object for the containment reference list '{@link org.apache.axis.model.wsdd.Deployment#getServices <em>Services</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Services</em>'.
     * @see org.apache.axis.model.wsdd.Deployment#getServices()
     * @see #getDeployment()
     * @generated
     */
    public EReference getDeployment_Services() {
        return (EReference)deploymentEClass.getEStructuralFeatures().get(4);
    }

    /**
     * Returns the meta object for enum '{@link org.apache.axis.model.wsdd.ParameterMode <em>Parameter Mode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Parameter Mode</em>'.
     * @see org.apache.axis.model.wsdd.ParameterMode
     * @generated
     */
    public EEnum getParameterMode() {
        return parameterModeEEnum;
    }

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
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

        mappingContainerEClass = createEClass(MAPPING_CONTAINER);
        createEReference(mappingContainerEClass, MAPPING_CONTAINER__TYPE_MAPPINGS);
        createEReference(mappingContainerEClass, MAPPING_CONTAINER__BEAN_MAPPINGS);
        createEReference(mappingContainerEClass, MAPPING_CONTAINER__ARRAY_MAPPINGS);

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

        parameterizableEClass = createEClass(PARAMETERIZABLE);
        createEReference(parameterizableEClass, PARAMETERIZABLE__PARAMETERS);

        handlerEClass = createEClass(HANDLER);
        createEAttribute(handlerEClass, HANDLER__NAME);
        createEAttribute(handlerEClass, HANDLER__TYPE);

        chainEClass = createEClass(CHAIN);
        createEAttribute(chainEClass, CHAIN__NAME);
        createEAttribute(chainEClass, CHAIN__TYPE);
        createEReference(chainEClass, CHAIN__HANDLERS);

        deployableItemEClass = createEClass(DEPLOYABLE_ITEM);
        createEReference(deployableItemEClass, DEPLOYABLE_ITEM__REQUEST_FLOW);
        createEReference(deployableItemEClass, DEPLOYABLE_ITEM__RESPONSE_FLOW);

        globalConfigurationEClass = createEClass(GLOBAL_CONFIGURATION);

        transportEClass = createEClass(TRANSPORT);
        createEAttribute(transportEClass, TRANSPORT__NAME);
        createEAttribute(transportEClass, TRANSPORT__PIVOT);

        serviceEClass = createEClass(SERVICE);
        createEAttribute(serviceEClass, SERVICE__NAME);
        createEAttribute(serviceEClass, SERVICE__PROVIDER);
        createEAttribute(serviceEClass, SERVICE__USE);
        createEAttribute(serviceEClass, SERVICE__STYLE);
        createEAttribute(serviceEClass, SERVICE__NAMESPACES);
        createEReference(serviceEClass, SERVICE__OPERATIONS);

        deploymentEClass = createEClass(DEPLOYMENT);
        createEAttribute(deploymentEClass, DEPLOYMENT__NAME);
        createEReference(deploymentEClass, DEPLOYMENT__GLOBAL_CONFIGURATION);
        createEReference(deploymentEClass, DEPLOYMENT__HANDLERS);
        createEReference(deploymentEClass, DEPLOYMENT__TRANSPORTS);
        createEReference(deploymentEClass, DEPLOYMENT__SERVICES);

        // Create enums
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
        XmlPackageImpl theXmlPackage = (XmlPackageImpl)EPackage.Registry.INSTANCE.getEPackage(XmlPackageImpl.eNS_URI);
        SOAPPackageImpl theSOAPPackage = (SOAPPackageImpl)EPackage.Registry.INSTANCE.getEPackage(SOAPPackageImpl.eNS_URI);

        // Add supertypes to classes
        typeMappingEClass.getESuperTypes().add(this.getMapping());
        arrayMappingEClass.getESuperTypes().add(this.getMapping());
        beanMappingEClass.getESuperTypes().add(this.getMapping());
        handlerEClass.getESuperTypes().add(this.getParameterizable());
        deployableItemEClass.getESuperTypes().add(this.getParameterizable());
        globalConfigurationEClass.getESuperTypes().add(this.getDeployableItem());
        transportEClass.getESuperTypes().add(this.getDeployableItem());
        serviceEClass.getESuperTypes().add(this.getDeployableItem());
        serviceEClass.getESuperTypes().add(this.getMappingContainer());
        deploymentEClass.getESuperTypes().add(this.getMappingContainer());

        // Initialize classes and features; add operations and parameters
        initEClass(parameterEClass, Parameter.class, "Parameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getParameter_Name(), ecorePackage.getEString(), "name", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getParameter_Value(), ecorePackage.getEString(), "value", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(mappingEClass, Mapping.class, "Mapping", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getMapping_Qname(), theXmlPackage.getQName(), "qname", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMapping_Type(), theXmlPackage.getQName(), "type", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMapping_EncodingStyle(), ecorePackage.getEString(), "encodingStyle", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(typeMappingEClass, TypeMapping.class, "TypeMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getTypeMapping_Serializer(), ecorePackage.getEString(), "serializer", null, 0, 1, TypeMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTypeMapping_Deserializer(), ecorePackage.getEString(), "deserializer", null, 0, 1, TypeMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(arrayMappingEClass, ArrayMapping.class, "ArrayMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getArrayMapping_InnerType(), theXmlPackage.getQName(), "innerType", null, 0, 1, ArrayMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(beanMappingEClass, BeanMapping.class, "BeanMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(mappingContainerEClass, MappingContainer.class, "MappingContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getMappingContainer_TypeMappings(), this.getTypeMapping(), null, "typeMappings", null, 0, -1, MappingContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getMappingContainer_BeanMappings(), this.getBeanMapping(), null, "beanMappings", null, 0, -1, MappingContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getMappingContainer_ArrayMappings(), this.getArrayMapping(), null, "arrayMappings", null, 0, -1, MappingContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(operationParameterEClass, OperationParameter.class, "OperationParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getOperationParameter_Name(), ecorePackage.getEString(), "name", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_Qname(), theXmlPackage.getQName(), "qname", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_Type(), theXmlPackage.getQName(), "type", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_Mode(), this.getParameterMode(), "mode", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_InHeader(), ecorePackage.getEBooleanObject(), "inHeader", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_OutHeader(), ecorePackage.getEBooleanObject(), "outHeader", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperationParameter_ItemQName(), theXmlPackage.getQName(), "itemQName", null, 0, 1, OperationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(faultEClass, Fault.class, "Fault", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getFault_Name(), ecorePackage.getEString(), "name", null, 0, 1, Fault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFault_Qname(), theXmlPackage.getQName(), "qname", null, 0, 1, Fault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFault_Class(), ecorePackage.getEString(), "class", null, 0, 1, Fault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFault_Type(), theXmlPackage.getQName(), "type", null, 0, 1, Fault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(operationEClass, Operation.class, "Operation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getOperation_Name(), ecorePackage.getEString(), "name", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_Qname(), theXmlPackage.getQName(), "qname", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_ReturnQName(), theXmlPackage.getQName(), "returnQName", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_ReturnType(), theXmlPackage.getQName(), "returnType", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_ReturnItemQName(), theXmlPackage.getQName(), "returnItemQName", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_ReturnItemType(), theXmlPackage.getQName(), "returnItemType", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_SoapAction(), ecorePackage.getEString(), "soapAction", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_Mep(), ecorePackage.getEString(), "mep", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getOperation_ReturnHeader(), ecorePackage.getEBooleanObject(), "returnHeader", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getOperation_Parameters(), this.getOperationParameter(), null, "parameters", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getOperation_Faults(), this.getFault(), null, "faults", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(parameterizableEClass, Parameterizable.class, "Parameterizable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getParameterizable_Parameters(), this.getParameter(), null, "parameters", null, 0, -1, Parameterizable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        EOperation op = addEOperation(parameterizableEClass, null, "setParameter");
        addEParameter(op, ecorePackage.getEString(), "name", 1, 1);
        addEParameter(op, ecorePackage.getEString(), "value", 1, 1);

        initEClass(handlerEClass, Handler.class, "Handler", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getHandler_Name(), ecorePackage.getEString(), "name", null, 0, 1, Handler.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getHandler_Type(), theXmlPackage.getQName(), "type", null, 0, 1, Handler.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(chainEClass, Chain.class, "Chain", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getChain_Name(), ecorePackage.getEString(), "name", null, 0, 1, Chain.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getChain_Type(), theXmlPackage.getQName(), "type", null, 0, 1, Chain.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getChain_Handlers(), this.getHandler(), null, "handlers", null, 0, -1, Chain.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(deployableItemEClass, DeployableItem.class, "DeployableItem", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getDeployableItem_RequestFlow(), this.getChain(), null, "requestFlow", null, 0, 1, DeployableItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDeployableItem_ResponseFlow(), this.getChain(), null, "responseFlow", null, 0, 1, DeployableItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(globalConfigurationEClass, GlobalConfiguration.class, "GlobalConfiguration", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(transportEClass, Transport.class, "Transport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getTransport_Name(), ecorePackage.getEString(), "name", null, 0, 1, Transport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTransport_Pivot(), theXmlPackage.getQName(), "pivot", null, 0, 1, Transport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(serviceEClass, Service.class, "Service", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getService_Name(), ecorePackage.getEString(), "name", null, 0, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getService_Provider(), theXmlPackage.getQName(), "provider", null, 0, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getService_Use(), theSOAPPackage.getUse(), "use", null, 0, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getService_Style(), theSOAPPackage.getStyle(), "style", null, 0, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getService_Namespaces(), ecorePackage.getEString(), "namespaces", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getService_Operations(), this.getOperation(), null, "operations", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(deploymentEClass, Deployment.class, "Deployment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDeployment_Name(), ecorePackage.getEString(), "name", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDeployment_GlobalConfiguration(), this.getGlobalConfiguration(), null, "globalConfiguration", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDeployment_Handlers(), this.getHandler(), null, "handlers", null, 0, -1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDeployment_Transports(), this.getTransport(), null, "transports", null, 0, -1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDeployment_Services(), this.getService(), null, "services", null, 0, -1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        op = addEOperation(deploymentEClass, null, "merge");
        addEParameter(op, this.getDeployment(), "other", 1, 1);

        // Initialize enums and add enum literals
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
          (getMappingContainer_TypeMappings(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "typeMapping",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getMappingContainer_BeanMappings(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "beanMapping",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getMappingContainer_ArrayMappings(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "arrayMapping",
             "namespace", "##targetNamespace"
           });		
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
          (getParameterizable_Parameters(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "parameter",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getChain_Handlers(), 
           source, 
           new String[] {
             "name", "handler",
             "kind", "element",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getDeployableItem_RequestFlow(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "requestFlow",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getDeployableItem_ResponseFlow(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "responseFlow",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getService_Namespaces(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "namespace",
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
          (deploymentEClass, 
           source, 
           new String[] {
             "name", "deployment",
             "kind", "element"
           });		
        addAnnotation
          (getDeployment_GlobalConfiguration(), 
           source, 
           new String[] {
             "kind", "element",
             "name", "globalConfiguration",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getDeployment_Handlers(), 
           source, 
           new String[] {
             "name", "handler",
             "kind", "element",
             "namespace", "##targetNamespace"
           });		
        addAnnotation
          (getDeployment_Transports(), 
           source, 
           new String[] {
             "name", "transport",
             "kind", "element",
             "namespace", "##targetNamespace"
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
    public interface Literals {
        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.ParameterImpl <em>Parameter</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.ParameterImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getParameter()
         * @generated
         */
        public static final EClass PARAMETER = eINSTANCE.getParameter();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute PARAMETER__NAME = eINSTANCE.getParameter_Name();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute PARAMETER__VALUE = eINSTANCE.getParameter_Value();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.MappingImpl <em>Mapping</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.MappingImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getMapping()
         * @generated
         */
        public static final EClass MAPPING = eINSTANCE.getMapping();

        /**
         * The meta object literal for the '<em><b>Qname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute MAPPING__QNAME = eINSTANCE.getMapping_Qname();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute MAPPING__TYPE = eINSTANCE.getMapping_Type();

        /**
         * The meta object literal for the '<em><b>Encoding Style</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute MAPPING__ENCODING_STYLE = eINSTANCE.getMapping_EncodingStyle();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.TypeMappingImpl <em>Type Mapping</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.TypeMappingImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getTypeMapping()
         * @generated
         */
        public static final EClass TYPE_MAPPING = eINSTANCE.getTypeMapping();

        /**
         * The meta object literal for the '<em><b>Serializer</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute TYPE_MAPPING__SERIALIZER = eINSTANCE.getTypeMapping_Serializer();

        /**
         * The meta object literal for the '<em><b>Deserializer</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute TYPE_MAPPING__DESERIALIZER = eINSTANCE.getTypeMapping_Deserializer();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.ArrayMappingImpl <em>Array Mapping</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.ArrayMappingImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getArrayMapping()
         * @generated
         */
        public static final EClass ARRAY_MAPPING = eINSTANCE.getArrayMapping();

        /**
         * The meta object literal for the '<em><b>Inner Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute ARRAY_MAPPING__INNER_TYPE = eINSTANCE.getArrayMapping_InnerType();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.BeanMappingImpl <em>Bean Mapping</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.BeanMappingImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getBeanMapping()
         * @generated
         */
        public static final EClass BEAN_MAPPING = eINSTANCE.getBeanMapping();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.MappingContainerImpl <em>Mapping Container</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.MappingContainerImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getMappingContainer()
         * @generated
         */
        public static final EClass MAPPING_CONTAINER = eINSTANCE.getMappingContainer();

        /**
         * The meta object literal for the '<em><b>Type Mappings</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference MAPPING_CONTAINER__TYPE_MAPPINGS = eINSTANCE.getMappingContainer_TypeMappings();

        /**
         * The meta object literal for the '<em><b>Bean Mappings</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference MAPPING_CONTAINER__BEAN_MAPPINGS = eINSTANCE.getMappingContainer_BeanMappings();

        /**
         * The meta object literal for the '<em><b>Array Mappings</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference MAPPING_CONTAINER__ARRAY_MAPPINGS = eINSTANCE.getMappingContainer_ArrayMappings();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.OperationParameterImpl <em>Operation Parameter</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.OperationParameterImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getOperationParameter()
         * @generated
         */
        public static final EClass OPERATION_PARAMETER = eINSTANCE.getOperationParameter();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION_PARAMETER__NAME = eINSTANCE.getOperationParameter_Name();

        /**
         * The meta object literal for the '<em><b>Qname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION_PARAMETER__QNAME = eINSTANCE.getOperationParameter_Qname();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION_PARAMETER__TYPE = eINSTANCE.getOperationParameter_Type();

        /**
         * The meta object literal for the '<em><b>Mode</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION_PARAMETER__MODE = eINSTANCE.getOperationParameter_Mode();

        /**
         * The meta object literal for the '<em><b>In Header</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION_PARAMETER__IN_HEADER = eINSTANCE.getOperationParameter_InHeader();

        /**
         * The meta object literal for the '<em><b>Out Header</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION_PARAMETER__OUT_HEADER = eINSTANCE.getOperationParameter_OutHeader();

        /**
         * The meta object literal for the '<em><b>Item QName</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION_PARAMETER__ITEM_QNAME = eINSTANCE.getOperationParameter_ItemQName();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.FaultImpl <em>Fault</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.FaultImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getFault()
         * @generated
         */
        public static final EClass FAULT = eINSTANCE.getFault();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute FAULT__NAME = eINSTANCE.getFault_Name();

        /**
         * The meta object literal for the '<em><b>Qname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute FAULT__QNAME = eINSTANCE.getFault_Qname();

        /**
         * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute FAULT__CLASS = eINSTANCE.getFault_Class();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute FAULT__TYPE = eINSTANCE.getFault_Type();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.OperationImpl <em>Operation</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.OperationImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getOperation()
         * @generated
         */
        public static final EClass OPERATION = eINSTANCE.getOperation();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION__NAME = eINSTANCE.getOperation_Name();

        /**
         * The meta object literal for the '<em><b>Qname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION__QNAME = eINSTANCE.getOperation_Qname();

        /**
         * The meta object literal for the '<em><b>Return QName</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION__RETURN_QNAME = eINSTANCE.getOperation_ReturnQName();

        /**
         * The meta object literal for the '<em><b>Return Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION__RETURN_TYPE = eINSTANCE.getOperation_ReturnType();

        /**
         * The meta object literal for the '<em><b>Return Item QName</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION__RETURN_ITEM_QNAME = eINSTANCE.getOperation_ReturnItemQName();

        /**
         * The meta object literal for the '<em><b>Return Item Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION__RETURN_ITEM_TYPE = eINSTANCE.getOperation_ReturnItemType();

        /**
         * The meta object literal for the '<em><b>Soap Action</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION__SOAP_ACTION = eINSTANCE.getOperation_SoapAction();

        /**
         * The meta object literal for the '<em><b>Mep</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION__MEP = eINSTANCE.getOperation_Mep();

        /**
         * The meta object literal for the '<em><b>Return Header</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute OPERATION__RETURN_HEADER = eINSTANCE.getOperation_ReturnHeader();

        /**
         * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference OPERATION__PARAMETERS = eINSTANCE.getOperation_Parameters();

        /**
         * The meta object literal for the '<em><b>Faults</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference OPERATION__FAULTS = eINSTANCE.getOperation_Faults();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.ParameterizableImpl <em>Parameterizable</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.ParameterizableImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getParameterizable()
         * @generated
         */
        public static final EClass PARAMETERIZABLE = eINSTANCE.getParameterizable();

        /**
         * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference PARAMETERIZABLE__PARAMETERS = eINSTANCE.getParameterizable_Parameters();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.HandlerImpl <em>Handler</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.HandlerImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getHandler()
         * @generated
         */
        public static final EClass HANDLER = eINSTANCE.getHandler();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute HANDLER__NAME = eINSTANCE.getHandler_Name();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute HANDLER__TYPE = eINSTANCE.getHandler_Type();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.ChainImpl <em>Chain</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.ChainImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getChain()
         * @generated
         */
        public static final EClass CHAIN = eINSTANCE.getChain();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute CHAIN__NAME = eINSTANCE.getChain_Name();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute CHAIN__TYPE = eINSTANCE.getChain_Type();

        /**
         * The meta object literal for the '<em><b>Handlers</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference CHAIN__HANDLERS = eINSTANCE.getChain_Handlers();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.DeployableItemImpl <em>Deployable Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.DeployableItemImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getDeployableItem()
         * @generated
         */
        public static final EClass DEPLOYABLE_ITEM = eINSTANCE.getDeployableItem();

        /**
         * The meta object literal for the '<em><b>Request Flow</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference DEPLOYABLE_ITEM__REQUEST_FLOW = eINSTANCE.getDeployableItem_RequestFlow();

        /**
         * The meta object literal for the '<em><b>Response Flow</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference DEPLOYABLE_ITEM__RESPONSE_FLOW = eINSTANCE.getDeployableItem_ResponseFlow();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.GlobalConfigurationImpl <em>Global Configuration</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.GlobalConfigurationImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getGlobalConfiguration()
         * @generated
         */
        public static final EClass GLOBAL_CONFIGURATION = eINSTANCE.getGlobalConfiguration();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.TransportImpl <em>Transport</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.TransportImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getTransport()
         * @generated
         */
        public static final EClass TRANSPORT = eINSTANCE.getTransport();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute TRANSPORT__NAME = eINSTANCE.getTransport_Name();

        /**
         * The meta object literal for the '<em><b>Pivot</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute TRANSPORT__PIVOT = eINSTANCE.getTransport_Pivot();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.ServiceImpl <em>Service</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.ServiceImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getService()
         * @generated
         */
        public static final EClass SERVICE = eINSTANCE.getService();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute SERVICE__NAME = eINSTANCE.getService_Name();

        /**
         * The meta object literal for the '<em><b>Provider</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute SERVICE__PROVIDER = eINSTANCE.getService_Provider();

        /**
         * The meta object literal for the '<em><b>Use</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute SERVICE__USE = eINSTANCE.getService_Use();

        /**
         * The meta object literal for the '<em><b>Style</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute SERVICE__STYLE = eINSTANCE.getService_Style();

        /**
         * The meta object literal for the '<em><b>Namespaces</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute SERVICE__NAMESPACES = eINSTANCE.getService_Namespaces();

        /**
         * The meta object literal for the '<em><b>Operations</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference SERVICE__OPERATIONS = eINSTANCE.getService_Operations();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.impl.DeploymentImpl <em>Deployment</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.impl.DeploymentImpl
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getDeployment()
         * @generated
         */
        public static final EClass DEPLOYMENT = eINSTANCE.getDeployment();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EAttribute DEPLOYMENT__NAME = eINSTANCE.getDeployment_Name();

        /**
         * The meta object literal for the '<em><b>Global Configuration</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference DEPLOYMENT__GLOBAL_CONFIGURATION = eINSTANCE.getDeployment_GlobalConfiguration();

        /**
         * The meta object literal for the '<em><b>Handlers</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference DEPLOYMENT__HANDLERS = eINSTANCE.getDeployment_Handlers();

        /**
         * The meta object literal for the '<em><b>Transports</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference DEPLOYMENT__TRANSPORTS = eINSTANCE.getDeployment_Transports();

        /**
         * The meta object literal for the '<em><b>Services</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public static final EReference DEPLOYMENT__SERVICES = eINSTANCE.getDeployment_Services();

        /**
         * The meta object literal for the '{@link org.apache.axis.model.wsdd.ParameterMode <em>Parameter Mode</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.model.wsdd.ParameterMode
         * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getParameterMode()
         * @generated
         */
        public static final EEnum PARAMETER_MODE = eINSTANCE.getParameterMode();

    }

} //WSDDPackageImpl
