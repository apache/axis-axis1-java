// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.logging;
import java.util.Enumeration;
import com.ibm.wsif.WSIFConstants;
import com.ibm.logging.*;
/**
* Trace log implementation for JLog. Instances of this class must be created
* through the {@link TraceLogger#newTraceLogger} factory method.
* @author <a href="mailto:fussi@de.ibm.com">Erich Fussi</a>
*/
final class JLogTraceLogger extends TraceLogger
{
  /**
  * Message type for event (low detail) traces.
  */
  private static final long TYPE_EVENT = IRecordType.TYPE_LEVEL1;

  /**
  * Message type for entry / exit (medium detail) traces.
  */
  private static final long TYPE_ENTRY_EXIT = IRecordType.TYPE_LEVEL2;

  /**
  * Message type for debug (high detail) traces.
  */
  private static final long TYPE_DEBUG = IRecordType.TYPE_LEVEL3;

  private com.ibm.logging.TraceLogger tLogger;
  private boolean logging;
  private IFormatter formatter;
  private String className;
  
  /**
  * Constructs a new trace interface for JLog. You must call {@link #addFileHandler} at least
  * once, otherwise no trace will be written.
  * @param product
  *    The product name.
  * @param component
  *    The main class of the component that is logging.
  * @param className
  *    The name of the logging component's main class.
  * @exception Throwable
  *    If the JLog classes could not be loaded.
  */
  public JLogTraceLogger(String product, String component, String className) throws Throwable
  {
    // use component as the logger's name and product as its description
    tLogger = new com.ibm.logging.TraceLogger(component, product);

    this.className = className;
    //formatter = (IFormatter) JLogFormatter.newInstance(null);
    formatter = new JLogFormatter(null);

    // Remove default filter AnyMaskFilter:
    //   TYPE_API TYPE_CALLBACK TYPE_ENTRY TYPE_EXIT TYPE_ERROR_EXC TYPE_MISC_DATA
    //   TYPE_OBJ_CREATE TYPE_OBJ_DELETE TYPE_PRIVATE TYPE_PUBLIC TYPE_STATIC TYPE_SVC
	tLogger.removeAllFilters();

    // Sets a flag that tells the logger whether to log data synchronously. When logging
    // synchronously, the logger waits for the handlers to write a log entry before returning to
    // the caller. Otherwise, the log entry is passed to the handler and the logger returns.
	tLogger.setSynchronous(false);
    logging = isLogging();
  }

  /**
  * Stops and removes all handlers associated with this logger.
  */
  public void destroy()
  {
      Enumeration handlers = (Enumeration) tLogger.getHandlers();

      while (handlers.hasMoreElements())
      {
        IHandler handler = (IHandler) handlers.nextElement();
        handler.stop();
		tLogger.removeHandler(handler);
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
      tLogger.addHandler(handler);
  }

  /**
  * Sets a flag that indicates whether the logger should log data or not. This method is just
  * a global switch, it does not allow you to control what trace types are written.
  * @param shouldLog
  *   <code>true</code> when the logger should log, <code>false</code> otherwise.
  */
  public void setLogging(boolean shouldLog)
  {
  	tLogger.setLogging(shouldLog);
    logging = isLogging();
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
    // cached for performance reasons
    return logging;
  }

  /**
  * Traces entry into a method with parameters.
  * @param parms
  *     An array of parameters passed to the method.
  */
  public void entry(Object[] parms)
  {
		tLogger.entry(TYPE_ENTRY_EXIT, className, 
        	           getMethodName(), parms);
  }

  private void entryNameOnly()
  {
     tLogger.entry(TYPE_ENTRY_EXIT, className, 
          	           getMethodName(),new Object[] {"NULL"});
  }  	

  /**
  * Traces exit from a method that returns a value.
  * @param returnValue
  *    The returned value.
  */
  public void exit(Object returnValue)
  {
	tLogger.exit(TYPE_ENTRY_EXIT,
                      className, getMethodName(), returnValue);
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
      type = mapConstant(type);
      String loggingMethod = getMethodName();
	  tLogger.text(type, className,
                       loggingMethod, text, parms);

      if (parms != null)
      {
        for (int i = 0; i < parms.length; ++i)
        {
          if (!(parms[i] instanceof Throwable))
            continue;
			tLogger.exception(type, className,
                           loggingMethod, (Throwable) parms[i]);
        }
      }
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
  	  if(type != TYPE_DEBUG && type != TYPE_ENTRY_EXIT && type != TYPE_EVENT)
      	type = mapConstant(type);
	  tLogger.exception(type, className,
                       getMethodName(), exception);
  }

  /**
  * Determines if the logger is enabled. For performance reasons, this method is only called
  * at startup and when the {@link #setLogging} method was invoked.
  * @return
  *    <code>true</code> if the logger is enabled, <code>false</code> otherwise.
  */
  private boolean isLogging()
  {
    boolean rc = tLogger.isLogging();
    return rc;
  }

  private long mapConstant(long in)
  {
  	if(in == WSIFConstants.TYPE_ENTRY_EXIT)
  	  return TYPE_ENTRY_EXIT;
  	else if(in == WSIFConstants.TYPE_DEBUG)
	  return TYPE_DEBUG;
  	else if(in == WSIFConstants.TYPE_EVENT)
  	  return TYPE_EVENT;
	else
	  return TYPE_EVENT;
  }
}
