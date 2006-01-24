package org.apache.axis.wsa ;

import javax.xml.namespace.QName;

public interface WSAConstants {
  public final String NS_WSA = "http://schemas.xmlsoap.org/ws/2004/08/addressing";

  public final String EN_EndpointReference = "EndpointReference";
  public final QName  QN_EndpointReference = new QName(NS_WSA, EN_EndpointReference);
  public final String EN_Address = "Address";
  public final QName  QN_Address = new QName(NS_WSA, EN_Address);

  public final String EN_ReferenceProperties = "ReferenceProperties";
  public final QName  QN_ReferenceProperties = new QName(NS_WSA, EN_ReferenceProperties);

  public final String EN_ReferenceParameters = "ReferenceParameters";
  public final QName  QN_ReferenceParameters = new QName(NS_WSA, EN_ReferenceParameters);

  public final String EN_PortType = "PortType";
  public final QName  QN_PortType = new QName(NS_WSA, EN_PortType);

  public final String EN_ServiceName = "ServiceName";
  public final QName  QN_ServiceName = new QName(NS_WSA, EN_ServiceName);

  public final String EN_Policy = "Policy";
  public final QName  QN_Policy = new QName(NS_WSA, EN_Policy);

  public final String Anonymous_Address = NS_WSA + "/role/anonymous" ;
  public final String Fault_URI = NS_WSA + "/fault" ;

  public final String OUTBOUND_MIH = "org.apache.axis.wsa.outboundMIH" ;
  public final String INBOUND_MIH  = "org.apache.axis.wsa.inboundMIH" ;

  public final String REQ_MIH = "org.apache.axis.wsa.reqMIHeader" ;
  public final String RES_MIH = "org.apache.axis.wsa.resMIHeader" ;
  public final String WSA_DONT_REROUTE = "org.apache.axis.wsa.dontroute";
}
