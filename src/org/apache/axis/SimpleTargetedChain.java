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

package org.apache.axis ;

import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.utils.JavaUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

import java.util.Enumeration;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glyn Normington (norm@uk.ibm.com)
 */
public class SimpleTargetedChain extends SimpleChain implements TargetedChain
{
   protected static Log log =
      LogFactory.getLog(SimpleTargetedChain.class.getName());

   protected Handler    requestHandler ;
   protected Handler    pivotHandler ;
   protected Handler    responseHandler ;

   /**
    * Pivot indicator sets "past pivot point" before the response handler
    * runs. This avoids having to reimplement SimpleChain.invoke and
    * SimpleChain.generateWSDL.
    */
   private class PivotIndicator extends BasicHandler {
       public PivotIndicator() {}

       public void invoke(MessageContext msgContext) throws AxisFault {
           msgContext.setPastPivot(true);
       }
   }

   /**
    * Default no-arg constructor.
    */
   public SimpleTargetedChain() {}

   /**
    * Constructor for an instance with effectively only a pivot handler.
    */
   public SimpleTargetedChain(Handler handler) {
       pivotHandler = handler;
       if (pivotHandler != null) {
           addHandler(pivotHandler);
           addHandler(new PivotIndicator());
       }
   }

   /**
    * Constructor which takes real or null request, pivot, and response
    * handlers.
    */
   public SimpleTargetedChain(Handler reqHandler, Handler pivHandler,
                              Handler respHandler) {
       init(reqHandler, null, pivHandler, null, respHandler);
   }

   /**
    * Initialiser which takes real or null request, pivot, and response
    * handlers and which allows for special request and response
    * handlers to be inserted just before and after any pivot handler.
    */
   protected void init(Handler reqHandler, Handler specialReqHandler,
                       Handler pivHandler, Handler specialRespHandler,
                       Handler respHandler) {

       requestHandler = reqHandler;
       if (requestHandler != null)
           addHandler(requestHandler);

       if (specialReqHandler != null)
           addHandler(specialReqHandler);

       pivotHandler = pivHandler;
       if (pivotHandler != null) {
           addHandler(pivotHandler);
           addHandler(new PivotIndicator());
       }

       if (specialRespHandler != null)
           addHandler(specialRespHandler);

       responseHandler = respHandler;
       if (responseHandler != null)
           addHandler(responseHandler);
   }

   public Handler getRequestHandler() { return( requestHandler ); }

   public Handler getPivotHandler() { return( pivotHandler ); }

   public Handler getResponseHandler() { return( responseHandler ); }

};
