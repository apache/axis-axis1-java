/**
 * PolymorphismTestSoapImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.polymorphism;

public class PolymorphismTestSoapImpl implements test.wsdl.polymorphism.PolymorphismTest_Port{
    public static final String B_TEXT = "this is my B field";
    public static final String A_TEXT = "this is my A field";

    public test.wsdl.polymorphism.A getBAsA(boolean sendC)
            throws java.rmi.RemoteException {
        if (sendC) {
            C myC = new C();
            myC.setA(A_TEXT);
            return myC;
        } else {
            B myB = new B();
            myB.setB(B_TEXT);
            myB.setA(A_TEXT);
            return myB;
        }
    }

}
