/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
package test.client;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.handlers.BasicHandler;

import java.net.URL;
import java.net.ConnectException;
import java.io.InterruptedIOException;

public class TestCall extends TestCase {
    public TestCall(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestCall.class);
    }

    protected void setup() {
    }

    /* Test case for Bug 23031 - No deserializer found for ns1:ArrayOfstring */
    public void testWeatherService() {
        try {
            Call call = new Call(new URL("http://live.capescience.com:80/ccx/GlobalWeather"));
            call.setUseSOAPAction(true);
            call.setSOAPActionURI("capeconnect:GlobalWeather:StationInfo#listCountries");
            call.setTimeout(new Integer(15*1000));
            call.setOperationName(new javax.xml.namespace.QName("capeconnect:GlobalWeather:StationInfo", "listCountries"));
            String[] c = (String[]) call.invoke(new Object[]{});
            System.out.println(c.length);
            for (int i = 0; i < c.length; i++) {
                System.out.println(c[i]);
            }
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
            System.err.println("testWeatherService connect error: " + ioe);
            return;
        }
    }
}
