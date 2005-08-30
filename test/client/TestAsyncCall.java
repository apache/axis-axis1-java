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
import org.apache.axis.client.Call;
import org.apache.axis.client.async.AsyncCall;
import org.apache.axis.client.async.IAsyncCallback;
import org.apache.axis.client.async.IAsyncResult;
import org.apache.axis.client.async.Status;

import java.net.MalformedURLException;
import java.net.URL;

public class TestAsyncCall extends TestCase {
    public TestAsyncCall(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestAsyncCall.class);
    }

    protected void setup() {
    }

    public void testAsyncPollWeatherService() throws MalformedURLException, InterruptedException {
        Call call = new Call(new URL("http://live.capescience.com:80/ccx/GlobalWeather"));
        call.setUseSOAPAction(true);
        call.setSOAPActionURI("capeconnect:GlobalWeather:StationInfo#listCountries");
        call.setTimeout(new Integer(15 * 1000));
        call.setOperationName(new javax.xml.namespace.QName("capeconnect:GlobalWeather:StationInfo", "listCountries"));
        AsyncCall ac = new AsyncCall(call);
        IAsyncResult result = ac.invoke(new Object[]{});
        System.out.println("STARTED");
        Status status = null;
        while ((status = result.getStatus()) == Status.NONE) {
            System.out.print('.');
            Thread.sleep(50);
        }
        System.out.println("FINISHED");
        if (status == Status.COMPLETED) {
            String[] c = (String[]) result.getResponse();
            System.out.println(c.length);
            for (int i = 0; i < c.length; i++) {
                System.out.println(c[i]);
            }
        } else if (status == Status.EXCEPTION) {
            result.getException().printStackTrace();
        }
    }

    public void testAsyncCallbackWeatherService() throws MalformedURLException, InterruptedException {
        final Call call = new Call(new URL("http://live.capescience.com:80/ccx/GlobalWeather"));
        call.setUseSOAPAction(true);
        call.setSOAPActionURI("capeconnect:GlobalWeather:StationInfo#listCountries");
        call.setTimeout(new Integer(15 * 1000));
        call.setOperationName(new javax.xml.namespace.QName("capeconnect:GlobalWeather:StationInfo", "listCountries"));
        final AsyncCall ac = new AsyncCall(call, new IAsyncCallback() {
            public void onCompletion(IAsyncResult result) {
                Status status = result.getStatus();
                System.out.println(".....FINISHED");
                if (status == Status.COMPLETED) {
                    String[] c = (String[]) result.getResponse();
                    System.out.println(c.length);
                    for (int i = 0; i < c.length; i++) {
                        System.out.println(c[i]);
                    }
                } else if (status == Status.EXCEPTION) {
                    result.getException().printStackTrace();
                }
                synchronized (call) {
                    call.notifyAll();
                }
            }
        });
        IAsyncResult result = ac.invoke(new Object[]{});
        System.out.println("STARTED....");
        synchronized (call) {
            call.wait(0);
        }
    }
}
