/**
 * ExtensibilityQueryTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.extensibility;

import org.apache.axis.client.AdminClient;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.Text;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class ExtensibilityQueryTestCase extends junit.framework.TestCase {
    protected static Log log =
        LogFactory.getLog(ExtensibilityQueryTestCase.class.getName());
    
    public ExtensibilityQueryTestCase(String name) {
        super(name);
    }

    public void testExtensibilityQueryPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.extensibility.ExtensibilityQueryLocator().getExtensibilityQueryPortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.extensibility.ExtensibilityQueryLocator().getServiceName());
        assertTrue(service != null);
    }

    public void testQuery() {
        ExtensibilityQueryPortType binding;
        try {
            ExtensibilityQueryLocator locator = new ExtensibilityQueryLocator();
            binding = locator.getExtensibilityQueryPort();
            deployServer();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        } 
        catch (Exception e) {
            throw new junit.framework.AssertionFailedError("Binding initialization Exception caught: " + e);
        }
        assertTrue("binding is null", binding != null);

        try {
            ExtensibilityType expression = new ExtensibilityType(); 
            BookType book = new BookType();
            book.setSubject("all");
            QName elementName = _FindBooksQueryExpressionElement.getTypeDesc().getFields()[0].getXmlName();
            MessageElement el = new MessageElement(elementName.getNamespaceURI(), elementName.getLocalPart(), book);
            expression.set_any(new MessageElement [] { el });
            // call the operation
            ExtensibilityType any = binding.query(expression);
            // validate results
            MessageElement [] anyContent = any.get_any();
            assertEquals(1, anyContent.length);
            ResultListType result = (ResultListType)anyContent[0].getObjectValue(ResultListType.class);
            log.debug("Message " + result + ": " + anyContent[0].toString());
            assertNotNull("ResultListType back from getResultList() is null", result);
            QueryResultType[] queryResult = result.getResult();
            assertTrue(queryResult.length == 2); 
            isValid(queryResult[0], "Computer Science", "The Grid"); 
            isValid(queryResult[1], "English", "The Oxford Dictionary"); 
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new junit.framework.AssertionFailedError("Exception caught: " + e);
        }
    }

    public void testMixedQuery() {
        ExtensibilityQueryPortType binding;
        try {
            ExtensibilityQueryLocator locator = new ExtensibilityQueryLocator();
            binding = locator.getExtensibilityQueryPort();
            deployServer();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        } 
        catch (Exception e) {
            throw new junit.framework.AssertionFailedError("Binding initialization Exception caught: " + e);
        }
        assertTrue("binding is null", binding != null);

        try {
        ExtensibilityType expression = new ExtensibilityType(); 

        MessageElement [] elements = new MessageElement[4];

            elements[0] = new Text("123");
        elements[1] = new Text("  456");
        
            BookType book = new BookType();
            book.setSubject("all");
            QName elementName = _FindBooksQueryExpressionElement.getTypeDesc().getFields()[0].getXmlName();
            elements[2] = new MessageElement(elementName.getNamespaceURI(), elementName.getLocalPart(), book);

        elements[3] = new Text("789");

            expression.set_any(elements);

            // call the operation
            ExtensibilityType any = binding.mixedQuery(expression);

        if (any == null) {
        throw new Exception("No output returned");
        }

        // validate results
            MessageElement [] anyContent = any.get_any();

        if (anyContent == null) {
        throw new Exception("No any");
        }
        if (anyContent.length != 2) {
        throw new Exception("Expected: 2 got: " + 
                    anyContent.length + " element");
        }

        Object obj = anyContent[0].getObjectValue(BookType.class);
        BookType bookQuery = (BookType)obj;
        String subject = bookQuery.getSubject();
        if (!"gotAll".equals(subject)) {
        throw new Exception("Book subject query reply should be gotAll, instead was " + subject);
        }

        String expected = "ABCD";
        String received = anyContent[1].toString();

        if (!expected.equals(received)) {
        throw new Exception("Expected: " + expected + 
                    " received: " + received);
        }
        
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new junit.framework.AssertionFailedError("Exception caught: " + e);
        }
    }

    private void isValid(QueryResultType result, String name, String value) {
        Logger root = Logger.getRootLogger();
        root.debug("Name: " + result.getName()); 
        root.debug("Value: " + result.getValue()); 
        assertTrue(result.getName().equals(name));
        assertTrue(result.getValue().equals(value));
        assertTrue(result.getStatus().equals(StatusType.MORE));
        Calendar now = Calendar.getInstance();
        Calendar then = result.getTime();
        assertTrue("Time check failed.  Result time = " + then + ", current time = " + now, then.before(now));
        assertTrue(result.getQueryType().getNamespaceURI().equals("urn:QueryType"));
        assertTrue(result.getQueryType().getLocalPart().equals("BookQuery"));
    }

    private void deployServer() {
        final String INPUT_FILE = "server-deploy.wsdd";

        InputStream is = getClass().getResourceAsStream(INPUT_FILE);
        if (is == null) {
            // try current directory
            try {
                is = new FileInputStream(INPUT_FILE);
            } catch (FileNotFoundException e) {
                is = null;
            }
        }
        assertNotNull("Unable to find " + INPUT_FILE + ". Make sure it is on the classpath or in the current directory.", is);
        AdminClient admin = new AdminClient();
        try {
            Options opts = new Options( null );
            opts.setDefaultURL("http://localhost:8080/axis/services/AdminService");
            admin.process(opts, is);
        } catch (Exception e) {
            assertTrue("Unable to deploy " + INPUT_FILE + ". ERROR: " + e, false);
        }
    }
}

