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
package org.apache.axis.features;

import org.apache.axis.AxisFault;

/**
 * A "feature" is a collection of behaviors such as 
 * reliable delivery, WS-Security support, etc that 
 * may be enabled or disabled.  Enabling a feature may
 * involve adding handlers to chain, setting properties,
 * etc.  The point is to make it easier to enable/disable
 * complex behaviors
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface FeatureEnabled {

    /**
     * @param featureId The id of the feature to enable
     * @throws AxisFault
     */
    public void enableFeature(String featureId)
            throws AxisFault;

    /**
     * @param featureId The id of the feature to disable
     * @throws AxisFault
     */
    public void disableFeature(String featureId)
            throws AxisFault;

    /**
     * @param featureId The id of the feature to check
     * @return boolean
     * @throws AxisFault
     */
    public boolean isFeatureEnabled(String featureId)
            throws AxisFault;
            
    /**
     * @return String[]
     * @throws AxisFault
     */
    public String[] getSupportedFeatures()
            throws AxisFault;

}
