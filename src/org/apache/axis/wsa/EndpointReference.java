package org.apache.axis.wsa ;

import java.io.Serializable;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.axis.utils.XMLUtils ;

import org.xml.sax.SAXException;

import java.util.Stack;
import org.xml.sax.Attributes;


abstract public class EndpointReference implements Serializable {
  protected static String staticNS = WSAConstants.NS_WSA ;

  protected String  namespace = staticNS ;
  protected String  address = null;
  protected String  portType = null; // QName
  protected String  serviceName = null; // QName
  protected String  portName = null;
  protected List    referenceProperties = new java.util.LinkedList();
  protected List    referenceParameters = new java.util.LinkedList();

  EndpointReference( final EndpointReference epr){
    this.address = epr.address;
    this.portType = epr.portType; //rrfoo still missing from serializers!
    this.serviceName = epr.serviceName;
    this.portName = epr.portName;
    this.referenceProperties = new java.util.LinkedList(epr.referenceProperties);
    this.referenceParameters = new java.util.LinkedList(epr.referenceParameters);
  }

  protected EndpointReference() {}

  public void setWSAVersion( String ns) {
    namespace = ns ;
  }

  public String getWSAVersion() {
    return namespace ;
  }

  public static void setGlobalWSAVersion(String ns) {
    staticNS = ns ;
  }

  public static String getGlobalWSAVersion() {
    return staticNS ;
  }

  public EndpointReference dup() throws Exception {
    EndpointReference newObj = (EndpointReference)this.getClass().newInstance();
    newObj.setAddress( this.getAddress() );
    newObj.setPortType( this.getPortType() );
    newObj.setServiceName( this.getServiceName() );
    newObj.setPortName( this.getPortName() );
    if ( this.referenceProperties != null ) {
      for ( int i = 0 ; i < this.referenceProperties.size() ; i++ ) {
        String prop = (String) this.referenceProperties.get(i);
        newObj.addReferenceProperty( new String(prop) );
      }
    }
    if ( this.referenceParameters != null ) {
      for ( int i = 0 ; i < this.referenceParameters.size() ; i++ ) {
        String prop = (String) this.referenceParameters.get(i);
        newObj.addReferenceParameter( new String(prop) );
      }
    }
    return newObj ;
  }

  public String toString() {
    return "" + getClass() +"@"+ hashCode() + " address: '" +address + 
           "' refProps: '" + referenceProperties + "' refParams: '" + 
           referenceParameters + "'"; 
  }


  /*-- Methods to create an instance ---*/

  public static EndpointReference newInstance() {
     return new AxisEndpointReference();
  }

  public static EndpointReference newInstance(String ns) {
     EndpointReference epr = new AxisEndpointReference();
     epr.setWSAVersion( ns );
     return epr ;
  }

  public static EndpointReference Anonymous() throws Exception { 
    if ( staticNS.equals(WSAConstants.NS_WSA1) )
      return EndpointReference.fromLocation( staticNS + "/role/anonymous" );
    return EndpointReference.fromLocation( staticNS + "/anonymous" );
  }

  public static EndpointReference Anonymous(String ns) throws Exception { 
    if ( ns.equals(WSAConstants.NS_WSA1) )
      return EndpointReference.fromLocation( ns + "/role/anonymous" );
    return EndpointReference.fromLocation( ns + "/anonymous" );
  }

  public boolean isAnonymous() {
    if ( namespace.equals(WSAConstants.NS_WSA1) )
      return this.address.equals( namespace + "/role/anonymous" );
    return this.address.equals( namespace + "/anonymous" );
  }

  public static EndpointReference None() throws Exception { 
    if ( staticNS.equals(WSAConstants.NS_WSA1) )  // should fault or something
      return EndpointReference.fromLocation( staticNS + "/role/none" );
    return EndpointReference.fromLocation( staticNS + "/none" );
  }

  public static EndpointReference None(String ns) throws Exception { 
    if ( ns.equals(WSAConstants.NS_WSA1) )  // should fault or something
      return EndpointReference.fromLocation( ns + "/role/none" );
    return EndpointReference.fromLocation( ns + "/none" );
  }

  public boolean isNone() {
    return this.address.equals( namespace + "/none" );
  }

