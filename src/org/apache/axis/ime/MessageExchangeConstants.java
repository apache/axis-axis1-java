package org.apache.axis.ime;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageExchangeConstants {

    //** MessageContext properties **//
  
    /**
     * Identifies the MessageExchangeCorrelator property 
     * within the MessageContext
     */
    public static final String MESSAGE_CORRELATOR_PROPERTY =
            MessageExchangeCorrelator.class.getName();

    /**
     * Boolean MessageContext property that indicates whether or
     * not the MessageExchangeCorrelationService should be used.
     * (e.g. when sending a one-way message, correlation is not
     * required)
     */
    public static final String ENABLE_CORRELATOR_SERVICE =
            MESSAGE_CORRELATOR_PROPERTY + "::Enable";

    /**
     * Default value for the ENABLE_CORRELATOR_SERVICE
     * MessageContext property
     */
    public static final Boolean ENABLE_CORRELATOR_SERVICE_DEFAULT =
            new Boolean(true);

}
