// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.logging;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.ibm.logging.*;
/**
* Formatter for the {@link JLogMessageLogger} and {@link JLogTraceLogger} logging implementations.
* If the record includes an <code>Exception</code>, the record text is taken from
* <code>Exception.getMessage()</code> and the exception's stack trace is appended to the formatted
* output.
* @author Erich Fussi
* @author Owen Burroughs
*/
final class JLogFormatter extends Formatter
{
  private final static DateFormat _timestamp = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss.SS");
  private final static String _sep = ", ";
  private final static String _eol = System.getProperty("line.separator");

  private Catalog catalog;	
	
  /**
  * Constructs a formatter for JLog messages and traces.
  * @param catalogName
  *    The name of the message catalog to use. May be <code>null</code>, in this case
  *    no message lookup takes place, just message formatting.
  */
  JLogFormatter(String catalogName)
  {
    catalog = new Catalog(catalogName);
  }

  /**
  * Formats a log record before it is passed to the registered trace handlers. This
  * implementation will prepend the message text with the current time stamp formatted
  * according to the ISO-8601 standard.
  * @param record
  *    The <code>ILogRecord</code> to be formatted.
  * @return
  *    Formatted String version of the input log record.
  */
  public final String format(ILogRecord record)
  {
    StringBuffer buf = new StringBuffer(512);
    ILogRecord irec = (ILogRecord) record;
    // Ad time stamp
    long timeStamp = irec.getTimeStamp();
    buf.append(_timestamp.format(new Date(timeStamp)));
    buf.append(_sep);

    // Add method (can be disabled to improve performance)
    String method = (String) irec.getAttribute(IConstants.KEY_LOGGING_METHOD);
    if (method != null && method.length() > 0)
    {
      buf.append(method);
      buf.append(_sep);
    }

    // Add actual text (cannot use Formatter.getText(record) here as this method is protected
    // and cannot be accessed using reflection)
    String text = irec.getText();
	String[] params = irec.getParameters();
    text = catalog.get(text, params);

    while (text.endsWith(_eol))
    {
      text = text.substring(0, text.length() - _eol.length());
    }
    buf.append(text);

    // Add stack trace, if present (would be handled by Formatter.getText(record) if this
    // method could be used)
	String stackTrace = (String) irec.getAttribute(IConstants.KEY_THROWABLE_TRACE);
    if (stackTrace != null)
    {
      while (stackTrace.endsWith(_eol))
      {
        stackTrace = stackTrace.substring(0, stackTrace.length() - _eol.length());
      }
      buf.append(stackTrace);
    }

    return buf.toString();
  }
}