  /**
   * Method to return a new EndpointReference object from
   * a DOM element.
   * 
   * @param el
   * 
   * @return EndpointReference
   */
  public static EndpointReference fromDOM(Element el) throws Exception {
     EndpointReference er = new AxisEndpointReference();
     String NSs[] = new String[] { WSAConstants.NS_WSA1, WSAConstants.NS_WSA2 };

     for ( int j = 0 ; j < NSs.length ; j++ ) {
       String ns = NSs[j] ;

       NodeList nl = el.getElementsByTagNameNS(ns, "Address");
       if ( nl.item(0)!=null ) {
         er.setAddress(Util.getText((Element)nl.item(0)));
         er.setWSAVersion( ns );
       }

       nl = el.getElementsByTagNameNS(ns, "PortType");
       if (nl.item(0)!=null)
         er.setPortType(Util.getText((Element)nl.item(0)));

       nl = el.getElementsByTagNameNS(ns, "ServiceName");
       if (nl.item(0)!=null) {
         Element child = (Element)nl.item(0);
         er.setServiceName(Util.getText(child));
         er.setPortName(child.getAttributeNS(ns, "PortName"));
       }

       nl = el.getElementsByTagNameNS(ns, "ReferenceProperties");
       if (nl.item(0)!=null) 
         er.addReferenceProperties( (Element) nl.item(0) );

       nl = el.getElementsByTagNameNS(ns, "ReferenceParameters");
       if (nl.item(0)!=null) 
         er.addReferenceParameters( (Element) nl.item(0) );
     }
     if ( er.getAddress() == null ) {
       String tmp = "Missing Address in EPR: " + XMLUtils.ElementToString(el);
       throw new Exception( tmp );
     }
     return er ;
  }

  /**
   * Passing in a wsa:ReferenceParameter element this will add all children
   * add references parameters to the EPR
   * @param refp The DOM element of the wsa:ReferenceParameter
   */
  public void addReferenceParameters(Element refp) {
    NodeList nl = refp.getChildNodes();

    for ( int i = 0 ; nl != null && i < nl.getLength() ; i++ ) {
      Node n = nl.item(i);
      if ( n.getNodeType() != Node.ELEMENT_NODE ) continue ;
      n = n.cloneNode(true);
      this.addReferenceParameter( XMLUtils.ElementToString((Element) n ));
    }
  }

  /**
   * Passing in a wsa:ReferenceProperties element this will add all children
   * add references properties to the EPR
   * @param refp The DOM element of the wsa:ReferenceProperties
   */
  public void addReferenceProperties(Element refp) {
    NodeList nl = refp.getChildNodes();

    for ( int i = 0 ; nl != null && i < nl.getLength() ; i++ ) {
      Node n = nl.item(i);
      if ( n.getNodeType() != Node.ELEMENT_NODE ) continue ;
      n = n.cloneNode(true);
      this.addReferenceProperty( XMLUtils.ElementToString((Element) n ));
    }
  }

/**
 * Method fromLocation. Obtain an endpoint reference from a String that contains in a URL
 *  format the location of the service.
 * @param location The location of the service.
 * @return EndpointReference
 */
  public static EndpointReference fromLocation( final String location){
    return new AxisEndpointReference(location);
  }

  public static EndpointReference fromLocation(final String location,String ns){
    EndpointReference epr = new AxisEndpointReference(location);
    epr.setWSAVersion( ns );
    return epr ;
  }

/**
 * Method getAddress returns the address of the Service Endpoint..
 * @return String
 */
  public String getAddress() {
    return address;
  }

  public void setAddress(final String address) {
    this.address = address;
  }

/**
 * Method getServiceName return QName of the service if available.
 * @return QName the QName of the service.
 */
  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String sn) {
     serviceName = sn;
  }

/**
 * Method getPortName return the PortName in the service if available.
 * @return String the portname in the service.
 */
  public String getPortName() {
    return portName;
  }

  public void setPortName(String portName) {
     this.portName = portName;
  }

/**
 * Method getPortType the WSDL PortType if available..
 * @return String
 */
  public String getPortType() {
    return portType;
  }

  public void setPortType(String pt) {
     portType = pt;
  }

  public Element toDOM() throws javax.xml.parsers.ParserConfigurationException {
     return toDOM(null, null);
  }
