/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.axis.wsdl.symbolTable;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis.utils.JavaUtils;

import org.w3c.dom.Node;

/**
 * This class represents a wsdl types entry that is supported by the WSDL2Java emitter.
 * A TypeEntry has a QName representing its XML name and a name, which in the
 * WSDL2Java back end is its full java name.  The TypeEntry may also have a Node,
 * which locates the definition of the emit type in the xml.
 * A TypeEntry object extends SymTabEntry and is built by the SymbolTable class for
 * each supported root complexType, simpleType, and elements that are
 * defined or encountered.
 *
 *                    SymTabEntry
 *                        |
 *                    TypeEntry
 *                  /           \
 *            Type                Element
 *             |                     |
 * (BaseType,                    (DefinedElement,
 *  CollectionType                UndefinedElement)
 *  DefinedType,
 *  UndefinedType)
 *
 *  UndefinedType and UndefinedElement are placeholders when the real type or element
 *  is not encountered yet.  Both of these implement the Undefined interface.
 *  
 *  A TypeEntry whose java (or other language) name depends on an Undefined type, will
 *  have its name initialization deferred until the Undefined type is replaced with
 *  a defined type.  The updateUndefined() method is invoked by the UndefinedDelegate to
 *  update the information.
 *
 *  Each TypeEntry whose language name depends on another TypeEntry will have the refType
 *  field set.  For example:
 *      <element name="foo" type="bar" />
 *  The TypeEntry for "foo" will have a refType set to the TypeEntry of "bar".
 * 
 *  Another Example:
 *     <xsd:complexType name="hobbyArray">
 *       <xsd:complexContent>
 *         <xsd:restriction base="soapenc:Array">
 *           <xsd:attribute ref="soapenc:arrayType" wsdl:arrayType="xsd:string[]"/>
 *         </xsd:restriction>
 *       </xsd:complexContent>
 *     </xsd:complexType>
 *  The TypeEntry for "hobbyArray" will have a refType that locates the TypeEntry for xsd:string
 *  and the dims field will be "[]"
 *
 *
 *
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 */
public abstract class TypeEntry extends SymTabEntry {

    protected Node    node;      // Node

    protected TypeEntry refType; // Some TypeEntries refer to other types.
                                 

    protected String  dims ="";  // If refType is an element, dims indicates 
                                 // the array dims (for example "[]").
                              
    protected boolean undefined; // If refType is an Undefined type 
                                 // (or has a refType that is Undefined) 
                                 // then the undefined flag is set.
                                 //  The name cannot be determined
                                 // until the Undefined type is found.
    protected boolean isBaseType;// Indicates if represented by a 
                                 // primitive or util class
    protected boolean isSimpleType = false; // Indicates if this type is a simple type
    protected boolean onlyLiteralReference = false; // Indicates
                                 // whether this type is only referenced
                                 // via a binding's literal use.

    /**
     * Create a TypeEntry object for an xml construct that references another type. 
     * Defer processing until refType is known.
     */  
    protected TypeEntry(QName pqName, TypeEntry refType, Node pNode, String dims) {
        super(pqName);
        node = pNode;
        this.undefined = refType.undefined;
        this.refType = refType;
        if (dims == null)
            dims = "";
        this.dims = dims;
        
        if (refType.undefined) {
            // Need to defer processing until known.
            TypeEntry uType = refType;
            while (!(uType instanceof Undefined)) {
                uType = uType.refType;
            }
            ((Undefined)uType).register(this);
        } else {
            isBaseType = (refType.isBaseType && refType.dims.equals("") && dims.equals(""));
        }
        
        //System.out.println(toString());

    }
       
    /**
     * Create a TypeEntry object for an xml construct that is not a base type
     */  
    protected TypeEntry(QName pqName, Node pNode) {
        super(pqName);
        node  = pNode;
        refType = null;
        undefined = false;
        dims = "";
        isBaseType = false;
        //System.out.println(toString());
    }

