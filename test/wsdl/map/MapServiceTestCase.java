/**
 * MapServiceServiceTestCase.java
 *
 */

package test.wsdl.map;

import java.util.Map;
import java.util.HashMap;

public class MapServiceTestCase extends junit.framework.TestCase {
    public MapServiceTestCase(java.lang.String name) {
        super(name);
    }
    public void test1EchoMap() throws Exception {
        test.wsdl.map.MapService binding;
        try {
            binding = new MapServiceServiceLocator().getMapService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // populate test data
        HashMap m = new HashMap();
        String stringKey = "stringKey";
        String stringVal = "stringValue";
        m.put(stringKey, stringVal);

        Integer intKey = new Integer(77);
        Double doubleVal = new Double(3.14159);
        m.put(intKey, doubleVal);

        Long longKey = new Long("1231231231");
        Boolean boolVal = new Boolean(true);
        m.put(longKey, boolVal);

        String[] stringArrayKey = new String[] {"array1", "array2"};
        Integer[] intArray = new Integer[] {new Integer(1), new Integer(2)};
        m.put(stringArrayKey, intArray );

        Long[] longArrayKey = new Long[] {new Long("1000001"), new Long(2000002)};
        Boolean[] boolArray = new Boolean[]{ new Boolean(true), new Boolean(false)};
        m.put(longArrayKey, boolArray);

        // Test operation
        Map outMap = binding.echoMap(m);

        // Verify return map
        Object value;
        value = outMap.get(stringKey);
        assertNotNull("Can not find entry for STRING key", value);
        assertEquals("The class of the map value does not match",  String.class.getName(), value.getClass().getName());
        assertEquals("The value does not match", stringVal, (String) value);

        value = outMap.get(intKey);
        assertNotNull("Can not find entry for INTEGER key", value);
        assertEquals("The class of the map value does not match", Double.class.getName(), value.getClass().getName());
        assertEquals("The value does not match", (Double) value, doubleVal);

        value = outMap.get(longKey);
        assertNotNull("Can not find entry for LONG key", value);
        assertEquals("The class of the map value does not match",  Boolean.class.getName(), value.getClass().getName());
        assertEquals("The value does not match", boolVal, (Boolean) value);

        // This is a pain because a get with the orignal keys wont return entries in the new map
        java.util.Iterator it = outMap.keySet().iterator();
        boolean foundInt = false;
        boolean foundBool = false;
        while (it.hasNext())
        {
            Object oKey = it.next();
            if (oKey.getClass().isArray())
            {
                Object[] oArrayKey = (Object[]) oKey;
                Object oValue = outMap.get(oKey);

                if (String.class.getName().equals(oArrayKey[0].getClass().getName()))
                {
                    // Verify Key data
                    String[] sArray = (String[]) oArrayKey;
                    for (int i = 0; i < sArray.length; i++)
                    {
                        assertEquals("STRING Array KEY data does not match", stringArrayKey[i], sArray[i]);
                    }

                    // verify value data
                    assertTrue("The Array VALUE does not match", oValue.getClass().isArray());
                    Object[] oArrayValue = (Object[]) oValue;
                    assertEquals("Class of the array does not match epected", Integer.class.getName(), oArrayValue[0].getClass().getName());
                    Integer[] ia = (Integer[]) oValue;
                    for (int i = 0; i < ia.length; i++)
                    {
                        assertEquals("INTEGER Array VALUE does not match", intArray[i], ia[i]);
                    }
                    foundInt = true;
                }
                else if (Long.class.getName().equals(oArrayKey[0].getClass().getName()))
                {
                    // verify Key data
                    Long[] lArray = (Long[]) oArrayKey;
                    for (int i = 0; i < lArray.length; i++)
                    {
                        assertEquals("LONG Array KEY data does not match", longArrayKey[i], lArray[i]);

                    }
                    // verify value data
                    assertTrue("The Array VALUE does not match", oValue.getClass().isArray());
                    Object[] oArrayValue = (Object[]) oValue;
                    assertEquals("Class of the array does not match epected", Boolean.class.getName(), oArrayValue[0].getClass().getName());
                    Boolean[] ba = (Boolean[]) oValue;
                    for (int i = 0; i < ba.length; i++)
                    {
                        assertEquals("BOOLEAN Array VALUE does not match", boolArray[i], ba[i]);
                    }
                    foundBool = true;
                }

            }
        }
        if (!foundInt || ! foundBool)
        {
            assertTrue("Unable to find integer or boolean key in returned Map", false);
        }
    }

}
