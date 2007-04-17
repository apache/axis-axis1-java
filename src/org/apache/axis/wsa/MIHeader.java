package org.apache.axis.wsa ;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Enumeration;
import java.util.Vector;
import javax.xml.soap.SOAPException;
import javax.xml.namespace.QName;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.Message;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.XMLUtils;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.apache.axis.message.SOAPHeaderElement;
import javax.xml.rpc.Call;


/**
 * MIHeader class represents the Message Information Headers
 * described in the WS-Addressing specification.
 * This is not a data class.
 * This class consists of a set of utility functions to
 * create and read these headers to/from a message.
 */
public class MIHeader {
  String            namespace      = EndpointReference.getGlobalWSAVersion();
  boolean           requestMessage = false;

  String            messageID           = null ;
  Vector            relatesTo           = null ;
  EndpointReference replyTo             = null ;
  EndpointReference from                = null ;
  EndpointReference faultTo             = null ;
  EndpointReference toFault             = null ;  // See below
  EndpointReference to                  = null ;
  String            action              = null ;
  boolean           processedOnGet      = true ;
  boolean           removeOnGet         = false ;
  boolean           mustUnderstand      = false;

  // 'faultTo' is the value that will appear in the SOAP envelope.
  // 'toFault' is a special case field.  When the server receives a message
  // it generates the response MIHeader right away so that people can override
  // values as needed.  When 'To' field is set based on either the ReplyTo
  // or the anonymous URI.  However, if a Fault is generated on the server
  // the the request message's FaulTo field should be used instead of the To
  // field.  Now, we alow the user (service/handlers...) to override any of
  // the fields in the response MIHeader - we need to allow them to override
  // the To field in the Fault case as well - hence the need for two different
  // "To" fields - one for normal messages and one for faults.  Lousy name
  // but it works.

  public String toString() {
    return "{ns: " + namespace + " id:" + messageID + 
           " to:" + to + " from:" + from +
           " replyTo:" + replyTo + " faultTo:" + faultTo +
           " action:" + action + " toFault:" + toFault + "}" ;
  }

  // Potentially problem causing. Would rather remove this constructor
  public MIHeader() {
    processedOnGet = true ;
    removeOnGet = false ;
    setMessageID( "uuid:" + UUIDGenerator.getInstance().getUUID());
  }

  public MIHeader(String ns) {
    namespace = ns ;
    processedOnGet = true ;
    removeOnGet = false ;
    setMessageID( "uuid:" + UUIDGenerator.getInstance().getUUID());
  }

  /**
   * Constructor intended for client use to create an
   * MIHeader instance that will interact with a Call
   * object.
   * 
   * @param call
   * 
   * @exception AxisFault
   */
  public MIHeader(javax.xml.rpc.Call call) {
    processedOnGet = true ;
    removeOnGet = false ;
    call.setProperty( WSAConstants.REQ_MIH, this );
    setMessageID( "uuid:" + UUIDGenerator.getInstance().getUUID());
  }

  public MIHeader(javax.xml.rpc.Call call, boolean _mustUnderstand) throws AxisFault {
    mustUnderstand = _mustUnderstand ;
    processedOnGet = true ;
    removeOnGet = false ;
    call.setProperty( WSAConstants.REQ_MIH, this );
    setMessageID( "uuid:" + UUIDGenerator.getInstance().getUUID());
  }

  public void setWSAVersion(String ns) {
    namespace = ns ;
  }

  public String getWSAVersion() {
    return namespace ;
  }

  public static String getCurrentWSAVersion() throws Exception {
    MIHeader mih = null ;
    MessageContext msgContext = MessageContext.getCurrentContext();
    if ( msgContext == null ) return EndpointReference.getGlobalWSAVersion() ;
    if ( msgContext.getPastPivot() ) 
       mih = fromResponse();
    else
       mih = fromRequest();
    if ( mih == null ) return EndpointReference.getGlobalWSAVersion() ;
    return mih.getWSAVersion();
  }

  public static MIHeader fromCurrentMessage() throws Exception {
     if ( MessageContext.getCurrentContext().getPastPivot() ) 
       return fromResponse();
     else
       return fromRequest();
  }

  /**
   * Instantiate a MIHeader object from the current
   * request message. This is intended for receiving
   * service use. It may also be used by client or server
   * side handlers.
   * 
   * @return 
   */
  public static MIHeader fromRequest() throws Exception {
     MessageContext msgContext = MessageContext.getCurrentContext();
     MIHeader mih = (MIHeader)msgContext.getProperty(WSAConstants.REQ_MIH);
     if (mih == null) {
       Message msg = msgContext.getRequestMessage();
       if ( msg == null ) return null ;
       mih = new MIHeader();
       mih.removeOnGet = false ;
       mih.fromEnvelope( (SOAPEnvelope) msg.getSOAPEnvelope() );
       if ( mih.getAction() == null) return null ;
       msgContext.setProperty( WSAConstants.REQ_MIH, mih );
     }
     return mih;
  }
  
