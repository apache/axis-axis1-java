/**
 * WSDLInteropTestDocLitPortBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.interop3.docLit;

public class WSDLInteropTestDocLitPortBindingImpl implements test.wsdl.interop3.docLit.WSDLInteropTestDocLitPortBinding {
    public java.lang.String echoString(java.lang.String echoStringParam) throws java.rmi.RemoteException {
        return echoStringParam;
    }

    public org.soapinterop.ArrayOfstringLiteral echoStringArray(org.soapinterop.ArrayOfstringLiteral echoStringArrayParam) throws java.rmi.RemoteException {
        return echoStringArrayParam;
    }

    public org.soapinterop.SOAPStruct echoStruct(org.soapinterop.SOAPStruct echoStructParam) throws java.rmi.RemoteException {
        return echoStructParam;
    }

    public void echoVoid() throws java.rmi.RemoteException {
    }

}
