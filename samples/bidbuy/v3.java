package samples.bidbuy ;

import org.apache.axis.client.HTTPCall ;
import org.apache.axis.message.RPCElement ;
import org.apache.axis.message.RPCParam ;
import org.apache.axis.utils.* ;
import org.apache.axis.encoding.* ;
import org.apache.axis.* ;
import java.util.* ;
import java.math.BigDecimal ;

public class v3 implements vInterface {
  public void register(String registryURL, Service s) {
    try {
      HTTPCall call = new HTTPCall( registryURL, 
                                    "http://www.soapinterop.org/Register" );
      RPCElement body = new RPCElement( "http://www.soapinterop.org/Registry",
                                        "Register",
                                        new RPCParam[] {
                                          new RPCParam("ServiceName",
                                                       s.getServiceName()),
                                          new RPCParam("SerivceURL",
                                                       s.getServiceUrl()),
                                          new RPCParam("ServiceType",
                                                       s.getServiceType()),
                                          new RPCParam("ServiceWSDL",
                                                       s.getServiceWsdl()) } );
      call.invoke( body );
      System.out.println( "Registered" );
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }

  public void unregister(String registryURL, String name) {
    try {
      HTTPCall call = new HTTPCall( registryURL, 
                                    "http://www.soapinterop.org/Unregister" );
      RPCElement body = new RPCElement( "http://www.soapinterop.org/Registry",
                                        "Unregister",
                                        new RPCParam[] {
                                          new RPCParam("ServiceName",
                                                       name) } );
      call.invoke( body );
      System.out.println( "Unregistered" );
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }

  public void register(String registryURL, String myServiceURL ) {
    try {
      HTTPCall call = new HTTPCall( registryURL, 
                                    "http://www.soapinterop.org/Register" );
      RPCElement body = new RPCElement( "http://www.soapinterop.org/Registry",
                                        "Register",
                                        new RPCParam[] {
                                          new RPCParam("ServiceName",
                                                       "EasySoap++"),
                                          new RPCParam("SerivceURL",
                                                       myServiceURL),
                                          new RPCParam("ServiceType",
                                                       "Bid"),
                                          new RPCParam("ServiceWSDL",
                                                       "") } );
      call.invoke( body );
      System.out.println( "Registered" );
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }

  public Boolean ping(String serverURL) {
    try {
      // Debug.setDebugLevel(1);
      HTTPCall call = new HTTPCall( serverURL,
                                    "http://www.soapinterop.org/Ping" );
      call.invoke( "http://www.soapinterop.org/Bid", "Ping", null );
      System.out.println( "Ping:" + serverURL + " is still alive" );
      return( new Boolean(true) );
    }
    catch( Exception e ) {
      // e.printStackTrace();
      return( new Boolean(false) );
    }
  }

  public Vector lookupAsString(String registryURL) throws Exception {
    try {
      // Debug.setDebugLevel(3);
      ServiceDescription sd = new ServiceDescription("lookup", true );
      sd.addOutputParam("RequestForQuoteResult", 
                        SOAPTypeMappingRegistry.XSD_DOUBLE);
      HTTPCall call = new HTTPCall( registryURL,
                                  "http://www.soapinterop.org/LookupAsString" );
      call.setServiceDescription(sd);
      RPCElement body = new RPCElement( "http://www.soapinterop.org/Registry",
                                        "LookupAsString",
                                        new RPCParam[] {
                                          new RPCParam("ServiceType", 
                                                       "Bid") } );
      String res = (String) call.invoke( body );
      if ( res == null ) return( null );
      System.out.println( "LookupAsString:" + res );
      StringTokenizer  lineParser = new StringTokenizer( res, "\n" );

      Vector services = new Vector();
      while ( lineParser.hasMoreTokens() ) {
        String           line       = (String) lineParser.nextElement();
        StringTokenizer  wordParser = new StringTokenizer( line, "\t" );
        Service          service    = null ;

        for ( int i = 0 ; wordParser.hasMoreTokens() && i < 4 ; i++ )
          switch(i) {
            case 0 : service = new Service();
                     if ( services == null ) services = new Vector();
                     services.add( service );
                     service.setServiceName( (String) wordParser.nextToken());
                     break ;
            case 1 : service.setServiceUrl( (String) wordParser.nextToken());
                     break ;
            case 2 : service.setServiceType( (String) wordParser.nextToken());
                     break ;
            case 3 : service.setServiceWsdl( (String) wordParser.nextToken());
                     break ;
          }
      }
      return( services );
    }
    catch( Exception e ) {
      // e.printStackTrace();
      throw e ;
    }
  }

  public double requestForQuote(String serverURL) {
    try {
      // Debug.setDebugLevel(1);
      ServiceDescription sd = new ServiceDescription("RequestForQuote", true );
      sd.addOutputParam("RequestForQuoteResult",
                        SOAPTypeMappingRegistry.XSD_DOUBLE);
      sd.addOutputParam("Result", 
                        SOAPTypeMappingRegistry.XSD_DOUBLE);
      sd.addOutputParam("return", 
                        SOAPTypeMappingRegistry.XSD_DOUBLE);
      HTTPCall call = new HTTPCall( serverURL,
                                  "http://www.soapinterop.org/RequestForQuote");
      call.setServiceDescription( sd );
      RPCElement body = new RPCElement( "http://www.soapinterop.org/Bid",
                                        "RequestForQuote",
                                        new RPCParam[] {
                                          new RPCParam( "ProductName",
                                                        "widget"),
                                          new RPCParam( "Quantity", 
                                                        new Integer(10) ) } );
      Object r = call.invoke( body );
      System.err.println("res type: " + r.getClass().getName() );
      System.err.println("res: " + r );
      if ( r instanceof Float ) r = ((Float)r).toString();
      if ( r instanceof String ) r = new Double((String) r);
      Double res = (Double) r ;
      System.out.println( "RequestForQuote:" + res );
      return( res.doubleValue() );
    }
    catch( Exception e ) {
      e.printStackTrace();
      return( 0.0 );
    }
  }

  public String simpleBuy(String serverURL, int quantity ) {
    try {
      ServiceDescription sd = new ServiceDescription("SimpleBuy", true );
      sd.addOutputParam("SimpleBuyResult",
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("Result", 
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("return", 
                        SOAPTypeMappingRegistry.XSD_STRING);
      HTTPCall call = new HTTPCall( serverURL,
                                  "http://www.soapinterop.org/SimpleBuy" );
      RPCElement  body = new RPCElement( "http://www.soapinterop.org/Bid",
                                         "SimpleBuy", 
                                         new RPCParam[] {
                                           new RPCParam("Address",
                                                        "123 Main St."),
                                           new RPCParam("ProductName",
                                                        "Widget"),
                                           new RPCParam("Quantity",
                                                        new Integer(quantity))});
      String res = (String) call.invoke( body );
      System.out.println( "Buy:" + res );
      return( res );
    }
    catch( Exception e ) {
      e.printStackTrace();
      return( "Error" + e.toString() );
    }
  }

  public String buy(String serverURL, int quantity, int numItems) {
    try {
      int      i ; 
      ServiceDescription sd = new ServiceDescription("SimpleBuy", true );
      sd.addOutputParam("BuyResult",
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("Result", 
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("return", 
                        SOAPTypeMappingRegistry.XSD_STRING);
      HTTPCall call = new HTTPCall( serverURL,
                                  "http://www.soapinterop.org/Buy" );


      // register the PurchaseOrder class
      QName poqn = new QName("http://www.soapinterop.org/Bid",
                             "PurchaseOrder");
      Class cls = PurchaseOrder.class;
      call.addSerializer(cls, poqn, new BeanSerializer(cls));
      call.addDeserializerFactory(poqn, cls, BeanSerializer.getFactory(cls));

      // register the Address class
      QName aqn = new QName("http://www.soapinterop.org/Bid", "Address");
      cls = Address.class;
      call.addSerializer(cls, aqn, new BeanSerializer(cls));
      call.addDeserializerFactory(aqn, cls, BeanSerializer.getFactory(cls));

      // register the LineItem class
      QName liqn = new QName("http://www.soapinterop.org/Bid", "LineItem");
      cls = LineItem.class;
      call.addSerializer(cls, liqn, new BeanSerializer(cls));
      call.addDeserializerFactory(liqn, cls, BeanSerializer.getFactory(cls));

      LineItem[]     lineItems = new LineItem[numItems];
      
      
      for ( i = 0 ; i < numItems ; i++ )
        lineItems[i] = new LineItem("Widget"+i, quantity, new BigDecimal(100));

      PurchaseOrder  po = new PurchaseOrder( "PO1", 
                                             new Date(),
                                             new Address("Mr Big",
                                                         "40 Wildwood Lane",
                                                         "Weston",
                                                         "CT",
                                                         "06883"),
                                             new Address("Mr Big's Dad",
                                                         "40 Wildwood Lane",
                                                         "Weston",
                                                         "CT",
                                                         "06883"),
                                             lineItems );

      RPCElement  body = new RPCElement( "http://www.soapinterop.org/Bid",
                                         "Buy", 
                                         new RPCParam[]{
                                           new RPCParam("PO",
                                                        po)} );
      String res = (String) call.invoke( body );
      System.out.println( "Buy:" + res );
      return( res );
    }
    catch( Exception e ) {
      e.printStackTrace();
      return( null );
    }
  }

}
