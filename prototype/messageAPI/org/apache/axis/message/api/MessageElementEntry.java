package org.apache.axis.message.api;

public interface MessageElementEntry extends MessageElement {
   public void setRoot(boolean flag);
   public void removeRoot();
   public boolean isRoot();
}