/**
 * EchoServiceAccess.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package samples.echo;

public interface EchoServiceAccess extends javax.xml.rpc.Service {
    public String getEchoServicePortTypeAddress();

    public samples.echo.EchoServicePortType getEchoServicePortType() throws javax.xml.rpc.ServiceException;

    public samples.echo.EchoServicePortType getEchoServicePortType(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
