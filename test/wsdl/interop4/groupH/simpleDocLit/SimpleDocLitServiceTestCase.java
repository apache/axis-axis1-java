/**
 * SimpleDocLitServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupH.simpleDocLit;

import java.net.URL;

public class SimpleDocLitServiceTestCase extends junit.framework.TestCase {
    
    public static URL url = null;
    
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            url = new URL(args[0]);
        } else {
            url = new URL(new SimpleDocLitServiceLocator().getSimpleDocLitPortAddress());
        }
        junit.textui.TestRunner.run(new junit.framework.TestSuite(SimpleDocLitServiceTestCase.class));
    } // main
    
    public SimpleDocLitServiceTestCase(java.lang.String name) throws Exception {
        super(name);
        if (url == null) {
            url = new URL(new SimpleDocLitServiceLocator().getSimpleDocLitPortAddress());
        }
    }

    public void test1SimpleDocLitPortEchoEmptyFault() throws Exception {
        SimpleDocLitPortType binding;
        try {
            binding = new SimpleDocLitServiceLocator().getSimpleDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        try {
            binding.echoEmptyFault(new _echoEmptyFaultRequest());
        }
        catch (_EmptyPart e1) {
            return; // success!
        }
        
        fail("Should have caught exception");
     }

    public void test2SimpleDocLitPortEchoStringFault() throws Exception {
        SimpleDocLitPortType binding;
        try {
            binding = new SimpleDocLitServiceLocator().getSimpleDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        try {
            binding.echoStringFault("testString");
        }
        catch (StringFault e1) {
            assertEquals("String values didn't match", "testString", e1.getPart2());
            return;
        }
        
        fail("Should have caught exception");
    }

    public void test3SimpleDocLitPortEchoIntArrayFault() throws Exception {
        SimpleDocLitPortType binding;
        try {
            binding = new SimpleDocLitServiceLocator().getSimpleDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        int[] param = new int[] {1, 2, 3};

        // Test operation
        try {
            binding.echoIntArrayFault(new ArrayOfInt(param));
        }
        catch (ArrayOfInt e1) {
            int[] ret = e1.getValue();
            assertEquals("Array element 1", param[0], ret[0]);
            assertEquals("Array element 2", param[1], ret[1]);
            assertEquals("Array element 3", param[2], ret[2]);
            return;
        }

        fail("Should have caught exception");
    }

    public void test4SimpleDocLitPortEchoMultipleFaults1() throws Exception {
        SimpleDocLitPortType binding;
        try {
            binding = new SimpleDocLitServiceLocator().getSimpleDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        float[] floatParam = new float[] {1.0F, 2.2F, 3.5F};
        String stringParam = "HELLO";
        for (int i=1; i < 4; i++) {
            try {
              _echoMultipleFaults1Request request =
                        new _echoMultipleFaults1Request();
                request.setWhichFault(i);
                request.setParam1(stringParam);
                request.setParam2(new ArrayOfFloat(floatParam));
                binding.echoMultipleFaults1(request);
            }
            catch (ArrayOfFloat e1) {
                assertEquals("Wrong fault thrown: " + e1.getClass(), 3, i);
                float[] ret = e1.getValue();
                assertEquals(floatParam[0], ret[0], 0.01F);
                assertEquals(floatParam[1], ret[1], 0.01F);
                assertEquals(floatParam[2], ret[2], 0.01F);
                continue;
            }
            catch (StringFault e2) {
                assertEquals("Wrong fault thrown: " + e2.getClass(), 2, i);
                assertEquals("HELLO", e2.getPart2());
                continue;
            }
            catch (_EmptyPart e3) {
                assertEquals("Wrong fault thrown: " + e3.getClass(), 1, i);
                continue;
            }

            fail("Should have caught exception");
        }
    }

    public void test5SimpleDocLitPortEchoMultipleFaults2() throws Exception {
        SimpleDocLitPortType binding;
        try {
            binding = new SimpleDocLitServiceLocator().getSimpleDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        String stringParam = "HELLO";
        String[] stringArrayParam = new String[] {"one", "two", "three"};
        float floatParam = 9.7F;
        for (int i=1; i < 4; i++) {
            try {
              _echoMultipleFaults2Request request = 
                        new _echoMultipleFaults2Request();
                request.setWhichFault(i);
                request.setParam1(stringParam);
                request.setParam2(floatParam);
                request.setParam3(new ArrayOfString(stringArrayParam));
                binding.echoMultipleFaults2(request);
            }
            catch (ArrayOfString e1) {
                assertEquals("Wrong fault thrown: " + e1.getClass(), 3, i);
                String[] ret = e1.getValue();
                assertEquals("Array element 1", stringArrayParam[0], ret[0]);
                assertEquals("Array element 2", stringArrayParam[1], ret[1]);
                assertEquals("Array element 3", stringArrayParam[2], ret[2]);
                continue;
            }
            catch (FloatFault e2) {
                assertEquals("Wrong fault thrown: " + e2.getClass(), 1, i);
                assertEquals(floatParam, e2.getPart4(), 0.01F);
                continue;
            }
            catch (StringFault e3) {
                assertEquals("Wrong fault thrown: " + e3.getClass(), 2, i);
                assertEquals(stringParam, e3.getPart2());
                continue;
            }
            
            fail("Should have caught exception");
        }
    }

    public void test6SimpleDocLitPortEchoMultipleFaults3() throws Exception {
        SimpleDocLitPortType binding;
        try {
            binding = new SimpleDocLitServiceLocator().getSimpleDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        String param1 = "Param1";
        String param2 = "Param2";
        for (int i=1; i < 3; i++) {
            try {
              _echoMultipleFaults3Request request = 
                        new _echoMultipleFaults3Request();
                request.setWhichFault(i);
                request.setParam1(param1);
                request.setParam2(param2);
                binding.echoMultipleFaults3(request);
            }
            catch (String2Fault e1) {
                assertEquals("Wrong fault thrown: " + e1.getClass(), 2, i);
                assertEquals(param2, e1.getPart2());
                continue;
            }
            catch (StringFault e2) {
                assertEquals("Wrong fault thrown: " + e2.getClass(), 1, i);
                assertEquals(param1, e2.getPart2());
                continue;
            }
            
            fail("Should have caught exception");
        }
    }

    public void test7SimpleDocLitPortEchoMultipleFaults4() throws Exception {
        SimpleDocLitPortType binding;
        try {
            binding = new SimpleDocLitServiceLocator().getSimpleDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        int intParam = 66;
        Enum enumParam = new Enum(1);
        for (int i=1; i < 3; i++) {
            try {
              _echoMultipleFaults4Request request =
                        new _echoMultipleFaults4Request();
                request.setWhichFault(i);
                request.setParam1(intParam);
                request.setParam2(enumParam);
                binding.echoMultipleFaults4(request);
            }
            catch (EnumFault e1) {
                assertEquals("Wrong fault thrown: " + e1.getClass(), 2, i);
                assertEquals(enumParam.getValue(), e1.getPart9().getValue());
                continue;
            }
            catch (IntFault e2) {
                assertEquals("Wrong fault thrown: " + e2.getClass(), 1, i);
                assertEquals(intParam, e2.getPart3());
                continue;
            }
            
            fail("Should have caught exception");
        }
    }

}
