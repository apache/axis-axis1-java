package org.apache.axis.ime;

import org.apache.axis.MessageContext;

import java.io.Serializable;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageExchangeReceiveListener
        extends Serializable {

    public void onReceive(
            MessageExchangeCorrelator correlator,
            MessageContext context);

}
