/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package test.wsdl.multithread;

/**
 * Collects the results from a set of {@link Invoker} instances.
 */
class Report {
    private int successCount = 0;
    private Throwable error;

    synchronized void addSuccess() {
        successCount++;
    }

    synchronized int getSuccessCount() {
        return successCount;
    }

    synchronized void setError(Throwable error) {
        if (this.error == null) {
            this.error = error;
        }
    }

    synchronized Throwable getError() {
        return error;
    }
}
