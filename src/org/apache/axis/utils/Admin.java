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
import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.registries.* ;
import org.apache.axis.handlers.* ;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.* ;
import org.apache.axis.suppliers.*;
import org.apache.axis.encoding.*;
import org.apache.axis.client.AxisClient;
import org.apache.axis.server.AxisServer;

import org.w3c.dom.* ;

/**
 * Handy static utility functions for turning XML into
 * Axis deployment operations.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class Admin {

  /**
   * Fill in options for a given handler.
   * 
   * @param root the element containing the options
   * @param handler the Handler to set options on
   */
  private static void getOptions(Element root, Handler handler) {
    NodeList  list = root.getElementsByTagName("option");
    for ( int i = 0 ; list != null && i < list.getLength() ; i++ ) {
      Element elem  = (Element) list.item(i);
      String  name  = elem.getAttribute( "name" );
      String  value = elem.getAttribute( "value" );

      if ( name != null && value != null )
        handler.addOption( name, value );
    }
  }
  
  /**
   * Register a set of type mappings for a service.
   * 
   * @param root the Element containing the service configuration
   * @param service the SOAPService we're working with.
   */
  private static void registerTypeMappings(Element root, SOAPService service)
    throws Exception
  {
    NodeList list = root.getElementsByTagName("bean");
    Debug.Print(1, "Registering " + list.getLength() + " service-specific beans.");
    for (int i = 0; list != null && i < list.getLength(); i++) {
      Element el = (Element)list.item(i);
      registerBeanMapping(el, service.getTypeMappingRegistry());
    }
  }

  /**
   * Process a given XML document - needs cleanup.
   */
  public Document AdminService(MessageContext msgContext, Document xml)
                  throws AxisFault
  {
    Debug.Print( 1, "Enter: Admin:AdminService" );
    Document doc = process( msgContext, xml );
    Debug.Print( 1, "Exit: Admin:AdminService" );
    return( doc );
  }

  public Document process(MessageContext msgContext, Document doc) throws AxisFault {
    return( process( msgContext, doc.getDocumentElement() ) );
  }

  /**
   * The meat of the Admin service.  Process an xML document rooted with
   * a "deploy", "undeploy", "list", or "quit" element.
   * 
   * @param msgContext the MessageContext we're servicing
   * @param root the root Element of the XML
   * @return an XML Document indicating the results.
   */
  public Document process(MessageContext msgContext, Element root) throws AxisFault {
    Document doc = null ;

    AxisEngine engine =  msgContext.getAxisEngine();
    HandlerRegistry hr = engine.getHandlerRegistry();
    HandlerRegistry sr = engine.getServiceRegistry();

    try {
      String            action = root.getLocalName();
      AxisClassLoader   cl     = AxisClassLoader.getClassLoader();

      if ( !action.equals("deploy") && !action.equals("undeploy") &&
           !action.equals("list") && !action.equals("quit") )
        throw new AxisFault( "Admin.error",
                             "Root element must be 'deploy', 'undeploy', " +
                             "'list', or 'quit'",
                             null, null );

        if (action.equals("quit")) {
            System.err.println("Admin service requested to quit, quitting.");
            if (msgContext != null) {
                // put a flag into message context so listener will exit after
                // sending response
                msgContext.setProperty(msgContext.QUIT_REQUESTED, "true");
            }
              doc = XMLUtils.newDocument();
              doc.appendChild( root = doc.createElement( "Admin" ) );
              root.appendChild( doc.createTextNode( "Quitting" ) );
            return doc;
        }
        
      if ( action.equals("list") ) {
        doc = XMLUtils.newDocument();
        
        Element tmpEl = doc.createElement("engineConfig");
        doc.appendChild(tmpEl);
        
        Element el = list(doc, engine, false);
        tmpEl.appendChild(el);
        
        el = list(doc, engine, true);
        tmpEl.appendChild(el);
        
        return( doc );
      }
  
      NodeList list = root.getChildNodes();
      for ( int loop = 0 ; loop < list.getLength() ; loop++ ) {
        Node     node    = list.item(loop);

        if ( node.getNodeType() != Node.ELEMENT_NODE ) continue ;

        Element  elem    = (Element) node ;
        String   type    = elem.getTagName();
        String   name    = elem.getAttribute("name");
  
        if ( action.equals( "undeploy" ) ) {
          if ( type.equals("service") ) {
            Debug.Print( 2, "Undeploying " + type + ": " + name );
            engine.undeployService( name );
          }
          else if ( type.equals("handler") || type.equals("chain") ) {
            Debug.Print( 2, "Undeploying " + type + ": " + name );
            engine.undeployHandler( name );
          }
          else
            throw new AxisFault( "Admin.error",
                                 "Unknown type; " + type,
                                 null, null );
          continue ;
        }
  
        if ( type.equals( "handler" ) ) {
          registerHandler(elem, engine);
        }
        else if ( type.equals( "chain" ) ) {
          registerChain(elem, engine);
        }
        else if ( type.equals( "service" ) ) {
          registerService(elem, engine);
        }

        // A streamlined means of deploying both a serializer and a deserializer
        // for a bean at the same time.
        else if ( type.equals( "bean" ) ) {
          Debug.Print( 2, "Deploying bean: " + name );
          TypeMappingRegistry engineTypeMap = engine.getTypeMappingRegistry();
          registerBeanMapping(elem, engineTypeMap);
        } else
          throw new AxisFault( "Admin.error",
                               "Unknown type to " + action + ": " + type,
                               null, null );
      }
      doc = XMLUtils.newDocument();
      doc.appendChild( root = doc.createElement( "Admin" ) );
      root.appendChild( doc.createTextNode( "Done processing" ) );
    }
    catch( Exception e ) {
      e.printStackTrace();
      if ( !(e instanceof AxisFault) ) e = new AxisFault( e );
      throw (AxisFault) e ;
    }
    return( doc );
  }
  
  /**
   * Return an XML Element containing the engine's handler or service
   * configuration.
   * 
   * @param doc the XML Document within which to work.
   * @param engine the AxisEngine to query
   * @param doServices true if we should return service configuration,
   *                   otherwise we return handler configuration.
   * @return Element our config element, suitable for pumping back through
   *                 Admin processing later, to redeploy.
   */
  public static Element list(Document doc, AxisEngine engine, boolean doServices)
    throws AxisFault
  {
    String elementName = doServices ? "services" : "handlers";
    Element root = doc.createElement( elementName );

    Element    elem = null ;
    Hashtable  opts = null ;
    String[]   names ;
    Handler    h ;
    int        i ;
    
    HandlerRegistry registry;
    if (doServices) {
      registry = engine.getServiceRegistry();
    } else {
      registry = engine.getHandlerRegistry();
    }

    names = registry.list();

    for( i = 0 ; names != null && i < names.length ; i++ ) {
      h = registry.find(names[i]);
      if (h == null)
        throw new AxisFault("Server", "Couldn't find registered handler '" + names[i] + "'", null, null);
      elem = h.getDeploymentData(doc);

      if ( elem == null ) continue ;

      elem.setAttribute( "name", names[i] );
      root.appendChild( doc.importNode(elem,true) );
    }
    
    return root;
  }
  
  /**
   * Deploy a chain described in XML into an AxisEngine.
   * 
   * @param elem the <chain> element
   * @param engine the AxisEngine in which to deploy
   */
  public static void registerChain(Element elem, AxisEngine engine)
    throws AxisFault
  {
    Handler tmpH = null;
    String hName;
    HandlerRegistry hr = engine.getHandlerRegistry();
    
    String   name    = elem.getAttribute( "name" );
    String   flow    = elem.getAttribute( "flow" );
    String   input   = elem.getAttribute( "input" );
    String   pivot   = elem.getAttribute( "pivot" );
    String   output  = elem.getAttribute( "output" );

    if ( flow   != null && flow.equals("") )   flow = null ;
    if ( input  != null && input.equals("") )  input = null ;
    if ( output != null && output.equals("") ) output = null ;
    if ( pivot  != null && pivot.equals("") )  pivot = null ;
    if ( name != null && name.equals("") ) name = null ;

    if ( flow != null && flow.length() > 0 ) {
      Debug.Print( 2, "Deploying chain: " + name );
      Chain    c       = (Chain) hr.find( name );

      if ( c == null ) c = new SimpleChain();
      else             c.clear();

      StringTokenizer st = new StringTokenizer( flow, " \t\n\r\f," );
      while ( st.hasMoreElements() ) {
        hName = st.nextToken();
        tmpH = hr.find( hName );
        if ( tmpH == null )
          throw new AxisFault( "Admin.error",
            "Unknown handler: " + hName,
            null, null );
        c.addHandler( tmpH );
      }
      getOptions( elem, c );
      engine.deployHandler( name, c );
    }
    else {
      Debug.Print( 2, "Deploying chain: " + name );
      StringTokenizer      st = null ;
      SimpleTargetedChain  cc = null ;
      Chain                c  = null ;

      tmpH = hr.find( name );
      if (!(tmpH instanceof SimpleTargetedChain))
        throw new AxisFault("Deploying chain: '" + name + "' in registry, " +
                            "but not a SimpleTargetedChain!");
      cc = (SimpleTargetedChain)tmpH;

      if ( cc == null ) cc = new SimpleTargetedChain();
      else              cc.clear();
      
      st = new StringTokenizer( input, " \t\n\r\f," );
      while ( st.hasMoreElements() ) {
        if ( c == null )
          cc.setRequestChain( c = new SimpleChain() );
        hName = st.nextToken();
        tmpH = hr.find( hName );
        if ( tmpH == null )
          throw new AxisFault( "Admin.error",
            "Deploying chain with unknown handler: " + hName,
            null, null );
        c.addHandler( tmpH );
      }
      
      cc.setPivotHandler( hr.find( pivot ) );
      
      st = new StringTokenizer( output, " \t\n\r\f," );
      c  = null ;
      while ( st.hasMoreElements() ) {
        if ( c == null )
          cc.setResponseChain( c = new SimpleChain() );
        hName = st.nextToken();
        tmpH = hr.find( hName );
        if ( tmpH == null )
          throw new AxisFault( "Admin.error",
            "Deploying chain with unknown handler: " + hName,
            null, null );
        c.addHandler( tmpH );
      }
      getOptions( elem, cc );
      
      engine.deployHandler( name, cc );
    }
  }
  
  /**
   * Deploy a service described in XML into an AxisEngine.
   * 
   * @param elem the <service> element
   * @param engine the AxisEngine in which to deploy
   */
  public static void registerService(Element elem, AxisEngine engine)
    throws AxisFault
  {
    HandlerRegistry hr = engine.getHandlerRegistry();
    HandlerRegistry sr = engine.getServiceRegistry();
    
    String   name    = elem.getAttribute( "name" );
    String   input   = elem.getAttribute( "input" );
    String   pivot   = elem.getAttribute( "pivot" );
    String   output  = elem.getAttribute( "output" );

    if ( input  != null && input.equals("") )  input = null ;
    if ( output != null && output.equals("") ) output = null ;
    if ( pivot  != null && pivot.equals("") )  pivot = null ;
    if ( name != null && name.equals("") ) name = null ;

    Debug.Print( 2, "Deploying service: " + name );
    String            hName = null ;
    Handler            tmpH = null ;
    StringTokenizer      st = null ;
    SOAPService     service = null ;
    Chain                c  = null ;

    if ( pivot == null && input == null && output == null )
      throw new AxisFault( "Admin.error",
        "Services must use targeted chains",
        null, null );

    service = (SOAPService) sr.find( name );

    if ( service == null ) service = new SOAPService();
    else              service.clear();
    
    // connect the deployed service's typemapping registry to the
    // engine's typemapping registry
    TypeMappingRegistry engineTypeMap = engine.getTypeMappingRegistry();
    service.getTypeMappingRegistry().setParent(engineTypeMap);

    if ( input != null && !"".equals(input) ) {
      st = new StringTokenizer( input, " \t\n\r\f," );
      c  = null ;
      while ( st.hasMoreElements() ) {
        if ( c == null )
          service.setRequestChain( c = new SimpleChain() );
        hName = st.nextToken();
        tmpH = hr.find( hName );
        if ( tmpH == null )
          throw new AxisFault( "Admin.error",
            "Unknown handler: " + hName,
            null, null );
        c.addHandler( tmpH );
      }
    }
    
    if ( pivot != null && !"".equals(pivot) ) {
      tmpH = hr.find(pivot);
      if (tmpH == null)
        throw new AxisFault("Deploying service " + name +
               ": couldn't find pivot Handler '" + pivot + "'");
      
      service.setPivotHandler( tmpH );
      // Save pivot name so we can list it later.
      service.addOption("pivot", pivot);
    }
    
    if ( output != null && !"".equals(output) ) {
      st = new StringTokenizer( output, " \t\n\r\f," );
      c  = null ;
      while ( st.hasMoreElements() ) {
        if ( c == null )
          service.setResponseChain( c = new SimpleChain() );
        hName = st.nextToken();
        tmpH = hr.find( hName );
        if ( tmpH == null )
          throw new AxisFault( "Admin.error",
            "Unknown handler: " + hName,
            null, null );
        c.addHandler( tmpH );
      }
    }
    
    getOptions( elem, service );
    
    try {
      registerTypeMappings(elem, service);
    } catch (Exception e) {
      throw new AxisFault(e);
    }
    
    engine.deployService( name, service );
  }
  
  /**
   * Deploy a handler described in XML into an AxisEngine.
   * 
   * @param elem the <handler> element
   * @param engine the AxisEngine in which to deploy
   */
  public static void registerHandler(Element elem, AxisEngine engine)
    throws AxisFault
  {
    HandlerRegistry hr = engine.getHandlerRegistry();
    
    try {
      AxisClassLoader   cl     = AxisClassLoader.getClassLoader();
      String   name    = elem.getAttribute( "name" );
      Handler h = null;

      if ( name != null && name.equals("") ) name = null ;

      String   cls   = elem.getAttribute( "class" );
      if ( cls != null && cls.equals("") ) cls = null ;
      Debug.Print( 2, "Deploying handler: " + name );
      
      h = hr.find( name );
      if ( h == null ) h = (Handler) cl.loadClass(cls).newInstance();
      getOptions( elem, h );
      engine.deployHandler( name, h );
    } catch (ClassNotFoundException cnfe) {
      throw new AxisFault(cnfe);
    } catch (InstantiationException ie) {
      throw new AxisFault(ie);
    } catch (IllegalAccessException iae) {
      throw new AxisFault(iae);
    }
  }

  /**
   * Deploy a type mapping described in XML.
   * 
   * @param root the type mapping element.
   * @param map the TypeMappingRegistry which gets this mapping.
   */
  private static void registerBeanMapping(Element root, TypeMappingRegistry map)
    throws Exception
  {
    NodeList  list = root.getChildNodes();
    for ( int i = 0 ; list != null && i < list.getLength() ; i++ ) {
      Node    node  = list.item(i);
      if ( node.getNodeType() != Node.ELEMENT_NODE ) continue ;
      Element elem  = (Element) node ;

      // Retrieve classname attribute

      String classname = elem.getAttribute("classname");
      if ((classname == null) || classname.equals(""))
        throw new AxisFault("Server.Admin.error",
                            "No classname attribute in bean mapping",
                            null, null);
    
      // Resolve class name

      Class cls;
      try {
        cls = Class.forName(classname);
      } catch (Exception e) {
        throw new AxisFault( "Admin.error", e.toString(), null, null);
      }

      // Resolve qname based on prefix and localpart

      String namespaceURI = elem.getNamespaceURI();
      String localName    = elem.getLocalName();
      QName qn = new QName(namespaceURI, localName);

      // register both serializers and deserializers for this bean

      map.addSerializer(cls, qn, new BeanSerializer(cls));
      map.addDeserializerFactory(qn, cls, BeanSerializer.getFactory(cls));
    }
  }
  
  public static void main(String args[]) throws Exception {
    int  i = 0 ;

    if ( args.length < 2 || !(args[0].equals("client") ||
                             args[0].equals("server")) ) {
      System.err.println( "Usage: Admin client|server <xml-file>\n" );

      System.err.println( "Where <xml-file> looks like:" );
      System.err.println( "<deploy>" );
      /*
      System.err.println( "  <transport name=a input=\"a,b,c\" sender=\"s\"");
      System.err.println( "                    output=\"d,e\"/>" );
      */
      System.err.println( "  <handler name=a class=className/>" );
      System.err.println( "  <chain name=a flow=\"a,b,c\" />" );
      System.err.println( "  <chain name=a input=\"a,b,c\" pivot=\"d\"" );
      System.err.println( "                  output=\"e,f,g\" />" );
      System.err.println( "  <service name=a handler=b />" );
      System.err.println( "</deploy>" );
      System.err.println( "<undeploy>" );
      System.err.println( "  <handler name=a/>" );
      System.err.println( "  <chain name=a/>" );
      System.err.println( "  <service name=a/>" );
      System.err.println( "</undeploy>\n" );
      System.err.println( "<list/>\n" );


      // throw an Exception which will go uncaught!  this way, a test suite
      // can invoke main() and detect the exception
      throw new IllegalArgumentException();
      // System.exit( 1 );
    }

    Admin admin = new Admin();

    AxisEngine engine;
    if ( args[0].equals("client") )
      engine = new AxisClient();
    else
      engine = new AxisServer();
    engine.init();
    MessageContext msgContext = new MessageContext(engine);

    try {
      for ( i = 1 ; i < args.length ; i++ ) {
        System.out.println( "Processing '" + args[i] + "'" );
        Document doc = XMLUtils.newDocument( new FileInputStream( args[i] ) );
        admin.process(msgContext, doc);
      }
    }
    catch( AxisFault e ) {
      e.dump();
      //System.exit(1);
        throw e;
    }
    catch( Exception e ) {
      System.err.println( "Error processing '" + args[i] + "'" );
      e.printStackTrace( System.err );
      //System.exit( 1 );
        throw e;
    }
  }
}
