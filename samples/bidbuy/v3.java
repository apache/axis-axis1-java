package samples.bidbuy ;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.BeanSerializer;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.transport.http.HTTPConstants;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.namespace.QName;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

public class v3 implements vInterface {
  public void register(String registryURL, samples.bidbuy.Service s) 
                 throws Exception {
    try {
      Service  service = new Service();
      Call     call    = (Call) service.createCall();

      call.setTargetEndpointAddress( new URL(registryURL) );
      call.setProperty( Call.NAMESPACE, "http://www.soapinterop.org/Register");
      call.setOperationName( "Register" );
      call.addParameter("ServiceName", XMLType.XSD_STRING, ParameterMode.PARAM_MODE_IN);
      call.addParameter("ServiceUrl", XMLType.XSD_STRING, ParameterMode.PARAM_MODE_IN);
      call.addParameter("ServiceType", XMLType.XSD_STRING, ParameterMode.PARAM_MODE_IN);
      call.addParameter("ServiceWSDL", XMLType.XSD_STRING, ParameterMode.PARAM_MODE_IN);
      
      call.invoke( new Object[] { s.getServiceName(), s.getServiceUrl(),
                                  s.getServiceType(), s.getServiceWsdl() } );
    }
    catch( Exception e ) {
      e.printStackTrace();
      throw e ;
    }
  }

  public void unregister(String registryURL, String name) throws Exception {
    try {
      Service  service = new Service();
      Call     call    = (Call) service.createCall();

      call.setTargetEndpointAddress( new URL(registryURL) );
      call.setProperty(Call.NAMESPACE, "http://www.soapinterop.org/Unregister");
      call.setOperationName( "Unregister" );
      call.addParameter( "ServiceName", XMLType.XSD_STRING, ParameterMode.PARAM_MODE_IN);
      call.invoke( new Object[] { name } );
    }
    catch( Exception e ) {
      e.printStackTrace();
      throw e ;
    }
  }

  public Boolean ping(String serverURL) throws Exception {
    try {
      Service  service = new Service();
      Call     call    = (Call) service.createCall();

      call.setTargetEndpointAddress( new URL(serverURL) );
      call.setProperty( Call.NAMESPACE, "http://www.soapinterop.org/Bid");
      call.setProperty( HTTPConstants.MC_HTTP_SOAPACTION, 
                        "http://www.soapinterop.org/Ping" );
      call.setOperationName( "Ping" );
      call.invoke( (Object[]) null );
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
      Service  service = new Service();
      Call     call    = (Call) service.createCall();

      call.setTargetEndpointAddress( new URL(registryURL) );
      call.setProperty( Call.NAMESPACE, "http://www.soapinterop.org/Registry");
      call.setProperty( HTTPConstants.MC_HTTP_SOAPACTION, 
                        "http://www.soapinterop.org/LookupAsString" );
      call.setOperationName( "LookupAsString" );
      call.addParameter( "ServiceType", XMLType.XSD_STRING, ParameterMode.PARAM_MODE_IN);
      call.setReturnType( XMLType.XSD_DOUBLE );

      String res= (String) call.invoke( new Object[] { "Bid" } );

      if ( res == null ) return( null );
      StringTokenizer  lineParser = new StringTokenizer( res, "\n" );

      Vector services = new Vector();
      while ( lineParser.hasMoreTokens() ) {
        String                  line       = (String) lineParser.nextElement();
        StringTokenizer         wordParser = new StringTokenizer( line, "\t" );
        samples.bidbuy.Service  s          = null ;

        for ( int i = 0 ; wordParser.hasMoreTokens() && i < 4 ; i++ )
          switch(i) {
            case 0 : s = new samples.bidbuy.Service();
                     if ( services == null ) services = new Vector();
                     services.add( s );
                     s.setServiceName( (String) wordParser.nextToken());
                     break ;
            case 1 : s.setServiceUrl( (String) wordParser.nextToken());
                     break ;
            case 2 : s.setServiceType( (String) wordParser.nextToken());
                     break ;
            case 3 : s.setServiceWsdl( (String) wordParser.nextToken());
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

      Service  service = new Service();
      Call     call    = (Call) service.createCall();

      call.setTargetEndpointAddress( new URL(serverURL) );
      call.setProperty( Call.NAMESPACE, "http://www.soapinterop.org/Bid" );
      call.setOperationName( "RequestForQuote" );
      call.setReturnType( XMLType.XSD_DOUBLE );
      call.setProperty( HTTPConstants.MC_HTTP_SOAPACTION, 
                        "http://www.soapinterop.org/RequestForQuote" );
      call.addParameter( "ProductName", XMLType.XSD_STRING, ParameterMode.PARAM_MODE_IN);
      call.addParameter( "Quantity", XMLType.XSD_INT, ParameterMode.PARAM_MODE_IN);
      Object r = call.invoke( new Object[] { "widget", new Integer(10) } );

/*
      sd.addOutputParam("RequestForQuoteResult",
                        SOAPTypeMappingRegistry.XSD_DOUBLE);
      sd.addOutputParam("Result",
                        SOAPTypeMappingRegistry.XSD_DOUBLE);
      sd.addOutputParam("return",
                        SOAPTypeMappingRegistry.XSD_DOUBLE);
*/

      // ??? if ( r instanceof Float ) r = ((Float)r).toString();
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
      Service  service = new Service();
      Call     call    = (Call) service.createCall();

      call.setTargetEndpointAddress( new URL(serverURL) );
      call.setProperty( Call.NAMESPACE, "http://www.soapinterop.org/Bid" );
      call.setProperty( HTTPConstants.MC_HTTP_SOAPACTION, 
                        "http://www.soapinterop.org/SimpleBuy" );
      call.setOperationName( "SimpleBuy" );
      call.setReturnType( XMLType.XSD_STRING );
      call.addParameter( "Address", XMLType.XSD_STRING, ParameterMode.PARAM_MODE_IN );
      call.addParameter( "ProductName", XMLType.XSD_STRING, ParameterMode.PARAM_MODE_IN);
      call.addParameter( "Quantity", XMLType.XSD_INT, ParameterMode.PARAM_MODE_IN );
      
      String res = (String) call.invoke(new Object[] { "123 Main St.",
                                                       "Widget",
                                                       new Integer(quantity)});

      /* sd.addOutputParam("SimpleBuyResult",
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("Result",
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("return",
                        SOAPTypeMappingRegistry.XSD_STRING); */

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

      Service  service = new Service();
      Call     call    = (Call) service.createCall();

      call.setTargetEndpointAddress( new URL(serverURL) );
      call.setProperty( Call.NAMESPACE, "http://www.soapinterop.org/Bid" );
      call.setProperty( HTTPConstants.MC_HTTP_SOAPACTION, 
                        "http://www.soapinterop.org/Buy" );
      call.setReturnType( XMLType.XSD_STRING );

      /* sd.addOutputParam("BuyResult",
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("Result",
                        SOAPTypeMappingRegistry.XSD_STRING);
      sd.addOutputParam("return",
                        SOAPTypeMappingRegistry.XSD_STRING); */


      // register the PurchaseOrder class
      QName poqn = new QName("http://www.soapinterop.org/Bid", "PurchaseOrder");
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

      call.addParameter( "PO", poqn, ParameterMode.PARAM_MODE_IN );
      call.setOperationName( "Buy" );

      String res = (String) call.invoke( new Object[] { po } );

      return( res );
    }
    catch( Exception e ) {
      e.printStackTrace();
      throw e ;
    }
  }

}

