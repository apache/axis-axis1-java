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
    public OmitTestCase(String name) {
        super(name);
    }
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
            test.wsdl.omit._Phone input = new test.wsdl.omit._Phone();
            input.setPrefix("555");
            input.setNumber("1212");

            test.wsdl.omit._Phone out = binding.echoPhone(input);
            
            // TODO: Verify the XML omitted the element
            assertNotNull("The return value from the operation was null", out);
            assertNull("areacode is not null", out.getAreaCode());
            assertEquals("prefix is incorrect", "555", out.getPrefix());
            assertEquals("number is incorrect", "1212", out.getNumber());
            
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public static void main(String[] args) {
        OmitTestCase tester = new OmitTestCase("tester");
        tester.test1OmitEchoPhone();
    }
}

