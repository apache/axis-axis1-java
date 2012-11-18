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
package org.apache.axis.test.spring;

import java.rmi.RemoteException;

import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

public class OrderEndpoint extends ServletEndpointSupport implements OrderManagerPortType {
    private OrderManager orderManager;
    
    protected void onInit() {
        orderManager = (OrderManager)getWebApplicationContext().getBean("orderManager");
    }
 
    public String submitOrder(Order order) throws RemoteException {
        return orderManager.submitOrder(order);
    }

    public Order getOrder(String orderID) throws RemoteException {
        return orderManager.getOrder(orderID);
    }
}
