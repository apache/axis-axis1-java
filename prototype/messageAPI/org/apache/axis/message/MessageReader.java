package org.apache.axis.message;

public interface MessageReader {
   public void read(Message message);
   public void read(MessageWriter writer);
   public boolean validate();
   public void writeTo(MessageWriter writer);
}
