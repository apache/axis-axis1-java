package org.apache.axis.wsa ;

import java.io.Serializable;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
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
  protected Vector  metadata = null ;
  protected Vector  extras = null ;

  EndpointReference( final EndpointReference epr){
    this.address = epr.address;
    this.portType = epr.portType; //rrfoo still missing from serializers!
    this.serviceName = epr.serviceName;
    this.portName = epr.portName;
    this.referenceProperties = new java.util.LinkedList(epr.referenceProperties);
    this.referenceParameters = new java.util.LinkedList(epr.referenceParameters);
    this.metadata = new Vector();
    for ( int i = 0 ; epr.extras != null && i < epr.metadata.size() ; i++ ) 
      metadata.add(  ((Element)epr.metadata.get(i)).cloneNode( true ) );

    this.extras = new Vector();
    for ( int i = 0 ; epr.extras != null && i < epr.extras.size() ; i++ ) 
      extras.add(  ((Element)epr.extras.get(i)).cloneNode( true ) );
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
    if ( this.metadata != null ) {
      for ( int i = 0 ; i < this.metadata.size() ; i++ ) {
        Element e = (Element) this.metadata.get(i);
        e = (Element) e.cloneNode( true );
        newObj.addExtraElement( e );
      }
    }
    if ( this.extras != null ) {
      for ( int i = 0 ; i < this.extras.size() ; i++ )  {
        Element e = (Element) this.extras.get(i);
        e = (Element) e.cloneNode( true );
        newObj.addExtraElement( e );
      }
    }
    return newObj ;
  }

  public String toString() {
    return "" + getClass() +"@"+ hashCode() + "ns: " + namespace + 
           " address: '" +address + 
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

  public static EndpointReference Anonymous() { 
    return Anonymous( staticNS );
  }

  public static EndpointReference Anonymous(String ns) { 
    EndpointReference newEPR = newInstance( ns );

    if ( ns.equals(WSAConstants.NS_WSA1) )
      newEPR.setAddress( ns + "/role/anonymous" );
    else
      newEPR.setAddress( ns + "/anonymous" );
    return newEPR ;
  }

  public boolean isAnonymous() {
    if ( namespace.equals(WSAConstants.NS_WSA1) )
      return this.address.equals( namespace + "/role/anonymous" );
    return this.address.equals( namespace + "/anonymous" );
  }

  public static EndpointReference None() { 
    return None( staticNS );
  }

  public static EndpointReference None(String ns) { 
    EndpointReference newEPR = newInstance( ns );

    if ( ns.equals(WSAConstants.NS_WSA1) )  // should fault or something
      newEPR.setAddress( ns + "/role/none" );
    else
      newEPR.setAddress( ns + "/none" );
    return newEPR ;
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
     String NS1 = WSAConstants.NS_WSA1 ;
     String NS2 = WSAConstants.NS_WSA2 ;

     String NSs[] = new String[] { WSAConstants.NS_WSA1, WSAConstants.NS_WSA2 };

     NodeList list = el.getChildNodes();
     for ( int j = 0 ; list != null && j < list.getLength() ; j++ ) {
       Node node = list.item( j );
       if ( node.getNodeType() != Node.ELEMENT_NODE ) continue ;
       Element e = (Element) node ;
       String  ns = e.getNamespaceURI();
       String  ln = e.getLocalName();

       if ( "Address".equals(ln) && (NS1.equals(ns) || NS2.equals(ns)) ) {
         er.setAddress( Util.getText( e ) );
         er.setWSAVersion( ns );
       }
       else if ( "PortType".equals(ln) && (NS1.equals(ns) || NS2.equals(ns)) ) {
         er.setPortType( Util.getText( e ) );
       }
       else if ("ServiceName".equals(ln) && (NS1.equals(ns) || NS2.equals(ns))){
         er.setServiceName( Util.getText( e ) );
         er.setPortName(e.getAttributeNS(ns, "PortName"));
       }
       else if ( "ReferenceProperties".equals(ln) &&
                 (NS1.equals(ns) || NS2.equals(ns)) ) {
         NodeList l1 = e.getChildNodes();
         for ( int k = 0 ; k < l1.getLength() ; k++ ) {
           Node n1 = l1.item( k );
           if ( n1.getNodeType() != Node.ELEMENT_NODE ) continue ;
           er.addReferenceProperty( (Element) n1 );
         }
       }
       else if ( "ReferenceParameters".equals(ln) &&
                 (NS1.equals(ns) || NS2.equals(ns)) ) {
         NodeList l1 = e.getChildNodes();
         for ( int k = 0 ; k < l1.getLength() ; k++ ) {
           Node n1 = l1.item( k );
           if ( n1.getNodeType() != Node.ELEMENT_NODE ) continue ;
           er.addReferenceParameter( (Element) n1 );
         }
       }
       else if ( "Metadata".equals(ln) && NS2.equals(ns) ) {
         NodeList l = e.getChildNodes();
         for ( int i = 0 ; i < l.getLength() ; i++ ) {
           Node n = l.item( i );
           if ( n.getNodeType() != Node.ELEMENT_NODE ) continue ;
           if ( er.metadata == null ) er.metadata = new Vector();
           er.metadata.add( n.cloneNode(true) );
         }
       }
       else {
         if ( er.extras == null ) er.extras = new Vector();
         er.extras.add( e.cloneNode(true) );
       }
     }
     if ( er.getAddress() == null ) {
       String tmp = "Missing Address in EPR: " + XMLUtils.ElementToString(el);
       throw new Exception( tmp );
     }
     return er ;
  }

  public void addExtraElement(Element e) {
    if ( this.extras == null ) this.extras = new Vector();
    this.extras.add( e.cloneNode(true) );
  }

  public void addMetadataElement(Element e) {
    if ( this.metadata == null ) this.metadata = new Vector();
    this.metadata.add( e.cloneNode(true) );
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

  public Vector getExtra() {
    return extras ;
  }

  public Vector getMetadata() {
    return metadata ;
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

    if (metadata != null) {
      Element md = null ;

      if (namespace.equals(WSAConstants.NS_WSA2)) {
        md = ehrDoc.createElementNS(namespace, "Metadata");
        md.setPrefix("wsa");
        rootChild.appendChild( md );
      }
      else
        md = rootChild ;

      for ( int i = 0 ; i < metadata.size() ; i++ ) {
        Element e = (Element) metadata.get(i);
        md.appendChild( ehrDoc.importNode( e, true ) );
      }
    }

    if ( extras != null ) {
      for ( int i = 0 ; i < extras.size() ; i++ ) {
        Element e = (Element) extras.get(i);
        rootChild.appendChild( ehrDoc.importNode( e, true ) );
      }
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
    if ( prefix != null ) 
      elem.setPrefix( prefix );
    else
      elem.setPrefix( "t" );

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
    else 
      elem.setPrefix( "t" );

    if ( value != null )
      elem.appendChild( doc.createTextNode( value ) );

    addReferenceParameter( XMLUtils.ElementToString(elem) );

    return elem ;
  }

  public List getReferenceParameters() {
    return referenceParameters;
  }

}
