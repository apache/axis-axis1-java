package org.apache.axis.ime;

import java.io.Serializable;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageExchangeStatusListener
  extends Serializable {

    public void onStatus(
        MessageExchangeCorrelator correlator,
        MessageExchangeStatus status);

}
