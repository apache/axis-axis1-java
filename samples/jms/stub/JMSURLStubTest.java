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
package samples.jms.stub;

import samples.jms.stub.xmltoday_delayed_quotes.*;
import org.apache.axis.AxisFault;
import org.apache.axis.utils.Options;
import org.apache.axis.transport.jms.JMSTransport;
import org.apache.axis.transport.jms.SimpleJMSListener;
import java.util.HashMap;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;

/**
 * Demonstrates use of wsdl2java-generated static stubs to invoke JMS endpoints.
 *
 * The JMS listener is an intermediary that receives the JMS service request and
 * invokes the actual stock quote service over HTTP.
 *
 * @author Ray Chun (rchun@sonicsoftware.com)
*/

public class JMSURLStubTest extends TestCase {
    public JMSURLStubTest(String name) {
        super(name);
    }

    public static Float getQuote(String ticker) throws AxisFault {
        float quote = -1.0F;

        GetQuoteServiceLocator locator = new GetQuoteServiceLocator();
        GetQuote getQuote;

        try {
            getQuote = locator.getGetQuote();
        }
        catch (ServiceException e) {
            throw new AxisFault("JAX-RPC ServiceException caught: ", e);
        }
        assertTrue("getQuote is null", getQuote != null);

        try {
            quote = getQuote.getQuote(ticker);
            System.out.println("quote: " + quote);

            // close matching connectors
            // note: this is optional, as all connectors will be closed upon exit
            String endptAddr = locator.getGetQuoteAddress();
            JMSTransport.closeMatchingJMSConnectors(endptAddr, null, null);
        }
        catch (RemoteException e) {
            throw new AxisFault("Remote Exception caught: ", e);
        }
        return new Float(quote);
    }

    public static void printUsage()
    {
        System.out.println("JMSURLStubTest: Tests JMS transport by obtaining stock quote using wsdl2java-generated stub classes");
        System.out.println("  Usage: JMSURLStubTest <symbol 1> <symbol 2> <symbol 3> ...");
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

    /**
     * Conn args are still required to set up the JMS listener
     */
    public static void main(String[] args) throws Exception
    {
        Options opts = new Options( args );

        // first check if we should print usage
        if ((opts.isFlagSet('?') > 0) || (opts.isFlagSet('h') > 0))
            printUsage();

        String username = opts.getUser();
        String password = opts.getPassword();

        HashMap connectorMap = SimpleJMSListener.createConnectorMap(opts);
        HashMap cfMap = SimpleJMSListener.createCFMap(opts);
        String destination = opts.isValueSet('d');

        args = opts.getRemainingArgs();
        if ( args == null || args.length == 0)
            printUsage();

        // create the jms listener
        SimpleJMSListener listener = new SimpleJMSListener(connectorMap,
                                                           cfMap,
                                                           destination,
                                                           username,
                                                           password,
                                                           false);
        listener.start();

        JMSURLStubTest stubTest = new JMSURLStubTest("JMS URL static stub test");

        for (int i = 0; i < args.length; i++)
        {
            try
            {
                Float quote = stubTest.getQuote(args[i]);
                System.out.println(args[i] + ": " + quote);
            }
            catch(AxisFault af)
            {
                System.out.println(af.dumpToString());
            }
        }

        listener.shutdown();

        System.exit(1);
    }
}
