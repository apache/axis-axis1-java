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

package test.dynamic;

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import samples.client.DynamicInvoker;

import java.io.InterruptedIOException;
import java.net.ConnectException;

public class TestDynamicInvoker extends TestCase {
    public TestDynamicInvoker(String name) {
        super(name);
    } // ctor

    public void test1() throws Exception {
        try {
            String[] args = new String[]{"http://www.xmethods.net/sd/2001/TemperatureService.wsdl", "getTemp", "02067"};
            DynamicInvoker.main(args);
        }  catch (java.net.ConnectException ce) {
            System.err.println("getTemp connect error: " + ce);
            return;
        }  catch (java.rmi.RemoteException re) {
            if (re instanceof AxisFault) {
                AxisFault fault = (AxisFault) re;
                if (fault.detail instanceof ConnectException ||
                    fault.detail instanceof InterruptedIOException ||
                    (fault.getFaultString().indexOf("Connection timed out") != -1) ||
                    fault.getFaultCode().getLocalPart().equals("HTTP")) {
                    System.err.println("getTemp HTTP error: " + fault);
                    return;
                }
            }
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }  catch (java.io.IOException ioe) {
            System.err.println("getTemp connect error: " + ioe);
            return;
        }
    }

    public void test2() throws Exception {
        try {
            String[] args = new String[]{"http://services.xmethods.net/soap/urn:xmethods-delayed-quotes.wsdl", "getQuote", "IBM"};
            DynamicInvoker.main(args);
        }  catch (java.net.ConnectException ce) {
            System.err.println("getQuote connect error: " + ce);
            return;
        }  catch (java.rmi.RemoteException re) {
            if (re instanceof AxisFault) {
                AxisFault fault = (AxisFault) re;
                if (fault.detail instanceof ConnectException ||
                    fault.detail instanceof InterruptedIOException ||
                    fault.getFaultCode().getLocalPart().equals("HTTP")) {
                    System.err.println("getQuote HTTP error: " + fault);
                    return;
                }
            }
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }  catch (java.io.IOException ioe) {
            System.err.println("getQuote connect error: " + ioe);
            return;
        }
    }

    public void test3() throws Exception {
        try {
            String[] args = new String[]{"http://mssoapinterop.org/asmx/xsd/round4XSD.wsdl", "echoString(Round4XSDTestSoap)", "Hello World!!!"};
            DynamicInvoker.main(args);
        }  catch (java.net.ConnectException ce) {
            System.err.println("round4XSD connect error: " + ce);
            return;
        }  catch (java.rmi.RemoteException re) {
            if (re instanceof AxisFault) {
                AxisFault fault = (AxisFault) re;
                if (fault.detail instanceof ConnectException ||
                    fault.detail instanceof InterruptedIOException ||
                    fault.getFaultCode().getLocalPart().equals("HTTP")) {
                    System.err.println("round4XSD HTTP error: " + fault);
                    return;
                }
            }
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }  catch (java.io.IOException ioe) {
            System.err.println("round4XSD connect error: " + ioe);
            return;
        }
    }

    public void test4() throws Exception {
        try {
            String[] args = new String[]{"http://samples.gotdotnet.com/quickstart/aspplus/samples/services/MathService/VB/MathService.asmx?WSDL", 
                                        "Add", 
                                        "3", 
                                        "4"};
            DynamicInvoker.main(args);
        }  catch (java.net.ConnectException ce) {
            System.err.println("MathService connect error: " + ce);
            return;
        }  catch (java.rmi.RemoteException re) {
            if (re instanceof AxisFault) {
                AxisFault fault = (AxisFault) re;
                if (fault.detail instanceof ConnectException ||
                    fault.detail instanceof InterruptedIOException ||
                    fault.getFaultCode().getLocalPart().equals("HTTP")) {
                    System.err.println("MathService HTTP error: " + fault);
                    return;
                }
            }
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }  catch (java.io.IOException ioe) {
            System.err.println("MathService connect error: " + ioe);
            return;
        }
    }
}
