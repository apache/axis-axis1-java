package test.encoding;

import org.apache.axis.*;
import org.apache.axis.client.*;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.registries.HandlerRegistry;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;

import java.util.*;
import junit.framework.TestCase;

public class TestArrayListConversions extends TestCase {
  private static final String SERVICE_NAME = "TestArrayConversions";
  
  private ServiceClient client;
  
  public TestArrayListConversions()
  {
    super("service");
  }
  
  public TestArrayListConversions(String name)
  {
    super(name);
    init();
  }
  
  private static boolean equals(List list, Object obj)
  {
    if ((list == null) || (obj == null))
      return false;
    
    if (!obj.getClass().isArray()) return false;
    
    Object [] array = (Object [])obj;
    Iterator iter = list.iterator();

    for (int i=0; i < array.length; i++)
      if (!(array[i].equals(iter.next()))) return false;    return true;
  }

  public void init()
  {
    ServiceClient.initialize();
    AxisServer server = new AxisServer();
    HandlerRegistry hr = (HandlerRegistry) server.getHandlerRegistry();
    Handler disp = hr.find("RPCDispatcher");    
    SOAPService service = new SOAPService(disp);
    service.addOption("className", "test.encoding.TestArrayListConversions");
    service.addOption("methodName", "*");
    
    server.deployService(SERVICE_NAME, service);
    
    client = new ServiceClient(new LocalTransport(server));
  }
  
  public void testVectorConversion() throws Exception
  {
    Vector v = new Vector();
    v.addElement("Hi there!");
    v.addElement("This'll be a SOAP Array and then a LinkedList!");
    Object ret = client.invoke(SERVICE_NAME, "echoLinkedList",
                               new Object [] { v });
    if (!equals(v, ret)) assertEquals(v, ret);
  }
  
  public void testLinkedListConversion() throws Exception
  {
    LinkedList l = new LinkedList();
    l.add("Linked list item #1");
    l.add("Second linked list item");
    l.add("This will be a SOAP Array then a Vector!");

    Object ret = client.invoke(SERVICE_NAME, "echoVector",
                        new Object [] { l });
    if (!equals(l, ret)) assertEquals(l, ret);
  }
      
  public void testArrayConversion() throws Exception
  {
    Vector v = new Vector();
    v.addElement("Hi there!");
    v.addElement("This'll be a SOAP Array");

    Object ret = client.invoke(SERVICE_NAME, "echoArray",
                        new Object [] { v });
    if (!equals(v, ret)) assertEquals(v, ret);
  }

  public static void main(String [] args)
  {
    TestArrayListConversions tester =
              new TestArrayListConversions("TestArrayListConversions");
    try {
      tester.testArrayConversion();
      tester.testLinkedListConversion();
      tester.testVectorConversion();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /****************************************************************
   * 
   * Service methods - this class is also deployed as an Axis RPC
   * service for convenience.  These guys just echo various things.
   * 
   */
  public LinkedList echoLinkedList(LinkedList l)
  {
    return l;
  }
  
  public Vector echoVector(Vector v)
  {
    return v;
  }
  
  public Object [] echoArray(Object [] array)
  {
    return array;
  }
}
