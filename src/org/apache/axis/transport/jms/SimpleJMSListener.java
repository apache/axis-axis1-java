/*
 * Copyright 2001, 2002,2004 The Apache Software Foundation.
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

package org.apache.axis.transport.jms;

import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.components.jms.JMSVendorAdapterFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;

import javax.jms.BytesMessage;
import javax.jms.MessageListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;


/**
 * SimpleJMSListener implements the javax.jms.MessageListener interface. Its
 *   basic purpose is listen asynchronously for messages and to pass them off
 *   to SimpleJMSWorker for processing.
 *
 * Note: This is a simple JMS listener that does not pool worker threads and
 *   is not otherwise tuned for performance. As such, its intended use is not
 *   for production code, but for demos, debugging, and performance profiling.
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 */
public class SimpleJMSListener implements MessageListener
{
    protected static Log log =
            LogFactory.getLog(SimpleJMSListener.class.getName());

    // Do we use (multiple) threads to process incoming messages?
    private static boolean doThreads;

    private JMSConnector connector;
    private JMSEndpoint endpoint;
    private AxisServer server;
    private HashMap connectorProps;

    public SimpleJMSListener(HashMap connectorMap, HashMap cfMap,
                             String destination, String username,
                             String password, boolean doThreads)
        throws Exception
    {
        SimpleJMSListener.doThreads = doThreads;

        try {
            // create a JMS connector using the default vendor adapter
            JMSVendorAdapter adapter = JMSVendorAdapterFactory.getJMSVendorAdapter();
            connector = JMSConnectorFactory.createServerConnector(connectorMap,
                                                                  cfMap,
                                                                  username,
                                                                  password,
                                                                  adapter);
            connectorProps = connectorMap;
        } catch (Exception e) {
            log.error(Messages.getMessage("exception00"), e);
            throw e;
        }

        // create the appropriate endpoint for the indicated destination
        endpoint = connector.createEndpoint(destination);
    }

    // Axis server (shared between instances)
    private static AxisServer myAxisServer = new AxisServer();

    protected static AxisServer getAxisServer()
    {
        return myAxisServer;
    }

    protected JMSConnector getConnector()
    {
        return connector;
    }

    /**
     * This method is called asynchronously whenever a message arrives.
     * @param message
     */
    public void onMessage(javax.jms.Message message)
    {
        try
        {
            // pass off the message to a worker as a BytesMessage
            SimpleJMSWorker worker = new SimpleJMSWorker(this, (BytesMessage)message);

            // do we allow multi-threaded workers?
            if (doThreads) {
                Thread t = new Thread(worker);
                t.start();
            } else {
                worker.run();
            }
        }
        catch(ClassCastException cce)
        {
            log.error(Messages.getMessage("exception00"), cce);
            cce.printStackTrace();
            return;
        }
    }

    public void start()
        throws Exception
    {
        endpoint.registerListener(this, connectorProps);
        connector.start();
    }

    public void shutdown()
        throws Exception
    {
        endpoint.unregisterListener(this);
        connector.stop();
        connector.shutdown();
    }

    public static final HashMap createConnectorMap(Options options)
    {
        HashMap connectorMap = new HashMap();
        if (options.isFlagSet('t') > 0)
        {
            //queue is default so only setup map if topic domain is required
            connectorMap.put(JMSConstants.DOMAIN, JMSConstants.DOMAIN_TOPIC);
        }
        return connectorMap;
    }

    public static final HashMap createCFMap(Options options)
        throws IOException
    {
        String cfFile = options.isValueSet('c');
        if(cfFile == null)
            return null;

        Properties cfProps = new Properties();
        cfProps.load(new BufferedInputStream(new FileInputStream(cfFile)));
        HashMap cfMap = new HashMap(cfProps);
        return cfMap;
    }

    public static void main(String[] args) throws Exception
    {
        Options options = new Options(args);

        // first check if we should print usage
        if ((options.isFlagSet('?') > 0) || (options.isFlagSet('h') > 0))
            printUsage();

        SimpleJMSListener listener = new SimpleJMSListener(createConnectorMap(options),
                                                           createCFMap(options),
                                                           options.isValueSet('d'),
                                                           options.getUser(),
                                                           options.getPassword(),
                                                           options.isFlagSet('s') > 0);
        listener.start();
    }

    public static void printUsage()
    {
        System.out.println("Usage: SimpleJMSListener [options]");
        System.out.println(" Opts: -? this message");
        System.out.println();
        System.out.println("       -c connection factory properties filename");
        System.out.println("       -d destination");
        System.out.println("       -t topic [absence of -t indicates queue]");
        System.out.println();
        System.out.println("       -u username");
        System.out.println("       -w password");
        System.out.println();
        System.out.println("       -s single-threaded listener");
        System.out.println("          [absence of option => multithreaded]");

        System.exit(1);
    }
}
