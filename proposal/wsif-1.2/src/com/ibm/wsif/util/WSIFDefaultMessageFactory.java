// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util;

import java.util.*;
import com.ibm.wsif.WSIFMessage;
import com.ibm.wsif.WSIFMessageFactory;

/**
 * This is a factory for creating WSIFMessages
 */
public class WSIFDefaultMessageFactory implements WSIFMessageFactory {

   public WSIFMessage createMessage() {
      return new WSIFDefaultMessage();
   }

   public WSIFMessage createMessage(String ns, String n) {
   	  String msgClass = getPackageName( ns ) + "." + n;
      try {
      	WSIFMessage msg = (WSIFMessage) Class.forName( msgClass ).newInstance();
      	return msg;
      } catch (Exception ex) {
        return new WSIFDefaultMessage();
      }
   }

   private String getPackageName(String nsURI) {
      String packageString = "";
      try {
         java.net.URL url = new java.net.URL(nsURI);
         
         String host = url.getHost();
         StringTokenizer st = new StringTokenizer(host,".");
         while (st.hasMoreTokens()) {
            packageString = st.nextToken() + 
               (packageString.equals("")?"":"."+packageString);
         }
         String path = url.getPath();
         st = new StringTokenizer(path,"/");
         while (st.hasMoreTokens()) {
            packageString = 
               (packageString.equals("")?"":packageString+".") + st.nextToken();			
         }
      } catch (java.net.MalformedURLException mue) {
         mue.printStackTrace();
      }		
      return packageString;
   }

}

