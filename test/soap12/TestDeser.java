package test.soap12;

import junit.framework.TestCase;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;

import javax.xml.namespace.QName;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Test deserialization of SOAP responses
 */
public class TestDeser extends TestCase {
    private AxisServer server = null;


    public TestDeser(String name) {
        super(name);

        server = new AxisServer();

        TypeMappingRegistry tmr = server.getTypeMappingRegistry();
        TypeMapping tm = (TypeMapping) tmr.createTypeMapping();
        tm.setSupportedEncodings(new String[] {Constants.URI_SOAP12_ENC});
        tmr.register(Constants.URI_SOAP12_ENC, tm);
        tm.register(java.lang.String[].class,
                    new QName("http://soapinterop.org/xsd", "ArrayOfString"),
                    new org.apache.axis.encoding.ser.ArraySerializerFactory(),
                    new org.apache.axis.encoding.ser.ArrayDeserializerFactory());
    }

    private final String SOAP_HEAD =
        "<?xml version=\"1.0\"?>\n" +
        "<soap:Envelope " +
          "xmlns:soap=\"http://www.w3.org/2002/12/soap-envelope\" " +
          "xmlns:soapenc=\"http://www.w3.org/2002/12/soap-encoding\" " +
          "xmlns:this=\"http://encoding.test\" " +
          "xmlns:xsi=\"" + Constants.URI_DEFAULT_SCHEMA_XSI + "\" " +
          "xmlns:xsd=\"" + Constants.URI_DEFAULT_SCHEMA_XSD + "\">\n";

    private final String ITEM =
           "<item xsi:type=\"xsd:string\">abc</item>\n";

    private final String BODY_HEAD =
          "<soap:Body>\n";

    private final String METHOD_HEAD =
            "<methodResult xmlns=\"http://tempuri.org/\">\n";

    private final String METHOD_TAIL =
            "</methodResult>\n";

    private final String BODY_TAIL =
          "</soap:Body>\n";

    private final String SOAP_TAIL =
        "</soap:Envelope>\n";


    private final String HEAD = SOAP_HEAD + BODY_HEAD + METHOD_HEAD;
    private final String TAIL = METHOD_TAIL + BODY_TAIL + SOAP_TAIL;


    private static boolean equals(Object obj1, Object obj2) {
       if ( (obj1 == null) || (obj2 == null) ) return (obj1 == obj2);
       if (obj1.equals(obj2)) return true;
       if (obj2.getClass().isArray() && obj1.getClass().isArray()) {
           if (Array.getLength(obj1) != Array.getLength(obj2)) return false;
           for (int i=0; i<Array.getLength(obj1); i++)
               if (!equals(Array.get(obj1,i),Array.get(obj2,i))) return false;
           return true;
       }
       if ((obj1 instanceof List) && (obj2 instanceof List)) {
           List list1 = (List)obj1;
           List list2 = (List)obj2;
           if (list1.size() != list2.size()) return false;
           for (int i=0; i < list1.size(); i++) {
               if (!equals(list1.get(i), list2.get(i))) return false;
           }
           return true;
       }
       if ((obj1 instanceof Map) && (obj2 instanceof Map)) {
           Map map1 = (Map)obj1;
           Map map2 = (Map)obj2;
           Set keys1 = map1.keySet();
           Set keys2 = map2.keySet();
           if (!(keys1.equals(keys2))) return false;
           Iterator i = keys1.iterator();
           while (i.hasNext()) {
               Object key = i.next();
               if (!map1.get(key).equals(map2.get(key)))
                   return false;
           }
           return true;
       }

       return false;
    }


    public void deserialize(String soap, Object expected, boolean tryConvert) throws Exception {
        Message message = new Message(soap);
        MessageContext context = new MessageContext(server);
        context.setSOAPConstants(SOAPConstants.SOAP12_CONSTANTS);
        context.setProperty(Constants.MC_NO_OPERATION_OK, Boolean.TRUE);

        message.setMessageContext(context);

        SOAPEnvelope envelope = message.getSOAPEnvelope();
        assertNotNull("SOAP envelope should not be null", envelope);

        RPCElement body = (RPCElement)envelope.getFirstBody();
        assertNotNull("SOAP body should not be null", body);

        Vector arglist = body.getParams();
        assertNotNull("arglist", arglist);
        assertTrue("param.size()<=0 {Should be > 0}", arglist.size()>0);

        RPCParam param = (RPCParam) arglist.get(0);
        assertNotNull("SOAP param should not be null", param);

        Object result = param.getValue();
        if (!equals(result, expected)) {
           // Try to convert to the expected class
           if (tryConvert) {
               Object result2 = JavaUtils.convert(result, expected.getClass());
               if (!equals(result2, expected)) {
                   assertEquals("The result is not what is expected.", expected, result);
               }
           } else {
               assertEquals("The result is not what is expected.", expected, result);
           }
        }
    }

