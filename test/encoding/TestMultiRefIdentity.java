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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.encoding.SerializationContextImpl;

import javax.xml.namespace.QName;
import java.io.CharArrayWriter;

/**
 * @author John Gregg (john.gregg@techarch.com)
 * @author $Author$
 * @version $Revision$
 */
public class TestMultiRefIdentity extends TestCase {
    
    public static Test suite() {
        return new TestSuite(test.encoding.TestMultiRefIdentity.class);
    }
    
    public static void main(String[] argv) {
        
        boolean swing = false;
        if (argv.length > 0) {
            if ("-swing".equals(argv[0])) {
                swing = true;
            }
        }
        
        if (swing) {
            junit.swingui.TestRunner.main(new String[] {"test.encoding.TestMultiRefIdentity"});
        } else {
            System.out.println("use '-swing' for the Swing version.");
            junit.textui.TestRunner.main(new String[] {"test.encoding.TestMultiRefIdentity"});
        }
    }
    
    
    public TestMultiRefIdentity(String name) {
        super(name);
    }
    
    /**
       Tests when beans are identical and use default hashCode().
    */
    public void testIdentity1() throws Exception {
        TestBeanA tb1 = new TestBeanA();
        tb1.s1 = "john";
        TestBeanA tb2 = tb1;
        
        CharArrayWriter caw = new CharArrayWriter();
        SerializationContextImpl sci = new SerializationContextImpl(caw);
        sci.setDoMultiRefs(true);
        sci.serialize(new QName("someLocalPart"), null, tb1);
        sci.serialize(new QName("someOtherLocalPart"), null, tb2);
        
        String s = caw.toString();
        
        // Cheap but fragile.
        int first = s.indexOf("#id0");
        int last = s.lastIndexOf("#id0");
        assertTrue(s, first >= 0);
        assertTrue(s, last >= 0 && last != first);
    }
    
    /**
       Tests when beans are identical and use their own hashCode().
    */
    public void testIdentity2() throws Exception {
        TestBeanB tb1 = new TestBeanB();
        tb1.s1 = "john";
        TestBeanB tb2 = tb1;
        
        CharArrayWriter caw = new CharArrayWriter();
        SerializationContextImpl sci = new SerializationContextImpl(caw);
        sci.setDoMultiRefs(true);
        sci.serialize(new QName("someLocalPart"), null, tb1);
        sci.serialize(new QName("someOtherLocalPart"), null, tb2);
        
        String s = caw.toString();
        
        // Cheap but fragile.
        int first = s.indexOf("#id0");
        int last = s.lastIndexOf("#id0");
        assertTrue(s,first >= 0);
        assertTrue(s,last >= 0 && last != first);
    }
    
    /**
       Tests when beans have different contents and rely on default hashCode().
    */
    public void testEquality1() throws Exception {
        TestBeanA tb1 = new TestBeanA();
        tb1.s1 = "john";
        TestBeanA tb2 = new TestBeanA();
        tb2.s1 = "gregg";
        
        CharArrayWriter caw = new CharArrayWriter();
        SerializationContextImpl sci = new SerializationContextImpl(caw);
        sci.setDoMultiRefs(true);
        sci.serialize(new QName("someLocalPart"), null, tb1);
        sci.serialize(new QName("someOtherLocalPart"), null, tb2);
        
        String s = caw.toString();
        
        // Cheap but fragile.
        int first = s.indexOf("#id0");
        int last = s.lastIndexOf("#id1");
        assertTrue(s,first >= 0);
        assertTrue(s,last >= 0);
    }
    
    /**
       Tests when beans have same contents but rely on default hashCode().
    */
    public void testEquality2() throws Exception {
        TestBeanA tb1 = new TestBeanA();
        tb1.s1 = "john";
        TestBeanA tb2 = new TestBeanA();
        tb2.s1 = "john";
        
        CharArrayWriter caw = new CharArrayWriter();
        SerializationContextImpl sci = new SerializationContextImpl(caw);
        sci.setDoMultiRefs(true);
        sci.serialize(new QName("someLocalPart"), null, tb1);
        sci.serialize(new QName("someOtherLocalPart"), null, tb2);
        
        String s = caw.toString();
        
        // Cheap but fragile.
        int first = s.indexOf("#id0");
        int last = s.lastIndexOf("#id1");
        assertTrue(s,first >= 0);
        assertTrue(s,last >= 0);
    }
    
    /**
       Tests when beans have same contents and use their own hashCode().
    */
    public void testEquality3() throws Exception {
        TestBeanB tb1 = new TestBeanB();
        tb1.s1 = "john";
        TestBeanB tb2 = new TestBeanB();
        tb2.s1 = "john";
        
        CharArrayWriter caw = new CharArrayWriter();
        SerializationContextImpl sci = new SerializationContextImpl(caw);
        sci.setDoMultiRefs(true);
        sci.serialize(new QName("someLocalPart"), null, tb1);
        sci.serialize(new QName("someOtherLocalPart"), null, tb2);
        
        String s = caw.toString();
        
        // Cheap but fragile.
        int first = s.indexOf("#id0");
        int last = s.lastIndexOf("#id1");
        assertTrue(s,first >= 0);
        assertTrue(s,last >= 0 && last != first);
    }
    
    class TestBeanA {
        String s1 = null;
        
        // uses default equals() and hashCode().
    }
    
    class TestBeanB {
        String s1 = null;
        
        public boolean equals(Object o) {
            if (o == null) return false;
            if (this == o) return true;
            if (!o.getClass().equals(this.getClass())) return false;
            
            TestBeanB tbb = (TestBeanB)o;
            if (this.s1 != null) {
                return this.s1.equals(tbb.s1);
            } else {
                return this.s1 == tbb.s1;
            }
        }
        
        public int hashCode() {
            // XXX???
            if (this.s1 == null) return super.hashCode();
            else return this.s1.hashCode();
        }
    }
}
