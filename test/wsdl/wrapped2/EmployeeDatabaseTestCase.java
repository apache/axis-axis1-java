/**
 * EmployeeDatabaseTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2dev Oct 26, 2003 (08:57:14 EST) WSDL2Java emitter.
 */

package test.wsdl.wrapped2;

public class EmployeeDatabaseTestCase extends junit.framework.TestCase {
    public EmployeeDatabaseTestCase(java.lang.String name) {
        super(name);
    }
    public void test1EmployeeDatabaseGetEmployeeCount() throws Exception {
        test.wsdl.wrapped2.EmployeeDBBindingStub binding;
        try {
            binding = (test.wsdl.wrapped2.EmployeeDBBindingStub)
                          new test.wsdl.wrapped2.EmployeeDatabaseLocator().getEmployeeDatabase();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        int value = -3;
        value = binding.getEmployeeCount(0);
        // TBD - validate results
    }

    public void test2EmployeeDatabaseAddGroups() throws Exception {
        test.wsdl.wrapped2.EmployeeDBBindingStub binding;
        try {
            binding = (test.wsdl.wrapped2.EmployeeDBBindingStub)
                          new test.wsdl.wrapped2.EmployeeDatabaseLocator().getEmployeeDatabase();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        boolean value = false;
        value = binding.addGroups(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test3EmployeeDatabaseIsManager() throws Exception {
        test.wsdl.wrapped2.EmployeeDBBindingStub binding;
        try {
            binding = (test.wsdl.wrapped2.EmployeeDBBindingStub)
                          new test.wsdl.wrapped2.EmployeeDatabaseLocator().getEmployeeDatabase();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        boolean value = false;
        value = binding.isManager(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test4EmployeeDatabasePromoteEmployee() throws Exception {
        test.wsdl.wrapped2.EmployeeDBBindingStub binding;
        try {
            binding = (test.wsdl.wrapped2.EmployeeDBBindingStub)
                          new test.wsdl.wrapped2.EmployeeDatabaseLocator().getEmployeeDatabase();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        boolean value = false;
        value = binding.promoteEmployee(new test.wsdl.wrapped2.xsd.NameType(), 0);
        // TBD - validate results
    }

    public void test5EmployeeDatabaseGetEmployees() throws Exception {
        test.wsdl.wrapped2.EmployeeDBBindingStub binding;
        try {
            binding = (test.wsdl.wrapped2.EmployeeDBBindingStub)
                          new test.wsdl.wrapped2.EmployeeDatabaseLocator().getEmployeeDatabase();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        test.wsdl.wrapped2.xsd.EmployeeType[] value = null;
        value = binding.getEmployees(new test.wsdl.wrapped2.xsd.NameType[0]);
        // TBD - validate results
    }

    public void test6EmployeeDatabaseScheduleMtg() throws Exception {
        test.wsdl.wrapped2.EmployeeDBBindingStub binding;
        try {
            binding = (test.wsdl.wrapped2.EmployeeDBBindingStub)
                          new test.wsdl.wrapped2.EmployeeDatabaseLocator().getEmployeeDatabase();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        boolean value = false;
        value = binding.scheduleMtg(new test.wsdl.wrapped2.xsd.EmployeeType[0]);
        // TBD - validate results
    }

}
