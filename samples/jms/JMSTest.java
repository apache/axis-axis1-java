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

package samples.jms;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;

import org.apache.axis.transport.jms.JMSTransport;
import org.apache.axis.transport.jms.JMSConstants;
import org.apache.axis.transport.jms.SimpleJMSListener;

import java.util.HashMap;

import javax.xml.rpc.ParameterMode;
import javax.xml.namespace.QName;

/** Tests the JMS transport.  To run:
 *      java org.apache.axis.utils.Admin client client_deploy.xml
 *      java org.apache.axis.utils.Admin server deploy.xml
 *      java samples.transport.FileTest IBM
 *      java samples.transport.FileTest XXX
 *
 * JMSTest is a simple test driver for the JMS transport. It sets up a
 *   JMS listener, then calls a delayed quote service for each of the symbols
 *   specified on the command line.
 *
 * @author Jaime Meritt  (jmeritt@sonicsoftware.com)
 * @author Richard Chung (rchung@sonicsoftware.com)
 * @author Dave Chappell (chappell@sonicsoftware.com)
 */

public class JMSTest {
    static final String wsdd =
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n" +
            " <transport name=\"JMSTransport\" pivot=\"java:org.apache.axis.transport.jms.JMSSender\"/>\n" +
            " <service name=\"" + WSDDConstants.URI_WSDD + "\" provider=\"java:MSG\">\n" +
            "  <parameter name=\"allowedMethods\" value=\"AdminService\"/>\n" +
            "  <parameter name=\"className\" value=\"org.apache.axis.utils.Admin\"/>\n" +
            " </service>\n" +
            "</deployment>";

    public static void main(String args[]) throws Exception {
        Options opts = new Options( args );

        // first check if we should print usage
        if ((opts.isFlagSet('?') > 0) || (opts.isFlagSet('h') > 0))
            printUsage();

        HashMap connectorMap = SimpleJMSListener.createConnectorMap(opts);
        HashMap cfMap = SimpleJMSListener.createCFMap(opts);
        String destination = opts.isValueSet('d');
        String username = opts.getUser();
        String password = opts.getPassword();
        // create the jms listener
        SimpleJMSListener listener = new SimpleJMSListener(connectorMap,
                                                           cfMap,
                                                           destination,
                                                           username,
                                                           password,
                                                           false);
        listener.start();

        args = opts.getRemainingArgs();
        if ( args == null || args.length == 0)
            printUsage();

        Service  service = new Service(new XMLStringProvider(wsdd));

        // create the transport
        JMSTransport transport = new JMSTransport(connectorMap, cfMap);

        // create a new Call object
        Call     call    = (Call) service.createCall();

        call.setOperationName( new QName("urn:xmltoday-delayed-quotes", "getQuote") );
        call.addParameter( "symbol", XMLType.XSD_STRING, ParameterMode.IN );
        call.setReturnType( XMLType.XSD_FLOAT );
        call.setTransport(transport);

        // set additional params on the call if desired
        //call.setUsername(username );
        //call.setPassword(password );
        //call.setProperty(JMSConstants.WAIT_FOR_RESPONSE, Boolean.FALSE);
        //call.setProperty(JMSConstants.PRIORITY, new Integer(5));
        //call.setProperty(JMSConstants.DELIVERY_MODE,
        //    new Integer(javax.jms.DeliveryMode.PERSISTENT));
        //call.setProperty(JMSConstants.TIME_TO_LIVE, new Long(20000));

        call.setProperty(JMSConstants.DESTINATION, destination);
        call.setTimeout(new Integer(10000));

        Float res = new Float(0.0F);

        // invoke a call for each of the symbols and print out
        for (int i = 0; i < args.length; i++)
        {
            try
            {
            res = (Float) call.invoke(new Object[] {args[i]});
            System.out.println(args[i] + ": " + res);
            }
            catch(AxisFault af)
            {
                System.out.println(af.dumpToString());
            }
        }

        // shutdown
        listener.shutdown();
        transport.shutdown();
    }

    public static void printUsage()
    {
        System.out.println("JMSTest: Tests JMS transport by obtaining stock quote");
        System.out.println("  Usage: JMSTest <symbol 1> <symbol 2> <symbol 3> ...");
        System.out.println("   Opts: -? this message");
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
