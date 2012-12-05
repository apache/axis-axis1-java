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
 * A representation of the literals of the enumeration '<em><b>Style</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.apache.axis.model.wsdd.impl.WSDDPackageImpl#getStyle()
 * @model
 * @generated
 */
public final class Style extends InternalStyle {
    /**
     * The '<em><b>RPC</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>RPC</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #RPC_LITERAL
     * @model literal="rpc"
     * @generated
     * @ordered
     */
    public static final int RPC = 0;

    /**
     * The '<em><b>DOCUMENT</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DOCUMENT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DOCUMENT_LITERAL
     * @model literal="document"
     * @generated
     * @ordered
     */
    public static final int DOCUMENT = 1;

    /**
     * The '<em><b>WRAPPED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>WRAPPED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #WRAPPED_LITERAL
     * @model literal="wrapped"
     * @generated
     * @ordered
     */
    public static final int WRAPPED = 2;

    /**
     * The '<em><b>MESSAGE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>MESSAGE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #MESSAGE_LITERAL
     * @model literal="message"
     * @generated
     * @ordered
     */
    public static final int MESSAGE = 3;

    /**
     * The '<em><b>RPC</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #RPC
     * @generated
     * @ordered
     */
    public static final Style RPC_LITERAL = new Style(RPC, "RPC", "rpc");

    /**
     * The '<em><b>DOCUMENT</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DOCUMENT
     * @generated
     * @ordered
     */
    public static final Style DOCUMENT_LITERAL = new Style(DOCUMENT, "DOCUMENT", "document");

    /**
     * The '<em><b>WRAPPED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #WRAPPED
     * @generated
     * @ordered
     */
    public static final Style WRAPPED_LITERAL = new Style(WRAPPED, "WRAPPED", "wrapped");

    /**
     * The '<em><b>MESSAGE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #MESSAGE
     * @generated
     * @ordered
     */
    public static final Style MESSAGE_LITERAL = new Style(MESSAGE, "MESSAGE", "message");

    /**
     * An array of all the '<em><b>Style</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final Style[] VALUES_ARRAY =
        new Style[] {
            RPC_LITERAL,
            DOCUMENT_LITERAL,
            WRAPPED_LITERAL,
            MESSAGE_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Style</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Style</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Style get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            Style result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Style</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Style getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            Style result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Style</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Style get(int value) {
        switch (value) {
            case RPC: return RPC_LITERAL;
            case DOCUMENT: return DOCUMENT_LITERAL;
            case WRAPPED: return WRAPPED_LITERAL;
            case MESSAGE: return MESSAGE_LITERAL;
        }
        return null;
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private Style(int value, String name, String literal) {
        super(value, name, literal);
    }

} //Style

/**
 * A private implementation class to construct the instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
class InternalStyle extends org.eclipse.emf.common.util.AbstractEnumerator {
    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected InternalStyle(int value, String name, String literal) {
        super(value, name, literal);
    }
}
