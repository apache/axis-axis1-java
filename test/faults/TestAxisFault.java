/*
 *  The Apache Software License, Version 1.1
 *
 *  Copyright (c) 2002 The Apache Software Foundation.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution, if
 *  any, must include the following acknowlegement:
 *  "This product includes software developed by the
 *  Apache Software Foundation (http://www.apache.org/)."
 *  Alternately, this acknowlegement may appear in the software itself,
 *  if and wherever such third-party acknowlegements normally appear.
 *
 *  4. The names "The Jakarta Project", "Ant", and "Apache Software
 *  Foundation" must not be used to endorse or promote products derived
 *  from this software without prior written permission. For written
 *  permission, please contact apache@apache.org.
 *
 *  5. Products derived from this software may not be called "Apache"
 *  nor may "Apache" appear in their names without prior written
 *  permission of the Apache Group.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of the Apache Software Foundation.  For more
 *  information on the Apache Software Foundation, please see
 *  <http://www.apache.org/>.
 */


package test.faults;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.NoEndPointException;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

/**
 * unit tests for the ubiquitous AxisFault 
 */
public class TestAxisFault extends TestCase {

    public TestAxisFault(String s) {
        super(s);
    }


    public static Test suite() {
        return new TestSuite(TestAxisFault.class);
    }
    /**
     * test that exceptions are filled in
     */
    public void testExceptionFillIn() {
        Exception e=new Exception("foo");
        AxisFault af=AxisFault.makeFault(e);
        Element stackTrace;
        stackTrace = af.lookupFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE);
        assertNotNull(stackTrace);
        Element exceptionName;
        exceptionName = af.lookupFaultDetail(Constants.QNAME_FAULTDETAIL_EXCEPTIONNAME);
        assertNull(exceptionName);
        QName faultCode=af.getFaultCode();
        assertEquals(faultCode.getLocalPart(), Constants.FAULT_SERVER_USER);
    }


    /**
     * test that making an axis fault from an axis fault retains it
     */
    public void testAxisFaultFillIn() {
        AxisFault af1=new AxisFault("fault1");
        AxisFault af2=AxisFault.makeFault(af1);
        assertSame(af1,af2);
    }

    /**
     * test we can remove some detail
     */
    public void testDetailRemoval() {
        Exception e = new Exception("foo");
        AxisFault af = AxisFault.makeFault(e);
        assertTrue(af.removeFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE));
        Element stackTrace;
        stackTrace = af.lookupFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE);
        assertNull(stackTrace);

    }

    /**
     * test what happens with subclasses. We expect the classname to be preserved
     * in the details
     */
    public void testSubclassProcessing() {
        AxisFault af=new NoEndPointException();
        Element exceptionName;
        exceptionName = af.lookupFaultDetail(Constants.QNAME_FAULTDETAIL_EXCEPTIONNAME);
        assertNotNull(exceptionName);
        String exceptionClassname= XMLUtils.getInnerXMLString(exceptionName);
        assertTrue(exceptionClassname.indexOf("NoEndPointException")>=0);
    }

    /**
     * verify we can properly lookup empty namespace stuff
     */
    public void testEmptyNamespaceLookup() {
        AxisFault af=new AxisFault();
        af.addFaultDetailString("alles geht gut");
        Element match=af.lookupFaultDetail(new QName(null,"string"));
        assertNotNull(match);
    }

    public void testArrayAddWorks() {
        AxisFault af = new AxisFault();
        af.addFaultDetailString("alles geht gut");
        Element array[]=new Element[2];
        array[0] = createElement("ein","un");
        array[1] = createElement("zwei", "deux");
        af.setFaultDetail(array);
        Element match = af.lookupFaultDetail(new QName(null, "zwei"));
        assertNotNull(match);
        Element old = af.lookupFaultDetail(new QName(null, "string"));
        assertNull(old);
    }

    public void testEmptyArrayAddWorks() {
        AxisFault af = new AxisFault();
        af.addFaultDetailString("alles geht gut");
        Element array[] = new Element[0];
        af.setFaultDetail(array);
        Element old = af.lookupFaultDetail(new QName(null, "string"));
        assertNull(old);
    }

    public Element createElement(String tag,String child) {
        Document doc = null;
        try {
            doc = XMLUtils.newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("xml trouble");
        }
        Element element = doc.createElement(tag);
        Text text = doc.createTextNode(child);
        element.appendChild(text);
        return element;
    }

    /**
     * helper method to stick in when diagnosing stuff
     * @param af
     */
    private void dumpFault(AxisFault af) {
        String s=af.dumpToString();
        System.out.println(s);
    }
}
