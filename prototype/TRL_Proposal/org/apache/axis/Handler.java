package org.apache.axis;

import java.io.Serializable;

/**
 * We may have to remove Serializable because we need 
 * onSerialize and onDederialize.
 */
public interface Handler extends Serializable {
    /**
     * Init is called when the chain containing this Chainable object
     * is instantiated.
     */
    void init();

    /**
     * Cleanup is called when the chain containing this Chainable object
     * is done processing the chain.
     */
    void cleanup();

    /**
     * Invoke is called to do the actual work of the Chainable object.
     */
    void invoke(MessageContext message);
}
