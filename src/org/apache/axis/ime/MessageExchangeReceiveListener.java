package org.apache.axis.ime;

import java.io.Serializable;
import org.apache.axis.MessageContext;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageExchangeReceiveListener
  extends Serializable {

    public void onReceive(
        MessageExchangeCorrelator correlator,
        MessageContext context);

}
