/**
 * ExtensibilityQueryTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.extensibility;

import org.apache.axis.AxisEngine;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AdminClient;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.Options;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMapping;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class ExtensibilityQueryTestCase extends junit.framework.TestCase {
    public ExtensibilityQueryTestCase(String name) {
        super(name);
    }
    public void testQuery() {
        ExtensibilityQueryPortType binding;
        Logger root = Logger.getRootLogger();
        Level origLevel = root.getEffectiveLevel();
        root.setLevel(Level.FATAL);
        try {
            ExtensibilityQueryLocator locator = new ExtensibilityQueryLocator();
            binding = locator.getExtensibilityQueryPort();
            addDynamicTypeMappings(locator.getEngine());
            deployServer();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        } 
        catch (Exception e) {
            throw new junit.framework.AssertionFailedError("Binding initialization Exception caught: " + e);
        }
        finally {
            root.setLevel(origLevel);
        }
        assertTrue("binding is null", binding != null);

        try {
            ExtensibilityType expression = new ExtensibilityType(); 
            FindBooksQueryExpressionElement bookQuery = new FindBooksQueryExpressionElement(); 
            BookType book = new BookType();
            book.setSubject("all");
            bookQuery.setBookQuery(book);
            MessageElement el = new MessageElement("foo", "Bar", bookQuery);
            expression.set_any(new MessageElement [] { el });
            // call the operation
            ExtensibilityType any = binding.query(expression);
            // validate results
            MessageElement [] anyContent = any.get_any();
            assertEquals(1, anyContent.length);
            QueryResultElement resEl = (QueryResultElement )anyContent[0].getObjectValue();
            assertNotNull("QueryResultElement back from anyContent[0].getObjectValue()", resEl);
            ResultListType result = resEl.getResultList();
            assertNotNull("ResultListType back from getResultList()", result);
            QueryResultType[] queryResult = result.getResult();
            assertTrue(queryResult.length == 2); 
            isValid(queryResult[0], "Computer Science", "The Grid"); 
            isValid(queryResult[1], "English", "The Oxford Dictionary"); 
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    private void isValid(QueryResultType result, String name, String value) {
        assertTrue(result.getName().equals(name));
        assertTrue(result.getValue().equals(value));
        Calendar now = Calendar.getInstance();
        Calendar then = result.getTime();
        assertTrue("Time check failed.  Result time = " + then + ", current time = " + now, then.before(now));
        assertTrue(result.getQueryType().getNamespaceURI().equals("urn:QueryType"));
        assertTrue(result.getQueryType().getLocalPart().equals("BookQuery"));
    }

    private void addDynamicTypeMappings(AxisEngine engine) throws Exception {
        TypeMappingRegistry registry = engine.getTypeMappingRegistry(); 
        TypeMapping mapping = registry.createTypeMapping();
        addBeanMapping(mapping, "FindBooksQueryExpressionElement", FindBooksQueryExpressionElement.class);
        addBeanMapping(mapping, "BookType", BookType.class);
        addBeanMapping(mapping, "ResultListType", ResultListType.class);
        addBeanMapping(mapping, "QueryResultType", QueryResultType.class);
        addBeanMapping(mapping, "QueryResultElement", QueryResultElement.class);
        registry.register("",mapping);
        EngineConfiguration config = engine.getConfig();
        config.writeEngineConfig(engine);
    }

    private void addBeanMapping(TypeMapping mapping, String localName, Class javaClass) {
        QName qname = new QName("urn:QueryTypes", localName);
        mapping.register(javaClass,
                         qname,
                         new BeanSerializerFactory(javaClass, qname),
                         new BeanDeserializerFactory(javaClass, qname));
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

