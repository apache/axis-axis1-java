package test.wsdl.getPort;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import java.util.Iterator;

// This test makes sure that the getPort method works in various service classes.

public class GetPortTestCase extends junit.framework.TestCase {

    private static final QName portAOne = new QName("portAOne");
    private static final QName portATwo = new QName("portATwo");
    private static final QName portAThree = new QName("portAThree");

    private static final QName portBOne = new QName("portBOne");
    private static final QName portBTwo = new QName("portBTwo");
    private static final QName portBTwoA = new QName("portBTwoA");
    
    private static final QName portCOne = new QName("portCOne");
    private static final QName portCTwo = new QName("portCTwo");
    private static final QName portCThree = new QName("portCThree");
    
    private static final String ADR_PORTAONE = "http://localhost:8080/axis/services/portAOne";
    private static final String ADR_PORTATWO = "http://localhost:8080/axis/services/portATwo";
    private static final String ADR_PORTATHREE = "http://localhost:8080/axis/services/portAThree";
    

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


/*

   <service name="serviceA">
    <documentation>
    Service with all ports unique. /-- Test Bug 13407 - embedded comments --/
    </documentation>
    <port name="portAOne" binding="tns:bindingOne">
      <soap:address location="http://localhost:8080/axis/services/portAOne"/>
    </port>
    <port name="portATwo" binding="tns:bindingTwo">
      <soap:address location="http://localhost:8080/axis/services/portATwo"/>
    </port>
    <port name="portAThree" binding="tns:bindingThree">
      <soap:address location="http://localhost:8080/axis/services/portAThree"/>
    </port>
  </service>

 */
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
            Stub one = (Stub) service.getPort(portAOne, One.class);
            Stub two = (Stub) service.getPort(portATwo, Two.class);
            Stub three = (Stub) service.getPort(portAThree, Three.class);
            assertTrue("getPort(portAOne) should be of type One, instead it is " + one.getClass().getName(), one instanceof One);
            assertTrue("getPort(portAOne) should have " + ADR_PORTAONE + ", instead it has " + one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), 
                ADR_PORTAONE.equals(one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
         
            assertTrue("getPort(portATwo) should be of type Two, instead it is " + two.getClass().getName(), two instanceof Two);
            assertTrue("getPort(portATwo) should have address " + ADR_PORTATWO + ", instead it has " + two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), 
               ADR_PORTATWO.equals(two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
         
            assertTrue("getPort(portAThree) should be of type Three, instead it is " + three.getClass().getName(), three instanceof Three);
            assertTrue("getPort(portAThree) should have address " + 
                       ADR_PORTATHREE + ", instead it has " + 
                       three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), 
                       ADR_PORTATHREE.equals(three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
        }
        catch (ServiceException se) {
            fail("unexpected failure:  " + se);
        }
    } // testNormalService

/*
  
 <service name="serviceB">
    <documentation>
    Service with two ports (portBTwo, portBTwoA) that share the same portType via the same binding.
    </documentation>
    <port name="portBOne" binding="tns:bindingOne">
      <soap:address location="http://localhost:8080/axis/services/portOne"/>
    </port>
    <port name="portBTwo" binding="tns:bindingTwo">
      <soap:address location="http://localhost:8080/axis/services/portBTwo"/>
    </port>
    <port name="portBTwoA" binding="tns:bindingTwo">
      <soap:address location="http://localhost:8080/axis/services/portBTwoA"/>
    </port>
  </service>
*/
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
            Stub one = (Stub) service.getPort(portBOne, One.class);
            Stub two = (Stub) service.getPort(portBTwo, Two.class);
            Stub three = (Stub) service.getPort(portBTwoA, Two.class);
            assertTrue("getPort(portBOne) should be of type One, instead it is " + one.getClass().getName(), one instanceof One);
            assertTrue("getPort(portBOne) should have address http://localhost:8080/axis/services/portBOne," 
                       + " instead it has " + one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY),
                       "http://localhost:8080/axis/services/portBOne".equals(one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));

            assertTrue("getPort(portBTwo) should be of type Two, instead it is " + two.getClass().getName(), two instanceof Two);
            assertTrue("getPort(portBTwo) should have address"
                       + "http://localhost:8080/axis/services/portBTwo," 
                       + "instead it has " + two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)
                       + ", port is " + two.toString(),
                       "http://localhost:8080/axis/services/portBTwo".equals(two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));

            assertTrue("getPort(portBTwoA) should be of type Two, instead it is " + three.getClass().getName(), three instanceof Two);
            assertTrue("getPort(portBTwoA) should have address "
            			+ "http://localhost:8080/axis/services/portBTwoA, "
            			+ "instead it has " + three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), 
            			"http://localhost:8080/axis/services/portBTwoA".equals(three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
        }
        catch (ServiceException se) {
            fail("unexpected failure:  " + se);
        }
    } // testDoublePortService1


/*
 *   <service name="serviceC">
    <documentation>
    Service with two ports (portCTwo, portCThree) that share the same portType via different bindings.
    </documentation>
    <port name="portCOne" binding="tns:bindingOne">
      <soap:address location="http://localhost:8080/axis/services/portCOne"/>
    </port>
    <port name="portCTwo" binding="tns:bindingTwo">
      <soap:address location="http://localhost:8080/axis/services/portCTwo"/>
    </port>
    <port name="portCThree" binding="tns:bindingAnotherOne">
      <soap:address location="http://localhost:8080/axis/services/portCThree"/>
    </port>
  </service>

*/
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
            Stub one = (Stub) service.getPort(portCOne, One.class);
            Stub two = (Stub) service.getPort(portCTwo, Two.class);
            Stub three = (Stub) service.getPort(portCThree, Three.class);
            assertTrue("getPort(portCOne) should be of type One, instead it is " + one.getClass().getName(), one instanceof One);
            assertTrue("getPort(portCOne) should have address "
            	 + "http://localhost:8080/axis/services/portCOne, "
            	 + "instead it has " + one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), 
            	 "http://localhost:8080/axis/services/portCOne".equals(one._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
            	 
            	 
            assertTrue("getPort(portCTwo) should be of type Two, instead it is " + two.getClass().getName(), two instanceof Two);
            assertTrue("getPort(portCTwo) should have address " 
                 + "http://localhost:8080/axis/services/portCTwo, "
                 + "instead it has " + two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), 
                 "http://localhost:8080/axis/services/portCTwo".equals(two._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
                 
                 
            assertTrue("getPort(portCThree) should be of type One, instead it is " + three.getClass().getName(), three instanceof One);
            assertTrue("getPort(portCThree) should have address "
                 + "http://localhost:8080/axis/services/portCThree,"
                 + " instead it has " + three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY), 
                 "http://localhost:8080/axis/services/portCThree".equals(three._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY)));
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

