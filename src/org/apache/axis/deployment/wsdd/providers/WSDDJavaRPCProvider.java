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
package org.apache.axis.deployment.wsdd.providers;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.deployment.wsdd.WSDDService;


/**
 *
 */
public class WSDDJavaRPCProvider
    extends WSDDProvider
{
    public String getName() {
        return WSDDConstants.PROVIDER_RPC;
    }
    /**
     *
     */
    public Handler newProviderInstance(WSDDService service,
                                       EngineConfiguration registry)
        throws Exception
    {
        return new org.apache.axis.providers.java.RPCProvider();
    }
}