    public void testDeser1() throws Exception {
        deserialize(SOAP_HEAD + BODY_HEAD + METHOD_HEAD + ITEM + METHOD_TAIL + BODY_TAIL + SOAP_TAIL, "abc", false);
    }

    public void testDeser2() throws Exception {
        boolean expectedExceptionThrown = false;
        try {
            deserialize(SOAP_HEAD + BODY_HEAD + METHOD_HEAD + ITEM + METHOD_TAIL + BODY_TAIL + "<hello/>" + SOAP_TAIL, null, false);
        } catch (org.apache.axis.AxisFault af) {
            String expected = Messages.getMessage("noElemAfterBody12");
            if(af.getFaultString().indexOf(expected)!=-1)
                expectedExceptionThrown = true;
        }
        assertTrue(expectedExceptionThrown);
    }

    public void testAfterBody() throws Exception {
        boolean expectedExceptionThrown = false;
        try {
            deserialize(SOAP_HEAD + BODY_HEAD + METHOD_HEAD + ITEM + METHOD_TAIL + "<hello/>" + BODY_TAIL + SOAP_TAIL, null, false);
        } catch (org.apache.axis.AxisFault af) {
            // Should drop an ex about soap 1.2 doesn't permit any element after body
            String expected = Messages.getMessage("onlyOneBodyFor12");
            if(af.getFaultString().indexOf(expected)!=-1)
                expectedExceptionThrown = true;
        }
        assertTrue(expectedExceptionThrown);
    }

    public void testArray() throws Exception {
        Vector v = new Vector();
        v.addElement("abc");
        v.addElement("def");
        deserialize(HEAD +
                    "<result xsi:type=\"soapenc:Array\" " +
                            "soapenc:itemType=\"xsd:string\" soapenc:arraySize=\"2\"> " +
                       "<item xsi:type=\"xsd:string\">abc</item>" +
                       "<item xsi:type=\"xsd:string\">def</item>" +
                    "</result>" +
                    TAIL,
                    v, true);
    }

    public void testSparseArray1() throws Exception {
        ArrayList list = new ArrayList(4);
        list.add(null);
        list.add(null);
        list.add("abc");
        list.add("def");
        boolean expectedExceptionThrown = false;
        try {
            deserialize(HEAD +
                        "<result xsi:type=\"soapenc:Array\" " +
                                "soapenc:itemType=\"xsd:string\" soapenc:arraySize=\"4\" " +
                                " soapenc:offset=\"[2]\">" +
                           "<item xsi:type=\"xsd:string\">abc</item>" +
                           "<item xsi:type=\"xsd:string\">def</item>" +
                        "</result>" +
                        TAIL,
                        list, true);
        } catch (Exception af) {
            String expected = Messages.getMessage("noSparseArray");
            if(af.toString().indexOf(expected)!=-1)
                expectedExceptionThrown = true;
        }
        assertTrue(expectedExceptionThrown);
    }

    public void testSparseArray2() throws Exception {
        ArrayList list = new ArrayList(4);
        list.add("abc");
        list.add(null);
        list.add("def");
        list.add(null);

        boolean expectedExceptionThrown = false;
        try {
            deserialize(HEAD +
                        "<result xsi:type=\"soapenc:Array\" " +
                        "soapenc:itemType=\"xsd:string\" soapenc:arraySize=\"4\">" +
                        "<item soapenc:position=\"[0]\" xsi:type=\"xsd:string\">abc</item>" +
                        "<item soapenc:position=\"[2]\" xsi:type=\"xsd:string\">def</item>" +
                        "</result>" +
                        TAIL,
                        list, true);
        } catch (Exception af) {
            String expected = Messages.getMessage("noSparseArray");
            if(af.toString().indexOf(expected)!=-1)
                expectedExceptionThrown = true;
        }
        assertTrue(expectedExceptionThrown);
    }

    public void testNoSizeDefinedArray() throws Exception {
        ArrayList a = new ArrayList();
        a.add("abc");
        a.add("def");

        deserialize(HEAD +
                    "<result xsi:type=\"soapenc:Array\" " +
                    "soapenc:itemType=\"xsd:string\" soapenc:arraySize=\"*\">" +
                        "<item xsi:type=\"xsd:string\">abc</item>" +
                        "<item xsi:type=\"xsd:string\">def</item>" +
                    "</result>" +
                    TAIL,
                    a, true);
    }

}
