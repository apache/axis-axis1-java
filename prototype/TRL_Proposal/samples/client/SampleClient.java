package samples.client;

import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.ChainContainer;

import org.apache.axis.transports.HttpTransportSender;
import org.apache.axis.message.SOAPDocument;
import org.apache.axis.message.impl.SOAPDocumentImpl;

import org.apache.axis.handlers.Signer;
import org.apache.axis.handlers.Verifier;
import org.apache.axis.handlers.KeyStoreUtil;

import org.apache.axis.util.xml.DOMConverter ;
import org.apache.axis.util.Logger ;

public class SampleClient {
    public static String ALIAS = "DSIG.ALIAS";
    public static String KEYPASS = "DSIG.KEYPASS";
    public static String KEY_STORE_PATH = "DSIG.KEY_STORE_PATH";
    public static String KEY_STORE_PASS = "DSIG.KEY_STORE_PASS";
    public static String SIGNER_URL = "DSIG.SIGNER_URL";
    public static String VERIFIER_URL = "DSIG.VERIFIER_URL";

    private Handler nextHandler;

    public void setNextHandler(Handler handler) {
        this.nextHandler = handler;
    }

    static void main(String args[]) {
        SampleClient client = new SampleClient();
        client.configure();
        String env =
                 "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"  >\n" +
                 "<SOAP-ENV:Body>\n" +
                 "<po xmlns=\"http://my/po/format\" id=\"body\">\n" +
                 "</po>\n" +
                 "</SOAP-ENV:Body>\n" +
                 "</SOAP-ENV:Envelope>" ;
     
        try {
            SOAPDocument msg = 
                new SOAPDocumentImpl(DOMConverter.toDOM(env));
            MessageContext  msgCntxt = new MessageContext(msg);
            Logger.normal("Sending Request ");
            Logger.normal(msg.toXML());

            client.nextHandler.invoke(msgCntxt);
            Logger.normal("Received Request ");
            Logger.normal(msgCntxt.getMessage().toXML());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    void configure() {
        try {
            // Setting digital signature properies
            String alias = System.getProperty(ALIAS);
            String keypass = System.getProperty(KEYPASS);
            String keyStorePath = System.getProperty(KEY_STORE_PATH);
            String keyStorePass = System.getProperty(KEY_STORE_PASS);
            String verifierURI = System.getProperty(SIGNER_URL);
            String signerURI = System.getProperty(VERIFIER_URL);
            if (alias == null ||
                    keypass == null ||
                    keyStorePath == null ||
                    keyStorePass == null ||
                    verifierURI == null ||
                    signerURI == null) {
                Logger.fatal("alias: " + alias);
                Logger.fatal("keypass: " + keypass);
                Logger.fatal("keyStorePath: " + keyStorePath);
                Logger.fatal("keyStorePass: " + keyStorePass);
                Logger.fatal("verifierURI: " + verifierURI);
                Logger.fatal("signerURI: " + signerURI);
                throw new Exception("Need to set up properties");
            }
            System.setProperty("com.ibm.trl.soapimpl.security.KeyStorePath",keyStorePath);
            System.setProperty("com.ibm.trl.soapimpl.security.KeyStorePassword",keyStorePass);
            java.security.Key key = KeyStoreUtil.getKey(alias, keypass);
            java.security.cert.Certificate cert = KeyStoreUtil.getCertificate(alias);

            ChainContainer cc = new ChainContainer();
            this.setNextHandler(cc);
            cc.addHandler(new Signer(key, cert, verifierURI));
               // need to specify a key
            cc.addHandler(new HttpTransportSender());
//            cc.addHandler(new Verifier());
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
};
