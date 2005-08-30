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
        String text=af.getFaultString();
        assertNotNull(text);
        text=af.toString();
        assertNotNull(text);
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