    /**
     * Create a TypeEntry object for an xml construct name that represents a base type
     */
    protected TypeEntry(QName pqName) {
        super(pqName);
        node = null;
        undefined = false;
        dims = "";
        isBaseType = true;
        //System.out.println(toString());
    }
       
    /**
     * Query the node for this type.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Returns the Base Type Name.
     * For example if the Type represents a schema integer, "int" is returned.
     * If this is a user defined type, null is returned.
     */
    public String getBaseType() {
        if (isBaseType) {
            return name;
        }
        else {
            return null;
        }
    }

    public boolean isSimpleType() {
        return isSimpleType;
    }

    public void setSimpleType(boolean simpleType) {
        isSimpleType = simpleType;
    }

    /**
     * Is this type references ONLY as a literal type?  If a binding's
     * message's soapBody says:  use="literal", then a type is referenced
     * literally.  Note that that type's contained types (ie., an address
     * contains a phone#) are not referenced literally.  Since a type
     * that is ONLY referenced as a literal may cause a generator to act
     * differently (like WSDL2Java), this extra reference distinction is
     * needed.
     */
    public boolean isOnlyLiteralReferenced() {
        return onlyLiteralReference;
    } // isOnlyLiteralReferenced

    /**
     * Set the isOnlyLiteralReference flag.
     */
    public void setOnlyLiteralReference(boolean set) {
        onlyLiteralReference = set;
    } // setOnlyLiteralRefeerence

    /**
     * getUndefinedTypeRef returns the Undefined TypeEntry that this entry depends on or NULL.
     */
    protected TypeEntry getUndefinedTypeRef() {
        if (this instanceof Undefined) 
            return this;
        if (undefined && refType != null) {
            if (refType.undefined) {
                TypeEntry uType = refType;
                while (!(uType instanceof Undefined)) {
                    uType = uType.refType;
                }
                return uType;
            }
        }
        return null;
    }

    /**
     * UpdateUndefined is called when the ref TypeEntry is finally known.
     * @param oldRef The TypeEntry representing the Undefined TypeEntry
     * @param newRef The replacement TypeEntry
     * @return true if TypeEntry is changed in any way.
     */
    protected boolean updateUndefined(TypeEntry oldRef, TypeEntry newRef) throws IOException {
        boolean changedState = false;
        // Replace refType with the new one if applicable
        if (refType == oldRef) {
            refType = newRef;
            changedState = true;
            // Detect a loop
            TypeEntry te = refType;
            while(te != null && te != this) {
                te = te.refType;
            }
            if (te == this) {
                // Detected a loop.
                undefined = false;
                isBaseType = false;
                node = null;                   
                throw new IOException(JavaUtils.getMessage("undefinedloop00", getQName().toString()));
            }
        }

        // Update information if refType is now defined
        if (refType != null && undefined && refType.undefined==false) {
            undefined = false;
            changedState = true;
            isBaseType = (refType.isBaseType && refType.dims.equals("") && dims.equals(""));
        }
        return changedState;
    }


    /**
     * If this type references another type, return that type, otherwise return null.
     */
    public TypeEntry getRefType() {
        return refType;
    } // getRefType

    public void setRefType(TypeEntry refType) {
        this.refType = refType;
    }

    /**
     * Return the dimensions of this type, which can be 0 or more "[]".
     */
    public String getDimensions() {
        return dims;
    } // getDimensions

    /**
     * Get string representation.
     */
    public String toString() {
        return toString("");
    }

    /**
     * Get string representation with indentation
     */
    protected String toString(String indent) {
        String refString = indent + "RefType:       null \n";
        if (refType != null)
            refString = indent + "RefType:\n" + refType.toString(indent + "  ") + "\n";
        return super.toString(indent) + 
            indent + "Class:         " + this.getClass().getName() + "\n" + 
            indent + "Base?:         " + isBaseType + "\n" + 
            indent + "Undefined?:    " + undefined + "\n" + 
            indent + "isSimpleType?  " + isSimpleType + "\n" +
            indent + "Node:          " + getNode() + "\n" +
            indent + "Dims:          " + dims + "\n" +
            refString;
    }
};
