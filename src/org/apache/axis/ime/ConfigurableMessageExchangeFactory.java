package org.apache.axis.ime;

import java.util.Map;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface ConfigurableMessageExchangeFactory
        extends MessageExchangeFactory {

    public ConfigurableMessageExchange createMessageExchange(
            Map properties,
            String[] enabledFeatures);

}
