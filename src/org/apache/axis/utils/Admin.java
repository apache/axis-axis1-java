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

package org.apache.axis.utils ;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Chain;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.SimpleChain;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.encoding.BeanSerializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.registries.HandlerRegistry;
import org.apache.axis.registries.SupplierRegistry;
import org.apache.axis.server.AxisServer;
import org.apache.axis.suppliers.SimpleChainSupplier;
import org.apache.axis.suppliers.TargetedChainSupplier;
import org.apache.axis.suppliers.TransportSupplier;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Handy static utility functions for turning XML into
 * Axis deployment operations.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class Admin {

    static Category category =
            Category.getInstance(Admin.class.getName());

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

    private static void getOptions(Element root, Hashtable table) {
        NodeList  list = root.getElementsByTagName("option");
        for ( int i = 0 ; list != null && i < list.getLength() ; i++ ) {
            Element elem  = (Element) list.item(i);
            String  name  = elem.getAttribute( "name" );
            String  value = elem.getAttribute( "value" );

            if ( name != null && value != null )
                table.put( name, value );
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
        TypeMappingRegistry reg = service.getTypeMappingRegistry();
        NodeList list = root.getElementsByTagName("beanMappings");
        for (int i = 0; list != null && i < list.getLength(); i++) {
            Element el = (Element)list.item(i);
            registerTypes(el, reg, true);
        }

        list = root.getElementsByTagName("typeMappings");
        for (int i = 0; list != null && i < list.getLength(); i++) {
            Element el = (Element)list.item(i);
            registerTypes(el, reg, false);
        }
    }

    private static void registerTypes(Element root,
                                      TypeMappingRegistry map,
                                      boolean isBean)
        throws Exception
    {
        NodeList list = root.getChildNodes();
        for (int i = 0; (list != null) && (i < list.getLength()); i++) {
            if (!(list.item(i) instanceof Element)) continue;
            registerTypeMapping((Element)list.item(i), map, isBean);
        }
    }

    /**
     * Process a given XML document - needs cleanup.
     */
    public Document AdminService(MessageContext msgContext, Document xml)
        throws AxisFault
    {
        category.debug("Enter: Admin:AdminService" );
        Document doc = process( msgContext, xml.getDocumentElement() );
        category.debug("Exit: Admin:AdminService" );
        return( doc );
    }

    /** Process an engine configuration file by deploying appropriate stuff
     * into the specified AxisEngine, and then telling it to save itself
     * when we're done.
     *
     * @param doc an XML document containing an Axis engine configuration
     * @param engine the AxisEngine in which to deploy
     * @exception Exception (should be DeploymentException?)
     */
    public static void processEngineConfig(Document doc, AxisEngine engine)
        throws Exception
    {
        Element el = doc.getDocumentElement();
        if (!el.getTagName().equals("engineConfig"))
            throw new Exception("Wanted 'engineConfig' element, got '" +
                el.getTagName() + "'");

        NodeList nl = el.getElementsByTagName("handlers");
        deploy(nl, engine);

        nl = el.getElementsByTagName("services");
        deploy(nl, engine);

        nl = el.getElementsByTagName("transports");
        deploy(nl, engine);

        nl = el.getElementsByTagName("typeMappings");
        deploy(nl, engine);
        /*
        if (nl.getLength() > 0)
        registerTypes((Element)nl.item(0),
        engine.getTypeMappingRegistry(),
        false);
        */

        engine.saveConfiguration();
    }

    private static final int
        TYPE_UNKNOWN = 0,
        TYPE_HANDLER = 1,
        TYPE_CHAIN = 2,
        TYPE_SERVICE = 3,
        TYPE_TRANSPORT = 4,
        TYPE_TYPEMAPPING = 5;
    private static final Hashtable typeTable = new Hashtable();
    static {
        typeTable.put("handler", new Integer(TYPE_HANDLER));
        typeTable.put("chain", new Integer(TYPE_CHAIN));
        typeTable.put("service", new Integer(TYPE_SERVICE));
        typeTable.put("transport", new Integer(TYPE_TRANSPORT));
        typeTable.put("typeMapping", new Integer(TYPE_TYPEMAPPING));
    }
    private static int getType(String tagName) {
        Integer i;
        if ((i = (Integer)typeTable.get(tagName)) == null)
            return TYPE_UNKNOWN;
        return i.intValue();
    }

    /** Deploy a set of individual items.
     *
     * NOTE: as it stands this doesn't care about the relationship between
     * these items and the enclosing tag.  We shouldn't really allow <service>
     * deployment underneath the <transports> tag, for instance.  Since this
     * is going to mutate some more, this is the simple way to do it for now.
     *
     * @param nl a DOM NodeList of deployable items.
     * @param engine the AxisEngine into which we deploy.
     * @exception Exception (should be DeploymentException?)
     */
    static void deploy(NodeList nl, AxisEngine engine) throws Exception
    {
        int lenI = nl.getLength();
        for (int i = 0; i < lenI; i++) {
            Element el = (Element)nl.item(i);

            NodeList children = el.getChildNodes();
            int lenJ = children.getLength();
            for (int j = 0; j < lenJ; j++) {
                if (!(children.item(j) instanceof Element)) continue;

                Element item = (Element)children.item(j);

                int type;
                switch (type = getType(item.getTagName())) {
                case TYPE_HANDLER:
                    registerHandler(item, engine);
                    break;
                case TYPE_CHAIN:
                    registerChain(item, engine);
                    break;
                case TYPE_SERVICE:
                    registerService(item, engine);
                    break;
                case TYPE_TRANSPORT:
                    registerTransport(item, engine);
                    break;
                case TYPE_TYPEMAPPING:
                    registerTypeMapping(item, engine.getTypeMappingRegistry(), false);
                    break;
                case TYPE_UNKNOWN:
                    // ignore it
                    break;
                default:
                    throw new UnknownError("Shouldn't happen: " + type);
                }
            }
        }
    }

    /**
     * The meat of the Admin service.  Process an xML document rooted with
     * a "deploy", "undeploy", "list", or "quit" element.
     *
     * @param msgContext the MessageContext we're processing
     * @param root the root Element of the XML
     * @return an XML Document indicating the results.
     */
    public Document process(MessageContext msgContext, Element root)
        throws AxisFault
    {
        Document doc = null ;

        AxisEngine engine = msgContext.getAxisEngine();
        HandlerRegistry hr = engine.getHandlerRegistry();
        HandlerRegistry sr = engine.getServiceRegistry();

        try {
            String            action = root.getLocalName();
            AxisClassLoader   cl     = AxisClassLoader.getClassLoader();

            if ( !action.equals("clientdeploy") && !action.equals("deploy") &&
                 !action.equals("undeploy") &&
                 !action.equals("list") &&
                 !action.equals("quit") &&
                 !action.equals("passwd"))
                throw new AxisFault( "Admin.error",
                    "Root element must be 'clientdeploy', 'deploy', 'undeploy', " +
                    "'list', 'passwd', or 'quit'",
                    null, null );

            /** Might do something like this once security is a little more
             * integrated.
            if (!engine.hasSafePassword() &&
                !action.equals("passwd"))
                throw new AxisFault("Server.MustSetPassword",
              "You must change the admin password before administering Axis!",
                                     null, null);
             */

            /** For now, though - make sure we can only admin from our own
             * IP, unless the remoteAdmin option is set.
             */
            Handler serviceHandler = msgContext.getServiceHandler();
            if (serviceHandler != null) {
                String remoteAdmin = (String)serviceHandler.
                                            getOption("enableRemoteAdmin");
                if ((remoteAdmin == null) ||
                    !remoteAdmin.equals("true")) {
                    String remoteIP =
                            msgContext.getStrProp(Constants.MC_REMOTE_ADDR);
                    if (remoteIP != null) {
                        if (!remoteIP.equals("127.0.0.1")) {
                            InetAddress myAddr = InetAddress.getLocalHost();
                            InetAddress remoteAddr =
                                            InetAddress.getByName(remoteIP);

                            if (!myAddr.equals(remoteAddr))
                                throw new AxisFault("Server.Unauthorized",
                                    "Remote admin access is not allowed! ",
                                    null, null);
                        }
                    }
                }
            }

            if (action.equals("passwd")) {
                String newPassword = root.getFirstChild().getNodeValue();
                engine.setAdminPassword(newPassword);
                doc = XMLUtils.newDocument();
                doc.appendChild( root = doc.createElement( "Admin" ) );
                root.appendChild( doc.createTextNode( "Done processing" ) );
                return doc;
            }

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
                return listConfig(engine);
            }

            if (action.equals("clientdeploy")) {
                // set engine to client engine
                engine = engine.getClientEngine();
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
                        category.info( "Undeploying " + type + ": " + name );
                        engine.undeployService( name );
                    }
                    else if ( type.equals("handler") || type.equals("chain") ) {
                        category.info( "Undeploying " + type + ": " + name );
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
                else if (type.equals("transport")) {
                    registerTransport(elem, engine);
                }

                // A streamlined means of deploying both a serializer and a deserializer
                // for a bean at the same time.
                else if ( type.equals( "beanMappings" ) ) {
                    TypeMappingRegistry engineTypeMap = engine.getTypeMappingRegistry();
                    registerTypes(elem, engineTypeMap, true);
                }
                else if (type.equals("typeMappings")) {
                    TypeMappingRegistry engineTypeMap = engine.getTypeMappingRegistry();
                    registerTypes(elem, engineTypeMap, false);
                } else
                    throw new AxisFault( "Admin.error",
                        "Unknown type to " + action + ": " + type,
                        null, null );
            }
            engine.saveConfiguration();

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

    /** Get an XML document representing this engine's configuration.
     *
     * This document is suitable for saving and reloading into the
     * engine.
     *
     * @param engine the AxisEngine to work with
     * @return an XML document holding the engine config
     * @exception AxisFault
     */
    public static Document listConfig(AxisEngine engine)
        throws AxisFault
    {
        Document doc = XMLUtils.newDocument();

        Element tmpEl = doc.createElement("engineConfig");
        doc.appendChild(tmpEl);

        Element el = doc.createElement("handlers");
        list(el, engine.getHandlerRegistry());
        tmpEl.appendChild(el);

        el = doc.createElement("services");
        list(el, engine.getServiceRegistry());
        tmpEl.appendChild(el);

        el = doc.createElement("transports");
        list(el, engine.getTransportRegistry());
        tmpEl.appendChild(el);

        category.debug( "Outputting registry");
        el = doc.createElement("typeMappings");
        engine.getTypeMappingRegistry().dumpToElement(el);
        tmpEl.appendChild(el);

        return( doc );
    }

    /**
     * Return an XML Element containing the configuration info for one
     * of the engine's Handler registries.
     *
     * @param root the Element to work with (same as the one we return)
     * @param registry the registry to write into this Element
     * @return Element our config element, suitable for pumping back through
     *                 Admin processing later, to redeploy.
     */
    public static Element list(Element root, HandlerRegistry registry)
        throws AxisFault
    {
        Document doc = root.getOwnerDocument();

        Element    elem = null ;
        Hashtable  opts = null ;
        String[]   names ;
        Handler    h ;
        int        i ;

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
        SupplierRegistry hr = (SupplierRegistry)engine.getHandlerRegistry();

        String   name    = elem.getAttribute( "name" );
        String   flow    = elem.getAttribute( "flow" );
        String   request   = elem.getAttribute( "request" );
        String   pivot   = elem.getAttribute( "pivot" );
        String   response  = elem.getAttribute( "response" );
        Hashtable options = new Hashtable();

        if ("".equals(flow)) flow = null;
        if ("".equals(request)) request = null;
        if ("".equals(response)) response = null;
        if ("".equals(pivot)) pivot = null;
        if ("".equals(name)) name = null;

        if (flow != null) {
            category.info( "Deploying chain: " + name );
            Vector names = new Vector();

            StringTokenizer st = new StringTokenizer( flow, " \t\n\r\f," );
            while ( st.hasMoreElements() ) {
                names.addElement(st.nextToken());
            }
            getOptions( elem, options );

            SimpleChainSupplier supp = new SimpleChainSupplier(name,
                                                               names,
                                                               options,
                                                               hr);

            hr.add(name, supp);
        }
        else {
            category.info( "Deploying chain: " + name );

            if ((request == null) &&
                (response == null) &&
                (pivot == null))
                throw new AxisFault("No request/response/pivot for chain '" + name + "'!");

            StringTokenizer      st = null ;
            Vector reqNames = new Vector();
            Vector respNames = new Vector();

            if (request != null) {
                st = new StringTokenizer( request, " \t\n\r\f," );
                while ( st.hasMoreElements() ) {
                    reqNames.addElement(st.nextToken());
                }
            }

            if (response != null) {
                st = new StringTokenizer( response, " \t\n\r\f," );
                while ( st.hasMoreElements() ) {
                    respNames.addElement(st.nextToken());
                }
            }

            getOptions( elem, options );

            TargetedChainSupplier supp = new TargetedChainSupplier(name,
                                                                   reqNames,
                                                                   respNames,
                                                                   pivot,
                                                                   options,
                                                                   hr);
            hr.add(name,supp);
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
        String   request   = elem.getAttribute( "request" );
        String   pivot   = elem.getAttribute( "pivot" );
        String   response  = elem.getAttribute( "response" );

        if ( request  != null && request.equals("") )  request = null ;
        if ( response != null && response.equals("") ) response = null ;
        if ( pivot  != null && pivot.equals("") )  pivot = null ;
        if ( name != null && name.equals("") ) name = null ;

        category.info( "Deploying service: " + name );
        String            hName = null ;
        Handler            tmpH = null ;
        StringTokenizer      st = null ;
        SOAPService     service = null ;
        Chain                c  = null ;

        if ( pivot == null && request == null && response == null )
            throw new AxisFault( "Admin.error",
                "Services must use targeted chains",
                null, null );

        service = (SOAPService) sr.find( name );

        if ( service == null ) service = new SOAPService();
        else              service.clear();

        if ( request != null && !"".equals(request) ) {
            st = new StringTokenizer( request, " \t\n\r\f," );
            c  = null ;
            while ( st.hasMoreElements() ) {
                if ( c == null )
                    service.setRequestHandler( c = new SimpleChain() );
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

            if (pivot.equals("MsgDispatcher")) {
                ServiceDescription sd = new ServiceDescription("msgService", false);
                service.setServiceDescription(sd);
            }
        }

        if ( response != null && !"".equals(response) ) {
            st = new StringTokenizer( response, " \t\n\r\f," );
            c  = null ;
            while ( st.hasMoreElements() ) {
                if ( c == null )
                    service.setResponseHandler( c = new SimpleChain() );
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
            category.info( "Deploying handler: " + name );

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
     * Deploy a transport described in XML into an AxisEngine.
     *
     * @param elem the <transport> element
     * @param engine the AxisEngine in which to deploy
     */
    public static void registerTransport(Element elem, AxisEngine engine)
        throws AxisFault
    {
        String   name    = elem.getAttribute( "name" );
        String   request   = elem.getAttribute( "request" );
        String   sender   = elem.getAttribute( "pivot" );
        String   response  = elem.getAttribute( "response" );
        Hashtable options = new Hashtable();

        if ( request  != null && request.equals("") )  request = null ;
        if ( response != null && response.equals("") ) response = null ;
        if ( sender  != null && sender.equals("") )  sender = null ;
        if ( name != null && name.equals("") ) name = null ;

        category.info( "Deploying Transport: " + name );
        StringTokenizer      st = null ;
        Vector reqNames = new Vector();
        Vector respNames = new Vector();

        if (request != null) {
            st = new StringTokenizer( request, " \t\n\r\f," );
            while ( st.hasMoreElements() ) {
                reqNames.addElement(st.nextToken());
            }
        }

        if (response != null) {
            st = new StringTokenizer( response, " \t\n\r\f," );
            while ( st.hasMoreElements() ) {
                respNames.addElement(st.nextToken());
            }
        }

        getOptions( elem, options );

        HandlerRegistry hr = engine.getHandlerRegistry();
        TargetedChainSupplier supp = new TransportSupplier(name,
                                                           reqNames,
                                                           respNames,
                                                           sender,
                                                           options,
                                                           hr);
        engine.deployTransport(name, supp);
    }

    /**
     * Deploy a type mapping described in XML.
     *
     * @param root the type mapping element.
     * @param map the TypeMappingRegistry which gets this mapping.
     */
    private static void registerTypeMapping(Element elem,
                                            TypeMappingRegistry map,
                                            boolean isBean)
        throws Exception
    {
        Serializer ser;
        DeserializerFactory dserFactory;

        // Retrieve classname attribute
        String classname = elem.getAttribute("classname");
        if ((classname == null) || classname.equals(""))
            throw new AxisFault("Server.Admin.error",
                "No classname attribute in type mapping",
                null, null);

        // Resolve class name

        Class cls;
        QName qn;

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            cls = cl.loadClass(classname);
        } catch (Exception e) {
            throw new AxisFault( "Admin.error", e.toString(), null, null);
        }

        if (isBean) {
            // Resolve qname based on prefix and localpart

            String namespaceURI = elem.getNamespaceURI();
            String localName    = elem.getLocalName();
            qn = new QName(namespaceURI, localName);

            category.debug( "Registering mapping for " + qn + " -> " + classname);

            // register both serializers and deserializers for this bean
            ser = new BeanSerializer(cls);
            dserFactory = BeanSerializer.getFactory();
        } else {
            String typeName = elem.getAttribute("type");
            int idx = typeName.indexOf(":");
            String prefix = typeName.substring(0, idx);
            String localPart = typeName.substring(idx + 1);

            qn = new QName(XMLUtils.getNamespace(prefix, elem), localPart);

            classname = elem.getAttribute("serializer");
            category.debug( "Serializer class is " + classname);
            try {
                ser = (Serializer)cl.loadClass(classname).newInstance();
            } catch (Exception e) {
                throw new AxisFault( "Admin.error",
                    "Couldn't load serializer class " + e.toString(),
                    null, null);
            }
            classname = elem.getAttribute("deserializerFactory");
            category.debug( "DeserializerFactory class is " + classname);
            try {
                dserFactory = (DeserializerFactory)cl.loadClass(classname).
                                                                            newInstance();
            } catch (Exception e) {
                throw new AxisFault( "Admin.error",
                    "Couldn't load deserializerFactory " +
                    e.toString(),
                    null, null);
            }

        }

        map.addSerializer(cls, qn, ser);
        map.addDeserializerFactory(qn, cls, dserFactory);
    }

    public static void main(String args[]) throws Exception {
        int  i = 0 ;

        if ( args.length < 2 || !(args[0].equals("client") ||
                                  args[0].equals("server")) ) {
            System.err.println( "Usage: Admin client|server <xml-file>\n" );

            System.err.println( "Where <xml-file> looks like:" );
            System.err.println( "<deploy>" );
            /*
            System.err.println( "  <transport name=a request=\"a,b,c\" sender=\"s\"");
            System.err.println( "                    response=\"d,e\"/>" );
            */
            System.err.println( "  <handler name=a class=className/>" );
            System.err.println( "  <chain name=a flow=\"a,b,c\" />" );
            System.err.println( "  <chain name=a request=\"a,b,c\" pivot=\"d\"" );
            System.err.println( "                  response=\"e,f,g\" />" );
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
            engine = new AxisClient(new FileProvider("client-config.xml"));
        else
            engine = new AxisServer(new FileProvider("server-config.xml"));
        engine.init();
        MessageContext msgContext = new MessageContext(engine);

        try {
            for ( i = 1 ; i < args.length ; i++ ) {
                System.out.println( "Processing '" + args[i] + "'" );
                Document doc = XMLUtils.newDocument( new FileInputStream( args[i] ) );
                admin.process(msgContext, doc.getDocumentElement());
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
