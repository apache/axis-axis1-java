// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.schema;

/**
 *
 * @author Matthew J. Duftler
 */
public class SchemaException extends Exception {
  Throwable targetException;

  public SchemaException (String message) {
    super (message);
  }

  public SchemaException (String message, Throwable targetException) {
    super (message);
    this.targetException = targetException;
  }

  Throwable getTargetException () {
    return targetException;
  }
}

