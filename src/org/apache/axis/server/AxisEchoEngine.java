package org.apache.axis.server ;

import org.apache.axis.common.* ;

public class AxisEchoEngine implements Chainable {
  public void init() {};

  public void cleanup() {};

  public void invoke(MessageContext msgCntxt) {
    msgCntxt.setOutgoingMessage( msgCntxt.getIncomingMessage() );
  };
};
