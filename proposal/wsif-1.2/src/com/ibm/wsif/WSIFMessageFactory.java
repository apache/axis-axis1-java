// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

public interface WSIFMessageFactory {

   public WSIFMessage createMessage();
   public WSIFMessage createMessage(String ns, String n);
   
}