package org.apache.axis.wsrm ;

import org.apache.axis.MessageContext ;

public interface RMInterface {
  public void    addRMHeaders(MessageContext msgContext) throws Exception ;
  public boolean sendMessage(MessageContext msgContext) throws Exception ;
  public boolean processRMHeaders(MessageContext msgContext) throws Exception ;
};
