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

package test.functional;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.axis.AxisFault ;
import org.apache.axis.client.http.AdminClient;
import org.apache.axis.utils.Debug ;
import org.apache.axis.utils.Options ;
import org.apache.axis.utils.QName ;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;

import junit.framework.TestCase;

import samples.stock.GetQuote;

/** Test the stock sample code.
 */
public class TestStockSample extends TestCase {
    
    public TestStockSample(String name) {
        super(name);
    }
    
    public void doTestStockJWS () throws Exception {
        String[] args = { "-uuser1", "-wpass1", "XXX", "-s/axis/StockQuoteService.jws" };
        float val = new GetQuote().getQuote(args);
        assertEquals("TestStockSample.doTestStockJWS(): stock price is 55.25", val, 55.25, 0.01);
    }
    
    public void doTestDeploy () throws Exception {
        String[] args = { "samples/stock/deploy.xml" };
        new AdminClient().doAdmin(args);
    }
    
    public void doTestStock () throws Exception {
        String[] args = { "-uuser1", "-wpass1", "XXX" };
        float val = new GetQuote().getQuote(args);
        assertEquals("TestStockSample.doTestStock(): stock price is 55.25", val, 55.25, 0.01);
    }
    
    public void doTestUndeploy () throws Exception {
        String[] args = { "samples/stock/undeploy.xml" };
        new AdminClient().doAdmin(args);
    }
    
    
    public void testStockService () throws Exception {
        try {
            System.out.println("Testing stock sample.");
            System.out.println("Testing JWS...");
            doTestStockJWS();
            System.out.println("Testing deployment...");
            doTestDeploy();
            System.out.println("Testing service...");
            doTestStock();
            System.out.println("Testing undeployment...");
            doTestUndeploy();
            System.out.println("Test complete.");
        }
        catch( Exception e ) {
            if ( e instanceof AxisFault ) ((AxisFault)e).dump();
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }
    
    /**
     * Read all the contents that are to be read
     */
    static void readFully(HttpURLConnection connection) throws IOException
    {
        // finish reading it to prevent (harmless) server-side exceptions
        BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
        byte[] buffer = new byte[256];
        while((is.read(buffer)) > 0) {}
        is.close();
    }
    
}

