
/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.utils ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */

// We should replace this with Log4J if we really want this stuff.  But
// for now I want something that's easy and doesn't require any other
// jar files

import java.io.* ;

public class Debug {
  private static int     debugLevel = 0 ;
  private static boolean toScreen   = true ;
  private static boolean toFile     = false ;

  public static void setDebugLevel( int dl ) {
    debugLevel = dl ;
  }

  public static int getDebugLevel() {
    return( debugLevel );
  }

  public static int incDebugLevel() {
    return( ++debugLevel );
  }

  public static int decDebugLevel() {
    return( debugLevel = ( (debugLevel == 0) ? 0 : (debugLevel-1) ) );
  }

  public static void setToScreen(boolean b) {
    toScreen = b ;
  }

  public static void setToFile(boolean b) {
    toFile = b ;
  }

  public static boolean DebugOn(int level) {
    return( debugLevel >= level );
  }

  public static void Print( int level, Exception exp ) {
    if ( debugLevel < level ) return ;
    try {
      String msg = "Exception: " + exp ;
      if ( toScreen )
        System.err.println( msg );
      if ( toFile ) {
        FileWriter   fw = new FileWriter( "AxisDebug.log", true );
        fw.write( msg, 0, msg.length() );
        PrintWriter  pw = new PrintWriter( fw );
        exp.printStackTrace( pw );
        pw.close();
        fw.close();
      }
    }
    catch( Exception e ) {
      System.err.println( "Can't log debug info: " + e );
      e.printStackTrace();
    }
  }

  public static void Print( int level, String msg ) {
    if ( debugLevel >= level ) {
      if ( toScreen ) System.err.println( msg );
      if ( toFile ) {
        try {
          FileWriter fw = new FileWriter( "AxisDebug.log", true );
          fw.write( msg, 0, msg.length() );
          fw.close();
        }
        catch( Exception e ) {
          System.err.println( "Can't log debug info: " + e );
          e.printStackTrace();
        }
      }
    }
  }
}
