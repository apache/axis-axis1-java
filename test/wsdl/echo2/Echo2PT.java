/**
 * Echo2PT.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package test.wsdl.echo2;

public interface Echo2PT extends java.rmi.Remote {
    public test.wsdl.echo2.MyBase64Bean echoMyBase64Bean(test.wsdl.echo2.MyBase64Bean input) throws java.rmi.RemoteException;
    public test.wsdl.echo2.MyBase64Bean[] echoArrayOfMyBase64Bean(test.wsdl.echo2.MyBase64Bean[] input) throws java.rmi.RemoteException;
    public java.lang.String[] echoArrayOfString_MaxOccursUnbounded(java.lang.String[] input) throws java.rmi.RemoteException;
    public java.lang.String[] echoArrayOfString_SoapEncArray(java.lang.String[] input) throws java.rmi.RemoteException;
}
