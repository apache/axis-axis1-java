/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.handlers;

import java.util.Hashtable;
import java.util.Enumeration;
import org.apache.axis.*;
import org.apache.axis.utils.QName;
import org.apache.axis.utils.Debug;
import org.apache.axis.utils.XMLUtils ;

import org.w3c.dom.* ;

/** <code>BasicHandler</code> is a utility class which implements simple
 * property setting/getting behavior, and stubs out a lot of the Handler
 * methods.  Extend this class to make writing your Handlers easier, and
 * then override what you need to.
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 * @author Doug Davis (dug@us.ibm.com
 */
public abstract class BasicHandler implements Handler {
    protected Hashtable  options ;

    /** Stubbed-out methods.  Override in your child class to implement
     * any real behavior.
     */
    
    public void init()
    {
    }
    
    public void cleanup()
    {
    }

    public boolean canHandleBlock(QName qname)
    {
        return false;
    }

    /** Must implement this in subclasses.
     */
    public abstract void undo(MessageContext msgContext);

    /** Must implement this in subclasses.
     */
    public abstract void invoke(MessageContext msgContext) throws AxisFault;

    /**
     * Add the given option (name/value) to this handler's bag of options
     */
    public void addOption(String name, Object value) {
        if ( options == null ) options = new Hashtable();
        options.put( name, value );
    }

    /**
     * Returns the option corresponding to the 'name' given
     */
    public Object getOption(String name) {
        if ( options == null ) return( null );
        return( options.get(name) );
    }

    /**
     * Return the entire list of options
     */
    public Hashtable getOptions() {
        return( options );
    }

    public void setOptions(Hashtable opts) {
        options = opts ;
    }

    public Element getDeploymentData(Document doc) {
      Debug.Print( 1, "Enter: BasicHandler::getDeploymentData" );

      Element  root = doc.createElement( "handler" );

      root.setAttribute( "class", this.getClass().getName() );
      options = this.getOptions();
      if ( options != null ) {
        Enumeration e = options.keys();
        while ( e.hasMoreElements() ) {
          String k = (String) e.nextElement();
          Object v = options.get(k);
          Element e1 = doc.createElement( "option" );
          e1.setAttribute( "name", k );
          e1.setAttribute( "value", v.toString() );
          root.appendChild( e1 );
        }
      }
      Debug.Print( 1, "Exit: BasicHandler::getDeploymentData" );
      return( root );
    }
}
