// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.logging;

/**
* Default messsage log implementation. Instances of this class must be created
* through the {@link MessageLogger#newMessageLogger} factory method.
* @author <a href="mailto:fussi@de.ibm.com">Erich Fussi</a>
*/
final class DefaultMessageLogger extends MessageLogger
{
  /**
  * Message type for informational messages. Corresponds to WebSphere 'Audit'.
  */
  public static final long TYPE_INFO = 0;


  /**
  * Message type for warning messages. Corresponds to WebSphere 'Warning'.
  */
  public static final long TYPE_WARNING = 0;


  /**
  * Message type for error messages. Corresponds to WebSphere 'Error'.
  */
  public static final long TYPE_ERROR = 0;
	
  /**
  * Constructs a new default message interface.
  * @param product
  *    The product name.
  * @param component
  *    The main class of the component that is logging.
  * @param className
  *    The name of the logging component's main class.
  * @param catalog
  *    The message catalog to use.
  */
  public DefaultMessageLogger(String product, String component, String className, String catalog)
  {
  }

  /**
  * Registers a new file handler with this logger.
  * @param fileName
  *    The name of the output file.
  */
  public void addFileHandler(String fileName)
  {
  }

  /**
  * Logs a message, by key, with an array of parameters.
  * @param type
  *    The type of the record.
  * @param key
  *    The message key.
  * @param parms
  *     An array of elements to be inserted into the message.
  */
  public void message(long type, String key, Object[] parms)
  {
  }

  /**
  * Logs a text message with no parameters.
  * @param type
  *    The type of the record.
  * @param text
  *    The message text.
  */
  public void text(long type, String text)
  {
  }
}
