/*
 * The Apache Software License, Version 1.1
 *
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
