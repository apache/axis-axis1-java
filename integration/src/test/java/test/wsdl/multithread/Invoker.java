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
package test.wsdl.multithread;

import java.util.concurrent.CountDownLatch;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import samples.addr.Address;
import samples.addr.AddressBook;
import samples.addr.Phone;
import samples.addr.StateType;

class Invoker implements Runnable {
    private static Log log = LogFactory.getLog(Invoker.class.getName());
    
    private final AddressBook binding;
    private final CountDownLatch readyLatch;
    private final CountDownLatch startLatch;
    private final Report report;
    private final int numInvocations;
    
    Invoker(AddressBook binding, CountDownLatch readyLatch, CountDownLatch startLatch, Report report,
            int numInvocations) {
        this.binding = binding;
        this.readyLatch = readyLatch;
        this.startLatch = startLatch;
        this.report = report;
        this.numInvocations = numInvocations;
    }

    public void run() {
        try {
            // This ensures that all threads start sending requests at the same time,
            // thereby increasing the probability of triggering a concurrency issue.
            readyLatch.countDown();
            startLatch.await();
            
            for (int i = 0; i < numInvocations; ++i) {
                Address address = new Address();
                Phone phone = new Phone();
                address.setStreetNum(i);
                address.setStreetName("2");
                address.setCity("3");
                address.setState(StateType.TX);
                address.setZip(i);
                phone.setAreaCode(11);
                phone.setExchange("22");
                phone.setNumber("33");
                address.setPhoneNumber(phone);
                
                binding.addEntry("hi", address); 
                Address addressRet = binding.getAddressFromName("hi");
                // succeeded, count it.
                report.addSuccess();
            }
        } catch (Throwable t) {
            // Log a stack trace as we may not be so lucky next time!
            log.fatal("Throwable caught: ", t);

            report.setError(t);
        }
    } // run
}