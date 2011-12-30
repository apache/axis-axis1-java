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

import javax.servlet.ServletContext;

/**
 * This is a 'front' for replacement logic.
 * Use EngineConfigurationFactoryFinder.newFactory(yourServletContext).
 * 
 * @author Richard A. Sitze
 * @author Glyn Normington (glyn@apache.org)
 * 
 * @deprecated
 */
public class ServletEngineConfigurationFactory
    extends DefaultEngineConfigurationFactory
{
    public ServletEngineConfigurationFactory(ServletContext ctx) {
        super(EngineConfigurationFactoryFinder.newFactory(ctx));
    }
}