  /**
   * Instantiate an MIHeader object from the current
   * response message.
   * 
   * @return 
   */
  public static MIHeader fromResponse() throws Exception {
     MessageContext msgContext = MessageContext.getCurrentContext();
     MIHeader mih = (MIHeader) msgContext.getProperty(WSAConstants.RES_MIH);
     if (mih==null) {
       Message msg = msgContext.getResponseMessage();
       if ( msg == null ) return null ;
       mih = new MIHeader();
       mih.removeOnGet = false ;
       mih.fromEnvelope( (SOAPEnvelope) msg.getSOAPEnvelope() );
       if ( mih.getAction() == null ) return null ;
       msgContext.setProperty(WSAConstants.RES_MIH, mih );
     }
     return mih;
  }

  public MIHeader generateReplyMI() throws Exception {
    MIHeader newMIH = new MIHeader( this.getWSAVersion() );

    EndpointReference toEPR = getEffectiveReplyTo();
    newMIH.setWSAVersion( namespace );
    newMIH.setFrom( to );
    newMIH.setTo( toEPR );
    newMIH.setToFault( faultTo );
    newMIH.setAction( action + "Response" );
    newMIH.setMessageID( "uuid:" + UUIDGenerator.getInstance().getUUID());
    newMIH.addRelatesTo(messageID, "wsa:Reply" );
    return newMIH ;
  }

  public void fromEnvelope(SOAPEnvelope env) throws Exception {
    SOAPHeaderElement header = null ;
    String NSs[] = { WSAConstants.NS_WSA1, WSAConstants.NS_WSA2 };

    for ( int i = 0 ; i < NSs.length ; i++ ) {
      String ns = NSs[i];

      header = env.getHeaderByName(ns, "MessageID");
      if ( header != null ) {
        messageID = Util.getText( header.getAsDOM() );
        if ( processedOnGet ) header.setProcessed(true);
        if ( removeOnGet ) env.removeHeader( header );
      }
  
      header = env.getHeaderByName(ns, "To");
      if ( header != null ) {
        to = EndpointReference.fromLocation( Util.getText( header.getAsDOM() ),
                                             ns);
        if ( processedOnGet ) header.setProcessed(true);
        if ( removeOnGet ) env.removeHeader( header );
        namespace = ns ;
      }
  
      header = env.getHeaderByName(ns, "Action");
      if ( header != null ) {
        action = Util.getText( header.getAsDOM() );
        if ( processedOnGet ) header.setProcessed(true);
        if ( removeOnGet ) env.removeHeader( header );
      }
  
      header = env.getHeaderByName(ns, "From");
      if ( header != null ) {
        from = EndpointReference.fromDOM( header.getAsDOM() );
        if ( processedOnGet ) header.setProcessed(true);
        if ( removeOnGet ) env.removeHeader( header );
      }

      header = env.getHeaderByName(ns, "ReplyTo");
      if ( header != null ) {
        replyTo = EndpointReference.fromDOM( header.getAsDOM() );
        if ( processedOnGet ) header.setProcessed(true);
        if ( removeOnGet ) env.removeHeader( header );
      }
  
      Enumeration ee = env.getHeadersByName(ns, "RelatesTo");
      if ( ee.hasMoreElements() ) {
        relatesTo = new Vector();
        while ( ee.hasMoreElements() ) {
          header = (SOAPHeaderElement) ee.nextElement();
          String type = header.getAttributeValue(new org.apache.axis.message.PrefixedQName("","RelationshipType", ""));
          String uri  = header.getValue();
          relatesTo.add(new RelatesToProperty(uri, type));
          if ( processedOnGet ) header.setProcessed(true);
          if ( removeOnGet ) env.removeHeader( header );
        }
      }

      header = env.getHeaderByName(ns, "FaultTo");
      if ( header != null ) {
        faultTo = EndpointReference.fromDOM( header.getAsDOM() );
        if ( processedOnGet ) header.setProcessed(true);
        if ( removeOnGet ) env.removeHeader( header );
      }
    }
  }

