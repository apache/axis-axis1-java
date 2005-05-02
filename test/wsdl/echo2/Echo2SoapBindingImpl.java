/**
 * Echo2SoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Mar 22, 2005 (05:08:41 CET) WSDL2Java emitter.
 */

package test.wsdl.echo2;

import java.rmi.RemoteException;

public class Echo2SoapBindingImpl implements test.wsdl.echo2.Echo2PT{
    public test.wsdl.echo2.MyBase64Bean echoMyBase64Bean(test.wsdl.echo2.MyBase64Bean input) throws java.rmi.RemoteException {
        return input;
    }
    public test.wsdl.echo2.MyBase64Bean[] echoArrayOfMyBase64Bean(test.wsdl.echo2.MyBase64Bean[] input) throws java.rmi.RemoteException {
        return input;
    }

    public String[] echoArrayOfString_MaxOccursUnbounded(String[] input) throws java.rmi.RemoteException {
        return input;
    }

    public java.lang.String[] echoArrayOfString_SoapEncArray(java.lang.String[] input) throws java.rmi.RemoteException {
        return input;
    }

}
