package org.apache.axis.ime;

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
     * @param String The id of the feature to enable
     * @throws AxisFault
     */
    public void enableFeature(String featureId)
            throws AxisFault;

    /**
     * @param String The id of the feature to disable
     * @throws AxisFault
     */
    public void disableFeature(String featureId)
            throws AxisFault;

    /**
     * @param String The id of the feature to check
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
