package org.apache.axis.wsa;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import java.util.Vector;
import java.net.URL;
import javax.xml.namespace.QName;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;

public class WSAHandler { // extends BasicHandler {

   public static void invoke(MessageContext msgContext) throws Exception {
      boolean onClient = true;
      boolean request  = true;


      // Request or response?
      if (msgContext.getCurrentContext().getPastPivot()) {
         request = false;
      } else {
         request = true;
      }

      // Determine if we're on the client or server
      if (msgContext.getCurrentContext().isClient()) {
         onClient = true;
      } else {
         onClient = false;
      }

      if (onClient && request ) {
        // Process client request
        processClientRequest(msgContext);
      } else if (onClient && !request) {
        // Process client response
        processClientResponse(msgContext);
      } else if (!onClient && request) {
        // Process server request
        processServerRequest(msgContext);
      } else if (!onClient && !request) {
        // Process server response
        processServerResponse(msgContext);
      }
   }

   static void processClientRequest(MessageContext msgContext) throws Exception {
     MIHeader mih = (MIHeader)msgContext.getProperty(WSAConstants.REQ_MIH);
     Message  msg = msgContext.getRequestMessage();

     if ( mih == null ) {
       String wsaVersion = (String) msgContext.getProperty( "useWSA" );
       if ( wsaVersion == null ) 
         wsaVersion = (String) msgContext.getAxisEngine().getOption("useWSA");
       if ( wsaVersion == null ) return ;
       if ( wsaVersion.equals("false") ) return ;

       if ( !WSAConstants.NS_WSA1.equals(wsaVersion) &&
            !WSAConstants.NS_WSA2.equals(wsaVersion) )
         wsaVersion = WSAConstants.NS_WSA ;
       
       msgContext.setProperty(WSAConstants.REQ_MIH, mih = new MIHeader());
       mih.setWSAVersion( wsaVersion );
       mih.setRemoveOnGet(true);
       mih.fromEnvelope( msg.getSOAPEnvelope() );
     }

     if ( mih.getMessageID() == null )
       mih.setMessageID( "uuid:" + UUIDGenerator.getInstance().getUUID() );

     if ( mih.getTo() == null )
       mih.setTo( msgContext.getStrProp( msgContext.TRANS_URL ) );
     if ( mih.getAction() == null )
       mih.setAction( msgContext.getSOAPActionURI() );

     mih.toEnvelope( msg.getSOAPEnvelope() );
   }

   static void processClientResponse(MessageContext msgContext) throws Exception {
      Message msg = msgContext.getResponseMessage();
      if ( msg == null ) return ;

      MIHeader mih = new MIHeader();

      SOAPEnvelope env = msg.getSOAPEnvelope();
      if ( env == null ) return ;

      mih.fromEnvelope( env );
      msgContext.setProperty( WSAConstants.RES_MIH, mih );
   }

   static void processServerRequest(MessageContext msgContext) throws Exception {
      MIHeader mih = MIHeader.fromRequest();

      // If mih is null then assume there were no headers and just leave
      if ( mih == null ) return ;
      String to = null ;
      if ( mih.getTo() != null )
        mih.getTo().getAddress();

      if ( to != null ) {
         // need to parse stuff out
         int i = to.lastIndexOf("/");
         to = to.substring(i+1);
         msgContext.setTargetService( to );
      }

      if ( mih.getAction() != null )
         msgContext.setSOAPActionURI( mih.getAction() );
        
      // If there's a relates to then assume this is a one-way message
      // and we should not try to send back a reply
      if ( mih.getRelatesTo() != null && mih.getRelatesTo().size() != 0 )
        msgContext.setIsOneWay(true);

      // Setup the response MIHeader now so that people can change it
      // if they need to w/o us overriding it
      MIHeader resMIH = mih.generateReplyMI();
      msgContext.setProperty(WSAConstants.RES_MIH, resMIH);
   }

   static void processServerResponse(MessageContext msgContext) throws Exception {
      MIHeader  reqMIH, resMIH ;

      Message msg = msgContext.getResponseMessage();
      if ( msg == null ) return ;

      reqMIH = (MIHeader) msgContext.getProperty(WSAConstants.REQ_MIH);
      if ( reqMIH == null ) return ;

      resMIH = (MIHeader) msgContext.getProperty(WSAConstants.RES_MIH);
      if ( resMIH == null ) {
        resMIH = reqMIH.generateReplyMI();
        msgContext.setProperty(WSAConstants.RES_MIH, resMIH );
      }

      if ( resMIH.getToFault() != null ) {
        SOAPBodyElement elem = msg.getSOAPEnvelope().getFirstBody();
        if ( elem != null ) {
          QName qn = elem.getQName();
          if ( qn.equals(msgContext.getSOAPConstants().getFaultQName()) ) 
            resMIH.setTo( resMIH.getToFault() );
        }
      }

      resMIH.toEnvelope( msg.getSOAPEnvelope() );
   }

   // List of properties to copy from one msgcontext to another
   private static String[]  copyProps = {
     "SignIt",
     "SCAnchorID",
     "RequireSigning" };

   static public void sendResponse( MessageContext msgContext ) 
       throws Exception {
    
     if ( msgContext.getProperty(WSAConstants.WSA_DONT_REROUTE) != null )
       return ;

     MIHeader resMIH = null ;
     Message  msg    = null ;
     EndpointReference to = null ;
     
     resMIH = MIHeader.fromResponse();
     if ( resMIH == null ) return ;

     to = resMIH.getTo();

     msg = msgContext.getResponseMessage();

     if ( msg==null || to==null || to.isAnonymous() )
       return ;

     Vector   relates    = (Vector) resMIH.getRelatesTo();

     for ( int i = 0 ; relates != null && i < relates.size() ; i++ ) {
       RelatesToProperty rtp = (RelatesToProperty) relates.get(i);
       if ( rtp.getType() == null || rtp.getType().equals("wsa:Reply") ) {
         // process replyTo by invoking a service
         Service service = new Service();
         Call    call    = (Call) service.createCall();

         for ( int j = 0 ; j < copyProps.length ; j++ ) {
           Object obj = msgContext.getProperty( copyProps[j] );
           if ( obj == null ) continue ;
           call.setProperty( copyProps[j], obj );
         }

         call.setSOAPVersion( msgContext.getSOAPConstants() );
         call.setTargetEndpointAddress(to.getAddress() );
         call.setRequestMessage(msg);
         call.setSOAPActionURI( msgContext.getSOAPActionURI() );

         // need to set the response message to null now, not later like we
         // used to, so that if the call.invoke() call fails Axis will create a
         // response message for the fault.  Axis has this interesting
         // bug/feature where if there's a fault and there's already a
         // response message then it WILL NOT replace it with the fault.
         msgContext.setResponseMessage( null );

         call.invoke();
       }
     }
   }

   static public void fixAction(MessageContext msgContext) throws Exception {
     MIHeader resMI = MIHeader.fromResponse();
     if ( resMI != null ) {
       resMI.setAction( resMI.getWSAVersion() + "/fault" );
       msgContext.setSOAPActionURI( resMI.getAction() );
     }
     else
       msgContext.setSOAPActionURI( EndpointReference.staticNS + "/fault" );
   }
}
