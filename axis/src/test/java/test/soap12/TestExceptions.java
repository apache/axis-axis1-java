package test.soap12;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.client.Call;
import test.GenericLocalTest;

import javax.xml.namespace.QName;

/**
 * Ensure that SOAP 1.2's FAULT_SUBCODE_PROC_NOT_PRESENT is thrown if the method is not found
 */ 
public class TestExceptions extends GenericLocalTest {
    public TestExceptions() {
        super("foo");
    }

    public TestExceptions(String s) {
        super(s);
    }

    /**
     * base test ensure that SOAP1.2 works :)
     * @throws Exception
     */ 
	public void testEcho() throws Exception {
        Object result = null;
        Call call = getCall();
        call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
        result = call.invoke("echo", null);
        assertEquals(result.toString(), "hello world");
	}
    
    /**
     * call a method that does not exist and check if the correct exception
     * is thrown from the server.
     * @throws Exception
     */ 
    public void testNoSuchProcedure() throws Exception {
        Object result = null;
        try {
            Call call = getCall();
            call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
            result = call.invoke("unknownFreakyMethod", null);
        } catch (AxisFault fault){
            assertEquals(Constants.FAULT_SOAP12_SENDER, fault.getFaultCode());
            QName [] subCodes = fault.getFaultSubCodes();
            assertNotNull(subCodes);
            assertEquals(1, subCodes.length);
            assertEquals(Constants.FAULT_SUBCODE_PROC_NOT_PRESENT, subCodes[0]);
            return;
        }
        fail("Didn't catch expected fault");
    }

    /**
     * Service method.  Returns a string
     * 
     * @return a string
     */ 
    public String echo() {
        return "hello world";
    }    
}
