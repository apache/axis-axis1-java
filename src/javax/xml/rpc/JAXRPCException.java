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
package javax.xml.rpc;

/**
 * The javax.xml.rpc.JAXRPCException is thrown from the core JAX-RPC APIs to indicate exceptions related to the JAX-RPC 
 * runtime mechanisms. A JAXRPCException is mapped to a java.rmi.RemoteException if the former is thrown during the processing of a remote method invocation. 
 *
 * @version 0.1
 */
public class JAXRPCException extends RuntimeException {

    Throwable cause;
    
    /**
     * Constructs a new exception with null as its detail message. 
     */
    public JAXRPCException() {}

    /**
     * Constructs a new exception with the specified detail message. 
     *
     * @param message exception message
     */
    public JAXRPCException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new exception which wraps another exception.
     * 
     * @param cause the exception to wrap
     */ 
    public JAXRPCException(Throwable cause) {
        this.cause = cause;
    }
    
    /**
     * Contructs a new exception with the specified detail message and
     * wraps another exception.
     * 
     * @param message exception message
     * @param cause the exception to wrap
     */ 
    public JAXRPCException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    /**
     * Returns the exception wrapped by this JAXRPCException
     * 
     * @return exception or null if none.
     */ 
    public Throwable getLinkedCause() {
        return cause;
    }

    /**
     * @deprecated Use getLinkedCause instead.
     */
    public Throwable getCause() {
        return cause;
    }
}


