/**
 * ExtensibilityQueryBindingImpl.java
 *
 */

package test.wsdl.extensibility;

import org.apache.axis.AxisEngine;
import org.apache.axis.server.AxisServer;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.RPCElement;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.client.Call;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import java.io.Reader;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Vector;
import java.rmi.RemoteException;


public class ExtensibilityQueryBindingImpl implements ExtensibilityQueryPortType {
    private final static String[] books = new String[] { "The Grid", "The Oxford Dictionary" }; 
    private final static String[] subjects = new String[] { "Computer Science", "English" }; 

    public ExtensibilityType query(ExtensibilityType query) throws RemoteException {
        ExtensibilityType result = new ExtensibilityType();
        Object obj = query.getAny();
        if (obj instanceof BookType) {
            BookType bookQuery = (BookType) obj;
            String subject = bookQuery.getSubject();
            System.out.println("ExtensibilityQueryBindingImpl: Found book subject query " + subject);
  
            QueryResultElement resultElement = new QueryResultElement();
            ResultListType resultList = new ResultListType();
            resultElement.setResultList(resultList);
            QueryResultType[] queryResult = new QueryResultType[books.length];
            for (int i = 0; i < books.length; i++) {
                queryResult[i] = new QueryResultType();
                queryResult[i].setName(subjects[i]);
                queryResult[i].setValue(books[i]);
                queryResult[i].setTime(Calendar.getInstance());
                queryResult[i].setQueryType(new QName("urn:QueryType","BookQuery"));
            }
            resultList.setResult(queryResult);
            result.setAny(resultElement);
        } else {
            throw new RemoteException("Failed to get book type. Got: " + obj.getClass().getName() + ":" + obj.toString());
        }
        return result;
    }
}

class ObjectSerializer {
    static Log logger =
           LogFactory.getLog(ObjectSerializer.class.getName());

    static Object toObject(Element element) throws Exception {
       MessageContext currentContext = MessageContext.getCurrentContext();
       MessageContext messageContext = new MessageContext(currentContext.getAxisEngine()); 
       messageContext.setTypeMappingRegistry(currentContext.getTypeMappingRegistry());
       messageContext.setEncodingStyle("");
       messageContext.setProperty(Call.SEND_TYPE_ATTR, Boolean.FALSE);
       SOAPEnvelope message = new SOAPEnvelope();
       Document doc = XMLUtils.newDocument();
       Element operationWrapper = doc.createElementNS("urn:operationNS","operation"); 
       doc.appendChild(operationWrapper); 
       Node node = doc.importNode(element,true);
       operationWrapper.appendChild(node);

       message.addBodyElement(new SOAPBodyElement(operationWrapper));
       
       StringWriter stringWriter = new StringWriter(); 
       SerializationContext context = new SerializationContextImpl(stringWriter, messageContext);
       context.setDoMultiRefs(false);
       message.output(context);
       stringWriter.close();
       String messageString = stringWriter.getBuffer().toString();
       logger.debug(messageString);
       Reader reader = new StringReader(messageString);
       messageContext.setProperty(BeanDeserializer.DESERIALIZE_ANY, Boolean.TRUE);
       DeserializationContext deserializer = new DeserializationContextImpl(new InputSource(reader),
                                                                           messageContext, 
                                                                           Message.REQUEST);
       deserializer.parse();
       SOAPEnvelope env = deserializer.getEnvelope();
       
       RPCElement rpcElem = (RPCElement)env.getFirstBody();
       Vector parameters = rpcElem.getParams();
       RPCParam param = (RPCParam) parameters.get(0);
       return param.getValue();
    }
}