  public void toEnvelope(SOAPEnvelope env) throws Exception {
    SOAPHeaderElement header = null ;

    if ( env.getNamespaceURI("wsa") == null )
      env.addNamespaceDeclaration("wsa", namespace );

    if ( messageID != null ) {
      header = new SOAPHeaderElement( namespace, "MessageID" );
      header.setActor( null );
      header.addTextNode(messageID);
      header.setMustUnderstand(mustUnderstand);
      env.addHeader(header);
    }

    if ( to != null ) {
      header = new SOAPHeaderElement( namespace, "To" );
      header.setActor( null );
      header.addTextNode( to.getAddress() );
      header.setMustUnderstand(mustUnderstand);
      env.addHeader(header);

      if ( namespace.equals(WSAConstants.NS_WSA1) ) {
        List refProps = to.getReferenceProperties();
        if ( refProps != null ) {
          for ( int i = 0 ; i < refProps.size() ; i++ ) {
            String elem = (String) refProps.get(i);
            SOAPHeaderElement h1 = 
              new SOAPHeaderElement(XMLUtils.StringToElement(elem));
            h1.setActor( null );
            h1.setMustUnderstand(mustUnderstand);
            env.addHeader( h1 );
          }
        }
      }

      List refParams = to.getReferenceParameters();
      if ( refParams != null ) {
        for ( int i = 0 ; i < refParams.size() ; i++ ) {
          String elem = (String) refParams.get(i);
          SOAPHeaderElement h1 = 
            new SOAPHeaderElement(XMLUtils.StringToElement(elem));
          if ( namespace.equals(WSAConstants.NS_WSA2) )
            h1.addAttribute(namespace, "IsReferenceParameter", "true");
          h1.setActor( null );
          h1.setMustUnderstand(mustUnderstand);
          env.addHeader( h1 );
        }
      }
    }

    if ( action != null ) {
      header = new SOAPHeaderElement( namespace, "Action" );
      header.setActor( null );
      header.addTextNode( action );
      header.setMustUnderstand(mustUnderstand);
      env.addHeader(header);
    }

    if ( from != null ) {
      header = new SOAPHeaderElement(from.toDOM("wsa",namespace, "From"));
      header.setActor( null );
      header.setMustUnderstand(mustUnderstand);
      env.addHeader(header);
    }

    if ( replyTo != null ) {
      header = new SOAPHeaderElement(replyTo.toDOM("wsa", namespace,
                                                   "ReplyTo"));
      header.setActor( null );
      header.setMustUnderstand(mustUnderstand);
      env.addHeader(header);
    }

    if ( relatesTo != null ) {
      for ( int i = 0 ; i < relatesTo.size() ; i++ ) {
        RelatesToProperty rtp = (RelatesToProperty) relatesTo.get(i);
        header = new SOAPHeaderElement( namespace, "RelatesTo" );
        header.setActor( null );
        if ( rtp.getType() != null && !"wsa:Reply".equals(rtp.getType()) ) {
          header.setAttribute("", "RelationshipType", rtp.getType() );
        }
        header.addTextNode( rtp.getURI() );
        header.setMustUnderstand(mustUnderstand);
        env.addHeader(header);
      }
    }

    if ( faultTo != null ) {
      header = new SOAPHeaderElement(faultTo.toDOM("wsa", namespace, 
                                                   "FaultTo"));
      header.setActor( null );
      header.setMustUnderstand(mustUnderstand);
      env.addHeader(header);
    }
  }

  public void setProcessedOnGet(boolean processedOnGet) {
     this.processedOnGet = processedOnGet;

  }
  public void setRemoveOnGet(boolean removeOnGet) {
     this.removeOnGet = removeOnGet;
  }

  //------------------------------------------------------
  // Getters and setters
  //------------------------------------------------------

  public String getMessageID() { return messageID ; }
  public void   setMessageID(String id) { messageID = id ; }

  public Vector getRelatesTo() { return relatesTo ; }
  /**
   * Set the collection of RelatesToProperties. Not additive,
   * this replaces the current collection.
   * 
   * @param v      Vector of RelatesToProperties
   */
  public void   setRelatesTo(Vector v) {
    int i = 0 ;
    for ( ; v != null && i < v.size() ; i++ ) {
      if ( i == 0 ) relatesTo = new Vector();
      RelatesToProperty rtp = (RelatesToProperty) v.get(i);
      addRelatesTo( rtp.getURI(), rtp.getType() );
    }
    if ( i == 0 ) relatesTo = null ;
  }

  public void addRelatesTo(String uri, String type) {
    if ( relatesTo == null ) relatesTo = new Vector();
    relatesTo.add( new RelatesToProperty(uri, type) );
  }

  public EndpointReference getTo() {
    /*
    if ( to == null )
      to = EndpointReference.Anonymous( namespace );
    */
    return to ; 
  }
  public EndpointReference getEffectiveTo() {
    if ( to == null )
      to = EndpointReference.Anonymous( namespace );
    return to ; 
  }
  public void   setTo(String _to) { 
    to = EndpointReference.fromLocation(_to,namespace) ;
  }
  public void   setTo(EndpointReference epr) { to = epr ; }

  public String getAction() { return action ; }
  public void   setAction(String _action) { action = _action ; }

  public EndpointReference getFrom(){ return from ; }
  public void              setFrom(EndpointReference epr) { from = epr ; }
  
  public EndpointReference getReplyTo(){ return replyTo ; }
  public void              setReplyTo(EndpointReference epr) { replyTo = epr ; }
  
  public EndpointReference getFaultTo(){ return faultTo ; }
  public void              setFaultTo(EndpointReference epr) {this.faultTo=epr;}

  public EndpointReference getToFault(){ return toFault ; }
  public void              setToFault(EndpointReference epr) {this.toFault=epr;}

  public EndpointReference getEffectiveReplyTo() throws Exception {
    if ( replyTo != null ) return getReplyTo();
    if ( from    != null ) return getFrom();
    return EndpointReference.Anonymous( namespace );
  }

}
