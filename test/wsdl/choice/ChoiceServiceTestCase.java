/*
 * Copyright 2002-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.wsdl.choice;

import java.util.Date;
import java.util.Calendar;

import org.apache.axis.types.URI;
import org.apache.axis.encoding.ser.CalendarSerializer;

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

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

public class ChoiceServiceTestCase extends TestCase {

    public ChoiceServiceTestCase(String name) {
        super(name);
    }

    public void testSerialization() throws Exception {
        ChoiceServiceSoap binding;
        try {
            ChoiceServiceLocator locator = new ChoiceServiceLocator();
            binding = locator.getChoiceServiceSoap();
            deployServer();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        } 
        catch (Exception e) {
            throw new AssertionFailedError("Binding initialization Exception caught: " + e);
        }
        assertTrue("binding is null", binding != null);

        FaultPropertyType fp1;
        FaultPropertyType fp2;
        Record1 r1;
        Record2 r2;

        // test 1
        FaultType f1 = new FaultType();
        f1.setDescription("foo");
        f1.setCommand("bar");

        fp1 = new FaultPropertyType();
        fp1.setFault1(f1);

        r1 = new Record1();
        r1.setElem(fp1);

        r2 = binding.get(r1);

        assertTrue(r2 != null);

        fp2 = r2.getElem();
        
        assertTrue(fp2 != null);

        assertTrue(fp2.getFault1() != null);
        assertTrue(fp2.getFault1() instanceof FaultType);
        assertEquals("foo", 
                     ((FaultType)fp2.getFault1()).getDescription());
        assertEquals("bar", 
                     ((FaultType)fp2.getFault1()).getCommand());
        
        // test 2
        StagingFaultType f2 = new StagingFaultType();
        f2.setDescription("foo1");
        f2.setCommand("bar1");
        f2.setAttribute("moo");

        fp1 = new FaultPropertyType();
        fp1.setFault2(f2);

        r1 = new Record1();
        r1.setElem(fp1);

        r2 = binding.get(r1);

        assertTrue(r2 != null);

        fp2 = r2.getElem();
        
        assertTrue(fp2 != null);

        assertTrue(fp2.getFault2() != null);
        assertTrue(fp2.getFault2() instanceof StagingFaultType);
        assertEquals("foo1", 
                     ((FaultType)fp2.getFault2()).getDescription());
        assertEquals("bar1", 
                     ((FaultType)fp2.getFault2()).getCommand());
        assertEquals("moo", 
                     ((StagingFaultType)fp2.getFault2()).getAttribute());

    }

    public void testChoiceSerialization() throws Exception {
        ChoiceServiceSoap binding;
        try {
            ChoiceServiceLocator locator = new ChoiceServiceLocator();
            binding = locator.getChoiceServiceSoap();
            deployServer();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        } 
        catch (Exception e) {
            throw new AssertionFailedError("Binding initialization Exception caught: " + e);
        }
        assertTrue("binding is null", binding != null);

        FaultPropertyType fp1;
        Record1 r1;
        Record2 r2;

        // serialize f1
        FaultType f1 = new FaultType();
        f1.setDescription("foo");
        f1.setCommand("bar");

        fp1 = new FaultPropertyType();
        fp1.setFault1(f1);

        r1 = new Record1();
        r1.setElem(fp1);

        r2 = binding.get(r1);
        assertTrue(r2 != null);

        // serialize f2
        StagingFaultType f2 = new StagingFaultType();
        f2.setDescription("foo1");
        f2.setCommand("bar1");
        f2.setAttribute("moo");

        fp1 = new FaultPropertyType();
        fp1.setFault2(f2);
        
        r1 = new Record1();
        r1.setElem(fp1);

        r2 = binding.get(r1);
        assertTrue(r2 != null);
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
