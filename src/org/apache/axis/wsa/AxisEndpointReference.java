package org.apache.axis.wsa ;

import javax.xml.namespace.QName;
import java.util.List;
import java.net.URL;
import org.w3c.dom.Element;
import org.apache.axis.message.SOAPHeaderElement;
import java.util.Iterator;

public class AxisEndpointReference extends EndpointReference {

  AxisEndpointReference() {;
  } 

  public AxisEndpointReference(final String endpoint) {
    setAddress(endpoint);
  }

  public AxisEndpointReference(final URL wsdlLocation,
    final String serviceName, final String portName) {
    this.portType = portType;
    this.serviceName = serviceName;
    this.portName = portName;
  }

  public AxisEndpointReference(AxisEndpointReference epr ){
    super(epr);
  }

/*
  public javax.xml.rpc.Call createCall() throws  java.net.MalformedURLException, javax.xml.parsers.ParserConfigurationException {
    org.apache.axis.client.Call ret = new org.apache.axis.client.Call(address); 

    addSOAPHeaders(ret);
    return ret;
  }

  public void addSOAPHeaders(org.apache.axis.client.Call c) throws javax.xml.parsers.ParserConfigurationException { 
    
    for (Iterator i = createWsaHeaderElements().iterator(); i.hasNext();) {
      Element refNode = (Element) i.next(); 

      c.addHeader(new SOAPHeaderElement(refNode));
    }
  }
  */

}
