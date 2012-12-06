/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.xml.impl;

import javax.xml.namespace.QName;

import org.apache.axis.model.util.AxisXMLResource;
import org.apache.axis.model.xml.*;

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
public class XmlFactoryImpl extends EFactoryImpl implements XmlFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final XmlFactoryImpl eINSTANCE = init();

    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static XmlFactoryImpl init() {
        try {
            XmlFactoryImpl theXmlFactory = (XmlFactoryImpl)EPackage.Registry.INSTANCE.getEFactory("http://axis.apache.org/emf/xml"); 
            if (theXmlFactory != null) {
                return theXmlFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new XmlFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlFactoryImpl() {
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
            case XmlPackageImpl.QNAME:
                return createQNameFromString(eDataType, initialValue);
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
            case XmlPackageImpl.QNAME:
                return convertQNameToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    public QName createQNameFromString(EDataType eDataType, String initialValue) {
        throw new UnsupportedOperationException("Please use " + AxisXMLResource.class.getName() + " to load the model from XML");
    }

    public String convertQNameToString(EDataType eDataType, Object instanceValue) {
        throw new UnsupportedOperationException("Please use " + AxisXMLResource.class.getName() + " to save the model to XML");
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlPackageImpl getXmlPackage() {
        return (XmlPackageImpl)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    public static XmlPackageImpl getPackage() {
        return XmlPackageImpl.eINSTANCE;
    }

} //XmlFactoryImpl
