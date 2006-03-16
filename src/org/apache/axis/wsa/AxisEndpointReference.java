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

}
