package org.apache.axis.message.api;

public interface MessageHeaderEntry extends MessageElementEntry {
   public void setMustUnderstand(boolean flag);
   public void removeMustUnderstand();
   public boolean isMustUnderstand();
   public void setActor(String uri);
   public void setActorAsNext();
   public boolean isNextActor();
   public String getActor();
   public void removeActor();
}