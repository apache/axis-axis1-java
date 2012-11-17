/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.transport.http.javanet;

import junit.framework.TestCase;

import org.apache.axis.Handler;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.DefaultConfiguration;

/**
 * Tests that {@link DefaultConfiguration} (with type <tt>client</tt>) configures the java.net
 * transport as default HTTP transport if it is in the classpath.
 * 
 * @author Andreas Veithen
 */
public class TestDefaultConfiguration extends TestCase {
    public void test() throws Exception {
        AxisClient client = new AxisClient(new DefaultConfiguration("client"));
        Handler[] handlers = ((SimpleTargetedChain)client.getTransport("http")).getHandlers();
        assertTrue(handlers[0] instanceof JavaNetHTTPSender);
    }
}
