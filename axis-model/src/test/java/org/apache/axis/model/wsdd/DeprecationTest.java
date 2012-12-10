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
package org.apache.axis.model.wsdd;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.xml.sax.InputSource;

public class DeprecationTest extends TestCase {
    public void testLanguageSpecificType() throws Exception {
        Deployment deployment = WSDDUtil.load(new InputSource(DeprecationTest.class.getResource("languageSpecificType.wsdd").toString()));
        Service service = (Service)deployment.getServices().get(0);
        BeanMapping beanMapping = (BeanMapping)service.getBeanMappings().get(0);
        assertEquals(new QName(WSDDConstants.URI_WSDD_JAVA, "test.rpc.Bean"), beanMapping.getType());
    }
}
