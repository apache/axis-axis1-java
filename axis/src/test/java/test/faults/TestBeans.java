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

package test.faults;

import org.apache.axis.client.Call;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.Constants;
import org.apache.axis.AxisFault;
import test.GenericLocalTest;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

/**
 * Confirm that faults using beans work 
 */
public class TestBeans extends GenericLocalTest {
    private QName TYPE_QNAME = new QName("ns", "dataType");

    public TestBeans() {
        super("service");
    }

    public TestBeans(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp(false); // don't deploy here
        TypeMapping tm = (TypeMapping)config.getTypeMappingRegistry().
                getDefaultTypeMapping();
        tm.register(BeanFault.class, TYPE_QNAME,
                    new BeanSerializerFactory(BeanFault.class, TYPE_QNAME),
                    new BeanDeserializerFactory(BeanFault.class, TYPE_QNAME));
        deploy("service", this.getClass(), Style.WRAPPED);
        //tm.register(BeanFault2.class, TYPE_QNAME,
        //            new BeanSerializerFactory(BeanFault2.class, TYPE_QNAME),
        //            new BeanDeserializerFactory(BeanFault2.class, TYPE_QNAME));
    }

    public void testBeanFault() throws Exception {
        Call call = getCall();
        call.setOperationStyle("wrapped");
        call.setOperationUse("literal");
        call.setEncodingStyle("");
        call.registerTypeMapping(BeanFault.class, TYPE_QNAME,
                    new BeanSerializerFactory(BeanFault.class, TYPE_QNAME),
                    new BeanDeserializerFactory(BeanFault.class, TYPE_QNAME));
        call.setReturnClass(BeanFault.class);
        call.addParameter("arg0", Constants.XSD_STRING, ParameterMode.IN);
        String data = "bean fault test - 1";
        try {
            call.invoke("echoString", new Object [] { data });
        } catch (AxisFault af){
            assertNotNull(af.detail);
            assertEquals(BeanFault.class,af.detail.getClass());
            assertEquals(data,((BeanFault)af.detail).getMessage());
            return;
        }
        fail("did not catch fault");
    }

    public void testBeanFault2() throws Exception {
        Call call = getCall();
        call.setOperationStyle("wrapped");
        call.setOperationUse("literal");
        call.setEncodingStyle("");
        call.registerTypeMapping(BeanFault2.class, TYPE_QNAME,
                    new BeanSerializerFactory(BeanFault2.class, TYPE_QNAME),
                    new BeanDeserializerFactory(BeanFault2.class, TYPE_QNAME));
        call.setReturnClass(BeanFault2.class);
        call.addParameter("arg0", Constants.XSD_STRING, ParameterMode.IN);
        String data = "bean fault test - 2";
        try {
            call.invoke("echoString2", new Object [] { data });
        } catch (AxisFault af){
            assertNotNull(af.detail);
            assertEquals(BeanFault2.class,af.detail.getClass());
            assertEquals(data,((BeanFault2)af.detail).getMessage());
            return;
        }
        fail("did not catch fault");
    }

    public String echoString(String data) throws BeanFault {
        BeanFault f = new BeanFault();
        f.setMessage(data);
        throw f;
    }

    public String echoString2(String data) throws BeanFault2 {
        BeanFault2 f = new BeanFault2(data);
        throw f;
    }
}
