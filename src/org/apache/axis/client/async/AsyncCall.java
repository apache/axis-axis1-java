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

import org.apache.axis.client.Call;

import javax.xml.namespace.QName;

/**
 * Support for Asynchronous call
 * 
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class AsyncCall {

    /**
     * Field call
     */
    private Call call = null;

    /**
     * Field callback
     */
    private IAsyncCallback callback = null;

    /**
     * Constructor AsyncCall
     * 
     * @param call 
     */
    public AsyncCall(Call call) {
        this(call, null);
    }

    /**
     * Constructor AsyncCall
     * 
     * @param call     
     * @param callback 
     */
    public AsyncCall(Call call, IAsyncCallback callback) {
        this.call = call;
        this.callback = callback;
    }

    /**
     * Method getCallback
     * 
     * @return 
     */
    public IAsyncCallback getCallback() {
        return callback;
    }

    /**
     * Method setCallback
     * 
     * @param callback 
     */
    public void setCallback(IAsyncCallback callback) {
        this.callback = callback;
    }

    /**
     * Method invoke
     * 
     * @param inputParams 
     * @return 
     */
    public IAsyncResult invoke(Object[] inputParams) {
        return new AsyncResult(this, null, inputParams);
    }

    /**
     * Method invoke
     * 
     * @param qName       
     * @param inputParams 
     * @return 
     */
    public IAsyncResult invoke(QName qName, Object[] inputParams) {
        return new AsyncResult(this, qName, inputParams);
    }

    /**
     * Method getCall
     * 
     * @return 
     */
    public Call getCall() {
        return call;
    }
}
