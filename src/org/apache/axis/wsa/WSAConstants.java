package org.apache.axis.wsa ;

import javax.xml.namespace.QName;

public interface WSAConstants {
  public final String NS_WSA1 = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
  public final String NS_WSA2 = "http://www.w3.org/2005/08/addressing";
  public final String NS_WSA = NS_WSA2 ;

  public final String EN_EndpointReference = "EndpointReference";
  public final String EN_Address = "Address";
  public final String EN_ReferenceProperties = "ReferenceProperties";
  public final String EN_ReferenceParameters = "ReferenceParameters";
  public final String EN_PortType = "PortType";
  public final String EN_ServiceName = "ServiceName";
  public final String EN_Policy = "Policy";

  public final String OUTBOUND_MIH = "org.apache.axis.wsa.outboundMIH" ;
  public final String INBOUND_MIH  = "org.apache.axis.wsa.inboundMIH" ;

  public final String REQ_MIH = "org.apache.axis.wsa.reqMIHeader" ;
  public final String RES_MIH = "org.apache.axis.wsa.resMIHeader" ;
  public final String WSA_DONT_REROUTE = "org.apache.axis.wsa.dontroute";
}
