/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.axis.model.wsdd;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Parameter Mode</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getParameterMode()
 * @model
 * @generated
 */
public final class ParameterMode extends InternalParameterMode {
    /**
     * The '<em><b>IN</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>IN</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #IN_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int IN = 1;

    /**
     * The '<em><b>OUT</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>OUT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #OUT_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int OUT = 2;

    /**
     * The '<em><b>INOUT</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>INOUT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #INOUT_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int INOUT = 3;

    /**
     * The '<em><b>IN</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #IN
     * @generated
     * @ordered
     */
    public static final ParameterMode IN_LITERAL = new ParameterMode(IN, "IN", "IN");

    /**
     * The '<em><b>OUT</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OUT
     * @generated
     * @ordered
     */
    public static final ParameterMode OUT_LITERAL = new ParameterMode(OUT, "OUT", "OUT");

    /**
     * The '<em><b>INOUT</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #INOUT
     * @generated
     * @ordered
     */
    public static final ParameterMode INOUT_LITERAL = new ParameterMode(INOUT, "INOUT", "INOUT");

    /**
     * An array of all the '<em><b>Parameter Mode</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final ParameterMode[] VALUES_ARRAY =
        new ParameterMode[] {
            IN_LITERAL,
            OUT_LITERAL,
            INOUT_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Parameter Mode</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Parameter Mode</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ParameterMode get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ParameterMode result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Parameter Mode</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ParameterMode getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ParameterMode result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Parameter Mode</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ParameterMode get(int value) {
        switch (value) {
            case IN: return IN_LITERAL;
            case OUT: return OUT_LITERAL;
            case INOUT: return INOUT_LITERAL;
        }
        return null;
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private ParameterMode(int value, String name, String literal) {
        super(value, name, literal);
    }

} //ParameterMode

/**
 * A private implementation class to construct the instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
class InternalParameterMode extends org.eclipse.emf.common.util.AbstractEnumerator {
    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected InternalParameterMode(int value, String name, String literal) {
        super(value, name, literal);
    }
}
