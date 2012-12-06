/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.soap.impl;

import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;

import org.apache.axis.model.soap.SOAPFactory;

import org.apache.axis.model.wsdd.impl.WSDDPackageImpl;

import org.apache.axis.model.xml.impl.XmlPackageImpl;

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
 * @see org.apache.axis.model.soap.SOAPFactory
 * @model kind="package"
 * @generated
 */
public class SOAPPackageImpl extends EPackageImpl {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String eNAME = "soap";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String eNS_URI = "http://axis.apache.org/emf/soap";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String eNS_PREFIX = "soap";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final SOAPPackageImpl eINSTANCE = org.apache.axis.model.soap.impl.SOAPPackageImpl.init();

    /**
     * The meta object id for the '<em>Use</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.constants.Use
     * @see org.apache.axis.model.soap.impl.SOAPPackageImpl#getUse()
     * @generated
     */
    public static final int USE = 0;

    /**
     * The meta object id for the '<em>Style</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.axis.constants.Style
     * @see org.apache.axis.model.soap.impl.SOAPPackageImpl#getStyle()
     * @generated
     */
    public static final int STYLE = 1;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType useEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType styleEDataType = null;

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
     * @see org.apache.axis.model.soap.impl.SOAPPackageImpl#eNS_URI
     * @see #init()
     * @generated
     */
    private SOAPPackageImpl() {
        super(eNS_URI, ((EFactory)SOAPFactory.INSTANCE));
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
     * <p>This method is used to initialize {@link SOAPPackageImpl#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static SOAPPackageImpl init() {
        if (isInited) return (SOAPPackageImpl)EPackage.Registry.INSTANCE.getEPackage(SOAPPackageImpl.eNS_URI);

        // Obtain or create and register package
        SOAPPackageImpl theSOAPPackage = (SOAPPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof SOAPPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new SOAPPackageImpl());

        isInited = true;

        // Obtain or create and register interdependencies
        WSDDPackageImpl theWSDDPackage = (WSDDPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(WSDDPackageImpl.eNS_URI) instanceof WSDDPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(WSDDPackageImpl.eNS_URI) : WSDDPackageImpl.eINSTANCE);
        XmlPackageImpl theXmlPackage = (XmlPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(XmlPackageImpl.eNS_URI) instanceof XmlPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(XmlPackageImpl.eNS_URI) : XmlPackageImpl.eINSTANCE);

        // Create package meta-data objects
        theSOAPPackage.createPackageContents();
        theWSDDPackage.createPackageContents();
        theXmlPackage.createPackageContents();

        // Initialize created meta-data
        theSOAPPackage.initializePackageContents();
        theWSDDPackage.initializePackageContents();
        theXmlPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theSOAPPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(SOAPPackageImpl.eNS_URI, theSOAPPackage);
        return theSOAPPackage;
    }


    /**
     * Returns the meta object for data type '{@link org.apache.axis.constants.Use <em>Use</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Use</em>'.
     * @see org.apache.axis.constants.Use
     * @model instanceClass="org.apache.axis.constants.Use"
     * @generated
     */
    public EDataType getUse() {
        return useEDataType;
    }

    /**
     * Returns the meta object for data type '{@link org.apache.axis.constants.Style <em>Style</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Style</em>'.
     * @see org.apache.axis.constants.Style
     * @model instanceClass="org.apache.axis.constants.Style"
     * @generated
     */
    public EDataType getStyle() {
        return styleEDataType;
    }

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    public SOAPFactory getSOAPFactory() {
        return (SOAPFactory)getEFactoryInstance();
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
        useEDataType = createEDataType(USE);
        styleEDataType = createEDataType(STYLE);
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
        initEDataType(useEDataType, Use.class, "Use", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        initEDataType(styleEDataType, Style.class, "Style", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

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
         * The meta object literal for the '<em>Use</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.constants.Use
         * @see org.apache.axis.model.soap.impl.SOAPPackageImpl#getUse()
         * @generated
         */
        public static final EDataType USE = eINSTANCE.getUse();

        /**
         * The meta object literal for the '<em>Style</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.apache.axis.constants.Style
         * @see org.apache.axis.model.soap.impl.SOAPPackageImpl#getStyle()
         * @generated
         */
        public static final EDataType STYLE = eINSTANCE.getStyle();

    }

} //SOAPPackageImpl
