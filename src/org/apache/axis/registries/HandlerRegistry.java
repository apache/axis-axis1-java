package org.apache.axis.registries ;

import org.apache.axis.* ;

public interface HandlerRegistry {
  /**
   * Init (ie. load settings...)
   * TODO: pass in the config info so it can scan it for options
   */
  public void init();

  /**
   * Add a new Handler to the registry.
   */
  public void add(String key, Handler handler);
  
  /**
   * Remove a Handler (locate by key) from the registry - returns old
   * value if it was there - or null if not.
   */
  public Handler remove(String key);

  /**
   * Given a 'key' return the corresponding Handler
   */
  public Handler find(String key);

  /**
   * Return the list (in an array) of keys for the Handlers
   */
  public String[] list();
};
