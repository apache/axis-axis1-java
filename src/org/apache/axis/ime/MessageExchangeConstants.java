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
package org.apache.axis.ime;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageExchangeConstants {

    //** MessageContext properties **//
  
    /**
     * Identifies the MessageExchangeCorrelator property 
     * within the MessageContext
     */
    public static final String MESSAGE_CORRELATOR_PROPERTY =
            MessageExchangeCorrelator.class.getName();

    /**
     * Boolean MessageContext property that indicates whether or
     * not the MessageExchangeCorrelationService should be used.
     * (e.g. when sending a one-way message, correlation is not
     * required)
     */
    public static final String ENABLE_CORRELATOR_SERVICE =
            MESSAGE_CORRELATOR_PROPERTY + "::Enable";

    /**
     * Default value for the ENABLE_CORRELATOR_SERVICE
     * MessageContext property
     */
    public static final Boolean ENABLE_CORRELATOR_SERVICE_DEFAULT =
            Boolean.TRUE;

}
