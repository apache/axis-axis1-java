package org.apache.axis.ime;

import java.util.Map;

/**
 * Extends the basic MessageExchange interface to allow
 * applications to configure the MessageExchange
 * 
 * Feature == a collection of default properties that
 *            represent a complex behavior of some sort.
 *            Reliable Delivery, for example.
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface ConfigurableMessageExchange 
    extends MessageExchange {

    public void enableFeature(String featureId);
  
    public void disableFeature(String featureId);
  
    public boolean isFeatureEnabled(String featureId);
  
    public void setProperty(
        String propertyId, 
        Object propertyValue);  
  
    public Object getProperty(
        String propertyId);
    
    public Object getProperty(
        String propertyId,
        Object defaultValue);
    
    public Map getProperties();
  
    public void clearProperties();

}
