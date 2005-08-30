package test.wsdl.qnameser;

import org.apache.axis.client.AdminClient;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.Text;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class QNameSerTestCase extends junit.framework.TestCase {

    public QNameSerTestCase(String name) {
        super(name);
    }
    
    public void testQName() throws Exception {
        PlanWSSoap binding;
        try {
            PlanWSLocator locator = new PlanWSLocator();
            binding = locator.getPlanWSSoap();
            deployServer();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        } 
        catch (Exception e) {
            throw new junit.framework.AssertionFailedError("Binding initialization Exception caught: " + e);
        }
        assertTrue("binding is null", binding != null);

        binding.getPlan(PlanService.Q_1);
        binding.getPlan(PlanService.Q_2);
        binding.getPlan(PlanService.Q_3);
    }


    public void testQNameList() throws Exception {
        PlanWSSoap binding;
        try {
            PlanWSLocator locator = new PlanWSLocator();
            binding = locator.getPlanWSSoap();
            deployServer();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        } 
        catch (Exception e) {
            throw new junit.framework.AssertionFailedError("Binding initialization Exception caught: " + e);
        }
        assertTrue("binding is null", binding != null);

        QName [] list =
            new QName[] {PlanService.Q_1, PlanService.Q_2, PlanService.Q_3};
        GetMPlan in = new GetMPlan(list);
        binding.getMPlan(in);
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

