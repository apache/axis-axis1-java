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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.utils ;

import java.io.* ;
import java.util.Properties;
import org.w3c.dom.* ;
import javax.xml.parsers.* ;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.* ;
import org.apache.axis.Constants;

public class XMLUtils {
  private static DocumentBuilder        db  ;
  private static DocumentBuilderFactory dbf = init();
  private static SAXParserFactory       saxFactory;
  
  static {
    // Initialize SAX Parser factory defaults
    initSAXFactory(null, true, false);
  }
                                             
  /** Initialize the SAX parser factory.
   * 
   * @param factoryClassName The class name of the desired SAXParserFactory
   *                         implementation.  Will be assigned to the system
   *                         property <b>javax.xml.parsers.SAXParserFactory</b>.
   * @param namespaceAware true if we want a namespace-aware parser (which we do)
   * @param validating true if we want a validating parser
   * 
   */
  public static void initSAXFactory(String factoryClassName,
                                     boolean namespaceAware,
                                     boolean validating)
  {
      if (factoryClassName != null) {
        System.setProperty("javax.xml.parsers.SAXParserFactory",
                           factoryClassName);
      }
      saxFactory = SAXParserFactory.newInstance();
      saxFactory.setNamespaceAware(namespaceAware);
      saxFactory.setValidating(validating);
  }

  public static DocumentBuilderFactory init() {
    Document               doc = null ;

    try {
      dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      db  = dbf.newDocumentBuilder();
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
    return( dbf );
  }
  
  /** Get a SAX parser instance from the JAXP factory.
   * 
   * @return a SAXParser instance.
   */
  public static SAXParser getSAXParser() {
    // Might want to cache the parser (on a per-thread basis, as I don't think
    // SAX parsers are thread-safe)...
    try {
      return saxFactory.newSAXParser();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      return null;
    } catch (SAXException se) {
      se.printStackTrace();
      return null;
    }
  }

  public static Document newDocument() {
    return( db.newDocument() );
  }

  public static Document newDocument(InputSource inp) {
    try {
      return( db.parse( inp ) );
    }
    catch( Exception e ) {
      e.printStackTrace();
      return( null );
    }
  }

  public static Document newDocument(InputStream inp) {
    try {
      return( db.parse( inp ) );
    }
    catch( Exception e ) {
      e.printStackTrace();
      return( null );
    }
  }

  public static Document newDocument(String uri) {
    try {
      return( db.parse( uri ) );
    }
    catch( Exception e ) {
      e.printStackTrace();
      return( null );
    }
  }

  /** Obtain a JAXP Transformer.
   * 
   * (I'm assuming that using the transformer API is the accepted JAXP-style
   *  way to do XML serialization)
   * 
   * @return a JAXP Transformer
   */
  public static Transformer getTransformer()
  {
      try {
          TransformerFactory factory = TransformerFactory.newInstance();
          return factory.newTransformer();
      } catch (TransformerConfigurationException e) {
          e.printStackTrace();
          return null;
      }
  }
  
  private static String privateElementToString(Element element,
                                               boolean omitXMLDecl)
  {
      try {
        StringWriter sw = new StringWriter();
        DOMSource source = new DOMSource(element);
        StreamResult result = new StreamResult(sw);
        Transformer transformer = getTransformer();
        Properties p = new Properties();
        p.put(OutputKeys.OMIT_XML_DECLARATION,
              omitXMLDecl ? "yes" : "no");
        transformer.setOutputProperties(p);
        transformer.transform(source, result);
        sw.close();
        return sw.toString();
      } 
      catch( Exception e) {
          e.printStackTrace();
      }
      return( null );
  }
  
  public static String DocumentToString(Document doc) {
      return privateElementToString(doc.getDocumentElement(), false);
  }

  public static void DocumentToStream(Document doc, OutputStream out) {
      ElementToStream(doc.getDocumentElement(), out);
  }

  public static String ElementToString(Element element) {
      return privateElementToString(element, true);
  }
  
  public static void ElementToStream(Element element, OutputStream out) {
    try {
      Transformer transformer = getTransformer();
      DOMSource source = new DOMSource(element);
      StreamResult result = new StreamResult(out);
      transformer.transform(source, result);
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }
  
  public static String getInnerXMLString(Element element) {
      String elementString = ElementToString(element);
      int start, end;
      start = elementString.indexOf(">") + 1;
      end = elementString.lastIndexOf("</");
      if (end > 0) 
          return elementString.substring(start,end);
      else 
          return null;
  }
  
  public static String getPrefix(String uri, Node e) {
      while (e != null && (e.getNodeType() == Element.ELEMENT_NODE)) {
          NamedNodeMap attrs = e.getAttributes();
          for (int n = 0; n < attrs.getLength(); n++) {
              Attr a = (Attr)attrs.item(n);
              String name;
              if ((name = a.getName()).startsWith("xmlns:") &&
                  a.getNodeValue().equals(uri)) {
                  return name.substring(6);
              }
          }
          e = e.getParentNode();
      }
      return null;
  }

  public static String getNamespace(String prefix, Node e) {
      while (e != null && (e.getNodeType() == Node.ELEMENT_NODE)) {
          String name = 
              ((Element)e).getAttributeNS(Constants.NS_URI_XMLNS, prefix);
          if (name != null) return name;
          e = e.getParentNode();
      }
      return null;
  }
}
