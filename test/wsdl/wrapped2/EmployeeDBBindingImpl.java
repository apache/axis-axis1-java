/**
 * EmployeeDBBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2dev Oct 26, 2003 (08:57:14 EST) WSDL2Java emitter.
 */

package test.wsdl.wrapped2;

public class EmployeeDBBindingImpl implements test.wsdl.wrapped2.EmployeeDBPort{
    public int getEmployeeCount(int empCountReq) throws java.rmi.RemoteException {
        return -3;
    }

    public boolean addGroups(java.lang.String[] group) throws java.rmi.RemoteException {
        return false;
    }

    public boolean isManager(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException {
        return false;
    }

    public boolean promoteEmployee(test.wsdl.wrapped2.xsd.NameType empName, int empID) throws java.rmi.RemoteException {
        return false;
    }

    public test.wsdl.wrapped2.xsd.EmployeeType[] getEmployees(test.wsdl.wrapped2.xsd.NameType[] name) throws java.rmi.RemoteException {
        return null;
    }

    public boolean scheduleMtg(test.wsdl.wrapped2.xsd.EmployeeType[] employee) throws java.rmi.RemoteException {
        return false;
    }

}
