/**
 * ExampleSoapImpl.java
 *
 * Verifies that wrapped operations featuring Java wrapper types are working.
 */

package test.wsdl.wrapperHolder;

public class ExampleSoapImpl implements test.wsdl.wrapperHolder.ExampleSoap {
    public void doExample(byte[][] doExampleValue1, java.lang.Long out, javax.xml.rpc.holders.ByteArrayHolder doExampleResponseRet, javax.xml.rpc.holders.LongWrapperHolder out2) throws java.rmi.RemoteException {
        doExampleResponseRet.value = doExampleValue1[0];
        out2.value = out;
    }
}
