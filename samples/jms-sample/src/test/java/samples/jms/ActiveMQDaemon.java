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
package samples.jms;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.axis.testutils.daemon.Daemon;

public class ActiveMQDaemon implements Daemon {
    private BrokerService broker;
    
    public void start(String[] args) throws Exception {
        broker = BrokerFactory.createBroker("broker:(tcp://localhost:" + args[0] + ")?useJmx=false&persistent=false");
        broker.start();
    }

    public void stop() throws Exception {
        broker.stop();
    }
}
