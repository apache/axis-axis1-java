package test.wsdl.interop3.compound2;


import java.net.URL;

import test.wsdl.interop3.compound2.xsd.Employee;
import test.wsdl.interop3.compound2.xsd.Person;
import test.wsdl.interop3.compound2.Compound2Locator;
import test.wsdl.interop3.compound2.SoapInteropCompound2Binding;

/*
    <!-- SOAP Builder's round III web services          -->
    <!-- interoperability testing:  import1             -->
    <!-- (see http://www.whitemesa.net/r3/plan.html)    -->
    <!-- Step 1.  Start with predefined WSDL            -->
    <!-- Step 2.  Generate client from predefined WSDL  -->
    <!-- Step 3.  Test generated client against         -->
    <!--          pre-built server                      -->
    <!-- Step 4.  Generate server from predefined WSDL  -->
    <!-- Step 5.  Test generated client against         -->
    <!--          generated server                      -->
    <!-- Step 6.  Generate second client from           -->
    <!--          generated server's WSDL (some clients -->
    <!--          can do this dynamically)              -->
    <!-- Step 7.  Test second generated client against  -->
    <!--          generated server                      -->
    <!-- Step 8.  Test second generated client against  -->
    <!--          pre-built server                      -->
*/

public class Compound2TestCase extends junit.framework.TestCase {
    static URL url;

    public Compound2TestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
    }

    public void testStep3() {
        SoapInteropCompound2Binding binding;
        try {
            if (url != null) {
                binding = new Compound2Locator().getSoapInteropCompound2Port(url);
            } else {
                binding = new Compound2Locator().getSoapInteropCompound2Port();
            }
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Employee emp = new Employee();
            Person person = new Person();
            person.setMale(true);
            person.setName("Joe Blow");
            emp.setPerson(person);
            emp.setID(314159);
            emp.setSalary(100000.50);
            
            Employee result = binding.echoEmployee(emp);

            assertTrue("Results did not match", result.equals(emp));

        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }



    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                url = new URL(args[0]);
            } catch (Exception e) {
            }
        }

        junit.textui.TestRunner.run(new junit.framework.TestSuite(Compound2TestCase.class));
    } // main
}

