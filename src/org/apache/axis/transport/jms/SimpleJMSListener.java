/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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

package org.apache.axis.transport.jms;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.HashMap;

import javax.jms.MessageListener;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.AxisFault;

import org.apache.axis.server.AxisServer;

import org.apache.axis.utils.Messages;
import org.apache.axis.utils.Options;

import org.apache.commons.logging.Log;
import org.apache.axis.components.logger.LogFactory;

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
    private static boolean doThreads = true;

    private JMSConnector connector;
    private JMSEndpoint endpoint;
    private AxisServer server;

    public SimpleJMSListener(Options options)
        throws Exception
    {
        HashMap cfMap = new HashMap();
        cfMap.put(SonicConstants.BROKER_URL, options.isValueSet('b'));

        // do we have a jndi name?
        String jndiName = options.isValueSet('n');
        if (jndiName != null) {
            cfMap.put(JMSConstants.CONNECTION_FACTORY_JNDI_NAME, jndiName);
        } else {
            // topics or queues?
            String cf = null;
            if (options.isFlagSet('t') > 0) {
                cf = SonicConstants.TCF_CLASS;
            } else {
                cf = SonicConstants.QCF_CLASS;
            }
            cfMap.put(JMSConstants.CONNECTION_FACTORY_CLASS, cf);
        }

        // single-threaded?
        if (options.isFlagSet('s') > 0) {
            doThreads = false;
        }

        try {
            connector = JMSConnectorFactory.
                                createServerConnector(null,
                                                      cfMap,
                                                      options.getUser(),
                                                      options.getPassword());
        } catch (Exception e) {
            log.error(Messages.getMessage("exception00"), e);
            throw e;
        }

        // create the appropriate endpoint for the indicated destination
        endpoint = connector.createEndpoint(options.isValueSet('d'));
        endpoint.registerListener(this);
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

            //should pool

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
    {
        connector.start();
    }

    public void shutdown()
    {
        connector.stop();
        connector.shutdown();
    }

    public static void main(String[] args) throws Exception
    {
        Options options = new Options(args);

        // first check if we should print usage
        if ((options.isFlagSet('?') > 0) || (options.isFlagSet('h') > 0)) {
            printUsage();
        }

        SimpleJMSListener listener = new SimpleJMSListener(options);
    }

    public static void printUsage()
    {
        System.out.println("Usage: SimpleJMSListener [options]");
        System.out.println(" Opts: -? this message");
        System.out.println();
        System.out.println("       -b brokerurl");
        System.out.println("       -u username");
        System.out.println("       -w password");
        System.out.println();
        System.out.println("       -d destination");
        System.out.println("       -t topic [absence of -t indicates queue]");
        System.out.println();
        System.out.println("       -n jndi name for connection factory");
        System.out.println("          [jndi name obviates need for -t option]");
        System.out.println();
        System.out.println("       -s single-threaded listener");
        System.out.println("          [absence of option => multithreaded]");

        System.exit(1);
    }
}