/**
 * Method toDOM.
 * @return Element The element that represents the Endpoint reference.
 * @throws ParserConfigurationException
 */
   public Element toDOM(String ns, String name) throws javax.xml.parsers.ParserConfigurationException {
     return toDOM( "ns99", ns, name );
   }

   public Element toDOM(String prefix, String ns, String name) throws javax.xml.parsers.ParserConfigurationException {
    DocumentBuilderFactory  dbf = DocumentBuilderFactory.newInstance();

    dbf.setNamespaceAware(true);
    dbf.setValidating(false);

    DocumentBuilder db = dbf.newDocumentBuilder();

    Document ehrDoc = db.newDocument();
    Element rootChild = null;
    if (ns == null) {
       rootChild = ehrDoc.createElementNS(namespace, "EndpointReference"); 
       rootChild.setPrefix("wsa");
    } else {
       rootChild = ehrDoc.createElementNS(ns, name); 
       rootChild.setPrefix( prefix );
    }
    ehrDoc.appendChild( rootChild);

    // Address
    Element child = ehrDoc.createElementNS(namespace, "Address"); 
    child.setPrefix("wsa");
    child.appendChild(ehrDoc.createTextNode(address));
    rootChild.appendChild(child);

    // PortType
    if (portType!=null) {
       Element pt = ehrDoc.createElementNS(namespace, "PortType"); 
       pt.setPrefix("wsa");
       pt.appendChild(ehrDoc.createTextNode(portType));
       rootChild.appendChild(pt);
    }
    
    // ServiceName
    if (serviceName!=null) {
       Element sn = ehrDoc.createElementNS(namespace, "ServiceName"); 
       sn.setPrefix("wsa");
       if (portName!=null) {
	  Attr attr = ehrDoc.createAttributeNS(namespace, "PortName");
	  attr.setValue(this.portName);
          sn.setAttributeNodeNS(attr); 
       }
       sn.appendChild(ehrDoc.createTextNode(serviceName));
       rootChild.appendChild(sn);
    }

    // Reference properties
    if (namespace.equals(WSAConstants.NS_WSA1)) {
      if (null != referenceProperties && !referenceProperties.isEmpty()) {
        Element refProp= ehrDoc.createElementNS(namespace, WSAConstants.EN_ReferenceProperties); 
        refProp.setPrefix("wsa");
        for (Iterator i = referenceProperties.iterator(); i.hasNext();) {
          String  refStr  = (String) i.next();
          Element refNode = XMLUtils.StringToElement( refStr );
  
          refProp.appendChild(ehrDoc.importNode(refNode, true));
        }
        rootChild.appendChild(refProp);
      }  
    }

    // Reference parameters
    if (null != referenceParameters && !referenceParameters.isEmpty()) {
      Element refProp= ehrDoc.createElementNS(namespace, WSAConstants.EN_ReferenceParameters); 
      refProp.setPrefix("wsa");
      for (Iterator i = referenceParameters.iterator(); i.hasNext();) {
        String refStr  = (String) i.next();
        Node   refNode = XMLUtils.StringToElement( refStr );

        refProp.appendChild(ehrDoc.importNode(refNode, true));
      }
      rootChild.appendChild(refProp);
    }  
    return rootChild;
  }

  public void addReferenceProperty(String str) {
    referenceProperties.add( str );
  }

  public void addReferenceProperty(Element elem) {
    addReferenceProperty( XMLUtils.ElementToString(elem) );
  }

  public void addReferenceProperty(String ns, String elemName, String value) 
    throws Exception {
    newReferenceProperty(ns, elemName, value);
  }

  // Util
  public Element newReferenceProperty(String ns, String name) throws Exception {
    return newReferenceProperty(null, ns, name, null );
  }

  public Element newReferenceProperty(String ns, String name, String value) 
      throws Exception {
    return newReferenceProperty(null, ns, name, value );
  }

  public Element newReferenceProperty(String prefix, String ns, String name, 
                                      String value)
       throws Exception {
    DocumentBuilderFactory  dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(false);

    DocumentBuilder db  = dbf.newDocumentBuilder();
    Document        doc = db.newDocument();
    Element         elem = doc.createElementNS( ns, name );
    if ( prefix == null ) prefix = "t" ;
    // if ( prefix != null ) 
    elem.setPrefix( prefix );

    if ( value != null )
      elem.appendChild( doc.createTextNode( value ) );

    referenceProperties.add( XMLUtils.ElementToString(elem) );

    return elem ;
  }

  public List getReferenceProperties() {
    return referenceProperties;
  }


  public void addReferenceParameter(String str) {
    referenceParameters.add(str);
  }

  public void addReferenceParameter(Element element) {
    addReferenceParameter( XMLUtils.ElementToString(element) );
  }

  public void addReferenceParameter(String ns, String elemName, String value) 
    throws Exception {
    newReferenceParameter(ns, elemName, value);
  }


  // Util
  public Element newReferenceParameter(String ns, String name) throws Exception {
    return newReferenceParameter(null, ns, name, null );
  }

  public Element newReferenceParameter(String ns, String name, String value) 
      throws Exception {
    return newReferenceParameter(null, ns, name, value );
  }

  public Element newReferenceParameter(String prefix, String ns, String name, 
                                      String value)
       throws Exception {
    DocumentBuilderFactory  dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(false);

    DocumentBuilder db  = dbf.newDocumentBuilder();
    Document        doc = db.newDocument();
    Element         elem = doc.createElementNS( ns, name );
    if ( prefix != null ) 
      elem.setPrefix( prefix );

    if ( value != null )
      elem.appendChild( doc.createTextNode( value ) );

    addReferenceParameter( XMLUtils.ElementToString(elem) );

    return elem ;
  }

  public List getReferenceParameters() {
    return referenceParameters;
  }

}
