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

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.axis.AxisFault ;
import org.apache.axis.utils.Debug ;
import org.apache.axis.utils.Options ;
import org.apache.axis.utils.QName ;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;

import junit.framework.TestCase;

import org.apache.axis.utils.Admin;

import samples.transport.FileTest;

/** Test the stock sample code.
 */
public class TestTransportSample extends TestCase {
    
    public TestTransportSample(String name) {
        super(name);
    }
    
    public void doTestClientDeploy () throws Exception {
        String[] args = { "client", "samples/transport/client_deploy.xml" };
        Admin.main(args);
    }
    
    public void doTestDeploy () throws Exception {
        String[] args = { "server", "samples/transport/deploy.xml" };
        Admin.main(args);
    }
    
    /* NOT RECOMMENDED -- this calls out to xmltoday.com which is flaky.
       Verify that it either succeeds, or that it produces a specific
       failure. */
    
    public void doTestIBM () throws Exception {
        String[] args = { "IBM" };
        try {
            FileTest.main(args);
        } catch (AxisFault e) {
            String fault = e.getFaultString();
            if (fault == null) throw e;
            if (fault.indexOf("java.net.UnknownHost")<0) {
                int start = fault.indexOf(": ");
                System.out.println(fault.substring(start+2));
            } else if (fault.equals("timeout")) {
                System.out.println("timeout");
            } else {
                throw e;
            }
        }
    }
    
    public void doTestXXX () throws Exception {
        String[] args = { "XXX" };
        FileTest.main(args);
    }
    
    public void testService () throws Exception {
        try {
            System.out.println("Testing transport sample.");
            System.out.println("Testing client deployment...");
            doTestClientDeploy();
            System.out.println("Testing deployment...");
            doTestDeploy();
            System.out.println("Testing service with symbol IBM...");
            doTestIBM();
            System.out.println("Testing service with symbol XXX...");
            doTestXXX();
            System.out.println("Test complete.");
        }
        catch( Exception e ) {
            if ( e instanceof AxisFault ) ((AxisFault)e).dump();
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }
    
    /**
     * bogus 'main'
     */
    public static void main (String[] args) throws Exception {
        new TestTransportSample("foo").testService();
    }
}

