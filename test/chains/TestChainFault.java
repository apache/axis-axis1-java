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
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SimpleChain;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.server.AxisServer;

import javax.xml.soap.SOAPBody;

/**
* Used to verify that Faults are processed properly in the Handler chain
* @author Russell Butek (butek@us.ibm.com)
* @author Chris Haddad <haddadc@cobia.net>
*/
public class TestChainFault extends TestCase
{
  // correlation message
    public static String FAULT_MESSAGE = "Blew a gasket!";

    public TestChainFault (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestChainFault.class);
    }

    protected void setup() {
    }

    private class TestMessageContext extends MessageContext {

        private int hcount = 0;

        public TestMessageContext() {
            super(new AxisServer());
        }

        public void incCount() {
            hcount++;
        }

        public void decCount() {
            hcount--;
        }

        public int count() {
            return hcount;
        }
    }

    private class TestHandler extends BasicHandler {
        private int chainPos;
        private boolean doFault = false;
        private String stFaultCatch = null;

        /* The following state really relates to a Message Context, so this Handler
         * must not be used for more than one Message Context. However, it
         * is sufficient for the purpose of this testcase.
         */
        private boolean invoked = false;

        public TestHandler(int pos) {
            chainPos = pos;
        }

        public void setToFault() {
            doFault = true;
        }

        public void setFaultCatch(String stValue) { stFaultCatch = stValue; }
        public String getFaultCatch() { return stFaultCatch; }

        public void invoke(MessageContext msgContext) throws AxisFault {
            TestMessageContext mc = (TestMessageContext)msgContext;
            assertEquals("Handler.invoke out of sequence", chainPos, mc.count());
            invoked = true;
            if (doFault) {
                throw new AxisFault(TestChainFault.FAULT_MESSAGE);
            }
            mc.incCount();
        }

        public void onFault(MessageContext msgContext) {
            TestMessageContext mc = (TestMessageContext)msgContext;
            mc.decCount();
            assertEquals("Handler.onFault out of sequence", chainPos, mc.count());
            assertTrue("Handler.onFault protocol error", invoked);
            // grap the Soap Fault String
            stFaultCatch = getFaultString(msgContext);
        }
    }

    /**
    * Extract the fault string from the Soap Response
    *
    **/
    String getFaultString(MessageContext msgContext) {
      String stRetval = null;
      Message message = msgContext.getResponseMessage();
      try {
          if (message != null) {
            SOAPBody oBody  = message.getSOAPEnvelope().getBody();
            stRetval = oBody.getFault().getFaultString();
          }
      }
      catch (javax.xml.soap.SOAPException e) {
          assertTrue("Unforseen soap exception", false);
      }
      catch (AxisFault f) {
          assertTrue("Unforseen axis fault", false);
      }

      return stRetval;
    }

    public void testSimpleChainFaultAfterInvoke()
    {
        try {
            SimpleChain c = new SimpleChain();

            for (int i = 0; i < 5; i++) {
                c.addHandler(new TestHandler(i));
            }

            TestMessageContext mc = new TestMessageContext();
            c.invoke(mc);
            c.onFault(mc);
            assertEquals("Some onFaults were missed", mc.count(), 0);

        } catch (Exception ex) {
            assertTrue("Unexpected exception", false);
            ex.printStackTrace();
        }
    }


    public void testSimpleChainFaultDuringInvoke()
    {
        try {
            SimpleChain c = new SimpleChain();

            for (int i = 0; i < 5; i++) {
                TestHandler th = new TestHandler(i);
                if (i == 3) {
                    th.setToFault();
                }
                c.addHandler(th);
            }


            TestMessageContext mc = new TestMessageContext();
            try {
                c.invoke(mc);
                assertTrue("Testcase error - didn't throw fault", false);
            } catch (AxisFault f) {
                assertEquals("Some onFaults were missed", mc.count(), 0);
            }

        } catch (Exception ex) {
            assertTrue("Unexpected exception", false);
        }
    }

/**
* Ensure that the fault detail is being passed back
* to handlers that executed prior to the fault
**/
    public void testFaultDetailAvailableDuringInvoke()
    {
      // the handler instance to validate
      // NOTE:must execute before the handler that throws the fault
      TestHandler testHandler = null;

        try {
            SimpleChain c = new SimpleChain();

            for (int i = 0; i < 5; i++) {
                TestHandler th = new TestHandler(i);
                if (i == 2)
                  testHandler = th;

                if (i == 3) {
                    th.setToFault();
                }
                c.addHandler(th);
            }


            TestMessageContext mc = new TestMessageContext();
            try {
                c.invoke(mc);
                assertTrue("Testcase error - didn't throw fault", false);
            } catch (AxisFault f) {
                // did we save off the fault string?
              assertEquals("faultstring does not match constant",
                testHandler.getFaultCatch(),TestChainFault.FAULT_MESSAGE);
                // does saved faultString match AxisFault?
              assertEquals("Fault not caught by handler",
                testHandler.getFaultCatch(),f.getFaultString());
            }

        } catch (Exception ex) {
            assertTrue("Unexpected exception", false);
        }
    }

}
