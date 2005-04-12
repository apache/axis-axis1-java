/**
 * AddrBookServiceImplServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package test.wsdl.arrays3;

import junit.framework.AssertionFailedError;

public class AddrBookServiceImplServiceTestCase extends junit.framework.TestCase {
    public AddrBookServiceImplServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testAddressBookWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.arrays3.testclient.AddrBookServiceImplServiceLocator().getarrays3Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.arrays3.testclient.AddrBookServiceImplServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void testFunctional() throws Exception {
        test.wsdl.arrays3.testclient.Arrays3SoapBindingStub binding;
        try {
            binding = (test.wsdl.arrays3.testclient.Arrays3SoapBindingStub)
                          new test.wsdl.arrays3.testclient.AddrBookServiceImplServiceLocator().getarrays3();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        test.wsdl.arrays3.testclient.Phone ph1 = new test.wsdl.arrays3.testclient.Phone();
        ph1.setAreaCode(765);
        ph1.setExchange("494");
        ph1.setNumber("4900");

        test.wsdl.arrays3.testclient.Phone ph2 = new test.wsdl.arrays3.testclient.Phone();
        ph2.setAreaCode(765);
        ph2.setExchange("494");
        ph2.setNumber("4901");

        test.wsdl.arrays3.testclient.Phone ph3 = new test.wsdl.arrays3.testclient.Phone();
        ph3.setAreaCode(765);
        ph3.setExchange("494");
        ph3.setNumber("4902");

        test.wsdl.arrays3.testclient.StateType state = new test.wsdl.arrays3.testclient.StateType();
        state.setState("IN");

        test.wsdl.arrays3.testclient.Address addr = new test.wsdl.arrays3.testclient.Address();
        addr.setCity("West Lafayette");
        addr.setState(state);
        addr.setStreetName("University Drive");
        addr.setStreetNum(1);
        addr.setZip(47907);
        addr.setPhoneNumber(ph1);
        addr.setOtherPhones(new test.wsdl.arrays3.testclient.Phone[] { ph2, ph3});

        test.wsdl.arrays3.testclient.Address[] addrs = null;

        addrs = binding.getAddressFromNames(null);
        assertNull("addrs is not null", addrs);
        // if (addrs != null) System.out.println("addrs.length = " + addrs.length);

        addrs = binding.getAddressFromNames(new String[] { });
        assertNull("addrs is not null", addrs);
        // if (addrs != null) System.out.println("addrs.length = " + addrs.length);

        addrs = binding.getAddressFromNames(new String[] { "unknown", "unregistered" });
        assertEquals("addrs.length should be 2", addrs.length, 2);
        System.out.println("addrs.length = " + addrs.length);


        binding.addEntry("Purdue Boilermaker", addr);
        test.wsdl.arrays3.testclient.Address retAddr1 = 
            binding.getAddressFromName("Purdue Boilermaker");

        binding.addEntry("Boilermaker Purdue", addr);
        test.wsdl.arrays3.testclient.Address retAddrs[] = 
            binding.getAddressFromNames(new String[] { "Purdue Boilermaker", "Boilermaker Purdue" });

        retAddrs = binding.echoAddresses(null);
        assertNull("retAddrs is not null", retAddrs);

        retAddrs = binding.echoAddresses(new test.wsdl.arrays3.testclient.Address[] { });
        assertNull("retAddrs is not null", retAddrs);

        retAddrs = binding.echoAddresses(new test.wsdl.arrays3.testclient.Address[] { addr });
        assertEquals("retAddrs.length should be 1", 1, retAddrs.length);
        assertTrue("addr does not match", compareAddress(addr, retAddrs[0]));

        addr.setOtherPhones(null);
        retAddrs = binding.echoAddresses(new test.wsdl.arrays3.testclient.Address[] { addr });
        assertEquals("retAddrs.length should be 1", 1, retAddrs.length);
        assertTrue("addr does not match", compareAddress(addr, retAddrs[0]));
        assertNull("retAddrs[0].getOtherPhones() should be null", retAddrs[0].getOtherPhones());
        test.wsdl.arrays3.testclient.Phone[] arrph = new test.wsdl.arrays3.testclient.Phone[] { };
        addr.setOtherPhones(arrph);
        retAddrs = binding.echoAddresses(new test.wsdl.arrays3.testclient.Address[] { addr });
        assertEquals("retAddrs.length should be 1", 1, retAddrs.length);

        assertTrue("addr does not match", compareAddress(addr, retAddrs[0]));
        //assertNull("retAddrs[0].getOtherPhones() should be null", retAddrs[0].getOtherPhones());

        addr.setOtherPhones(arrph);
        retAddrs = binding.echoAddresses(new test.wsdl.arrays3.testclient.Address[] { addr });
        assertEquals("addrs.length should be 1", 1, retAddrs.length);
        assertTrue("addr does not match", compareAddress(addr, retAddrs[0]));

        retAddrs = binding.echoAddresses(new test.wsdl.arrays3.testclient.Address[] { addr, addr });
        assertEquals("addrs.length should be 2", 2, retAddrs.length);
        assertTrue("addr does not match", compareAddress(addr, retAddrs[0]));
        assertTrue("addr does not match", compareAddress(addr, retAddrs[1]));

        test.wsdl.arrays3.testclient.Address[] retAddrs2 = binding.echoAddresses(retAddrs);
        assertEquals("addrs.length should be 2", 2, retAddrs2.length);
        assertTrue("addr does not match", compareAddress(retAddrs[0], retAddrs2[0]));
        assertTrue("addr does not match", compareAddress(retAddrs[1], retAddrs2[1]));
    }


    public boolean compareAddress(test.wsdl.arrays3.testclient.Address addr1, test.wsdl.arrays3.testclient.Address addr2) {
        if (addr1 == null && addr2 != null) {
            throw new AssertionFailedError("");
        }
        if (addr1 != null && addr2 == null) {
            throw new AssertionFailedError("");
        }
        if (addr1 == null && addr2 == null) {
            return true;
        }

        if (!addr1.getCity().equals(addr2.getCity()))  {
            throw new AssertionFailedError("");
        }

        if (!addr1.getStreetName().equals(addr2.getStreetName()))  {
            throw new AssertionFailedError("");
        }

        if (addr1.getStreetNum() != addr2.getStreetNum()) {
            throw new AssertionFailedError("");
        }

        if (addr1.getZip() != addr2.getZip()) {
            throw new AssertionFailedError("");
        }

        if (!comparePhone(addr1.getPhoneNumber(), addr2.getPhoneNumber())) {
            throw new AssertionFailedError("");
        }

        if (!compareStateType(addr1.getState(), addr2.getState())) {
            throw new AssertionFailedError("");
        }

        if (!comparePhoneArray(addr1.getOtherPhones(), addr2.getOtherPhones())) {
            throw new AssertionFailedError("");
        }

        return true;
    }

    public boolean comparePhoneArray(test.wsdl.arrays3.testclient.Phone[] arr1,
            test.wsdl.arrays3.testclient.Phone[] arr2) {
        if (arr1 == null && arr2 != null) {
            throw new AssertionFailedError("");
        }
        if (arr1 != null && arr2 == null) {
            throw new AssertionFailedError("");
        }
        if (arr1 == null && arr2 == null) {
            return true;
        }
        if (arr1.length != arr2.length) {
            throw new AssertionFailedError("");
        }
        for (int i = 0; i < arr1.length; i++) {
            if (comparePhone(arr1[i], arr2[i]) == false) {
                throw new AssertionFailedError("");
            }
        }
        return true;
    }

    public boolean comparePhone(test.wsdl.arrays3.testclient.Phone phone1, test.wsdl.arrays3.testclient.Phone phone2) {
        if (phone1 == null && phone2 != null) {
            throw new AssertionFailedError("");
        }
        if (phone1 != null && phone2 == null) {
            throw new AssertionFailedError("");
        }
        if (phone1 == null && phone2 == null) {
            return true;
        }

        if (phone1.getAreaCode() != phone2.getAreaCode()) {
            throw new AssertionFailedError("");
        }

        if (!phone1.getExchange().equals(phone2.getExchange()))  {
            throw new AssertionFailedError("");
        }
        if (!phone1.getNumber().equals(phone2.getNumber()))  {
            throw new AssertionFailedError("");
        }

        return true;
    }

    public boolean compareStateType(test.wsdl.arrays3.testclient.StateType st1, test.wsdl.arrays3.testclient.StateType st2) {
        if (st1 == null && st2 != null) {
            throw new AssertionFailedError("");
        }
        if (st1 != null && st2 == null) {
            throw new AssertionFailedError("");
        }
        if (st1 == null && st2 == null) {
            return true;
        }
        if (!st1.getState().equals(st2.getState()))  {
            throw new AssertionFailedError("");
        }
        return true; 
    }


}
