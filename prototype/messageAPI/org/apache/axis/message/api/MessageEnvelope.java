package org.apache.axis.message.api;

public interface MessageEnvelope extends MessageElement {
   public String getEnvelopeNamespaceURI();
   public boolean hasHeader();
   public MessageElement getHeader();
   public void removeHeader();
   public void setHeader(MessageElement header);
   public MessageElement getBody();
   public void setBody(MessageElement body);
}