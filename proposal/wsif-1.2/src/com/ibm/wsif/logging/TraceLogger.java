// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.logging;

import com.ibm.wsif.util.WSIFProperties;
import java.io.*;
import java.util.*;
import com.ibm.wsif.WSIFConstants;

/**
* Interface for trace loggers. Each component should create its own TraceLogger instance
* using the {@link #newTraceLogger(Class)} factory method.
* @author <a href="mailto:fussi@de.ibm.com">Erich Fussi</a>
*/
public abstract class TraceLogger
{ 	  
  /**
   * WSIF-wide trace logger.
   */
  private static TraceLogger generalTraceLogger = null;

  /**
   * Protected constructor - users must use newTraceLogger instead.
   */
  protected TraceLogger() {}
  
  /**
   * Returns the WSIF-wide TraceLogger.
   */
  public static TraceLogger getGeneralTraceLogger()
  {
  	 if (generalTraceLogger==null) 
  	 {
  	   generalTraceLogger = newTraceLogger("com.ibm.wsif");
  	 }
  	 
  	 return generalTraceLogger;
  }
  
  public static TraceLogger newTraceLogger(Class component)
  {
  	 return newTraceLogger(component.getName());
  }
  
  /**
  * Creates a trace logger object. 
  * @param component
  *    The main class of the component that is tracing.
  * @return
  *    A new trace logger object.
  */
  public static TraceLogger newTraceLogger(String className)
  {
  	TraceLogger logger=null;
    try
    {
      // Attempt to get a JLog class. If it fails then JLog is not available
      // and we'll fall through this block.	
      Class jlogc = Class.forName("com.ibm.logging.ILogRecord");
      logger = new JLogTraceLogger("@product@", className, className);
      
	  String wsifTrace = WSIFProperties.getProperty(WSIFConstants.WSIF_TRACE);
	  String wsifTraceFile = WSIFProperties.getProperty(WSIFConstants.WSIF_TRACEFILE);

	  if (wsifTrace!=null && wsifTrace.equals("on") && wsifTraceFile!=null)
	  {
        logger.addFileHandler(wsifTraceFile);
        logger.setLogging(true);
	  }

	  return logger;
    }
    catch (Throwable ignored) { }

    logger = new DefaultTraceLogger("@product@", className, className);
    return logger;
  }

  protected void finalize() throws Throwable
  {
    destroy();
    super.finalize();
  }

  /**
  * Cleans up all resources connected with this trace logger.
  */
  public void destroy()
  {
  }

  /**
  * Registers a new file handler with this logger. Note that this has no effect when running
  * within WebSphere.
  * @param fileName
  *    The name of the output file.
  */
  public abstract void addFileHandler(String fileName);

  /**
  * Sets a flag that indicates whether the logger should log data or not. Note that this has
  * no effect when running within WebSphere. In that case, you have to use the WebSphere
  * Admin Console to enable or disable logging. It allows you to even control what types
  * of traces should be written whereas this method is just a global switch.
  * @param shouldLog
  *   <code>true</code> when the logger should log, <code>false</code> otherwise.
  */
  public abstract void setLogging(boolean shouldLog);

  /**
  * Determines if a log entry will be processed. Wrapping a trace call with this method can
  * improve performance. Log entries that will not be processed need not even be built.
  * @param type
  *    The type of the trace entry.
  * @return
  *    <code>true</code> if the logger is enabled and the log entry will be processed,
  *    <code>false</code> otherwise.
  */
  public abstract boolean isLogging(long type);

  /**
  * Traces entry into a method.
  */
  public void entry() { entry(null); }

  /**
  * Traces entry into a method with one parameter.
  * @param parm1
  *    The parameter passed to the method.
  */
  public void entry(Object parm1) { entry(new Object[] { parm1 }); }

  /**
  * Traces entry into a method with two parameters.
  * @param parm1
  *    The first parameter passed to the method.
  * @param parm2
  *    The second parameter passed to the method.
  */
  public void entry(Object parm1, Object parm2)
  {
    entry(new Object[] { parm1, parm2 });
  }

  /**
  * Traces entry into a method with parameters.
  * @param parms
  *     An array of parameters passed to the method.
  */
  public abstract void entry(Object[] parms);

  /**
  * Traces exit from a method.
  */
  public void exit() { exit(null); }

  /**
  * Traces exit from a method that returns a value.
  * @param returnValue
  *    The returned value.
  */
  public abstract void exit(Object returnValue);

  public String getMethodName()
  {
  	Exception e=new Exception();
  	StringWriter sw=new StringWriter();
  	PrintWriter pw=new PrintWriter(sw);
  	e.printStackTrace(pw);
  	String stack=sw.getBuffer().toString();

    int idx=stack.indexOf("com.ibm.wsif.logging");
    while (idx!=-1)
    {
  	  stack = stack.substring(idx+12);
  	  idx=stack.indexOf("com.ibm.wsif.logging");
    }

  	idx=stack.indexOf("com.ibm.wsif");
    stack=stack.substring(idx);  	

  	idx=stack.indexOf("\n");
  	return stack.substring(0,idx-1);
  }
  
  /**
  * Traces a text message with no parameters.
  * @param type
  *    The trace type.
  * @param text
  *    The text to trace.
  */
  public void trace(long type, String text)
  {
    trace(type, text, null);
  }

  /**
  * Traces a text message with one parameter.
  * @param type
  *    The trace type.
  * @param text
  *    The text to trace.
  * @param parm1
  *    The parameter to trace.
  */
  public void trace(long type, String text, Object parm1)
  {
    trace(type, text, new Object[] { parm1 } );
  }

  /**
  * Traces a text message with two parameters.
  * @param type
  *    The trace type.
  * @param text
  *    The text to trace.
  * @param parm1
  *    The first parameter to trace.
  * @param parm2
  *    The second parameter to trace.
  */
  public void trace(long type, String text, Object parm1, Object parm2)
  {
    trace(type, text, new Object[] { parm1, parm2 } );
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
  public abstract void trace(long type, String text, Object[] parms);

  /**
  * Traces an exception.
  * @param type
  *    The trace type.
  * @param exception
  *    The exception to trace.
  */
  public abstract void exception(long type, Exception exception);
}
