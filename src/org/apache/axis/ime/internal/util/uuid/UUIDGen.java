
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
public interface UUIDGen
{
  public void init();
  public void destroy();
  public String nextUUID();
}