/*
 * Copyright 2002,2004 The Apache Software Foundation.
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

package test.functional;

import junit.framework.TestCase;
import org.apache.axis.AxisProperties;
import org.apache.axis.client.AdminClient;
import org.apache.axis.utils.NetworkUtils;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.Stub;
import java.net.URL;
import java.util.Date;

public class TestAutoTypes extends TestCase {

    protected void setUp() throws java.lang.Exception {
        AxisProperties.setProperty("axis.doAutoTypes", "true");
        String[] args = {"test/functional/auto-deploy.wsdd"};
        AdminClient.main(args);
    }

    protected void tearDown() throws java.lang.Exception {
        AxisProperties.setProperty("axis.doAutoTypes", "false");
        String[] args = {"test/functional/auto-undeploy.wsdd"};
        AdminClient.main(args);
    }

    private IAutoTypes getSimpleProxy() throws Exception {
        String thisHost = NetworkUtils.getLocalHostname();
        String thisPort = System.getProperty("test.functional.ServicePort",
                "8080");

        //location of wsdl file
        String wsdlLocation = "http://" + thisHost + ":" + thisPort +
                "/jws/AutoTypesTest.jws?wsdl";
        URL urlWsdl = new URL(wsdlLocation);
        String nameSpaceUri = "http://" + thisHost + ":" + thisPort +
                "/jws/AutoTypesTest.jws";
        String serviceName = "AutoTypesTestService";
        String portName = "AutoTypesTest";
        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        Service service = serviceFactory.createService(urlWsdl, new
                QName(nameSpaceUri, serviceName));
        Stub stub = (Stub) service.getPort(new
                QName(nameSpaceUri, portName), IAutoTypes.class);
        IAutoTypes myProxy = (IAutoTypes) stub;
        return myProxy;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(TestAutoTypes.class);
    }

    public void testPing() throws Exception {
        IAutoTypes test = getSimpleProxy();
        String ret = test.ping();
        assertEquals("echoed string incorrect IntValue", "Pong", ret);
    }

    public void testGetBean() throws Exception {
        getSimpleProxy().getBean();
    }

    public void testSetBean() throws Exception {
        getSimpleProxy().setBean(new SimpleBean());
    }

    public void testEchoBean() throws Exception {
        SimpleBean in = new SimpleBean();
        in.setIntValue(42);
        SimpleBean out = getSimpleProxy().echoBean(in);
        assertEquals("echoed bean incorrect IntValue", 42, out.getIntValue());
    }

    public void testEchoBeanArray() throws Exception {
        SimpleBean[] beans = (SimpleBean[]) getSimpleProxy().echoBeanArray(new SimpleBean[]{
            new SimpleBean(), new SimpleBean(),
            new SimpleBean(), new SimpleBean(), new SimpleBean(),
            new SimpleBean()});
        assertEquals("expected array of SimpleBean", SimpleBean[].class, beans
                .getClass());
        assertEquals("expected array of SimpleBean of length 6", 6,
                beans.length);
    }

    public void testGetBeanArray() throws Exception {
        SimpleBean[] beans = (SimpleBean[]) getSimpleProxy().getBeanArray(6);
        assertEquals("expected array of SimpleBean", SimpleBean[].class, beans
                .getClass());
        assertEquals("expected array of SimpleBean of length 6", 6,
                beans.length);
    }

    public void testSetBeanArray() throws Exception {
        SimpleBean[] beans = new SimpleBean[]{new SimpleBean(),
                                              new SimpleBean(),
                                              new SimpleBean(),
                                              new SimpleBean(),
                                              new SimpleBean(),
                                              new SimpleBean()};
        getSimpleProxy().setBeanArray(beans);
    }

    public void testGetNestedBean() throws Throwable {
        NestedBean result = getSimpleProxy().getNestedBean();
        assertNotNull("StartDate is not null ", result.getStartDate());
        assertEquals("Test String is correct",
                "some test string " + result.getStartDate(),
                result.getTestString()); //$NON-NLS-1$
        assertEquals("Result Array Correct length ", 3,
                result.getSimpleBeanList().length); //$NON-NLS-1$
        assertEquals("Result Array[0] Correct", 1,
                result.getSimpleBeanList()[0].getIntValue()); //$NON-NLS-1$
        assertEquals("Result Array[1] Correct", 2,
                result.getSimpleBeanList()[1].getIntValue()); //$NON-NLS-1$
        assertEquals("Result Array[2] Correct", 3,
                result.getSimpleBeanList()[2].getIntValue()); //$NON-NLS-1$
    }

    public void testSetNestedBean() throws Throwable {
        NestedBean result = new NestedBean();
        result.setStartDate(new Date());
        result.setTestString("some test string " + result.getStartDate()); //$NON-NLS-1$
        SimpleBean[] sba = new SimpleBean[3];
        sba[0] = new SimpleBean();
        sba[1] = new SimpleBean();
        sba[2] = new SimpleBean();
        sba[0].setIntValue(1);
        sba[1].setIntValue(2);
        sba[2].setIntValue(3);
        result.setSimpleBeanList(sba);
        getSimpleProxy().setNestedBean(result);
    }

    public void testEchoNestedBean() throws Throwable {
        NestedBean result = new NestedBean();
        result.setStartDate(new Date());
        result.setTestString("some test string " + result.getStartDate()); //$NON-NLS-1$
        SimpleBean[] sba = new SimpleBean[3];
        sba[0] = new SimpleBean();
        sba[1] = new SimpleBean();
        sba[2] = new SimpleBean();
        sba[0].setIntValue(1);
        sba[1].setIntValue(2);
        sba[2].setIntValue(3);
        result.setSimpleBeanList(sba);
        getSimpleProxy().echoNestedBean(result);
    }
}
