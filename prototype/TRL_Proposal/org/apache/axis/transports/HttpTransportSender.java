package org.apache.axis.transports;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import org.apache.axis.TransportSender;
import org.apache.axis.MessageContext;
import org.apache.axis.SOAPException;
import org.apache.axis.message.impl.SOAPDocumentImpl;

import org.apache.axis.util.xml.DOMConverter;

public class HttpTransportSender implements TransportSender {
    private String targetHost;
    private int targetPort;
    private String servletName;

    public void init() {
        this.targetHost = "localhost";
        this.targetPort = 8080;
        this.servletName = "axis/echoServlet";
    };

    public void cleanup() {};

    public void invoke(MessageContext msgCntxt) {
        Reader in = null;
        try {
            in = new InputStreamReader(
                    send(msgCntxt.getMessage().getEnvelope().toXML()), 
                    "UTF8");            
            msgCntxt.setMessage(new SOAPDocumentImpl(DOMConverter.toDOM(in)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (in != null) in.close(); } catch (IOException e) { }
        }
  }

    private InputStream send(String content)
        throws IOException, Exception //, SOAPFaultException
    {
        URL url = new URL("http://localhost:8080/axis/EchoServlet");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
//        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        con.setRequestProperty("SOAPAction", "\"\"");
        con.setDoOutput(true);
        OutputStream out = con.getOutputStream();
        out.write(content.getBytes());
        out.flush();

        if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
//            throw new ServerException("HttpURLConnection received a response: " + con.getResponseMessage());
            throw new SOAPException("HttpURLConnection received a response: " + con.getResponseMessage());
        return con.getInputStream();
    }

};
