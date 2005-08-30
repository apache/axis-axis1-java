package test.wsdl.opStylesDoc;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import java.rmi.RemoteException;

public class VerifyTestCase extends junit.framework.TestCase {
    public VerifyTestCase(String name) {
        super(name);
    }

    public void testOpStyles() throws Exception {
        OpStyles binding;
        try {
            binding = new OpStyleDocServiceLocator().getOpStylesDoc();
        }
        catch (ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }

        binding.requestResponse();
        binding.requestResponse2();
        binding.requestResponse3(null);

   } // testOpStyles
} // class VerifyTestCase
