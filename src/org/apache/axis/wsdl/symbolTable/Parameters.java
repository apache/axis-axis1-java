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

import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;

/**
 * This class simply collects all the parameter or message data for an operation into one place.
 */
public class Parameters {

    // This vector contains instances of the Parameter class
    public Vector list = new Vector();

    // The type of the first output part, used as the method's return value
    public TypeEntry returnType = null;

    // The name of the return type (from the part name of the output message.
    // Used to create the RPCParam for the return value.
    public QName returnName = null;

    // The MIME type of the return, null if it doesn't exist.
    public String returnMIMEType = null;

    // A map of the faults
    public Map faults = null;

    // The signature that the interface and the stub will use
    public String signature = null;

    // The numbers of the respective parameters
    public int inputs = 0;
    public int inouts = 0;
    public int outputs = 0;

    public String toString() {
        return "\nreturnType = " + returnType
                + "\nreturnName = " + returnName
                + "\nfaults = " + faults
                + "\nsignature = " + signature
                + "\n(inputs, inouts, outputs) = (" + inputs + ", " + inouts + ", " + outputs + ")"
                + "\nlist = " + list;
    } // toString
} // class Parameters
