package test.wsdl.interop3.docLit;


import test.wsdl.interop3.docLit.xsd.ArrayOfstring_literal;
import test.wsdl.interop3.docLit.xsd.SOAPStruct;

import java.net.URL;

/*
    <!-- SOAP Builder's round III web services          -->
    <!-- interoperability testing:  import1             -->
    <!-- (see http://www.whitemesa.net/r3/plan.html)    -->
    <!-- Step 1.  Start with predefined WSDL            -->
    <!-- Step 2.  Generate client from predefined WSDL  -->
    <!-- Step 3.  Test generated client against         -->
    <!--          pre-built server                      -->
    <!-- Step 4.  Generate server from predefined WSDL  -->
    <!-- Step 5.  Test generated client against         -->
    <!--          generated server                      -->
    <!-- Step 6.  Generate second client from           -->
    <!--          generated server's WSDL (some clients -->
    <!--          can do this dynamically)              -->
    <!-- Step 7.  Test second generated client against  -->
    <!--          generated server                      -->
    <!-- Step 8.  Test second generated client against  -->
    <!--          pre-built server                      -->
*/

public class DocLitTestCase extends junit.framework.TestCase {
    static URL url;

    public DocLitTestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
    }

    public void testStep3() throws Exception {
        WSDLInteropTestDocLitPortType binding;
        try {
            if (url != null) {
                binding = new WSDLInteropTestDocLitServiceLocator().getWSDLInteropTestDocLitPort(url);
            } else {
                binding = new WSDLInteropTestDocLitServiceLocator().getWSDLInteropTestDocLitPort();
            }
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        String str = "Hello there!";
        String [] strArray = new String [] { "1", "two", "trois" };
        ArrayOfstring_literal param = new ArrayOfstring_literal();
        param.setString(strArray);

        assertEquals("echoString results differ", binding.echoString(str), str);

        String [] resArray = binding.echoStringArray(param).getString();
        assertEquals("String array lengths differ",
                     strArray.length,
                     resArray.length);
        for (int i = 0; i < strArray.length; i++) {
            assertEquals("Array members at index " + i + " differ",
                         strArray[i],
                         resArray[i]);
        }

        SOAPStruct struct = new SOAPStruct();
        struct.setVarFloat(3.14159F);
        struct.setVarInt(69);
        struct.setVarString("Struct-o-rama");

        assertTrue("Structs weren't equal",
                   struct.equals(binding.echoStruct(struct)));
        
        // test echoVoid
        binding.echoVoid();
    }



    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                url = new URL(args[0]);
            } catch (Exception e) {
            }
        }

        junit.textui.TestRunner.run(new junit.framework.TestSuite(DocLitTestCase.class));
    } // main
}

