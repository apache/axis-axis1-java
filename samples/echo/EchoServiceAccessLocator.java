/**
 * EchoServiceAccessLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package samples.echo;

public class EchoServiceAccessLocator extends org.apache.axis.client.Service implements samples.echo.EchoServiceAccess {

    // Use to get a proxy class for EchoServicePortType
    private final java.lang.String EchoServicePortType_address = "http://nagoya.apache.org:5049/axis/services/echo";

    public String getEchoServicePortTypeAddress() {
        return EchoServicePortType_address;
    }

    public samples.echo.EchoServicePortType getEchoServicePortType() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EchoServicePortType_address);
        }
        catch (java.net.MalformedURLException e) {
            return null; // unlikely as URL was validated in WSDL2Java
        }
        return getEchoServicePortType(endpoint);
    }

    public samples.echo.EchoServicePortType getEchoServicePortType(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            return new samples.echo.EchoServiceBindingStub(portAddress, this);
        }
        catch (org.apache.axis.AxisFault e) {
            return null; // ???
        }
    }
}
