package org.apache.axis.ime;

import java.io.Serializable;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageExchangeFaultListener
        extends Serializable {

    public void onFault(
            MessageExchangeCorrelator correlator,
            Throwable exception);

}
