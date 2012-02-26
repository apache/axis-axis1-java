/**
 * EsrTestBindingImpl.java
 *
 * Test for bug 12636
 */

package test.wsdl.esr;

public class EsrTestBindingImpl implements test.wsdl.esr.EsrTest{
    public void esrInOut(short value, javax.xml.rpc.holders.ShortHolder echoVal, javax.xml.rpc.holders.DoubleHolder sqrtVal) throws java.rmi.RemoteException {
        echoVal.value = (short)value;
        sqrtVal.value = Math.sqrt(value);
    }
    public void esrInOut2(java.lang.String bstrSAH, java.lang.String bstrSUH, short value, javax.xml.rpc.holders.ShortHolder echoVal, javax.xml.rpc.holders.DoubleHolder sqrtVal) throws java.rmi.RemoteException
    {
        echoVal.value = value;
        sqrtVal.value = Math.sqrt( (double) value );
        if(Double.isNaN(sqrtVal.value)) {
          throw org.apache.axis.AxisFault.makeFault(new Exception("arg cannot be < 0"));
        }
    }
}