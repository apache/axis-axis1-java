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

/**
 * @author Glen Daniels (gdaniels@apache.org)
 */
package test.functional;

import junit.framework.TestCase;
import org.apache.axis.client.AdminClient;
import org.apache.axis.client.Call;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.utils.Options;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;

public class TestJWSGlobalTypes extends TestCase {
    private static final String TYPEMAPPING_WSDD =
            "<deployment xmlns=\"" + WSDDConstants.URI_WSDD + "\" " +
                        "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\" " +
                        "xmlns:ns=\"http://globalTypeTest\">\n" +
            "  <beanMapping type=\"java:test.functional.GlobalBean\" " +
                        "qname=\"ns:GlobalType\"/>\n" +
            "</deployment>";

    public TestJWSGlobalTypes(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        // Deploy the type mapping
        AdminClient client = new AdminClient();
        Options opts = new Options(null);
        ByteArrayInputStream bis =
                new ByteArrayInputStream(TYPEMAPPING_WSDD.getBytes());
        client.process(opts, bis);
    }

    public void testGlobalTypes() throws Exception {
        Call call = new Call("http://localhost:8080/jws/GlobalTypeTest.jws");
        QName qname = new QName("http://globalTypeTest", "GlobalType");
        call.registerTypeMapping(GlobalBean.class, qname,
                    new BeanSerializerFactory(GlobalBean.class, qname),
                    new BeanDeserializerFactory(GlobalBean.class, qname));
        GlobalBean bean = new GlobalBean();
        bean.setIntValue(4);
        GlobalBean ret = (GlobalBean)call.invoke("echo", new Object [] { bean });
        assertEquals(4, ret.getIntValue());
    }
}
