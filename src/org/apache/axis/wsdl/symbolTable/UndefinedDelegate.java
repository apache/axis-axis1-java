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

import java.io.IOException;
import java.util.Vector;

/**
 * This UndefinedDelegate class implements the common functions of UndefinedType and UndefinedElement.
 */
public class UndefinedDelegate implements Undefined {

    /** Field list */
    private Vector list;

    /** Field undefinedType */
    private TypeEntry undefinedType;

    /**
     * Constructor
     * 
     * @param te 
     */
    UndefinedDelegate(TypeEntry te) {
        list = new Vector();
        undefinedType = te;
    }

    /**
     * Register referrant TypeEntry so that
     * the code can update the TypeEntry when the Undefined Element or Type is defined
     * 
     * @param referrant 
     */
    public void register(TypeEntry referrant) {
        list.add(referrant);
    }

    /**
     * Call update with the actual TypeEntry.  This updates all of the
     * referrant TypeEntry's that were registered.
     * 
     * @param def 
     * @throws IOException 
     */
    public void update(TypeEntry def) throws IOException {

        boolean done = false;

        while (!done) {
            done = true;             // Assume this is the last pass

            // Call updatedUndefined for all items on the list
            // updateUndefined returns true if the state of the te TypeEntry
            // is changed.  The outer loop is traversed until there are no more
            // state changes.
            for (int i = 0; i < list.size(); i++) {
                TypeEntry te = (TypeEntry) list.elementAt(i);

                if (te.updateUndefined(undefinedType, def)) {
                    done = false;    // Items still undefined, need another pass
                }
            }
        }

        // It is possible that the def TypeEntry depends on an Undefined type.
        // If so, register all of the entries with the undefined type.
        TypeEntry uType = def.getUndefinedTypeRef();

        if (uType != null) {
            for (int i = 0; i < list.size(); i++) {
                TypeEntry te = (TypeEntry) list.elementAt(i);

                ((Undefined) uType).register(te);
            }
        }
    }
}
