// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.util;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.apache.soap.Constants;
import javax.wsdl.*;
import javax.wsdl.extensions.*;
import com.ibm.wsdl.extensions.*;
import com.ibm.wsdl.util.*;
import com.ibm.wsif.*;

/**
 *
 * @author Matthew J. Duftler
 */
public class Utils
{
  public static void addAllTypesElements(Definition def, List toList)
  {
	Types types = def.getTypes();
	if (types!=null)
	{
	  Iterator extEleIt = types.getExtensibilityElements().iterator();
	  while (extEleIt.hasNext())
  	  {
		// the unknown extensibility element are wrappers for DOM elements
		UnknownExtensibilityElement typesElement = 
			(UnknownExtensibilityElement) extEleIt.next();
		toList.add(typesElement);
  	  }
	}

    Map imports = def.getImports();

    if (imports != null)
    {
      Iterator valueIterator = imports.values().iterator();

      while (valueIterator.hasNext())
      {
        List importList = (List)valueIterator.next();

        if (importList != null)
        {
          Iterator importIterator = importList.iterator();

          while (importIterator.hasNext())
          {
            Import tempImport = (Import)importIterator.next();

            if (tempImport != null)
            {
              Definition importedDef = tempImport.getDefinition();

              if (importedDef != null)
              {
                addAllTypesElements(importedDef, toList);
              }
            }
          }
        }
      }
    }
  }

  public static List getAllTypesElements(Definition def)
  {
    List ret = new Vector();

    addAllTypesElements(def, ret);

    return ret;
  }

  public static String getPackageName(String fqClassName)
  {
    String packageName = "";

    if (fqClassName != null)
    {
      int index = fqClassName.lastIndexOf('.');

      if (index != -1)
      {
        packageName = fqClassName.substring(0, index);
      }
    }

    return packageName;
  }

  public static String getClassName(String fqClassName)
  {
    if (fqClassName != null)
    {
      int index = fqClassName.lastIndexOf('.');

      if (index != -1)
      {
        fqClassName = fqClassName.substring(index + 1);
      }
    }

    return fqClassName;
  }

  public static String queryJavaTypeName(QName type,
                                         String encodingStyleURI,
                                         Hashtable typeReg)
                                           throws IllegalArgumentException
  {
    TypeMapping tm = (TypeMapping)typeReg.get(type);

    if (tm != null)
    {
      return tm.javaType;
    }
    else if (encodingStyleURI != null
             && encodingStyleURI.equals(Constants.NS_URI_LITERAL_XML))
    {
      return "org.w3c.dom.Element";
    }
    else
    {
      throw new IllegalArgumentException("No mapping was found for '" + type +
                                         "'.");
    }
  }

  public static String getQuotedString(Reader source,
                                       int indent)
                                         throws WSIFException
  {
    String indentStr = StringUtils.getChars(indent, ' ');

    try
    {
      BufferedReader br = new BufferedReader(source);
      StringWriter sw = new StringWriter();
      int count = 0;
      String tempLine = null;

      while ((tempLine = br.readLine()) != null)
      {
        sw.write((count > 0
                  ? " + \"" + StringUtils.lineSeparatorStr + "\" +" +
                    StringUtils.lineSeparator
                  : "") +
                 indentStr + '\"' + StringUtils.cleanString(tempLine) + '\"');

        count++;
      }

      return sw.toString();
    }
    catch (IOException e)
    {
      throw new WSIFException("Problem writing strings.", e);
    }
  }

  public static String convertToObject(String sourceClassName,
                                       String expr)
                                         throws WSIFException
  {
    return convertClass(sourceClassName, expr, "java.lang.Object");
  }

  public static String convertFromObject(String expr,
                                         String targetClassName)
                                           throws WSIFException
  {
    return convertClass("java.lang.Object", expr, targetClassName);
  }

  private static String convertClass(String sourceClassName,
                                     String expr,
                                     String targetClassName)
                                       throws WSIFException
  {
    if (sourceClassName == null || targetClassName == null)
    {
      throw new WSIFException("I was unable to convert an object from " +
                              sourceClassName + " to " + targetClassName +
                              ".");
    }

    String shortTargetClassName = getShortName(targetClassName);

    if (sourceClassName.equals("java.lang.Object"))
    {
      if (isPrimitive(targetClassName))
      {
        return "((" + getWrapperClassName(targetClassName) +
               ")" + expr + ")." + shortTargetClassName + "Value()";
      }
      else
      {
        return "(" + targetClassName + ")" + expr;
      }
    }
    else if (isPrimitive(sourceClassName)
             && targetClassName.equals("java.lang.Object"))
    {
      return "new " + getWrapperClassName(sourceClassName) + "(" + expr + ")";
    }
    else
    {
      // Target class must be "assignableFrom" source class.
      return expr;
    }
  }

  private static String getShortName(String className)
  {
    if (className.startsWith("java.lang."))
      return className.substring(10);
    else
      return className;
  }

  private static String getWrapperClassName(String primitiveClassName)
  {
    if (primitiveClassName.equals("int"))
      return "Integer";
    else if (primitiveClassName.equals("char"))
      return "Character";
    else
      return getCapitalized(primitiveClassName);
  }

  private static String getCapitalized(String className)
  {
    return Character.toUpperCase(className.charAt(0)) +
           className.substring(1);
  }

  private static boolean isPrimitive(String className)
  {
    String[] primNames = {
                           "boolean", "byte", "char", "short", "int", "long",
                           "float", "double", "void"
                         };

    for (int i = 0; i < primNames.length; i++)
    {
      if (primNames[i].equals(className))
      {
        return true;
      }
    }

    return false;
  }
}
