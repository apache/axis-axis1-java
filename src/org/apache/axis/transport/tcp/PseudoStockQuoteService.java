package org.apache.axis.transport.tcp;

public class PseudoStockQuoteService {
  public float getQuote (String symbol) throws Exception {
    // get a real (delayed by 20min) stockquote from
    // http://www.xmltoday.com/examples/stockquote/. The IP addr
    // below came from the host that the above form posts to ..

    if ( symbol.equals("XXX") ) return( (float) 55.25 );
    throw new IllegalArgumentException("Symbol not known");
  }
}
  
  

