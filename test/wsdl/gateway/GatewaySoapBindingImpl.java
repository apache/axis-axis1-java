/**
 * GatewaySoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.gateway;

public class GatewaySoapBindingImpl implements test.wsdl.gateway.Gateway{
    public java.lang.String test1(test.wsdl.gateway.MyClass myClass) throws java.rmi.RemoteException {
        String[][] textArray=myClass.getValues();
        return "value of 1,1 = "+textArray[0][0];
    }

    public test.wsdl.gateway.MyClass test2() throws java.rmi.RemoteException {
        String[][] param=new String[2][];
        param[0]=new String[1];
        param[0][0]="0,0";
        param[1]=new String[3];
        param[1][0]="1,0";
        param[1][1]="*1,1";
        param[1][2]="1,2";
        MyClass myClass=new MyClass();
        myClass.setValues(param);
        return myClass;
    }

    public java.lang.String[][] test3() throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String test4(java.lang.String[][] in0) throws java.rmi.RemoteException {
        return null;
    }

}
