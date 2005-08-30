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

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.URL;

public class TestJAXRPCDII extends TestCase {
    public TestJAXRPCDII(String name) {
        super(name);
    } // ctor

    public void test1() throws Exception {
        try {
            String wsdlLocation = "http://www.xmethods.net/sd/2001/TemperatureService.wsdl";
            String wsdlNsp = "http://www.xmethods.net/sd/TemperatureService.wsdl";
            ServiceFactory factory = ServiceFactory.newInstance();
            Service service = factory.createService(new URL(wsdlLocation),
                                  new QName(wsdlNsp, "TemperatureService"));
            Call[] calls = service.getCalls(new QName(wsdlNsp,"TemperaturePort"));
            assertTrue(calls != null);
            assertEquals(calls[0].getOperationName().getLocalPart(),"getTemp");
            ((org.apache.axis.client.Call)calls[0]).setTimeout(new Integer(15*1000));
            Object ret = calls[0].invoke(new Object[]{"02067"});
            System.out.println("Temperature:" + ret);
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
}
