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
package org.apache.axis;

// fixme: this interface needs a propper description of what it is, who uses
//  it and how to use it
/**
 * @author James Snell (jasnell@us.ibm.com)
 */
public interface TargetedChain extends Chain {

    /**
     * Returns the Request handler.
     *
     * @return the request <code>Handler</code>
     */
    public Handler   getRequestHandler();

    /**
     * Returns the Pivot Handler.
     *
     * @return the pivot <code>Handler</code>
     */
    public Handler getPivotHandler();

    /**
     * Returns the Response Handler.
     *
     * @return the response <code>Handler</code>
     */
    public Handler   getResponseHandler();

}
