/**
 * WSDLInteropTestDocLitPortBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.interop3.docLit;

public class WSDLInteropTestDocLitPortBindingImpl implements test.wsdl.interop3.docLit.WSDLInteropTestDocLitPortType {
    public java.lang.String echoString(java.lang.String echoStringParam) throws java.rmi.RemoteException {
        return echoStringParam;
    }

    public test.wsdl.interop3.docLit.xsd.ArrayOfstring_Literal echoStringArray(test.wsdl.interop3.docLit.xsd.ArrayOfstring_Literal echoStringArrayParam) throws java.rmi.RemoteException {
        return echoStringArrayParam;
    }

    public test.wsdl.interop3.docLit.xsd.SOAPStruct echoStruct(test.wsdl.interop3.docLit.xsd.SOAPStruct echoStructParam) throws java.rmi.RemoteException {
        return echoStructParam;
    }

    public void echoVoid() throws java.rmi.RemoteException {
    }

}
