/**
 * ExtensibilityQueryTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.extensibility;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.AxisEngine;
import org.apache.axis.client.AdminClient;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;

import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.namespace.QName;
import java.util.Calendar;

public class ExtensibilityQueryTestCase extends junit.framework.TestCase {
    public ExtensibilityQueryTestCase(String name) {
        super(name);
    }
    public void testQuery() {
        ExtensibilityQueryBinding binding;
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
        assertTrue("binding is null", binding != null);

        try {
           
            ExtensibilityType expression = new ExtensibilityType(); 
            FindBooksQueryExpressionType bookQuery = new FindBooksQueryExpressionType(); 
            BookQuery book = new BookQuery();
            book.setSubject("Computer Science");
            bookQuery.setBookQuery(book);
            expression.setAny(bookQuery); 
            ExtensibilityType any = binding.query(expression);
            QueryResult result = (QueryResult) any.getAny();
            System.out.println("ExtensibilityQueryTestCase: Result name: " + result.getName() + " value: " + result.getValue() +
                               " date: " + result.getTime().getTime() + " type: " + result.getQueryType());
            assertTrue(result.getName().equals("Computer Science"));
            assertTrue(result.getValue().equals("The Grid"));
            assertTrue(result.getTime().before(Calendar.getInstance()));
            assertTrue(result.getQueryType().getNamespaceURI().equals("urn:QueryType"));
            assertTrue(result.getQueryType().getLocalPart().equals("BookQuery"));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    private void addDynamicTypeMappings(AxisEngine engine) throws Exception {
        TypeMappingRegistry registry = engine.getTypeMappingRegistry(); 
        TypeMapping mapping = registry.createTypeMapping();
        QName queryQname = new QName("urn:QueryTypes", "FindBooksQueryExpressionType");
        mapping.register(FindBooksQueryExpressionType.class,
                         queryQname,
                         new BeanSerializerFactory(FindBooksQueryExpressionType.class, queryQname),
                         new BeanDeserializerFactory(FindBooksQueryExpressionType.class, queryQname));
        QName bookQname = new QName("urn:QueryTypes", "BookQuery");
        mapping.register(BookQuery.class,
                         bookQname,
                         new BeanSerializerFactory(BookQuery.class, bookQname),
                         new BeanDeserializerFactory(BookQuery.class, bookQname));
        QName resultQname = new QName("urn:QueryTypes", "queryResult");
        mapping.register(QueryResult.class,
                         resultQname,
                         new BeanSerializerFactory(QueryResult.class, resultQname),
                         new BeanDeserializerFactory(QueryResult.class, resultQname));
        registry.register("",mapping);
        EngineConfiguration config = engine.getConfig();
        config.writeEngineConfig(engine);
    }

    private void deployServer() {
        AdminClient.main(new String[] { "test/wsdl/extensibility/server-deploy.wsdd" });
    }
}

