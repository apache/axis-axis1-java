package org.apache.axis.message.api;

import org.w3c.dom.Element;

public interface MessageFault extends MessageElementEntry {
   public void setFaultCode(String first, String second);
   public void setFaultString(String value);
   public void setFaultActor(String value);
   public void setDetail(Object detail);
   public String getFaultCode();
   public String getFaultString();
   public String getFaultActor();
   public Object getDetail();
   public void removeFaultCode();
   public void removeFaultString();
   public void removeFaultActor();
   public void removeDetail();
}