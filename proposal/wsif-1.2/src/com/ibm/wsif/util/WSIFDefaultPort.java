// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util;

import java.util.*;

import com.ibm.wsif.*;

/**
 * A DefaultWSIFPort is a default implementation of WSIFPort
 * all methods are implemented except execute*.
 *
 * @author Paul Fremantle
 * @author Alekander Slominski
 * @author Matthew J. Duftler
 * @author Sanjiva Weerawarana
 * @author Nirmal Mukhi
 */
public abstract class WSIFDefaultPort implements WSIFPort {
  
  /**
   * NOTE: Not implemented.
   * @deprecated
   */
  public boolean executeRequestResponseOperation(String op,
                                                 WSIFMessage input,
                                                 WSIFMessage output,
                                                 WSIFMessage fault)
    throws WSIFException
  {
    throw new WSIFException("This service has no input-output operations.");
  }
  
  /**
   * NOTE: Not implemented.
   * @deprecated
   */
  public void executeInputOnlyOperation (String op,
                                         WSIFMessage input)
    throws WSIFException
  {
    throw new WSIFException("This service has no input-only operations.");
  }
  
  /**
   * @deprecated
   */
  public WSIFMessage createInputMessage ()
  {
    return new WSIFDefaultMessage();
  }
  
  /**
   * @deprecated
   */
  public WSIFMessage createInputMessage (String name)
  {
    WSIFMessage message = createInputMessage();
    message.setName(name);
    return message;
  }

  /**
   * @deprecated
   */
  public WSIFMessage createOutputMessage ()
  {
    return new WSIFDefaultMessage();
  }
  
  /**
   * @deprecated
   */
  public WSIFMessage createOutputMessage (String name)
  {
    WSIFMessage message = createOutputMessage();
    message.setName(name);
    return message;
  }
  
  /**
   * @deprecated
   */
  public WSIFMessage createFaultMessage ()
  {
    return new WSIFDefaultMessage();
  }
  
  /**
   * @deprecated
   */
  public WSIFMessage createFaultMessage (String name)
  {
    WSIFMessage message = createFaultMessage();
    message.setName(name);
    return message;
  }
  
  public void close () throws WSIFException
  {
  }
  
  /**
   * Utility method to return key suitable for hash table.
   */
  protected String getKey(String name, String inputName, String outputName) {
    return name
      + (inputName != null ? ":" + inputName : "")
      + (outputName != null ? ":" + outputName : "");
  }

  /**
   * Utility method to retrieve extensibility element from list
   * checks also that it is exactly one extensibility element.
   */
  protected Object getExtElem(Object ctx, Class extType, List extElems)
    throws WSIFException {
    Object found = null;
    if (extElems != null) {
      for (Iterator i = extElems.iterator(); i.hasNext();) {
        // if so return new
        Object o = i.next();
        if (extType.isAssignableFrom(o.getClass())) {
          if (found != null) {
            throw new WSIFException(
              "duplicated extensibility element "
                + extType.getClass().getName()
                + " in " 
                + ctx);
          }
          found = o;
        }
      }
    }
    return found;
  }

  /**
   * Utility method to retrieve multiple extensibility elements from a list.
   */
  protected List getExtElems(Object ctx, Class extType, List extElems)
    throws WSIFException 
  {
    List found = new ArrayList();
    if (extElems != null) for (Iterator i = extElems.iterator(); i.hasNext();) 
    {
      Object o = i.next();
      if (extType.isAssignableFrom(o.getClass())) found.add(o);
    }
    if (found.size()==0) return null;
    return found;
  }

  /**
   * Tests if this port supports synchronous calls to operations.
   * 
   * @return true   by default WSIFPorts do support synchronous calls
   */
  public boolean supportsSync() {
     return true;
  }
  
  /**
   * Tests if this port supports asynchronous calls to operations.
   * 
   * @return false   by default ports do not support asynchronous calls
   */
  public boolean supportsAsync() {
     return false;
  }
  
}

