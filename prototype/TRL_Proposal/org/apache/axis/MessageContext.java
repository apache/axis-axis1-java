package org.apache.axis;

import java.util.Hashtable;
import org.apache.axis.message.SOAPDocument;

// Note that this is the complete implementation of MessageContext we
// propose.
final public class MessageContext {
    private SOAPDocument message;
    private final Hashtable properties;

    public MessageContext(SOAPDocument message) {
        this.message = message;
        this.properties = new Hashtable();
    }

    public SOAPDocument getMessage() { return message; }
    public void setMessage(SOAPDocument message) { this.message = message; }

    public Hashtable getProperties() { return properties; }
}
