// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.logging;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* This class is a collection of various utility functions related to Java
* class names, package names, and file names.
* @author <a href="mailto:fussi@de.ibm.com">Erich Fussi</a>
*/
public final class JavaUtilities
{
  /**
  * This constant is used to 'protect' code segments which are not
  * supposed to be used in a release build.
  * To exclude a certain code segment please write
  * <xmp>
  * if (!JavaUtilities.IS_RELEASE_BUILD)
  * {
  *   // Block not needed in release build
  * }
  * </xmp>
  * When setting <i>IS_RELEASE_BUILD</i> to false then the respective
  * code segments are excluded.
  */
  public static final boolean IS_RELEASE_BUILD = false;

  /**
  * Extracts the plain class name from a fully qualified class name.
  * @param clazz
  *    The fully qualified class name.
  * @return
  *    The class name (without the package name prefix).
  */
  public static String getClassName(String clazz)
  {
    return clazz.substring(clazz.lastIndexOf('.') + 1);
  }

  /**
  * Extracts the package name from a fully qualified class name.
  * @param clazz
  *    The fully qualified class name.
  * @return
  *    The package name.
  */
  public static String getPackageName(String clazz)
  {
    return clazz.substring(0, clazz.lastIndexOf('.'));
  }

  /**
  * Return name of the main WebSphere Workflow package (<code>com.ibm.flow</code>).
  * If the package for generated classes is needed, use {@link #getGenPackageName()} instead.
  * @return
  *    The name of the main package.
  */
  public static String getMainPackageName()
  {
    return getPackageName(getPackageName(JavaUtilities.class.getName()));
  }

  /**
  * Query the package name for generated classes.
  * @return
  *    The name of the package for generated classes, <code>com.ibm.flow.gen</code>
  */
  public static String getGenPackageName()
  {
    return getMainPackageName() + ".gen";
  }

  /**
  * Converts a fully qualified class name to a java file name including the
  * <code>.java</code> extension.
  * @param clazz
  *    The fully qualified class name.
  * @return
  *    The corresponding java file name.
  */
  public static String getJavaFileName(String clazz)
  {
    return clazz.replace('.', File.separatorChar) + ".java";
  }

  /**
  * Converts a fully qualified class name to a class file name including the
  * <code>.class</code> extension.
  * @param clazz
  *    The fully qualified class name.
  * @return
  *    The corresponding class file name.
  */
  public static String getClassFileName(String clazz)
  {
    return clazz.replace('.', '/') + ".class";
  }

  /**
  * Determines the classpath that has to be used to access a given class. Assumes
  * that the class has been loaded from a file.
  * @param clazz
  *    The class whose path is required.
  * @return
  *    Either the path to the .jar file or the directory that contains the class.
  */
  public static String getClasspath(Class clazz)
  {
    // Either something like
    //   file:/E:/projects/wswf/bindbg/flow.jar!/com/ibm/flow/util/JavaUtilities.class
    // or
    //   /E:/projects/wswf/objdbg/wswf/com/ibm/flow/util/JavaUtilities.class
    String cp = getCodebase(clazz);
    int from = cp.startsWith("file:") ? 5 : 0;
    int to = cp.indexOf(getClassFileName(clazz.getName()));
    to -= cp.charAt(to - 2) == '!' ? 2 : 1;

    return cp.substring(from, to);
  }

  /**
  * Determines where a certain class has been loaded from.
  * @param clazz
  *    The class to check.
  * @return
  *    The file part of the URL the class has been loaded from.
  */
  public static String getCodebase(Class clazz)
  {
    URL url = clazz.getResource("/" + getClassFileName(clazz.getName()));

    // if (TraceLog.isTracing) TraceLog.trace(TraceLog.TYPE_INFO, "URL for " + clazz + " is " + url);

    return url == null ? null : URLDecoder.decode(url.getFile());
  }

  /**
  * Checks if the application is running in VisualAge for Java.
  * @return
  *    Returns true if the application is running in VisualAge for Java.
  */
  public static boolean isRunningInVAJ()
  {
    if (_isRunningInVaj == -1)
    {
      // Need to do the lookup
      String java_vm_name = System.getProperty("java.vm.name");

      if (java_vm_name != null && java_vm_name.lastIndexOf("VisualAge") != -1)
        _isRunningInVaj = 1 ;
      else
        _isRunningInVaj = 0 ;
    }

    return _isRunningInVaj == 1 ? true : false;
  }

  /**
   * This method converts a xsd DateTime String into a {@link java.util.Date} object.
   *
   * @param dateString
   *    XSD DateTime String that shoud be canverted into {@link java.util.Date}
   *
   * @return Date
   *    Date object for the passed String
   *
   * @exception ParseException
   *    Thrown if the passed String is null or has not the necessary format so
   *    that it can be parsed
   */
  public static Date convertStringToDate(String dateString)
    throws  ParseException {
    if (dateString != null) {
      return _xsdDateTimeFormat.parse(dateString);
    } else {
      return new Date();
    }
  }

  /**
  * Variable which is used to determine if we are running in VisualAge for
  * Java.<br>
  * The following are possible values:
  * <xmp>
  * -1 : not yet initialized
  *  0 : not running in VAJ
  *  1 : running in VAJ
  * </xmp>
  */
  private static int _isRunningInVaj = -1;

  // xsd DateTime format
  private static DateFormat _xsdDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
}
