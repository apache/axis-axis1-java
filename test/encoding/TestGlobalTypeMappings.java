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

package test.encoding;

import org.apache.axis.client.Call;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.enum.Style;
import test.GenericLocalTest;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

/**
 * Confirm that global type mappings work in both RPC and Document
 * contexts.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class TestGlobalTypeMappings extends GenericLocalTest {
    private QName TYPE_QNAME = new QName("ns", "dataType");

    public TestGlobalTypeMappings() {
        super("service");
    }

    public TestGlobalTypeMappings(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp(false); // don't deploy here
        TypeMapping tm = (TypeMapping)config.getTypeMappingRegistry().
                getDefaultTypeMapping();
        tm.register(Data.class, TYPE_QNAME,
                    new BeanSerializerFactory(Data.class, TYPE_QNAME),
                    new BeanDeserializerFactory(Data.class, TYPE_QNAME));
    }

    public void testDocLit() throws Exception {
        deploy("service", this.getClass(), Style.WRAPPED);
        Call call = getCall();
        call.setOperationStyle("wrapped");
        call.setOperationUse("literal");
        call.setEncodingStyle("");
        call.registerTypeMapping(Data.class, TYPE_QNAME,
                    new BeanSerializerFactory(Data.class, TYPE_QNAME),
                    new BeanDeserializerFactory(Data.class, TYPE_QNAME));
        call.setReturnClass(Data.class);
        call.addParameter("arg0", TYPE_QNAME, ParameterMode.IN);
        Data data = new Data();
        data.stringMember = "doc lit test";
        data.floatMember = new Float(451.0F);
        call.invoke("echoData", new Object [] { data });
    }

    /**
     * Our service method.  We'll deploy this several ways.
     *
     * @param data
     * @return
     */
    public Data echoData(Data data) {
        return data;
    }
}
