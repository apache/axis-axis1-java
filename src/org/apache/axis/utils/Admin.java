/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.utils ;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.server.AxisServer;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Handy static utility functions for turning XML into
 * Axis deployment operations.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class Admin
{
    protected static Log log =
        LogFactory.getLog(Admin.class.getName());

    /**
     * Process a given XML document - needs cleanup.
     */
    public Element[] AdminService(Element [] xml)
        throws Exception
    {
        log.debug("Enter: Admin::AdminService");
        MessageContext msgContext = MessageContext.getCurrentContext();
        Document doc = process( msgContext, xml[0] );
        Element[] result = new Element[1];
        result[0] = doc.getDocumentElement();
        log.debug("Exit: Admin::AdminService");
        return result;
    }

    protected static Document processWSDD(MessageContext msgContext,
                                          AxisEngine engine,
                                          Element root)
        throws Exception
    {
        Document doc = null ;

        String action = root.getLocalName();
        if (action.equals("passwd")) {
            String newPassword = root.getFirstChild().getNodeValue();
            engine.setAdminPassword(newPassword);
            doc = XMLUtils.newDocument();
            doc.appendChild( root = doc.createElementNS("", "Admin" ) );
            root.appendChild( doc.createTextNode( Messages.getMessage("done00") ) );
            return doc;
        }

        if (action.equals("quit")) {
            log.error(Messages.getMessage("quitRequest00"));
            if (msgContext != null) {
                // put a flag into message context so listener will exit after
                // sending response
                msgContext.setProperty(MessageContext.QUIT_REQUESTED, "true");
            }
            doc = XMLUtils.newDocument();
            doc.appendChild( root = doc.createElementNS("", "Admin" ) );
            root.appendChild( doc.createTextNode( Messages.getMessage("quit00", "") ) );
            return doc;
        }

        if ( action.equals("list") ) {
            return listConfig(engine);
        }

        if (action.equals("clientdeploy")) {
            // set engine to client engine
            engine = engine.getClientEngine();
        }

        WSDDDocument wsddDoc = new WSDDDocument(root);
        EngineConfiguration config = engine.getConfig();
        if (config instanceof WSDDEngineConfiguration) {
            WSDDDeployment deployment =
                ((WSDDEngineConfiguration)config).getDeployment();
            wsddDoc.deploy(deployment);
        }
        engine.refreshGlobalOptions();

        engine.saveConfiguration();

        doc = XMLUtils.newDocument();
        doc.appendChild( root = doc.createElementNS("", "Admin" ) );
        root.appendChild( doc.createTextNode( Messages.getMessage("done00") ) );

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
        throws Exception
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
        Handler serviceHandler = msgContext.getService();
        if (serviceHandler != null  &&
            !JavaUtils.isTrueExplicitly(serviceHandler.getOption("enableRemoteAdmin"))) {

            String remoteIP = msgContext.getStrProp(Constants.MC_REMOTE_ADDR);
            if (remoteIP != null  &&
                !remoteIP.equals("127.0.0.1")) {

                try {
                    InetAddress myAddr = InetAddress.getLocalHost();
                    InetAddress remoteAddr =
                            InetAddress.getByName(remoteIP);
                    if(log.isDebugEnabled()) {
                        log.debug("Comparing remote caller " + remoteAddr +" to "+ myAddr);
                    }


                    if (!myAddr.equals(remoteAddr)) {
                        log.error(Messages.getMessage("noAdminAccess01",
                                remoteAddr.toString()));
                        throw new AxisFault("Server.Unauthorized",
                           Messages.getMessage("noAdminAccess00"),
                           null, null);
                    }
                } catch (UnknownHostException e) {
                    throw new AxisFault("Server.UnknownHost",
                        Messages.getMessage("unknownHost00"),
                        null, null);
                }
            }
        }

        String rootNS = root.getNamespaceURI();
        AxisEngine engine = msgContext.getAxisEngine();

        // If this is WSDD, process it correctly.
        if (rootNS != null && rootNS.equals(WSDDConstants.URI_WSDD)) {
            return processWSDD(msgContext, engine, root);
        }

        // Else fault
        // TODO: Better handling here
        throw new Exception(Messages.getMessage("adminServiceNoWSDD"));
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
        SerializationContext context = new SerializationContextImpl(writer, null);
        context.setPretty(true);
        try {
            EngineConfiguration config = engine.getConfig();

            if (config instanceof WSDDEngineConfiguration) {
                WSDDDeployment deployment =
                    ((WSDDEngineConfiguration)config).getDeployment();
                deployment.writeToContext(context);
            }
        } catch (Exception e) {
            // If the engine config isn't a FileProvider, or we have no
            // engine config for some odd reason, we'll end up here.

            throw new AxisFault(Messages.getMessage("noEngineWSDD"));
        }

        try {
            writer.close();
            return XMLUtils.newDocument(new InputSource(new StringReader(writer.getBuffer().toString())));
        } catch (Exception e) {
            log.error("exception00", e);
            return null;
        }
    }

    public static void main(String args[]) throws Exception {
        int  i = 0 ;

        if ( args.length < 2 || !(args[0].equals("client") ||
                                  args[0].equals("server")) ) {
            log.error( Messages.getMessage("usage00", "Admin client|server <xml-file>") );

            log.error( Messages.getMessage("where00", "<xml-file>") );
            log.error( "<deploy>" );
            /*
            log.error( "  <transport name=a request=\"a,b,c\" sender=\"s\"");
            log.error( "                    response=\"d,e\"/>" );
            */
            log.error( "  <handler name=a class=className/>" );
            log.error( "  <chain name=a flow=\"a,b,c\" />" );
            log.error( "  <chain name=a request=\"a,b,c\" pivot=\"d\"" );
            log.error( "                  response=\"e,f,g\" />" );
            log.error( "  <service name=a handler=b />" );
            log.error( "</deploy>" );
            log.error( "<undeploy>" );
            log.error( "  <handler name=a/>" );
            log.error( "  <chain name=a/>" );
            log.error( "  <service name=a/>" );
            log.error( "</undeploy>" );
            log.error( "<list/>" );


            // throw an Exception which will go uncaught!  this way, a test
            // suite can invoke main() and detect the exception
            throw new IllegalArgumentException(
                    Messages.getMessage("usage00",
                                         "Admin client|server <xml-file>"));
            // System.exit( 1 );
        }

        Admin admin = new Admin();

        AxisEngine engine;
        if ( args[0].equals("client") )
            engine = new AxisClient();
        else
            engine = new AxisServer();
        engine.setShouldSaveConfig(true);
        engine.init();
        MessageContext msgContext = new MessageContext(engine);

        try {
            for ( i = 1 ; i < args.length ; i++ ) {
                if (log.isDebugEnabled())
                    log.debug( Messages.getMessage("process00", args[i]) );

                Document doc = XMLUtils.newDocument( new FileInputStream( args[i] ) );
                Document result = admin.process(msgContext, doc.getDocumentElement());
                if (result != null) {
                    System.out.println(XMLUtils.DocumentToString(result));
                }
            }
        }
        catch( Exception e ) {
            log.error( Messages.getMessage("errorProcess00", args[i]), e );
            throw e;
        }
    }
}
