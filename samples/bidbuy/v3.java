package samples.bidbuy ;

import org.apache.axis.client.ServiceClient;
import org.apache.axis.encoding.BeanSerializer;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.transport.http.HTTPTransport;
import javax.xml.rpc.namespace.QName;

import java.math.BigDecimal;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

public class v3 implements vInterface {
  public void register(String registryURL, Service s) throws Exception {
    try {
      ServiceClient call = new ServiceClient
            (new HTTPTransport(registryURL, "http://www.soapinterop.org/Register"));
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
    }
    catch( Exception e ) {
      e.printStackTrace();
      throw e ;
    }
  }

  public void unregister(String registryURL, String name) throws Exception {
    try {
      ServiceClient call = new ServiceClient
            (new HTTPTransport(registryURL, "http://www.soapinterop.org/Unregister"));
      RPCElement body = new RPCElement( "http://www.soapinterop.org/Registry",
                                        "Unregister",
                                        new RPCParam[] {
                                          new RPCParam("ServiceName",
                                                       name) } );
      call.invoke( body );
    }
    catch( Exception e ) {
      e.printStackTrace();
      throw e ;
    }
  }

  public Boolean ping(String serverURL) throws Exception {
    try {
      ServiceClient call = new ServiceClient(new HTTPTransport());
      call.set(HTTPTransport.URL, serverURL);
      call.set(HTTPTransport.ACTION, "http://www.soapinterop.org/Ping");
      call.invoke( "http://www.soapinterop.org/Bid", "Ping", null );
      return( new Boolean(true) );
    }
    catch( Exception e ) {
      e.printStackTrace();
      throw e ;
    }
  }

  public Vector lookupAsString(String registryURL) throws Exception
  {
    try {
      ServiceDescription sd = new ServiceDescription("lookup", true );
      sd.addOutputParam("RequestForQuoteResult",
                        SOAPTypeMappingRegistry.XSD_DOUBLE);
      ServiceClient call = new ServiceClient(new HTTPTransport());
      call.set(HTTPTransport.URL, registryURL);
      call.set(HTTPTransport.ACTION, "http://www.soapinterop.org/LookupAsString");
      call.setServiceDescription(sd);
      RPCElement body = new RPCElement( "http://www.soapinterop.org/Registry",
                                        "LookupAsString",
                                        new RPCParam[] {
                                          new RPCParam("ServiceType",
                                                       "Bid") } );
      String res = (String) call.invoke( body );
      if ( res == null ) return( null );
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
      e.printStackTrace();
      throw e ;
    }
  }

  public double requestForQuote(String serverURL) throws Exception {
    try {
      ServiceDescription sd = new ServiceDescription("RequestForQuote", true );
      sd.addOutputParam("RequestForQuoteResult",
                        SOAPTypeMappingRegistry.XSD_DOUBLE);
      sd.addOutputParam("Result",
                        SOAPTypeMappingRegistry.XSD_DOUBLE);
      sd.addOutputParam("return",
                        SOAPTypeMappingRegistry.XSD_DOUBLE);
      ServiceClient call =
            new ServiceClient(new HTTPTransport
                                  (serverURL, "http://www.soapinterop.org/RequestForQuote"));
      call.setServiceDescription( sd );
      RPCElement body = new RPCElement( "http://www.soapinterop.org/Bid",
                                        "RequestForQuote",
                                        new RPCParam[] {
                                          new RPCParam( "ProductName",
                                                        "widget"),
                                          new RPCParam( "Quantity",
                                                        new Integer(10) ) } );
      Object r = call.invoke( body );
      if ( r instanceof Float ) r = ((Float)r).toString();
      if ( r instanceof String ) r = new Double((String) r);
      Double res = (Double) r ;
      return( res.doubleValue() );
    }
    catch( Exception e ) {
      e.printStackTrace();
      throw e ;
    }
  }

  public String simpleBuy(String serverURL, int quantity ) throws Exception {
    try {
      ServiceDescription sd = new ServiceDescription("SimpleBuy", true );
      sd.addOutputParam("SimpleBuyResult",
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("Result",
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("return",
                        SOAPTypeMappingRegistry.XSD_STRING);
      ServiceClient call =
            new ServiceClient(new HTTPTransport
                                  (serverURL, "http://www.soapinterop.org/SimpleBuy"));
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
      return( res );
    }
    catch( Exception e ) {
      e.printStackTrace();
      throw e ;
    }
  }

  public String buy(String serverURL, int quantity, int numItems, double price)
      throws Exception
  {
    try {
      int      i ;
      ServiceDescription sd = new ServiceDescription("SimpleBuy", true );
      sd.addOutputParam("BuyResult",
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("Result",
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("return",
                        SOAPTypeMappingRegistry.XSD_STRING);
      ServiceClient call = new ServiceClient(new HTTPTransport());
      call.set(HTTPTransport.URL, serverURL);
      call.set(HTTPTransport.ACTION, "http://www.soapinterop.org/Buy");


      // register the PurchaseOrder class
      QName poqn = new QName("http://www.soapinterop.org/Bid",
                             "PurchaseOrder");
      Class cls = PurchaseOrder.class;
      call.addSerializer(cls, poqn, new BeanSerializer(cls));
      call.addDeserializerFactory(poqn, cls, BeanSerializer.getFactory());

      // register the Address class
      QName aqn = new QName("http://www.soapinterop.org/Bid", "Address");
      cls = Address.class;
      call.addSerializer(cls, aqn, new BeanSerializer(cls));
      call.addDeserializerFactory(aqn, cls, BeanSerializer.getFactory());

      // register the LineItem class
      QName liqn = new QName("http://www.soapinterop.org/Bid", "LineItem");
      cls = LineItem.class;
      call.addSerializer(cls, liqn, new BeanSerializer(cls));
      call.addDeserializerFactory(liqn, cls, BeanSerializer.getFactory());

      LineItem[]     lineItems = new LineItem[numItems];
      
      
      for ( i = 0 ; i < numItems ; i++ )
        lineItems[i] = new LineItem("Widget"+i,quantity,new BigDecimal(price));

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
      return( res );
    }
    catch( Exception e ) {
      e.printStackTrace();
      throw e ;
    }
  }

}

