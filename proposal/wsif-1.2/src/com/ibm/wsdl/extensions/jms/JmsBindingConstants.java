// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.jms;

import javax.wsdl.*;
import com.ibm.wsdl.*;

/**
 * WSDL Jms extension
 * 
 * @author <a href="mailto:ake@de.ibm.com">Hermann Akermann</a>
 */
public class JmsBindingConstants {

   // Namespace URIs. 
   public static final String NS_URI_JMS = "http://schemas.xmlsoap.org/wsdl/jms/";

   // Element names.
   public static final String ELEM_ADDRESS = "address";
   public static final String ELEM_ATTRIBUTE = "attribute";
   public static final String ELEM_HEADER = "header";
   public static final String ELEM_BODY = "body";

   public static final String ELEM_JMS_MESSAGE_TYPE = "type";

   // managed environment elements
   public static final String ELEM_JMS_JMS_VENDOR_URL      = "jmsVendorURL";
   public static final String ELEM_JMS_INIT_CXT_FACT       = "initialContextFactory";
   public static final String ELEM_JMS_JNDI_PROV_URL       = "jndiProviderURL";
   public static final String ELEM_JMS_DEST_STYLE          = "destinationStyle";
   public static final String ELEM_JMS_JNDI_CONN_FACT_NAME = "jndiConnectionFactoryName";
   public static final String ELEM_JMS_JNDI_DEST_NAME      = "jndiDestinationName";
   public static final String ELEM_JMS_JMS_PROV_DEST_NAME  = "jmsproviderDestinationName";
   public static final String ELEM_JMS_JMS_IMPL_SPEC_URL   = "jmsImplementationSpecificURL";

   public static final String ELEM_JMS_QUEUE = "queue";  
   public static final String ELEM_JMS_TOPIC = "topic";  
   public static final String ELEM_JMS_NAME  = "name";  
   public static final String ELEM_JMS_PART  = "part";
   public static final String ELEM_JMS_VALUE = "value";

   // Jms body extension	
   public static final String ELEM_JMS_BODY_PARTS = "parts";
   public static final String ELEM_JMS_BODY_USE   = "use";
   public static final String ELEM_JMS_BODY_ES    = "encodingstyle";
   public static final String ELEM_JMS_BODY_NS    = "namespace";
   
   // Qualified element names.
   public static final QName Q_ELEM_JMS_ADDRESS =
      new QName(NS_URI_JMS, ELEM_ADDRESS);
   public static final QName Q_ELEM_JMS_BINDING =
      new QName(NS_URI_JMS, Constants.ELEM_BINDING);
   public static final QName Q_ELEM_JMS_ATTRIBUTE =
      new QName(NS_URI_JMS, ELEM_ATTRIBUTE);
   public static final QName Q_ELEM_JMS_HEADER =
      new QName(NS_URI_JMS, ELEM_HEADER);
   public static final QName Q_ELEM_JMS_OPERATION =
      new QName(NS_URI_JMS, Constants.ELEM_OPERATION);
   public static final QName Q_ELEM_JMS_BODY =
      new QName(NS_URI_JMS, ELEM_BODY);   

   // Jms message (body) types
   public static final int MESSAGE_TYPE_NOTSET = 0;
   public static final int MESSAGE_TYPE_BYTEMESSAGE = 10;
   public static final int MESSAGE_TYPE_MAPMESSAGE = 20;
   public static final int MESSAGE_TYPE_OBJECTMESSAGE = 30;
   public static final int MESSAGE_TYPE_STREAMMESSAGE = 40;
   public static final int MESSAGE_TYPE_TEXTMESSAGE = 50;

}