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

import org.apache.axis.Handler;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.transport.local.LocalResponder;
import org.apache.axis.transport.local.LocalSender;

/**
 * A SimpleProvider set up with hardcoded basic configuration for a server
 * (i.e. local transport).  Mostly handy for testing.
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class BasicServerConfig extends SimpleProvider {
    /**
     * Constructor - deploy a hardcoded basic server-side configuration.
     */ 
    public BasicServerConfig() {
        Handler h = new LocalResponder();
        SimpleTargetedChain transport = new SimpleTargetedChain(null, null, h);
        deployTransport("local", transport);
        deployTransport("java", new SimpleTargetedChain(new LocalSender()));
    }
}
