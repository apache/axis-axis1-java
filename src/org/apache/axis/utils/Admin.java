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
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.providers.java.MsgProvider;
import org.apache.axis.deployment.wsdd.*;
import org.apache.axis.deployment.DeploymentException;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.encoding.*;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.server.AxisServer;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.rpc.namespace.QName;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
                handler.setOption( name, value );
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
    private static void registerTypeMappings(Element root, WSDDService service)
        throws Exception
    {
        NodeList list = root.getElementsByTagName("beanMappings");
        for (int i = 0; list != null && i < list.getLength(); i++) {
            Element el = (Element)list.item(i);
            registerTypes(el, service, true, null);
        }

        list = root.getElementsByTagName("typeMappings");
        for (int i = 0; list != null && i < list.getLength(); i++) {
            Element el = (Element)list.item(i);
            registerTypes(el, service, false, null);
        }
    }

    private static void registerTypes(Element root,
                                      WSDDTypeMappingContainer container,
                                      boolean isBean,
                                      DeploymentRegistry registry)
        throws Exception
    {
        NodeList list = root.getChildNodes();
        for (int i = 0; (list != null) && (i < list.getLength()); i++) {
            if (!(list.item(i) instanceof Element)) continue;
            registerTypeMapping((Element)list.item(i), container, isBean, registry);
        }
    }

    /**
     * Process a given XML document - needs cleanup.
     */
    public Element[] AdminService(MessageContext msgContext, Vector xml)
        throws AxisFault
    {
        category.debug(JavaUtils.getMessage("enter00", "Admin:AdminService") );
        Document doc = process( msgContext, (Element) xml.get(0) );
        Element[] result = new Element[1];
        result[0] = doc.getDocumentElement();
        category.debug(JavaUtils.getMessage("exit00", "Admin:AdminService") );
        return( result );
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
        String namespace = el.getNamespaceURI();
        
        // If this is WSDD, process it correctly.
        if (namespace != null && namespace.equals(WSDDConstants.WSDD_NS)) {
            processWSDD(engine, el);
            return;
        }
        
        if (!el.getTagName().equals("engineConfig"))
            throw new Exception(
                    JavaUtils.getMessage("noEngineConfig00", el.getTagName()));

        NodeList nl = el.getElementsByTagName("handlers");
        deploy(nl, engine);

        nl = el.getElementsByTagName("services");
        deploy(nl, engine);

        nl = el.getElementsByTagName("transports");
        deploy(nl, engine);

        nl = el.getElementsByTagName("typeMappings");
        deploy(nl, engine);
        
        //engine.saveConfiguration();
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
        WSDDDocument wd = (WSDDDocument)engine.getDeploymentRegistry().getConfigDocument();
        WSDDDeployment dep = wd.getDeployment();
        
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
                    registerTypeMapping(item, dep, false, null);
                    break;
                case TYPE_UNKNOWN:
                    // ignore it
                    break;
                default:
                    throw new UnknownError(JavaUtils.getMessage(
                            "never00",
                            "org.apache.axis.utils.Admin",
                            "type = " + type));
                }
            }
        }
    }
    
    protected static Document processWSDD(AxisEngine engine, Element root)
        throws AxisFault
    {
        Document doc = null ;

        WSDDDocument wsddDoc = new WSDDDocument(root);
        engine.deployWSDD(wsddDoc);
        
        engine.saveConfiguration();
        
        doc = XMLUtils.newDocument();
        doc.appendChild( root = doc.createElementNS("", "Admin" ) );
        root.appendChild( doc.createTextNode( JavaUtils.getMessage("done00") ) );
        
        return doc;
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
        // Check security FIRST.
        
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
                        try {
                            InetAddress myAddr = InetAddress.getLocalHost();
                            InetAddress remoteAddr =
                                    InetAddress.getByName(remoteIP);
                            
                            if (!myAddr.equals(remoteAddr))
                                throw new AxisFault("Server.Unauthorized",
                                   JavaUtils.getMessage("noAdminAccess00"),
                                   null, null);
                        } catch (UnknownHostException e) {
                            throw new AxisFault("Server.UnknownHost",
                                JavaUtils.getMessage("unknownHost00"),
                                null, null);
                        }
                    }
                }
            }
        }
        
        String rootNS = root.getNamespaceURI();
        String rootName = root.getLocalName();
        AxisEngine engine = msgContext.getAxisEngine();
        
        // If this is WSDD, process it correctly.
        if (rootNS != null && rootNS.equals(WSDDConstants.WSDD_NS)) {
            return processWSDD(engine, root);
        }

        // Not WSDD, use old code.
        // 
        // NOTE : THIS CODE IS DEPRECATED AND WILL DISAPPEAR BY
        //        BETA 1.  YOU SHOULD SWITCH TO WSDD.
        //
        Document doc = null ;

        try {
            String            action = rootName;
            AxisClassLoader   cl     = AxisClassLoader.getClassLoader();

            if ( !action.equals("clientdeploy") && !action.equals("deploy") &&
                 !action.equals("undeploy") &&
                 !action.equals("list") &&
                 !action.equals("quit") &&
                 !action.equals("passwd"))
                throw new AxisFault( "Admin.error",
                    JavaUtils.getMessage("badRootElem00"),
                    null, null );


            if (action.equals("passwd")) {
                String newPassword = root.getFirstChild().getNodeValue();
                engine.setAdminPassword(newPassword);
                doc = XMLUtils.newDocument();
                doc.appendChild( root = doc.createElementNS("", "Admin" ) );
                root.appendChild( doc.createTextNode( JavaUtils.getMessage("done00") ) );
                return doc;
            }

            if (action.equals("quit")) {
                System.err.println(JavaUtils.getMessage("quitRequest00"));
                if (msgContext != null) {
                    // put a flag into message context so listener will exit after
                    // sending response
                    msgContext.setProperty(msgContext.QUIT_REQUESTED, "true");
                }
                doc = XMLUtils.newDocument();
                doc.appendChild( root = doc.createElementNS("", "Admin" ) );
                root.appendChild( doc.createTextNode( JavaUtils.getMessage("quit00", "") ) );
                return doc;
            }

            if ( action.equals("list") ) {
                return listConfig(engine);
            }

            if (action.equals("clientdeploy")) {
                // set engine to client engine
                engine = engine.getClientEngine();
            }
            
            WSDDDocument wd = (WSDDDocument)engine.getDeploymentRegistry().getConfigDocument();
            WSDDDeployment dep = wd.getDeployment();

            NodeList list = root.getChildNodes();
            for ( int loop = 0 ; loop < list.getLength() ; loop++ ) {
                Node     node    = list.item(loop);

                if ( node.getNodeType() != Node.ELEMENT_NODE ) continue ;

                Element  elem    = (Element) node ;
                String   type    = elem.getTagName();
                String   name    = elem.getAttribute("name");

                if ( action.equals( "undeploy" ) ) {
                    if ( type.equals("service") ) {
                        category.info( JavaUtils.getMessage("undeploy00", type + ": " + name) );
                        engine.undeployService( name );
                    }
                    else if ( type.equals("handler") || type.equals("chain") ) {
                        category.info( JavaUtils.getMessage("undeploy00", type + ": " + name) );
                        engine.undeployHandler( name );
                    }
                    else
                        throw new AxisFault( "Admin.error",
                            JavaUtils.getMessage("unknownType00", type),
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
                    registerTypes(elem, dep, true, engine.getDeploymentRegistry());
                }
                else if (type.equals("typeMappings")) {
                    registerTypes(elem, dep, false, engine.getDeploymentRegistry());
                } else
                    throw new AxisFault( "Admin.error",
                        JavaUtils.getMessage("unknownType01", action + ": " + type),
                        null, null );
            }
            
            engine.saveConfiguration();

            doc = XMLUtils.newDocument();
            doc.appendChild( root = doc.createElementNS("", "Admin" ) );
            root.appendChild( doc.createTextNode( JavaUtils.getMessage("done00") ) );
        }
        catch( Exception e ) {
            e.printStackTrace();
            throw AxisFault.makeFault(e);
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
        StringWriter writer = new StringWriter();
        SerializationContext context = new SerializationContext(writer, null);
        context.setPretty(true);
        try {
            engine.getDeploymentRegistry().writeToContext(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            writer.close();
            return XMLUtils.newDocument(new InputSource(new StringReader(writer.getBuffer().toString())));
        } catch (IOException e) {
            return null;
        }
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
            category.info( JavaUtils.getMessage("deployChain00", name) );
            Vector names = new Vector();

            getOptions( elem, options );
            
            WSDDDocument wsddDoc = (WSDDDocument)engine.
                    getDeploymentRegistry().getConfigDocument();
            
            WSDDChain chain = new WSDDChain();
            chain.setName(name);
            chain.setOptionsHashtable(options);

            StringTokenizer st = new StringTokenizer( flow, " \t\n\r\f," );
            while ( st.hasMoreElements() ) {
                String handlerName = st.nextToken();
                WSDDHandler handler = new WSDDHandler();
                //handler.setName(handlerName);
                handler.setType(new QName("", handlerName));
                chain.addHandler(handler);
            }

            engine.getDeploymentRegistry().deployHandler(chain);
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
        String   name    = elem.getAttribute( "name" );
        String   request   = elem.getAttribute( "request" );
        String   pivot   = elem.getAttribute( "pivot" );
        String   response  = elem.getAttribute( "response" );

        if ( request  != null && request.equals("") )  request = null ;
        if ( response != null && response.equals("") ) response = null ;
        if ( pivot  != null && pivot.equals("") )  pivot = null ;
        if ( name != null && name.equals("") ) name = null ;

        category.info( JavaUtils.getMessage("deployService01", name) );
        String            hName = null ;
        Handler            tmpH = null ;
        StringTokenizer      st = null ;

        if ( pivot == null && request == null && response == null )
            throw new AxisFault( "Admin.error",
                JavaUtils.getMessage("noChains00"),
                null, null );

        WSDDService serv = new WSDDService();
        
        serv.setName(name);
        
        if ( request != null && !"".equals(request) ) {
            st = new StringTokenizer( request, " \t\n\r\f," );
            WSDDRequestFlow req = new WSDDRequestFlow();
            serv.setRequestFlow(req);
            while ( st.hasMoreElements() ) {
                hName = st.nextToken();
                WSDDHandler h = new WSDDHandler();
                h.setType(new QName("",hName));
                req.addHandler(h);
            }
        }

        Hashtable opts = new Hashtable();
        getOptions( elem, opts );
        serv.setOptionsHashtable(opts);
        
        /**
         * Pivots only make sense on the server.
         */ 
        if (engine instanceof AxisServer) {
            Handler pivotHandler = engine.getHandler(pivot);
            if (pivotHandler == null)
                throw new AxisFault(JavaUtils.getMessage("noPivot00", pivot));
            Class pivotClass = pivotHandler.getClass();
            if (pivotClass == RPCProvider.class) {
                serv.setProviderQName(WSDDConstants.JAVARPC_PROVIDER);
            } else if (pivotClass == MsgProvider.class) {
                serv.setProviderQName(WSDDConstants.JAVAMSG_PROVIDER);
            } else {
                serv.setParameter("handlerClass", pivotClass.getName());
                serv.setProviderQName(WSDDConstants.HANDLER_PROVIDER);
            }
        }

        if ( response != null && !"".equals(response) ) {
            st = new StringTokenizer( response, " \t\n\r\f," );
            WSDDResponseFlow resp = new WSDDResponseFlow();
            serv.setResponseFlow(resp);
            while ( st.hasMoreElements() ) {
                hName = st.nextToken();
                WSDDHandler h = new WSDDHandler();
                h.setType(new QName("", hName));
                resp.addHandler(h);
            }
        }

        try {
            registerTypeMappings(elem, serv);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }

        engine.getDeploymentRegistry().deployService(serv);
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
        try {
            AxisClassLoader   cl     = AxisClassLoader.getClassLoader();
            String   name    = elem.getAttribute( "name" );
            Handler h = null;

            if ( name != null && name.equals("") ) name = null ;

            String   cls   = elem.getAttribute( "class" );
            if ( cls != null && cls.equals("") ) cls = null ;
            category.info( JavaUtils.getMessage("deployHandler00", name) );

            h = engine.getHandler( name );
            if ( h == null ) h = (Handler) cl.loadClass(cls).newInstance();
            getOptions( elem, h );
            engine.deployHandler( name, h );
        } catch (ClassNotFoundException e) {
              throw AxisFault.makeFault(e);
        } catch (InstantiationException e) {
              throw AxisFault.makeFault(e);
        } catch (IllegalAccessException e) {
              throw AxisFault.makeFault(e);
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

        category.info( JavaUtils.getMessage("deployTransport00", name) );
        StringTokenizer      st = null ;
        Vector reqNames = new Vector();
        Vector respNames = new Vector();

        WSDDDocument wd = (WSDDDocument)engine.getDeploymentRegistry().getConfigDocument();
        WSDDTransport transport = new WSDDTransport();
        
        transport.setName(name);

        if (request != null) {
            WSDDRequestFlow req = new WSDDRequestFlow();
            transport.setRequestFlow(req);
            st = new StringTokenizer( request, " \t\n\r\f," );
            while ( st.hasMoreElements() ) {
                WSDDHandler h = new WSDDHandler();
                h.setType(new QName("", st.nextToken()));
                req.addHandler(h);
            }
        }

        if (response != null) {
            WSDDResponseFlow resp = new WSDDResponseFlow();
            transport.setResponseFlow(resp);
            st = new StringTokenizer( response, " \t\n\r\f," );
            while ( st.hasMoreElements() ) {
                WSDDHandler h = new WSDDHandler();
                h.setType(new QName("", st.nextToken()));
                resp.addHandler(h);
            }
        }

        getOptions( elem, options );
        transport.setOptionsHashtable(options);

        engine.getDeploymentRegistry().deployTransport(transport);
    }

    /**
     * Deploy a type mapping described in XML.
     *
     * @param root the type mapping element.
     * @param map the TypeMappingRegistry which gets this mapping.
     */
    private static void registerTypeMapping(Element elem,
                                            WSDDTypeMappingContainer container,
                                            boolean isBean,
                                            DeploymentRegistry registry)
        throws Exception
    {
        WSDDTypeMapping mapping = new WSDDTypeMapping();
        
        // Retrieve classname attribute
        String classname = elem.getAttribute("classname");
        
        if ((classname == null) || classname.equals(""))
            throw new AxisFault("Server.Admin.error",
                JavaUtils.getMessage("noClassname00"),
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

        mapping.setLanguageSpecificType(cls);
        
        if (isBean) {
            // Resolve qname based on prefix and localpart

            String namespaceURI = elem.getNamespaceURI();
            String localName    = elem.getLocalName();
            qn = new QName(namespaceURI, localName);

            category.debug( JavaUtils.getMessage("registerTypeMap00", "" + qn, classname));

            // register both serializers and deserializers for this bean
            mapping.setQName(qn);
            mapping.setSerializer(BeanSerializer.class);
            mapping.setDeserializer(BeanSerializer.getFactory().getClass());
        } else {
            String typeName = elem.getAttribute("type");
            int idx = typeName.indexOf(':');
            String prefix = typeName.substring(0, idx);
            String localPart = typeName.substring(idx + 1);

            qn = new QName(XMLUtils.getNamespace(prefix, elem), localPart);

            mapping.setQName(qn);
            classname = elem.getAttribute("serializer");
            category.debug( JavaUtils.getMessage("serializer00", classname));
            try {
                cls = cl.loadClass(classname);
                mapping.setSerializer(cls);
            } catch (Exception e) {
                throw new AxisFault( "Admin.error",
                    JavaUtils.getMessage("noSerializer01", e.toString()),
                    null, null);
            }
            classname = elem.getAttribute("deserializerFactory");
            category.debug( JavaUtils.getMessage("deserFact00", classname));
            try {
                cls = cl.loadClass(classname);
                mapping.setDeserializer(cls);
            } catch (Exception e) {
                throw new AxisFault( "Admin.error",
                    JavaUtils.getMessage("noDeserFact00", e.toString()),
                    null, null);
            }
        }
        
        if (registry != null) {
            WSDDDeployment.deployMappingToRegistry(mapping, registry);
        }
    }

    public static void main(String args[]) throws Exception {
        int  i = 0 ;

        if ( args.length < 2 || !(args[0].equals("client") ||
                                  args[0].equals("server")) ) {
            System.err.println( JavaUtils.getMessage("usage00", "Admin client|server <xml-file>") + "\n" );

            System.err.println( JavaUtils.getMessage("where00", "<xml-file>") );
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
            engine = new AxisClient(new FileProvider(Constants.CLIENT_CONFIG_FILE));
        else
            engine = new AxisServer(new FileProvider(Constants.SERVER_CONFIG_FILE));
        engine.setShouldSaveConfig(true);
        engine.init();
        MessageContext msgContext = new MessageContext(engine);

        try {
            for ( i = 1 ; i < args.length ; i++ ) {
                System.out.println( JavaUtils.getMessage("process00", args[i]) );
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
            System.err.println( JavaUtils.getMessage("errorProcess00", args[i]) );
            e.printStackTrace( System.err );
            //System.exit( 1 );
            throw e;
        }
    }
}
