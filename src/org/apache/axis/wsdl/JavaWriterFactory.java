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
package org.apache.axis.wsdl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.Service;

/**
* This is Wsdl2java's implementation of the WriterFactory.
*/

public class JavaWriterFactory implements WriterFactory {
    private Emitter emitter;

    /**
     * Default constructor.  Note that this class is unusable until setEmitter
     * is called.
     */
    public JavaWriterFactory() {
    } // ctor

    /**
     * Do the Wsdl2java writer pass:
     * - resolve name clashes
     * - construct signatures
     */
    public void writerPass(Definition def, SymbolTable symbolTable) {
        javifyNames(symbolTable);
        resolveNameClashes(symbolTable);
        ignoreNonSOAPBindingPortTypes(symbolTable);
        constructSignatures(symbolTable);
        determineIfHoldersNeeded(symbolTable);
    } // writerPass

    /**
     * Provide the emitter object to this class.
     */
    public void setEmitter(Emitter emitter) {
        this.emitter = emitter;
    } // setEmitter

    /**
     * Since Wsdl2java doesn't emit anything for Messages, return the No-op writer.
     */
    public Writer getWriter(Message message, SymbolTable symbolTable) {
        return new NoopWriter();
    } // getWriter

    /**
     * Return Wsdl2java's JavaPortTypeWriter object.
     */
    public Writer getWriter(PortType portType, SymbolTable symbolTable) {
        return new JavaPortTypeWriter(emitter, portType, symbolTable);
    } // getWriter

    /**
     * Return Wsdl2java's JavaBindingWriter object.
     */
    public Writer getWriter(Binding binding, SymbolTable symbolTable) {
        return new JavaBindingWriter(emitter, binding, symbolTable);
    } // getWriter

    /**
     * Return Wsdl2java's JavaServiceWriter object.
     */
    public Writer getWriter(Service service, SymbolTable symbolTable) {
        return new JavaServiceWriter(emitter, service, symbolTable);
    } // getWriter

    /**
     * Return Wsdl2java's JavaTypeWriter object.
     */
    public Writer getWriter(Type type, SymbolTable symbolTable) {
        return new JavaTypeWriter(emitter, type, symbolTable);
    } // getWriter

    /**
     * Return Wsdl2java's JavaDefinitionWriter object.
     */
    public Writer getWriter(Definition definition, SymbolTable symbolTable) {
        return new JavaDefinitionWriter(emitter, definition, symbolTable);
    } // getWriter

