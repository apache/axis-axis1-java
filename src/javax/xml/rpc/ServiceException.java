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
package javax.xml.rpc;

/**
 * The <code>javax.xml.rpc.ServiceException</code> is thrown from the
 * methods in the <code>javax.xml.rpc.Service</code> interface and
 * <code>ServiceFactory</code> class.
 *
 *
 * @version 1.0
 */
public class ServiceException extends Exception {

    // fixme: could we refactor this to use the jdk1.4 exception wrapping stuff?

    /** The cause of this exception. */
    Throwable cause;

    /**
     * Constructs a new exception with <code>null</code> as its
     * detail message. The cause is not initialized.
     */
    public ServiceException() {}

    /**
     * Constructs a new exception with the specified detail
     * message.  The cause is not initialized.
     *
     * @param message The detail message which is later
     *            retrieved using the <code>getMessage</code> method
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail
     * message and cause.
     *
     * @param message the detail message which is later retrieved
     *            using the <code>getMessage</code> method
     * @param cause the cause which is saved for the later
     *            retrieval throw by the <code>getCause</code>
     *            method
     */
    public ServiceException(String message, Throwable cause) {

        super(message);

        this.cause = cause;
    }

    /**
     * Constructs a new exception with the specified cause
     * and a detail message of <tt>(cause==null ? null :
     * cause.toString())</tt> (which typically contains the
     * class and detail message of <tt>cause</tt>).
     *
     * @param cause the cause which is saved for the later
     *            retrieval throw by the getCause method.
     *            (A <tt>null</tt> value is permitted, and
     *            indicates that the cause is nonexistent or
     *          unknown.)
     */
    public ServiceException(Throwable cause) {
        this.cause = cause;
    }

    /**
     * Gets the linked cause.
     *
     * @return the cause of this Exception or <code>null</code>
     *     if the cause is noexistent or unknown
     */
    public Throwable getLinkedCause() {
        return cause;
    }
}

