package org.apache.axis.ime.internal;

import org.apache.axis.ime.MessageExchangeContext;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.MessageExchangeCorrelatorService;
import java.util.Hashtable;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class NonPersistentMessageExchangeCorrelatorService
  implements MessageExchangeCorrelatorService {

  Hashtable contexts = new Hashtable();

  /**
   * @see org.apache.axis.ime.MessageExchangeCorrelatorService#put(MessageExchangeCorrelator, MessageExchangeContext)
   */
  public void put(
    MessageExchangeCorrelator correlator,
    MessageExchangeContext context) {
      synchronized(contexts) {
        contexts.put(correlator, context);
      }
    }

  /**
   * @see org.apache.axis.ime.MessageExchangeCorrelatorService#get(MessageExchangeCorrelator)
   */
  public MessageExchangeContext get(MessageExchangeCorrelator correlator) {
    synchronized(contexts) {
      return (MessageExchangeContext)contexts.remove(correlator);
    }
  }

}