    /**
     * Fill in the names of each SymTabEntry with the javaified name
     */
    private void javifyNames(SymbolTable symbolTable) {
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector) it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);

                // If entry instanceof Type, then the java name has already been filled in.
                // Don't try to do it again.  This method should really be doing the filling in
                // of ALL enty java names, but that's another step toward generalizing the
                // framework that I don't have time for right now.
                if (!(entry instanceof Type)) {
                    entry.setName(symbolTable.getJavaName(entry.getQName()));
                }
            }
        }
    } // javifyNames

    /**
     * Messages, PortTypes, Bindings, and Services can share the same name.  If they do in this
     * Definition, force their names to be suffixed with _PortType and _Service, respectively.
     */
    private void resolveNameClashes(SymbolTable symbolTable) {
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector) it.next();
            if (v.size() > 1) {
                for (int i = 0; i < v.size(); ++i) {
                    SymTabEntry entry = (SymTabEntry) v.elementAt(i);
                    if (entry instanceof ElementType) {
                        entry.setName(entry.getName() + "_ElemType");
                    }
                    else if (entry instanceof Type) {
                        entry.setName(entry.getName() + "_Type");
                    }
                    else if (entry instanceof PortTypeEntry) {
                        entry.setName(entry.getName() + "_Port");
                    }
                    else if (entry instanceof ServiceEntry) {
                        entry.setName(entry.getName() + "_Service");
                    }
                    // else if (entry instanceof MessageEntry) {
                    //     we don't care about messages
                    // }
                    // else if (entry instanceof BindingEntry) {
                    //     since files generated from bindings all append strings to the name,
                    //     we don't care about bindings
                    // }
                }
            }
        }
    } // resolveNameClashes

    /**
     * If a binding's type is not TYPE_SOAP, then we don't use that binding's portType.
     */
    private void ignoreNonSOAPBindingPortTypes(SymbolTable symbolTable) {

        // Look at all uses of the portTypes.  If none of the portType's bindings are of type
        // TYPE_SOAP, then turn off that portType's isReferenced flag.

        Vector unusedPortTypes = new Vector();
        Vector usedPortTypes = new Vector();

        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector) it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);
                if (entry instanceof BindingEntry) {
                    BindingEntry bEntry = (BindingEntry) entry;
                    Binding binding = bEntry.getBinding();
                    PortType portType = binding.getPortType();
                    PortTypeEntry ptEntry =
                      symbolTable.getPortTypeEntry(portType.getQName());

                    if (bEntry.getBindingType() == BindingEntry.TYPE_SOAP) {
                        // If a binding is of type TYPE_SOAP, then mark its portType used
                        // (ie., add it to the usedPortTypes list.  If the portType was
                        // previously marked as unused, unmark it (in other words, remove it
                        // from the unusedPortTypes list).
                        usedPortTypes.add(ptEntry);
                        if (unusedPortTypes.contains(ptEntry)) {
                            unusedPortTypes.remove(ptEntry);
                        }
                    }
                    else {
                        // If a binding is not of type TYPE_SOAP, then mark its portType as
                        // unused ONLY if it hasn't already been marked as used.
                        if (!usedPortTypes.contains(ptEntry)) {
                            unusedPortTypes.add(ptEntry);
                        }
                    }
                }
            }
        }

        // Go through all the portTypes that are marked as unused and set their isReferenced flags
        // to false.
        for (int i = 0; i < unusedPortTypes.size(); ++i) {
            PortTypeEntry ptEntry = (PortTypeEntry) unusedPortTypes.get(i);
            ptEntry.setIsReferenced(false);
        }
    } // ignoreNonSOAPBindingPortTypes

    private void constructSignatures(SymbolTable symbolTable) {
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector) it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);
                if (entry instanceof PortTypeEntry) {
                    PortTypeEntry ptEntry = (PortTypeEntry) entry;
                    PortType portType = ptEntry.getPortType();
                    // Remove Duplicates - happens with only a few WSDL's. No idea why!!! 
                    // (like http://www.xmethods.net/tmodels/InteropTest.wsdl) 
                    // TODO: Remove this patch...
                    // NOTE from RJB:  this is a WSDL4J bug and the WSDL4J guys have been
                    // notified.
                    Iterator operations =
                            new HashSet(portType.getOperations()).iterator();
                    while(operations.hasNext()) {
                        Operation operation = (Operation) operations.next();
                        String name = operation.getName();
                        constructSignatures(ptEntry.getParameters(name), name);
                    }
                }
            }
        }
    } // constructSignatures

    /**
     * Construct the signatures.  signature is used by both the interface and the stub.
     * skelSig is used by the skeleton.
     */
    private void constructSignatures(Parameters parms, String opName) {
        String name  = Utils.xmlNameToJava(opName);
        int allOuts = parms.outputs + parms.inouts;
        String ret = parms.returnType == null ? "void" : parms.returnType.getName();
        String signature = "    public " + ret + " " + name + "(";
        String skelSig = null;

        if (allOuts == 0)
            skelSig = "    public void " + name + "(";
        else
            skelSig = "    public Object " + name + "(";

        boolean needComma = false;

        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter) parms.list.get(i);

            if (needComma) {
                signature = signature + ", ";
                if (p.mode != Parameter.OUT)
                    skelSig = skelSig + ", ";
            }
            else
                needComma = true;

            String javifiedName = Utils.xmlNameToJava(p.name);
            if (p.mode == Parameter.IN) {
                signature = signature + p.type.getName() + " " + javifiedName;
                skelSig = skelSig + p.type.getName() + " " + javifiedName;
            }
            else if (p.mode == Parameter.INOUT) {
                signature = signature + Utils.holder(p.type) + " " +
                        javifiedName;
                skelSig = skelSig + p.type.getName() + " " + javifiedName;
            }
            else// (p.mode == Parameter.OUT)
            {
                signature = signature + Utils.holder(p.type) + " " +
                        javifiedName;
            }
        }
        signature = signature + ") throws java.rmi.RemoteException";
        skelSig = skelSig + ") throws java.rmi.RemoteException";
        if (parms.faultString != null) {
            signature = signature + ", " + parms.faultString;
            skelSig = skelSig + ", " + parms.faultString;
        }
        parms.signature = signature;
        parms.skelSignature = skelSig;
    } // constructSignatures

    /**
     * Find all inout/out parameters and add a flag to the Type of that parameter saying a holder
     * is needed.
     */
    private void determineIfHoldersNeeded(SymbolTable symbolTable) {
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector) it.next();
            for (int i = 0; i < v.size(); ++i) {
                if (v.get(i) instanceof PortTypeEntry) {

                        // If entry is a portTypeEntry, look at all the Parameters
                    PortTypeEntry ptEntry = (PortTypeEntry) v.get(i);
                    Iterator operations =
                      ptEntry.getParameters().values().iterator();
                    while (operations.hasNext()) {
                        Parameters parms = (Parameters) operations.next();
                        for (int j = 0; j < parms.list.size(); ++j) {
                            Parameter p =
                                    (Parameter)parms.list.get(j);

                            // If the given parameter is an inout or out parameter, then
                            // set a HOLDER_IS_NEEDED flag using the dynamicVar design.
                            if (p.mode != Parameter.IN) {
                                p.type.setDynamicVar(
                                        JavaTypeWriter.HOLDER_IS_NEEDED,
                                        new Boolean(true));
                            }
                        }
                    }
                }
            }
        }
    } // determineIfHoldersNeeded

} // class JavaWriterFactory
