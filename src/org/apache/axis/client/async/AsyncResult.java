/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
