/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

/**
 * OmitTestCase.java
 *
 * This tests the omitting of elements when minOccurs=0 and the value 
 * of the element is Null.
 * 
 * For instance:
 *  <Phone>
 *   <prefix>555</prefix>
 *   <number>1212</number>
 *  </Phone>
 *
 * This would normally have the additional areaCode element: 
 *   <areaCode xsi:nil=true/>
 * 
 */

package test.wsdl.omit;

public class OmitTestCase extends junit.framework.TestCase {
    private static final String AREA_CODE = "111";
    public OmitTestCase(String name) {
        super(name);
    }
    /**
     * Optimistic scenario:
     *   - area code is echoed successfully
     *   - prefix is not part of XML exchanged between the client and servers
     *   - number is passed as null.
     * There does not seem to be a good way to verify what's exchanged over the wire.
     */
    public void test1OmitEchoPhone() {
        test.wsdl.omit.Omit binding;
        try {
            binding = new test.wsdl.omit.OmitTestLocator().getomit();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.omit.Phone input = new test.wsdl.omit.Phone();
            input.setAreaCode(AREA_CODE);

            test.wsdl.omit.Phone out = binding.echoPhone(input);

            assertNotNull("The return value from the operation was null", out);
            assertEquals("area code is incorrect", AREA_CODE, out.getAreaCode());
            assertNull("prefix is not null", out.getPrefix());
            assertNull("number is not null", out.getNumber());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    /**
     * Validating that an exception is thrown when area code (which is a required element) is null:
     */
    public void test2OmitEchoPhone() {
        test.wsdl.omit.Omit binding;
        try {
            binding = new test.wsdl.omit.OmitTestLocator().getomit();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.omit.Phone input = new test.wsdl.omit.Phone();
            test.wsdl.omit.Phone out = binding.echoPhone(input);
            throw new junit.framework.AssertionFailedError("web services call succeeded despite of AreaCode being null.");
        }
        catch (java.rmi.RemoteException re) {
            // this is desired
            System.out.println(re);
        }
    }

    public static void main(String[] args) {
        OmitTestCase tester = new OmitTestCase("tester");
        tester.test1OmitEchoPhone();
    }
}

