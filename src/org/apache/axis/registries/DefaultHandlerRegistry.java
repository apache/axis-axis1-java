/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:  
 *     "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *  not be used to endorse or promote products derived from this
 *  software without prior written permission. For written 
 *  permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *  nor may "Apache" appear in their name, without prior written
 *  permission of the Apache Software Foundation.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.registries ;

import java.io.* ;
import java.util.* ;

import org.apache.axis.* ;
import org.apache.axis.utils.Debug ;
import org.apache.axis.utils.Admin ;
import org.apache.axis.utils.XMLUtils ;
import org.apache.axis.handlers.* ;
import org.apache.axis.handlers.http.*;
import org.apache.axis.suppliers.* ;
import org.apache.axis.registries.* ;

import org.apache.axis.transport.http.* ;

/** 
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class DefaultHandlerRegistry extends SupplierRegistry {
  private boolean dontSave = false ;
  private boolean onServer = false ;

  public DefaultHandlerRegistry(String fileName) {
   super( fileName );
  }

  public void setOnServer(boolean b) {
    onServer = b ;
  }

  public void setDontSave(boolean b) {
    dontSave = b ;
  }
  
  protected void load() { 
    super.load();
    if ( suppliers != null && suppliers.size() != 0 ) 
      return ;

    /* If we got here then there was no registry on the filesystem */
    /* or it was empty, which means they're dead in the water,   */
    /* so let's just load some default handlers/services.  In a  */
    /* system that's been properly configured we should never hit  */
    /* this section.                         */
    /* Set the 'dontSave' flag so that we don't save this list to  */
    /* the filesystem.  We'll only start to save it once they've   */
    /* actually done an add/remove - this will prevent us from   */
    /* creating a registry on disk for the non-configured case.  */
    /***************************************************************/
    dontSave = true ;

    Admin                 admin      = new Admin();

    Handler h = null ;
    Chain   c = null ;

    if ( onServer ) {
      this.add( "debug"        , new DebugHandler() );
      //this.add( "SOAPServer"   , new SOAPServerHandler() );
      this.add( "MsgDispatcher", new MsgDispatchHandler() );
      this.add( "RPCDispatcher", new RPCDispatchHandler() );
      this.add( "HTTPSender"   , new HTTPDispatchHandler() );
      this.add( "HTTPAction"   , new HTTPActionHandler() );
      this.add( "HTTPAuth"     , new HTTPAuthHandler() );
      this.add( "JWSHandler"   , new JWSHandler() );
      this.add( "JWSProcessor" , new JWSProcessor() );
      // Want this around by default for testing.
      this.add( "EchoHandler"  , new EchoHandler() );

      c = new SimpleChain();
      c.addHandler( this.find( "debug" ) );
      this.add( "global.input", c );

      c = new SimpleChain();
      c.addHandler( this.find( "HTTPAuth" ) );
      c.addHandler( this.find( "JWSHandler" ) );
      c.addHandler( this.find( "HTTPAction" ) );
      this.add( "HTTP.input", c );

    }
    else {
      this.add( "HTTPSender", new HTTPDispatchHandler() );
    }

    dontSave = false ;
  }

  protected void save() {
    if ( dontSave ) return ;
    super.save();
  }
};
