// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.logging;

/**
* Default trace log implementation. Does nothing, when using this classes tracing will
* effectively be disabled. Instances of this class must be created
* through the {@link TraceLogger#newTraceLogger} factory method.
* @author <a href="mailto:fussi@de.ibm.com">Erich Fussi</a>
*/
final class DefaultTraceLogger extends TraceLogger
{
  /**
  * Message type for event (low detail) traces.
  */
  public static final long TYPE_EVENT = 0;


  /**
  * Message type for entry / exit (medium detail) traces.
  */
  public static final long TYPE_ENTRY_EXIT = 0;


  /**
  * Message type for debug (high detail) traces.
  */
  public static final long TYPE_DEBUG = 0;
  	
  /**
  * Constructs a new default trace interface.
  * @param product
  *    The product name.
  * @param component
  *    The main class of the component that is logging.
  * @param className
  *    The name of the logging component's main class.
  */
  public DefaultTraceLogger(String product, String component, String className)
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
  * Sets a flag that indicates whether the logger should log data or not. This method is just
  * a global switch, it does not allow you to control what trace types are written.
  * @param shouldLog
  *   <code>true</code> when the logger should log, <code>false</code> otherwise.
  */
  public void setLogging(boolean shouldLog)
  {
    if (shouldLog && !isTracing)
    {
      isTracing = true;
    }
  }

  /**
  * Determines if the logger is enabled.
  * @param type
  *    The type of the trace entry.
  * @return
  *    <code>true</code> if the logger is enabled, <code>false</code> otherwise.
  */
  public boolean isLogging(long type)
  {
    return false;
  }

  /**
  * Traces entry into a method with parameters.
  * @param parms
  *     An array of parameters passed to the method.
  */
  public void entry(Object[] parms)
  {
  }

  /**
  * Traces exit from a method that returns a value.
  * @param returnValue
  *    The returned value.
  */
  public void exit(Object returnValue)
  {
  }

  /**
  * Traces a text message with parameters.
  * @param type
  *    The trace type.
  * @param text
  *    The text to trace.
  * @param parms
  *    The parameters to trace.
  */
  public void trace(long type, String text, Object[] parms)
  {
  }

  /**
  * Traces an exception.
  * @param type
  *    The trace type.
  * @param exception
  *    The exception to trace.
  */
  public void exception(long type, Exception exception)
  {
  }

  private boolean isTracing = false;
}
