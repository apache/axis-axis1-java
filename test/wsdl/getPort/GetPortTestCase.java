package test.wsdl.getPort;

import javax.xml.rpc.ServiceException;

// This test makes sure that the getPort method works in various service classes.

public class GetPortTestCase extends junit.framework.TestCase {
    public GetPortTestCase(String name) {
        super(name);
    } // ctor

    public void testEmptyService() {
        Empty empty = new EmptyLocator();
        try {
            empty.getPort(null);
            fail("empty.getPort(null) should have failed.");
        }
        catch (ServiceException se) {
            assertTrue("Wrong exception!  " + se.getCause(), se.getCause() == null);
        }
    } // testEmptyService

    public void testNormalService() {
        ServiceA service = new ServiceALocator();
        try {
            One one = (One) service.getPort(One.class);
            Two two = (Two) service.getPort(Two.class);
            Three three = (Three) service.getPort(Three.class);
        }
        catch (Throwable t) {
            fail("Should not have gotten an exception:  " + t);
        }
        try {
            service.getPort(java.util.Vector.class);
            fail("service.getPort(Vector.class) should have failed.");
        }
        catch (ServiceException se) {
            assertTrue("Wrong exception!  " + se.getCause(), se.getCause() == null);
        }
    } // testNormalService

    public void testDoublePortService1() {
        ServiceB service = new ServiceBLocator();
        try {
            One one = (One) service.getPort(One.class);
            Two two = (Two) service.getPort(Two.class);
        }
        catch (Throwable t) {
            fail("Should not have gotten an exception:  " + t);
        }
        try {
            service.getPort(Three.class);
            fail("service.getPort(Three.class) should have failed.");
        }
        catch (ServiceException se) {
            assertTrue("Wrong exception!  " + se.getCause(), se.getCause() == null);
        }
    } // testDoublePortService1

    public void testDoublePortService2() {
        ServiceC service = new ServiceCLocator();
        try {
            One one = (One) service.getPort(One.class);
            Two two = (Two) service.getPort(Two.class);
        }
        catch (Throwable t) {
            fail("Should not have gotten an exception:  " + t);
        }
        try {
            service.getPort(Three.class);
            fail("service.getPort(Three.class) should have failed.");
        }
        catch (ServiceException se) {
            assertTrue("Wrong exception!  " + se.getCause(), se.getCause() == null);
        }
    } // testDoublePortService2

} // class VerifyTestCase

