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
package test.chains;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.InternalException;
import org.apache.axis.MessageContext;
import org.apache.axis.SimpleChain;
import org.apache.axis.handlers.BasicHandler;

public class TestSimpleChain extends TestCase
{
    public TestSimpleChain (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestSimpleChain.class);
    }

    protected void setup() {
    }

    private class TestHandler extends BasicHandler {
        public TestHandler() {}
        public void invoke(MessageContext msgContext) throws AxisFault {}
    }

    public void testSimpleChainAddHandler()
    {
        SimpleChain c = new SimpleChain();

        Handler h1 = new TestHandler();
        assertTrue("Empty chain has a handler", !c.contains(h1));

        c.addHandler(h1);
        assertTrue("Added handler not in chain", c.contains(h1));
    }

    public void testSimpleChainAddHandlerAfterInvoke()
    {
        try {
            SimpleChain c = new SimpleChain();
            Handler h1 = new TestHandler();
            c.addHandler(h1);

            // A null engine is good enough for this test
            MessageContext mc = new MessageContext(null);
            c.invoke(mc);

            // while testing, disable noise
            boolean oldLogging = InternalException.getLogging();
            InternalException.setLogging(false);

            try {
                Handler h2 = new TestHandler();
                c.addHandler(h2);
                assertTrue("Handler added after chain invoked", false);
            } catch (Exception e) {
                // Correct behaviour. Exact exception isn't critical
            }

            // resume noise
            InternalException.setLogging(oldLogging);
        } catch (AxisFault af) {
            assertTrue("Unexpected exception", false);
        }
    }
}
