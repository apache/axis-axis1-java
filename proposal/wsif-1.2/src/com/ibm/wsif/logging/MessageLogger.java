// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.logging;

/**
* Interface for message loggers. Each component should create its own MessageLogger instance
* using the {@link #newMessageLogger(String)} factory method. It is recommended not to use
* the {@link #text(MessageEventType,String)} method as it will bypass the message catalog lookup.
* When using this method, make sure the string passed as parameter was loaded through an
* appropriate message catalog.
* @author <a href="mailto:fussi@de.ibm.com">Erich Fussi</a>
*/
public abstract class MessageLogger
{
  /**
  * Default message catalog used when logging messages.
  */
  public static final String MSG_CATALOG = Catalog.MESSAGES_FILE;

  /**
  * Creates a message logger object. The JLog logger already has a console handler
  * associated with it. If in addition messages should be written to a file, use the
  * {@link #addFileHandler} method.
  * @param component
  *    The main class of the component that is logging.
  * @return
  *    A new message logger object.
  */
  public static MessageLogger newMessageLogger(String component)
  {
    return newMessageLogger(component, MSG_CATALOG);
  }

  /**
  * Creates a message logger object.
  * @param component
  *    The main class of the component that is logging.
  * @param catalog
  *    The message catalog to use.
  * @return
  *    A new message logger object.
  */
  public static MessageLogger newMessageLogger(String component, String catalog)
  {
    String className = component;

    try
    {
      return new JLogMessageLogger("@product@", component, className, catalog);
    }
    catch (Throwable ignored) { }

    return new DefaultMessageLogger("@product@", component, className, catalog);
  }

  protected void finalize() throws Throwable
  {
    destroy();
    super.finalize();
  }

  /**
  * Cleans up all resources connected with this message logger.
  */
  public void destroy()
  {
  }

  /**
  * Registers a new file handler with this logger. Note that this has no effect when running
  * within WebSphere. In that case the messages will always be written to
  * <code>&lt;WASDir&gt;/logs/actvity.log</code>.
  * @param fileName
  *    The name of the output file.
  */
  public abstract void addFileHandler(String fileName);

  /**
  * Logs a message, by key, with no parameters.
  * @param type
  *    The type of the record.
  * @param key
  *    The message key.
  */
  public void message(long type, String key)
  {
    message(type, key, null);
  }

  /**
  * Logs a message, by key, with one parameter.
  * @param type
  *    The type of the record.
  * @param key
  *    The message key.
  * @param parm1
  *     An element to be inserted into the message.
  */
  public void message(long type, String key, Object parm1)
  {
    message(type, key, new Object[] { parm1 } );
  }

  /**
  * Logs a message, by key, with two parameters.
  * @param type
  *    The type of the record.
  * @param key
  *    The message key.
  * @param parm1
  *     An element to be inserted into the message.
  * @param parm2
  *     An element to be inserted into the message.
  */
  public void message(long type, String key, Object parm1, Object parm2)
  {
    message(type, key, new Object[] { parm1, parm2 } );
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
  public abstract void message(long type, String key, Object[] parms);

  /**
  * Logs a text message with no parameters.
  * @param type
  *    The type of the record.
  * @param text
  *    The message text.
  */
  public abstract void text(long type, String text);
}
