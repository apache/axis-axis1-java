/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
package org.apache.axis.configuration;

import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.java.JavaSender;
import org.apache.axis.transport.local.LocalSender;

/**
 * A SimpleProvider set up with hardcoded basic configuration for a client
 * (i.e. http and local transports).
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class BasicClientConfig extends SimpleProvider {
    /**
     * Constructor - deploy client-side basic transports.
     */
    public BasicClientConfig() {
        deployTransport("java", new SimpleTargetedChain(new JavaSender()));
        deployTransport("local", new SimpleTargetedChain(new LocalSender()));
        deployTransport("http", new SimpleTargetedChain(new HTTPSender()));
    }
}
