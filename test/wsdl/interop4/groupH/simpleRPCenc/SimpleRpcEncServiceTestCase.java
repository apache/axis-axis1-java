/**
 * SimpleRpcEncServiceTestCase.java
 *
 * Test case for SoapBuilders interop round 4
 * http://soapinterop.java.sun.com/soapbuilders/r4/faults.shtml
 * 
 * @author Tom Jordahl (tomj@macromedia.com)
 */

package test.wsdl.interop4.groupH.simpleRPCenc;

import java.net.URL;

public class SimpleRpcEncServiceTestCase extends junit.framework.TestCase {
    
    public static URL url = null;
    
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            url = new URL(args[0]);
        } else {
            url = new URL(new SimpleRpcEncServiceLocator().getSimpleRpcEncPortAddress());
        }
        junit.textui.TestRunner.run(new junit.framework.TestSuite(SimpleRpcEncServiceTestCase.class));
    } // main
    
    
    public SimpleRpcEncServiceTestCase(java.lang.String name) throws Exception {
        super(name);
        if (url == null) {
            url = new URL(new SimpleRpcEncServiceLocator().getSimpleRpcEncPortAddress());
        }
    }
    
    
    public void test1SimpleRpcEncPortEchoEmptyFault() throws Exception {
        SimpleRpcEncPortType binding;
        try {
            binding = new SimpleRpcEncServiceLocator().getSimpleRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        ///////////////////////////////////////////////////////////////////////
        // EmptyFault
        try {
            binding.echoEmptyFault();
        }
        catch(EmptyFault ef) {
            return;
        }
        fail("Did NOT catch any exception");
    }

    
    
    public void test2SimpleRpcEncPortEchoStringFault() throws Exception {
        SimpleRpcEncPortType binding;
        try {
            binding = new SimpleRpcEncServiceLocator().getSimpleRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // StringFault
        try {
            binding.echoStringFault("HELLO");
        }
        catch(StringFault sf) {
            assertEquals("HELLO", sf.getPart2());
            return;
        }
        fail("Did NOT catch any exception");
    }

    public void test3SimpleRpcEncPortEchoIntArrayFault() throws Exception {
        SimpleRpcEncPortType binding;
        try {
            binding = new SimpleRpcEncServiceLocator().getSimpleRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        ///////////////////////////////////////////////////////////////////////
        // IntArrayFault
        int[] param = new int[] {1, 2, 3};
        try {
            binding.echoIntArrayFault(param);
        }
        catch(IntArrayFault f) {
            int[] ret = f.getPart5();
            assertEquals("Array element 1", param[0], ret[0]);
            assertEquals("Array element 2", param[1], ret[1]);
            assertEquals("Array element 3", param[2], ret[2]);
            return;
        }
        fail("Did NOT catch any exception");
    }

    public void test4SimpleRpcEncPortEchoMultipleFaults1() throws Exception {
        SimpleRpcEncPortType binding;
        try {
            binding = new SimpleRpcEncServiceLocator().getSimpleRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        ///////////////////////////////////////////////////////////////////////
        // echoMultipleFaults1
        float[] floatParam = new float[] {1.0F, 2.2F, 3.5F};
        String stringParam = "HELLO";
        for (int i=1; i < 4; i++) {
            try {
                binding.echoMultipleFaults1(i, stringParam, floatParam);
            }
            catch (EmptyFault e1) {
                assertEquals("Wrong fault thrown: " + e1.getClass(), 1, i);
                continue;
            }
            catch (StringFault e2) {
                assertEquals("Wrong fault thrown: " + e2.getClass(), 2, i);
                assertEquals("HELLO", e2.getPart2());
                continue;
            }
            catch (FloatArrayFault e3) {
                assertEquals("Wrong fault thrown: " + e3.getClass(), 3, i);
                float[] ret = e3.getPart7();
                assertEquals(floatParam[0], ret[0], 0.01F);
                assertEquals(floatParam[1], ret[1], 0.01F);
                assertEquals(floatParam[2], ret[2], 0.01F);
                continue;
            }
            fail("Did NOT catch any exception");
        }
    }

    public void test5SimpleRpcEncPortEchoMultipleFaults2() throws Exception {
        SimpleRpcEncPortType binding;
        try {
            binding = new SimpleRpcEncServiceLocator().getSimpleRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        ///////////////////////////////////////////////////////////////////////
        // echoMultipleFaults2
        String stringParam = "HELLO";
        String[] stringArrayParam = new String[] {"one", "two", "three"};
        float floatParam = 9.7F;
        for (int i=1; i < 4; i++) {
            try {
                binding.echoMultipleFaults2(i, stringParam, floatParam, stringArrayParam);
            }
            catch (FloatFault e1) {
                assertEquals("Wrong fault thrown: " + e1.getClass(), 1, i);
                assertEquals(floatParam, e1.getPart4(), 0.01F);
                continue;
            }
            catch (StringFault e2) {
                assertEquals("Wrong fault thrown: " + e2.getClass(), 2, i);
                assertEquals(stringParam, e2.getPart2());
                continue;
            }
            catch (StringArrayFault e3) {
                assertEquals("Wrong fault thrown: " + e3.getClass(), 3, i);
                String[] ret = e3.getPart6();
                assertEquals("Array element 1", stringArrayParam[0], ret[0]);
                assertEquals("Array element 2", stringArrayParam[1], ret[1]);
                assertEquals("Array element 3", stringArrayParam[2], ret[2]);
                continue;
            }
            fail("Did NOT catch any exception");
        }
    }

    public void test6SimpleRpcEncPortEchoMultipleFaults3() throws Exception {
        SimpleRpcEncPortType binding;
        try {
            binding = new SimpleRpcEncServiceLocator().getSimpleRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        ///////////////////////////////////////////////////////////////////////
        // echoMultipleFaults3
        String param1 = "Param1";
        String param2 = "Param2";
        for (int i=1; i < 3; i++) {
            try {
                binding.echoMultipleFaults3(i, param1, param2);
            }
            catch (StringFault e1) {
                assertEquals("Wrong fault thrown: " + e1.getClass(), 1, i);
                assertEquals(param1, e1.getPart2());
                continue;
            }
            catch (String2Fault e2) {
                assertEquals("Wrong fault thrown: " + e2.getClass(), 2, i);
                assertEquals(param2, e2.getPart2());
                continue;
            }
            fail("Did NOT catch any exception");
        }
    }

    public void test7SimpleRpcEncPortEchoMultipleFaults4() throws Exception {
        SimpleRpcEncPortType binding;
        try {
            binding = new SimpleRpcEncServiceLocator().getSimpleRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        ////////////////////////////////////////////////////////////////////////
        // echoMultipleFaults4
        int intParam = 66;
        Enum enumParam = new Enum(1);
        for (int i=1; i < 3; i++) {
            try {
                binding.echoMultipleFaults4(i, intParam, enumParam);
            }
            catch (IntFault e1) {
                assertEquals("Wrong fault thrown: " + e1.getClass(), 1, i);
                assertEquals(intParam, e1.getPart3());
                continue;
            }
            catch (EnumFault e2) {
                assertEquals("Wrong fault thrown: " + e2.getClass(), 2, i);
                assertEquals(enumParam.getValue(), e2.getPart9().getValue());
                continue;
            }
            fail("Did NOT catch any exception");
        }
    }

}
