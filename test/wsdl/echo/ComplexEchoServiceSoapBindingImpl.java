/**
 * ComplexEchoServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.echo;

public class ComplexEchoServiceSoapBindingImpl implements Echo{
    public void echo(test.wsdl.echo.holders.MyComplexTypeHolder myElement) throws java.rmi.RemoteException {
        myElement.value.setSimpleItem("MY_SIMPLE_ITEM");    
    }

}
