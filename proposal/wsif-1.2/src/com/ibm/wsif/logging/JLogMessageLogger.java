// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.logging;

import java.util.Enumeration;
import com.ibm.wsif.WSIFConstants;
import com.ibm.logging.*;

/**
* Messsage log implementation for JLog. Instances of this class must be created
* through the {@link MessageLogger#newMessageLogger} factory method.
* @author <a href="mailto:fussi@de.ibm.com">Erich Fussi</a>
*/
final class JLogMessageLogger extends MessageLogger
{
  /**
  * Message type for info messages.
  */
  private static final long TYPE_INFO = IRecordType.TYPE_INFO;

  /**
  * Message type for warning messages.
  */
  private static final long TYPE_WARNING = IRecordType.TYPE_WARNING;

  /**
  * Message type for error messages.
  */
  private static final long TYPE_ERROR = IRecordType.TYPE_ERROR;	

  private com.ibm.logging.MessageLogger mLogger;
  private String className;
  private com.ibm.logging.IFormatter formatter;
  		
  /**
  * Constructs a new message interface for JLog. This message logger will log
  * asynchronously to the console. If logging to a file is desired, the
  * {@link #addFileHandler} method must be called.
  * @param product
  *    The product name.
  * @param component
  *    The main class of the component that is logging.
  * @param className
  *    The name of the logging component's main class.
  * @param catalog
  *    The message catalog to use.
  * @exception Throwable
  *    If the JLog classes could not be loaded.
  */
  public JLogMessageLogger(String product, String component, String
                           className, String catalog) throws Throwable
  {
    // use component as the logger's name and product as its description
	mLogger = new com.ibm.logging.MessageLogger(component, product);
    this.className = className;

    // Sets a flag that tells the logger whether to log data synchronously. When logging
    // synchronously, the logger waits for the handlers to write a log entry before returning to
    // the caller. Otherwise, the log entry is passed to the handler and the logger returns.
	mLogger.setSynchronous(false);
    //formatter = (IFormatter) JLogFormatter.newInstance(catalog);
    formatter = new JLogFormatter(catalog);
    ConsoleHandler handler = new ConsoleHandler(component);
	handler.addFormatter(formatter);
	mLogger.addHandler(handler);
  }

  /**
  * Stops and removes all handlers associated with this logger.
  */
  public void destroy()
  {
      Enumeration handlers = (Enumeration) mLogger.getHandlers();
      while (handlers.hasMoreElements())
      {
		IHandler handler = (IHandler) handlers.nextElement();
		handler.stop();
		mLogger.removeHandler(handler);
      }
  }

  /**
  * Registers a new file handler with this logger.
  * @param fileName
  *    The name of the output file.
  */
  public void addFileHandler(String fileName)
  {
	FileHandler handler = new FileHandler();
	handler.addFormatter(formatter);
	handler.setFileName(fileName);
	mLogger.addHandler(handler);
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
	mLogger.message(type, className, "", key, parms);
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
	mLogger.text(type, className,"", text);
  }

  private long mapConstant(long in)
  {
  	if(in == WSIFConstants.TYPE_INFO)
  	  return TYPE_INFO;
  	else if(in == WSIFConstants.TYPE_WARNING)
	  return TYPE_WARNING;
  	else if(in == WSIFConstants.TYPE_ERROR)
  	  return TYPE_ERROR;
	else
	  return TYPE_ERROR;
  }
}
