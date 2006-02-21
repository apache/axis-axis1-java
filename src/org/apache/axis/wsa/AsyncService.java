package org.apache.axis.wsa ;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import org.w3c.dom.Element ;
import org.apache.axis.MessageContext ;
import org.apache.axis.persistence.Persistence ;
import org.apache.axis.wsa.MIHeader ;
import org.apache.axis.wsa.RelatesToProperty ;
import org.apache.axis.Message ;

public class AsyncService {
  static private Persistence store = null ;

  public Element[] process(Element[] bodies) throws Exception {
    // Just stick the msg in the persistence
    MessageContext msgContext = MessageContext.getCurrentContext();
    msgContext.setProperty( "ASYNCRESPONSE", "true" );
    msgContext.setIsOneWay(true);

    if ( store == null ) {
      String cls = (String) msgContext.getAxisEngine()
                                      .getOption("asyncPersistence");
      if ( cls == null || "".equals(cls) )
        cls = "org.apache.axis.persistence.JVMPersistence" ;
      store = (Persistence) Class.forName( cls ).newInstance();
    }

    String msgID = null ;
    Vector rv = MIHeader.fromCurrentMessage().getRelatesTo();
    if ( rv == null ) return null ;
    for ( int i = 0 ; i < rv.size(); i ++ ) {
      RelatesToProperty rtp = (RelatesToProperty) rv.get(i);
      msgID = rtp.getURI();
      break ;
    }
    if ( msgID == null ) return null ;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream    oos  = new ObjectOutputStream(baos);
    msgContext.toStream( oos );

    store.put( "asyncMsg", msgID, baos.toByteArray() );

    oos.close();
    baos.close();
    return null ;
  }

  static public MessageContext getResponseContext(String reqMsgID) 
      throws Exception {
    if ( store == null ) return null ;

    Object obj = store.remove( "asyncMsg", reqMsgID );
    if ( obj == null ) return null ;

    MessageContext msgContext = MessageContext.getCurrentContext();
    MessageContext newMC = new MessageContext( msgContext.getAxisEngine() );
    ByteArrayInputStream bais = new ByteArrayInputStream( (byte[]) obj );
    ObjectInputStream    ois  = new ObjectInputStream( bais );
    newMC.fromStream( ois );
    ois.close();
    bais.close();
    return newMC ;
  }
}
