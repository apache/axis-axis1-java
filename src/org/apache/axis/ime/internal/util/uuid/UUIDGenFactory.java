
/**
 * 
 *  UUIDGen adopted from the juddi project
 *  (http://sourceforge.net/projects/juddi/)
 * 
 */

package org.apache.axis.ime.internal.util.uuid;

import java.io.*;

/**
 * A Universally Unique Identifier (UUID) is a 128 bit number generated
 * according to an algorithm that is garanteed to be unique in time and space
 * from all other UUIDs. It consists of an IEEE 802 Internet Address and
 * various time stamps to ensure uniqueness. For a complete specification,
 * see ftp://ietf.org/internet-drafts/draft-leach-uuids-guids-01.txt [leach].
 *
 * @author  Steve Viens
 * @version 1.0 11/7/2000
 * @since   JDK1.2.2
 */
public abstract class UUIDGenFactory
{
  private static final String defaultUUIDGenClassName = "org.apache.axis.ime.internal.util.uuid.SimpleUUIDGen";

  /**
   * getInstance
   *
   * Returns the singleton instance of UUIDGen
   */
  public static UUIDGen getUUIDGen(String uuidgenClassName)
  {
    UUIDGen uuidgen = null;

    if ((uuidgenClassName == null) || (uuidgenClassName.length() == 0))
    {
      // use the default UUIDGen implementation
      uuidgenClassName = defaultUUIDGenClassName;
    }

    Class uuidgenClass = null;
    try
    {
      // instruct the class loader to load the UUIDGen implementation
      uuidgenClass = java.lang.Class.forName(uuidgenClassName);
    }
    catch(ClassNotFoundException e)
    {
      throw new RuntimeException("The implementation of UUIDGen interface " +
  "specified cannot be found in the classpath: "+uuidgenClassName +
  " not found.");
    }

    try
    {
      // try to instantiate the UUIDGen subclass
      uuidgen = (UUIDGen)uuidgenClass.newInstance();
    }
    catch(java.lang.Exception e)
    {
      throw new RuntimeException("Exception encountered while attempting to " +
  "instantiate the specified implementation of UUIDFactory: " +
  uuidgenClass.getName() + "; message = " + e.getMessage());
    }

    return uuidgen;
  }

  /**
   * Release any aquired external resources and stop any background threads.
   */
  public static void destroyUUIDGen(UUIDGen uuidgen)
  {
    if (uuidgen != null)
      uuidgen.destroy();
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  // test driver
  public static void main(String argc[])
  {
    long startTime = 0;
    long endTime = 0;
    UUIDGen uuidgen = null;

    uuidgen = UUIDGenFactory.getUUIDGen(null);
//    uuidgen = UUIDGenFactory.getUUIDGen("org.juddi.uuidgen.SimpleUUIDGen");
    startTime = System.currentTimeMillis();
    for (int i = 1; i <= 50; ++i)
    {
      String u = uuidgen.nextUUID();
      System.out.println( i + ":  " + u );
    }
    endTime = System.currentTimeMillis();
    System.out.println("SimpleJavaUUIDGen took "+(endTime-startTime)+" milliseconds");

    UUIDGenFactory.destroyUUIDGen(uuidgen);
  }
}