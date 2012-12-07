/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.xml.impl;

import javax.xml.namespace.QName;

import org.apache.axis.model.soap.impl.SOAPPackageImpl;
import org.apache.axis.model.wsdd.impl.WSDDPackageImpl;

import org.apache.axis.model.xml.XmlFactory;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

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
 * @see org.apache.axis.model.xml.XmlFactory
 * @model kind="package"
 * @generated
 */
public class XmlPackageImpl extends EPackageImpl {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String eNAME = "xml";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String eNS_URI = "http://axis.apache.org/emf/xml";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String eNS_PREFIX = "xsd";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final XmlPackageImpl eINSTANCE = org.apache.axis.model.xml.impl.XmlPackageImpl.init();

    /**
     * The meta object id for the '<em>QName</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see javax.xml.namespace.QName
     * @see org.apache.axis.model.xml.impl.XmlPackageImpl#getQName()
     * @generated
     */
    public static final int QNAME = 0;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType qNameEDataType = null;

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
     * @see org.apache.axis.model.xml.impl.XmlPackageImpl#eNS_URI
     * @see #init()
     * @generated
     */
    private XmlPackageImpl() {
        super(eNS_URI, ((EFactory)XmlFactory.INSTANCE));
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
     * <p>This method is used to initialize {@link XmlPackageImpl#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static XmlPackageImpl init() {
        if (isInited) return (XmlPackageImpl)EPackage.Registry.INSTANCE.getEPackage(XmlPackageImpl.eNS_URI);

        // Obtain or create and register package
        XmlPackageImpl theXmlPackage = (XmlPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof XmlPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new XmlPackageImpl());

        isInited = true;

        // Obtain or create and register interdependencies
        WSDDPackageImpl theWSDDPackage = (WSDDPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(WSDDPackageImpl.eNS_URI) instanceof WSDDPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(WSDDPackageImpl.eNS_URI) : WSDDPackageImpl.eINSTANCE);
        SOAPPackageImpl theSOAPPackage = (SOAPPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SOAPPackageImpl.eNS_URI) instanceof SOAPPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SOAPPackageImpl.eNS_URI) : SOAPPackageImpl.eINSTANCE);

        // Create package meta-data objects
        theXmlPackage.createPackageContents();
        theWSDDPackage.createPackageContents();
        theSOAPPackage.createPackageContents();

        // Initialize created meta-data
        theXmlPackage.initializePackageContents();
        theWSDDPackage.initializePackageContents();
        theSOAPPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theXmlPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(XmlPackageImpl.eNS_URI, theXmlPackage);
        return theXmlPackage;
    }


    /**
     * Returns the meta object for data type '{@link javax.xml.namespace.QName <em>QName</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>QName</em>'.
     * @see javax.xml.namespace.QName
     * @model instanceClass="javax.xml.namespace.QName"
     * @generated
     */
    public EDataType getQName() {
        return qNameEDataType;
    }

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    public XmlFactory getXmlFactory() {
        return (XmlFactory)getEFactoryInstance();
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

        // Create data types
        qNameEDataType = createEDataType(QNAME);
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

        // Initialize data types
        initEDataType(qNameEDataType, QName.class, "QName", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
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
         * The meta object literal for the '<em>QName</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see javax.xml.namespace.QName
         * @see org.apache.axis.model.xml.impl.XmlPackageImpl#getQName()
         * @generated
         */
        public static final EDataType QNAME = eINSTANCE.getQName();

    }

} //XmlPackageImpl
