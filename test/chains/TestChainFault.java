/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
