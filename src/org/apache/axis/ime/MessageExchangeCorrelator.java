package org.apache.axis.ime;

import java.io.Serializable;

/**
 * Used for correlating outbound/inbound messages.
 * This class may be extended to allow for more complex
 * Correlation mechanisms
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class MessageExchangeCorrelator
        implements Serializable {

    private String identifier;

    private MessageExchangeCorrelator() {
    }

    public MessageExchangeCorrelator(
            String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

}
