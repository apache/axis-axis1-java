/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights 
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
 * 4. The names "AXIS" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 2000, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package samples.stock ;

import java.io.*;
import java.net.URL;

import org.w3c.dom.*;
import org.xml.sax.InputSource ;
import org.apache.xerces.parsers.* ;
import org.apache.xerces.framework.* ;

/**
 * See \samples\stock\readme for info.
 *
 * @author Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 */
public class StockQuoteService {
  public float getQuote (String symbol) throws Exception {
    // get a real (delayed by 20min) stockquote from 
    // http://www.xmltoday.com/examples/stockquote/. The IP addr 
    // below came from the host that the above form posts to ..

    URL          url = new URL( "http://www.xmltoday.com/examples/" +
                                "stockquote/getxmlquote.vep?s="+symbol );

    DOMParser    parser = new DOMParser();
    InputSource  inp    = new InputSource( url.openStream() );

    parser.parse( inp );

    Document     doc  = parser.getDocument();
    Element      elem = doc.getDocumentElement ();
    NodeList     list = elem.getElementsByTagName( "price" );

    elem = (Element) list.item( 0 );
    String quoteStr = elem.getAttribute ("value");
    try {
      return Float.valueOf(quoteStr).floatValue();
    } catch (NumberFormatException e1) {
      // maybe its an int?
      try {
        return Integer.valueOf(quoteStr).intValue() * 1.0F;
      } catch (NumberFormatException e2) {
        return -1.0F;
      }
    }
  }
}
