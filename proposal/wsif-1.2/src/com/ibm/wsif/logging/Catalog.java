// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.logging;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.text.MessageFormat;

/**
* This class is the main interface to message catalogs (.properties files). It hides the
* details of <code>java.util.ResourceBundle</code> and <code>java.util.MessageFormat</code>.
* This class will not throw <code>java.util.MissingResourceException</code>s, but rather
* return <code>null</code> for message strings that cannot be found.
* @author <a href="mailto:fussi@de.ibm.com">Erich Fussi</a>
*/
public final class Catalog
{
  /**
  * The name of the default message catalog, <code>com.ibm.flow.catalog.Messages</code>.
  */
  public final static String MESSAGES_FILE = JavaUtilities.getMainPackageName() + ".catalog.Messages";

  /**
  * Constructs the default message catalog in the default language.
  */
  public Catalog()
  {
    this(MESSAGES_FILE, Locale.getDefault());
  }

  /**
  * Constructs a named message catalog in the default language.
  * @param bundleName
  *    The name of the message catalog (without the <code>.properties<code> extension).
  */
  public Catalog(String bundleName)
  {
    this(bundleName, Locale.getDefault());
  }

  /**
  * Constructs the default message catalog in the given language.
  * @param locale
  *    The language to use. If that language is not available, the default lookup mechanism
  *    of <code>java.util.ResourceBundle</code> will be applied.
  */
  public Catalog(Locale locale)
  {
    this(MESSAGES_FILE, locale);
  }

  /**
  * Constructs a named message catalog in the given language.
  * @param bundleName
  *    The name of the message catalog (without the <code>.properties<code> extension).
  * @param locale
  *    The language to use. If that language is not available, the default lookup mechanism
  *    of <code>java.util.ResourceBundle</code> will be applied.
  */
  private Catalog(String bundleName, Locale locale)
  {
    if (bundleName != null)
      _catalog = ResourceBundle.getBundle(bundleName, locale);
  }

  /**
  * Retrieves a message with no parameters from the catalog.
  * @param key
  *    The key of the message to retrieve.
  * @return
  *    The message text or <code>key</code>, if the key cannot be found.
  */
  public String get(String key)
  {
    try
    {
      if (key != null && _catalog != null)
        return _catalog.getString(key);
    }
    catch (MissingResourceException ignored) { }

    return key;
  }

  /**
  * Retrieves a message with one parameter from the catalog. This is just a convenience
  * wrapper for {@link #get(String,Object[])}.
  * @param key
  *    The key of the message to retrieve.
  * @param var
  *    The substitution variable to use.
  * @return
  *    The message text or <code>null</code>, if the key cannot be found.
  */
  public String get(String key, Object var)
  {
    return get(key, new Object[] { var });
  }

  /**
  * Retrieves a message with two parameters from the catalog. This is just a convenience
  * wrapper for {@link #get(String,Object[])}.
  * @param key
  *    The key of the message to retrieve.
  * @param var1
  *    The first substitution variable to use.
  * @param var2
  *    The second substitution variable to use.
  * @return
  *    The message text or <code>null</code>, if the key cannot be found.
  */
  public String get(String key, Object var1, Object var2)
  {
    return get(key, new Object[] { var1, var2 });
  }

  /**
  * Retrieves a message with an arbitrary number of parameters from the catalog.
  * <ul>
  * <li>If you specify less variables than there are parameters, the placeholders for
  *     the missing parameters will be left as-is. This is also true if you omit
  *     the variables at all using {@link #get(String)}.</li>
  * <li>If you specify a <code>null</code> variable, the corresponding placeholder
  *     will be replaced with the string <code>null</code>.</li>
  * <li>If you specify any variables at all when retrieving a message (it does not matter
  *     if they are actually used or not), you must escape the single quote character
  *     by doubling it in the message string. In contrast, if you don't specify any
  *     variables by using the {@link #get(String)} method (again, it does not matter if they
  *     are actually used), you must not escape the single quote character. This may
  *     be confusing at first, check the source code of <code>CatalogJunitTest</code>
  *     for details.</li>
  * </ul>
  * @param key
  *    The key of the message to retrieve.
  * @param vars
  *    The list of substitution variables to use.
  * @return
  *    The message text or <code>null</code>, if the key cannot be found.
  */
  public String get(String key, Object[] vars)
  {
    String text = get(key);
    return text == null ? null : MessageFormat.format(text, vars);
  }

  private ResourceBundle _catalog;
}
