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
 * A representation of the literals of the enumeration '<em><b>Use</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getUse()
 * @model
 * @generated
 */
public final class Use extends InternalUse {
    /**
     * The '<em><b>ENCODED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ENCODED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #ENCODED_LITERAL
     * @model literal="encoded"
     * @generated
     * @ordered
     */
    public static final int ENCODED = 0;

    /**
     * The '<em><b>LITERAL</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>LITERAL</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #LITERAL_LITERAL
     * @model literal="literal"
     * @generated
     * @ordered
     */
    public static final int LITERAL = 1;

    /**
     * The '<em><b>ENCODED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ENCODED
     * @generated
     * @ordered
     */
    public static final Use ENCODED_LITERAL = new Use(ENCODED, "ENCODED", "encoded");

    /**
     * The '<em><b>LITERAL</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #LITERAL
     * @generated
     * @ordered
     */
    public static final Use LITERAL_LITERAL = new Use(LITERAL, "LITERAL", "literal");

    /**
     * An array of all the '<em><b>Use</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final Use[] VALUES_ARRAY =
        new Use[] {
            ENCODED_LITERAL,
            LITERAL_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Use</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Use</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Use get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            Use result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Use</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Use getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            Use result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Use</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Use get(int value) {
        switch (value) {
            case ENCODED: return ENCODED_LITERAL;
            case LITERAL: return LITERAL_LITERAL;
        }
        return null;
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private Use(int value, String name, String literal) {
        super(value, name, literal);
    }

} //Use

/**
 * A private implementation class to construct the instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
class InternalUse extends org.eclipse.emf.common.util.AbstractEnumerator {
    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected InternalUse(int value, String name, String literal) {
        super(value, name, literal);
    }
}
