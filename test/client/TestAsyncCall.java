/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
