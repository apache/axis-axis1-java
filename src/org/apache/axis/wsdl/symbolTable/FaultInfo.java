/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.axis.enum.Use;
import org.apache.axis.utils.Messages;

import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.wsdl.extensions.soap.SOAPHeaderFault;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.Map;

/**
 * Fault information object.  This should probably really be FaultEntry and
 * it should be a subclass of SymTabEntry, but faults aren't first-class
 * objects in WSDL, so I'm not sure what the FaultEntry should contain and
 * how it should be constructed, so for now leave it as a simple object.
 */
public class FaultInfo {

    /** Field message */
    private Message message;

    /** Field xmlType */
    private QName xmlType;

    /** Field use */
    private Use use;

    /** Field qName */
    private QName qName;

    /** Field name */
    private String name;

    /**
     * This constructor creates FaultInfo for a binding fault.
     * <p/>
     * If the part of the fault is a type, then the QName is
     * derived from the element name and the provided namespace
     * (this namespace SHOULD come from the binding).
     * <p/>
     * If the part of the fault is an element, then the QName is
     * the QName of the element, and the given namespace is ignored.
     * 
     * @param fault       
     * @param use         
     * @param namespace   
     * @param symbolTable 
     */
    public FaultInfo(Fault fault, Use use, String namespace,
                     SymbolTable symbolTable) {

        this.message = fault.getMessage();
        this.xmlType = getFaultType(symbolTable, getFaultPart());
        this.use = use;
        this.name = fault.getName();

        Part part = getFaultPart();

        if (part == null) {
            this.qName = null;
        } else if (part.getTypeName() != null) {
            this.qName = new QName(namespace, part.getName());
        } else {
            this.qName = part.getElementName();
        }
    }    // ctor

    /**
     * This constructor creates FaultInfo for a soap:headerFault.
     * 
     * @param fault       
     * @param symbolTable 
     * @throws IOException 
     */
    public FaultInfo(SOAPHeaderFault fault, SymbolTable symbolTable)
            throws IOException {

        MessageEntry mEntry = symbolTable.getMessageEntry(fault.getMessage());

        if (mEntry == null) {
            throw new IOException(
                    Messages.getMessage("noMsg", fault.getMessage().toString()));
        }

        this.message = mEntry.getMessage();

        Part part = message.getPart(fault.getPart());

        this.xmlType = getFaultType(symbolTable, part);
        this.use = Use.getUse(fault.getUse());

        if (part == null) {
            this.qName = null;
        } else if (part.getTypeName() != null) {
            this.qName = new QName(fault.getNamespaceURI(), part.getName());
        } else {
            this.qName = part.getElementName();
        }

        this.name = qName.getLocalPart();
    }    // ctor

    /**
     * Constructor FaultInfo
     * 
     * @param faultMessage      
     * @param faultPart         
     * @param faultUse          
     * @param faultNamespaceURI 
     * @param symbolTable       
     * @throws IOException 
     */
    public FaultInfo(
            QName faultMessage, String faultPart, String faultUse, String faultNamespaceURI, SymbolTable symbolTable)
            throws IOException {

        MessageEntry mEntry = symbolTable.getMessageEntry(faultMessage);

        if (mEntry == null) {
            throw new IOException(Messages.getMessage("noMsg",
                    faultMessage.toString()));
        }

        this.message = mEntry.getMessage();

        Part part = message.getPart(faultPart);

        this.xmlType = getFaultType(symbolTable, part);
        this.use = Use.getUse(faultUse);

        if (part == null) {
            this.qName = null;
        } else if (part.getTypeName() != null) {
            this.qName = new QName(faultNamespaceURI, part.getName());
        } else {
            this.qName = part.getElementName();
        }

        this.name = qName.getLocalPart();
    }    // ctor

    /**
     * Method getMessage
     * 
     * @return 
     */
    public Message getMessage() {
        return message;
    }    // getMessage

    /**
     * Method getXMLType
     * 
     * @return 
     */
    public QName getXMLType() {
        return xmlType;
    }    // getXMLType

    /**
     * Method getUse
     * 
     * @return 
     */
    public Use getUse() {
        return use;
    }    // getUse

    /**
     * Return the QName of a fault.  This method may return null if no parts
     * are in the fault message.
     * <p/>
     * If the part of the fault is a type, then the QName is
     * derived from the element name and the provided namespace
     * (this namespace SHOULD come from the binding).
     * <p/>
     * If the part of the fault is an element, then the QName is
     * the QName of the element, and the given namespace is ignored.
     * 
     * @return 
     */
    public QName getQName() {
        return qName;
    }    // getQName

    /**
     * Return the name of the fault.
     * This is the name= attribute from a portType fault
     * or the localname of a header fault.
     * 
     * @return 
     */
    public String getName() {
        return name;
    }    // getName

    /**
     * It is assumed at this point that the fault message has only
     * 0 or 1 parts.  If 0, return null.  If 1, return it.
     * 
     * @return 
     */
    private Part getFaultPart() {

        // get the name of the part
        Map parts = message.getParts();

        // If no parts, skip it
        if (parts.size() == 0) {
            return null;
        } else {
            return (Part) parts.values().iterator().next();
        }
    }

    /**
     * Get the XML type (QName) for a Fault - look in the (single) fault
     * part for type="" or element="" - if type, return the QName.  If
     * element, return the reference type for the element.
     * 
     * @param part the Fault to dig into
     * @param st   the SymbolTable we're using
     * @return the XML type of the Fault's part, or null
     */
    private QName getFaultType(SymbolTable st, Part part) {

        if (part != null) {
            if (part.getTypeName() != null) {
                return part.getTypeName();
            }

            // Literal, so get the element's type
            TypeEntry entry = st.getElement(part.getElementName());

            if ((entry != null) && (entry.getRefType() != null)) {
                return entry.getRefType().getQName();
            }
        }

        return null;
    }
}
