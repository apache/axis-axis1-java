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

package test.functional;

import samples.jaxm.DelayedStockQuote;
import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import samples.jaxm.UddiPing;

import java.net.ConnectException;

/**
 * Test the JAX-RPC compliance samples.
 */
public class TestJAXMSamples extends TestCase {
    static Log log = LogFactory.getLog(TestJAXMSamples.class.getName());

    public TestJAXMSamples(String name) {
        super(name);
    } // ctor

    public void testUddiPing() throws Exception {
        try {
            log.info("Testing JAXM UddiPing sample.");
            UddiPing.searchUDDI("IBM", "http://www-3.ibm.com/services/uddi/testregistry/inquiryapi");
            log.info("Test complete.");
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Fault returned from test: " + t);
        }
    } // testGetQuote

    public void testDelayedStockQuote() throws Exception {
        try {
            log.info("Testing JAXM DelayedStockQuote sample.");
            DelayedStockQuote stockQuote = new DelayedStockQuote();
            System.out.print("The last price for SUNW is " + stockQuote.getStockQuote("SUNW"));
            log.info("Test complete.");
        } catch (javax.xml.soap.SOAPException e) {
            Throwable t = e.getCause();
            if (t != null) {
                t.printStackTrace();
                if (t instanceof AxisFault) {
                    if (((AxisFault) t).detail instanceof ConnectException) {
                        System.out.println("Connect failure caused JAXM DelayedStockQuote to be skipped.");
                        return;
                    }
                }
                throw new Exception("Fault returned from test: " + t);
            } else {
                e.printStackTrace();
                throw new Exception("Exception returned from test: " + e);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Fault returned from test: " + t);
        }
    } // testGetQuote

    public static void main(String args[]) throws Exception {
        TestJAXMSamples tester = new TestJAXMSamples("tester");
        tester.testUddiPing();
    } // main
}


