package org.apache.axis.ime;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageExchangeCorrelatorService {

    public void put(
        MessageExchangeCorrelator correlator,
        MessageExchangeContext context);
    
    public MessageExchangeContext get(
        MessageExchangeCorrelator correlator);

}
