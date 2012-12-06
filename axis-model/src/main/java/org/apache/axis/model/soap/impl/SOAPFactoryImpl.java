/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.soap.impl;

import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;

import org.apache.axis.model.soap.*;

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
public class SOAPFactoryImpl extends EFactoryImpl implements SOAPFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final SOAPFactoryImpl eINSTANCE = init();

    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static SOAPFactoryImpl init() {
        try {
            SOAPFactoryImpl theSOAPFactory = (SOAPFactoryImpl)EPackage.Registry.INSTANCE.getEFactory("http://axis.apache.org/emf/soap"); 
            if (theSOAPFactory != null) {
                return theSOAPFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new SOAPFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SOAPFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
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
            case SOAPPackageImpl.USE:
                return createUseFromString(eDataType, initialValue);
            case SOAPPackageImpl.STYLE:
                return createStyleFromString(eDataType, initialValue);
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
            case SOAPPackageImpl.USE:
                return convertUseToString(eDataType, instanceValue);
            case SOAPPackageImpl.STYLE:
                return convertStyleToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    public Use createUseFromString(EDataType eDataType, String initialValue) {
        return Use.getUse(initialValue);
    }

    public String convertUseToString(EDataType eDataType, Object instanceValue) {
        return ((Use)instanceValue).getName();
    }

    public Style createStyleFromString(EDataType eDataType, String initialValue) {
        return Style.getStyle(initialValue);
    }

    public String convertStyleToString(EDataType eDataType, Object instanceValue) {
        return ((Style)instanceValue).getName();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SOAPPackageImpl getSOAPPackage() {
        return (SOAPPackageImpl)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    public static SOAPPackageImpl getPackage() {
        return SOAPPackageImpl.eINSTANCE;
    }

} //SOAPFactoryImpl
