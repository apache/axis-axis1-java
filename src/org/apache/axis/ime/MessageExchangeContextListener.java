package org.apache.axis.ime;

import java.io.Serializable;
import org.apache.axis.ime.*;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageExchangeContextListener
  extends Serializable {

    public void onMessageExchangeContext(
        MessageExchangeContext context);

}
