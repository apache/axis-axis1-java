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
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.utils.ClassUtils;


/**
 *
 */
public class WSDDComProvider
    extends WSDDProvider
{
    public static final String OPTION_PROGID = "ProgID";
    public static final String OPTION_THREADING_MODEL = "threadingModel";
    
    public String getName() {
        return WSDDConstants.PROVIDER_COM;
    }

    public Handler newProviderInstance(WSDDService service,
                                       EngineConfiguration registry)
        throws Exception
    {
        Class _class = ClassUtils.forName("org.apache.axis.providers.ComProvider");

        BasicProvider provider = (BasicProvider) _class.newInstance();

        String option = service.getParameter("ProgID");

        if (!option.equals("")) {
            provider.setOption(OPTION_PROGID, option);
        }

        option = service.getParameter("threadingModel");

        if (option!= null && !option.equals("")) {
            provider.setOption(OPTION_THREADING_MODEL, option);
        }

        return provider;
    }
}
