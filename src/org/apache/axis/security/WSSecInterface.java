package org.apache.axis.security ;

import org.apache.axis.MessageContext ;

public interface WSSecInterface { 
  public void init(MessageContext msgContext) throws Exception ;
  public void protect(MessageContext msgContext) throws Exception ;
  public void verify(MessageContext msgContext) throws Exception ;
}
