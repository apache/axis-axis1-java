package test.wsdl.getPort;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import java.util.Iterator;

// This test makes sure that the getPort method works in various service classes.

public class GetPortTestCase extends junit.framework.TestCase {

    private static final QName portOne = new QName("portOne");
    private static final QName portTwo = new QName("portTwo");
    private static final QName portTwoA = new QName("portTwoA");
    private static final QName portThree = new QName("portThree");

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
            assertTrue("Wrong exception!  " + se.getLinkedCause(),
                    se.getLinkedCause() == null);
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
            assertTrue("Wrong exception!  " + se.getLinkedCause(),
                    se.getLinkedCause() == null);
        }

        // Make sure we get the proper ports
        try {
            Stub one = (Stub) service.getPort(portOne, One.class);
            Stub two = (Stub) service.getPort(portTwo, Two.class);
            Stub three = (Stub) service.getPort(portThree, Three.class);
            assertTrue("getPort(portOne) should be of type One, instead it is " + one.getClass().getName(), one instanceof One);
            assertTrue("getPort(portOne) should have address http://localhost:8080/axis/services/portOne, instead it has " + one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), "http://localhost:8080/axis/services/portOne".equals(one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
            assertTrue("getPort(portTwo) should be of type Two, instead it is " + two.getClass().getName(), two instanceof Two);
            assertTrue("getPort(portTwo) should have address http://localhost:8080/axis/services/portTwo, instead it has " + two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), "http://localhost:8080/axis/services/portTwo".equals(two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
            assertTrue("getPort(portThree) should be of type Three, instead it is " + three.getClass().getName(), three instanceof Three);
            assertTrue("getPort(portThree) should have address http://localhost:8080/axis/services/portThree, instead it has " + three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), "http://localhost:8080/axis/services/portThree".equals(three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
        }
        catch (ServiceException se) {
            fail("unexpected failure:  " + se);
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
            assertTrue("Wrong exception!  " + se.getLinkedCause(),
                    se.getLinkedCause() == null);
        }

        // Make sure we get the proper ports
        try {
            Stub one = (Stub) service.getPort(portOne, One.class);
            Stub two = (Stub) service.getPort(portTwo, Two.class);
            Stub three = (Stub) service.getPort(portTwoA, Three.class);
            assertTrue("getPort(portOne) should be of type One, instead it is " + one.getClass().getName(), one instanceof One);
            assertTrue("getPort(portOne) should have address http://localhost:8080/axis/services/portOne, instead it has " + one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), "http://localhost:8080/axis/services/portOne".equals(one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
            assertTrue("getPort(portTwo) should be of type Two, instead it is " + two.getClass().getName(), two instanceof Two);
            assertTrue("getPort(portTwo) should have address http://localhost:8080/axis/services/portTwo, instead it has " + two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), "http://localhost:8080/axis/services/portTwo".equals(two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
            assertTrue("getPort(portTwoA) should be of type Two, instead it is " + three.getClass().getName(), three instanceof Two);
            assertTrue("getPort(portThree) should have address http://localhost:8080/axis/services/portTwoA, instead it has " + three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), "http://localhost:8080/axis/services/portTwoA".equals(three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
        }
        catch (ServiceException se) {
            fail("unexpected failure:  " + se);
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
            assertTrue("Wrong exception!  " + se.getLinkedCause(),
                    se.getLinkedCause() == null);
        }

        // Make sure we get the proper ports
        try {
            Stub one = (Stub) service.getPort(portOne, One.class);
            Stub two = (Stub) service.getPort(portTwo, Two.class);
            Stub three = (Stub) service.getPort(portThree, Three.class);
            assertTrue("getPort(portOne) should be of type One, instead it is " + one.getClass().getName(), one instanceof One);
            assertTrue("getPort(portOne) should have address http://localhost:8080/axis/services/portOne, instead it has " + one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), "http://localhost:8080/axis/services/portOne".equals(one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
            assertTrue("getPort(portTwo) should be of type Two, instead it is " + two.getClass().getName(), two instanceof Two);
            assertTrue("getPort(portTwo) should have address http://localhost:8080/axis/services/portTwo, instead it has " + two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), "http://localhost:8080/axis/services/portTwo".equals(two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
            assertTrue("getPort(portThree) should be of type One, instead it is " + three.getClass().getName(), three instanceof One);
            assertTrue("getPort(portThree) should have address http://localhost:8080/axis/services/portFour, instead it has " + three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), "http://localhost:8080/axis/services/portFour".equals(three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
        }
        catch (ServiceException se) {
            fail("unexpected failure:  " + se);
        }
    } // testDoublePortService2

    public void testGetPorts() {
        Service service = null;
        try {
            service = new EmptyLocator();
            verifyNumberOfPorts("Empty", service.getPorts(), 0);
        }
        catch (ServiceException se) {
            fail("EmptyLocator.getPorts() should not have failed:  " + se);
        }
        try {
            service = new ServiceALocator();
            verifyNumberOfPorts("ServiceA", service.getPorts(), 3);
        }
        catch (ServiceException se) {
            fail("ServiceA.getPorts() should not have failed:  " + se);
        }
        try {
            service = new ServiceBLocator();
            verifyNumberOfPorts("ServiceB", service.getPorts(), 3);
        }
        catch (ServiceException se) {
            fail("ServiceB.getPorts() should not have failed:  " + se);
        }
        try {
            service = new ServiceCLocator();
            verifyNumberOfPorts("ServiceC", service.getPorts(), 3);
        }
        catch (ServiceException se) {
            fail("ServiceC.getPorts() should not have failed:  " + se);
        }
    } // testGetPorts

    private void verifyNumberOfPorts(String service, Iterator i, int shouldHave) {
        int count = 0;
        for (;i.hasNext();count++,i.next());
        assertTrue("Service " + service + " should have " + shouldHave + " ports but instead has " + count, shouldHave == count);
    } // verifyNumberOfPorts

} // class VerifyTestCase

