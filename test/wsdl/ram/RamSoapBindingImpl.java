/**
 * RamSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.ram;

public class RamSoapBindingImpl implements Ram{
    public Response[] validate(RamData[] in0) throws java.rmi.RemoteException {
        java.util.Date inDate = in0[0].getCreditCard().getExpires();
        java.util.Date now = new java.util.Date();
        if (!inDate.before(now)) {
            throw new java.rmi.RemoteException("Time check failed.  Expires date = " + inDate + ", current time = " + now);
        }
        Response[] response = new Response[1];
        response[0] = new Response();
        response[0].setAccountingDate(new java.util.Date());
        response[0].setAuthorization("AUTH");
        response[0].setHostResponseCode("HRC");
        response[0].setHostResponseMessage("HRM");
        response[0].setProtoBaseResponseCode("PBRC");
        response[0].setProtoBaseResponseMessage("PBRM");
        response[0].setReasonCode("REASON");
        response[0].setReturnCode("RETURN");
        response[0].setSalePostingReferenceText("SPRT");
        response[0].setTransactionReferenceNumber("TRN");
        return response;
    }
}
