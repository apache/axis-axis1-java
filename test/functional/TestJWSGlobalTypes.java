/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
