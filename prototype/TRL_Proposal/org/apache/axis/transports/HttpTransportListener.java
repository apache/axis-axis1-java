package org.apache.axis.transports;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.Handler;
import org.apache.axis.ChainContainer;
import org.apache.axis.MessageContext;
import org.apache.axis.SOAPException;
import org.apache.axis.TransportListener;

import org.apache.axis.handlers.Signer;
import org.apache.axis.handlers.Verifier;

import org.apache.axis.message.SOAPDocument;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.message.impl.SOAPDocumentImpl ;
import org.apache.axis.util.Logger ;
import org.apache.axis.util.xml.DOMConverter ;

public class HttpTransportListener extends HttpServlet 
    implements TransportListener {

    private Handler nextHandler;

    public void setNextHandler(Handler handler) {
        this.nextHandler = handler;
    }

    public void init() {
      // get a Configurator class name form servlet init parameter
      // The following should be performed in the Configurator.
        ChainContainer cc = new ChainContainer();
        this.setNextHandler(cc);
        cc.addHandler(new Verifier());
        cc.addHandler(new samples.server.ReceiptResponder());
          // Hard-coded at this moment
        //cc.addHandler(new Signer());
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException {
        res.setContentType("text/html");
        res.getWriter().println( "In doGet" );
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException {
        ServletConfig  config  = getServletConfig();
        ServletContext context = config.getServletContext();
        HttpSession session = req.getSession();

        // Set-up the Axis objects...
        MessageContext  msgCntxt = null;

        // Read in the incoming SOAP message and set it as the incoming msg

        try {
            Reader in = req.getReader() ;
            SOAPDocument doc = new SOAPDocumentImpl(DOMConverter.toDOM(in));
            msgCntxt = new MessageContext(doc);
        } catch (Exception e) { // Invalid XML
            e.printStackTrace();
            return ;
        }
        // Invoke the next Chainable...
        this.nextHandler.invoke(msgCntxt);

        // Grab the output...
        SOAPDocument respDoc = msgCntxt.getMessage();

        // Send it back along the wire...
        try {
            res.setContentType("text/xml; charset=UTF-8") ;
            res.getWriter().print( respDoc.getEnvelope().toXML() );
        } catch (Exception e) { 
            e.printStackTrace();
        } finally {
            res.getWriter().flush() ;
        }
    }
}
