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

package org.apache.axis.client.async;

import javax.xml.namespace.QName;

/**
 * Access the results of the Async call
 * 
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class AsyncResult implements IAsyncResult, Runnable {

    /**
     * Field thread
     */
    private Thread thread = null;

    /**
     * Field response
     */
    private Object response = null;

    /**
     * Field exception
     */
    private Throwable exception = null;

    /**
     * Field ac
     */
    private AsyncCall ac = null;

    /**
     * Field opName
     */
    private QName opName = null;

    /**
     * Field params
     */
    private Object[] params = null;

    /**
     * Field status
     */
    private Status status = Status.NONE;

    /**
     * Constructor AsyncResult
     * 
     * @param ac     
     * @param opName 
     * @param params 
     */
    public AsyncResult(AsyncCall ac, QName opName, Object[] params) {
        this.ac = ac;
        this.opName = opName;
        this.params = params;

        if (opName == null) {
            this.opName = ac.getCall().getOperationName();
        }

        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Method abort
     */
    public void abort() {
        thread.interrupt();
        status = Status.INTERRUPTED;
    }

    /**
     * Method getStatus
     * 
     * @return 
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Method waitFor
     * 
     * @param timeout 
     * @throws InterruptedException 
     */
    public void waitFor(long timeout) throws InterruptedException {
        thread.wait(timeout);
    }

    /**
     * Method getResponse
     * 
     * @return 
     */
    public Object getResponse() {
        return response;
    }

    /**
     * Method getException
     * 
     * @return 
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Method run
     */
    public void run() {
        try {
            response = ac.getCall().invoke(opName, params);
            status = Status.COMPLETED;
        } catch (Throwable e) {
            exception = e;
            status = Status.EXCEPTION;
        } finally {
            IAsyncCallback callback = ac.getCallback();
            if (callback != null) {
                callback.onCompletion(this);
            }
        }
    }
}
