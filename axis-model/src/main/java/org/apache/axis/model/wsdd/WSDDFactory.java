/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @generated
 */
public interface WSDDFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    WSDDFactory INSTANCE = org.apache.axis.model.wsdd.impl.WSDDFactoryImpl.eINSTANCE;
    /**
     * Returns a new object of class '<em>Parameter</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Parameter</em>'.
     * @generated
     */
    Parameter createParameter();

    /**
     * Returns a new object of class '<em>Flow</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Flow</em>'.
     * @generated
     */
    Flow createFlow();

    /**
     * Returns a new object of class '<em>Type Mapping</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Type Mapping</em>'.
     * @generated
     */
    TypeMapping createTypeMapping();

    /**
     * Returns a new object of class '<em>Array Mapping</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Array Mapping</em>'.
     * @generated
     */
    ArrayMapping createArrayMapping();

    /**
     * Returns a new object of class '<em>Bean Mapping</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Bean Mapping</em>'.
     * @generated
     */
    BeanMapping createBeanMapping();

    /**
     * Returns a new object of class '<em>Mapping Container</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Mapping Container</em>'.
     * @generated
     */
    MappingContainer createMappingContainer();

    /**
     * Returns a new object of class '<em>Operation Parameter</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Operation Parameter</em>'.
     * @generated
     */
    OperationParameter createOperationParameter();

    /**
     * Returns a new object of class '<em>Fault</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Fault</em>'.
     * @generated
     */
    Fault createFault();

    /**
     * Returns a new object of class '<em>Operation</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Operation</em>'.
     * @generated
     */
    Operation createOperation();

    /**
     * Returns a new object of class '<em>Parameterizable</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Parameterizable</em>'.
     * @generated
     */
    Parameterizable createParameterizable();

    /**
     * Returns a new object of class '<em>Handler</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Handler</em>'.
     * @generated
     */
    Handler createHandler();

    /**
     * Returns a new object of class '<em>Global Configuration</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Global Configuration</em>'.
     * @generated
     */
    GlobalConfiguration createGlobalConfiguration();

    /**
     * Returns a new object of class '<em>Transport</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Transport</em>'.
     * @generated
     */
    Transport createTransport();

    /**
     * Returns a new object of class '<em>Service</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Service</em>'.
     * @generated
     */
    Service createService();

    /**
     * Returns a new object of class '<em>Deployment</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Deployment</em>'.
     * @generated
     */
    Deployment createDeployment();

} //WSDDFactory
