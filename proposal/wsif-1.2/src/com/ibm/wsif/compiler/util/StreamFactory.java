// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.util;

import java.io.*;
import java.net.*;
import org.apache.soap.util.*;
import com.ibm.wsif.*;
import com.ibm.wsif.compiler.schema.tools.*;

/**
 *
 * @author Matthew J. Duftler
 */
public class StreamFactory
{
  public OutputStream getOutputStream(String root,
                                      String name,
                                      boolean overwrite)
                                        throws WSIFException
  {
    boolean verbose = Conventions.getVerbose();

    if (root != null)
    {
      File directory = new File(root);

      if (!directory.exists())
      {
        if (!directory.mkdirs())
        {
          throw new WSIFException("Failed to create directory '" + root +
                                  "'.");
        }
        else if (verbose)
        {
          System.out.println("Created directory '" +
                             directory.getAbsolutePath() + "'.");
        }
      }
    }

    File file = new File(root, name);
    String absolutePath = file.getAbsolutePath();

    if (file.exists())
    {
      if (!overwrite)
      {
        throw new WSIFException("File '" + absolutePath +
                                "' already exists. Please remove it or " +
                                "enable the overwrite option.");
      }
      else
      {
        file.delete();

        if (verbose)
        {
          System.out.println("Deleted file '" + absolutePath + "'.");
        }
      }
    }

    if (verbose)
    {
      System.out.println("Created file '" + absolutePath + "'.");
    }

    try
    {
      return new FileOutputStream(absolutePath);
    }
    catch (FileNotFoundException e)
    {
      throw new WSIFException("Problem getting output stream.", e);
    }
  }

  public InputStream getInputStream(String root, String name)
    throws IOException
  {
    String fileName = (root != null)
                      ? root + File.separatorChar + name
                      : name;
    URL url = null;
    Object content = null;

    try
    {
      url = StringUtils.getURL(null, fileName);
      content = url.getContent();
    }
    catch (SecurityException e)
    {
      throw new IOException("Your JVM's security manager has disallowed " +
                            "access to '" + fileName + "'.");
    }
    catch (IOException e)
    {
      throw new IOException("The resource at '" + fileName +
                            "' was not found.");
    }

    if (content == null)
    {
      throw new IllegalArgumentException("No content at '" + fileName + "'.");
    }
    else if (content instanceof InputStream)
    {
      return (InputStream)content;
    }
    else
    {
      throw new IOException("The content of '" + fileName +
                            "' is not a stream.");
    }
  }
}
