// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

import java.util.Hashtable;

/**
 * The interceptor config contains information that's given to
 * an interceptor at initialization time.
 *
 * @author Sanjiva Weerawarana <sanjiva@watson.ibm.com>
 * @author Paul Fremantle <pzf@uk.ibm.com>
 */
public class WSIFInterceptorConfig {
  Hashtable properties = new Hashtable ();

  public void setProperty (String key, Object value) {
    properties.put (key, value);
  }

  public Object getProperty (String key) {
    return properties.get (key);
  }
}
