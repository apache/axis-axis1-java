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
package org.apache.axis.attachments;

import junit.framework.TestCase;

import org.apache.axiom.testutils.activation.InstrumentedDataSource;
import org.apache.axiom.testutils.activation.RandomDataSource;
import org.apache.commons.io.output.NullOutputStream;

public class TestDimeBodyPart extends TestCase {
    public void testWriteToWithDynamicContentDataHandlerClosesInputStreams() throws Exception {
        InstrumentedDataSource ds = new InstrumentedDataSource(new RandomDataSource(1000));
        DimeBodyPart bp = new DimeBodyPart(new DynamicContentDataHandler(ds), "1234");
        bp.write(new NullOutputStream(), (byte)0);
        assertEquals(0, ds.getOpenStreamCount());
    }
}
