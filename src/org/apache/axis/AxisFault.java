/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

package org.apache.axis ;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFaultElement;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.xml.namespace.QName;

/**
 * An exception which maps cleanly to a SOAP fault.
 * This is a base class for exceptions which are mapped to faults.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author James Snell (jasnell@us.ibm.com)
 */

public class AxisFault extends java.rmi.RemoteException {
    protected static Log log =
        LogFactory.getLog(AxisFault.class.getName());

    private static final String LS = System.getProperty("line.separator");

    protected QName     faultCode ;
    protected String    faultString = "";
    protected String    faultActor ;
    protected Vector    faultDetails ;  // vector of Element's

    /**
     * Make an AxisFault based on a passed Exception.  If the Exception is
     * already an AxisFault, simply use that.  Otherwise, wrap it in an
     * AxisFault.  If the Exception is an InvocationTargetException (which
     * already wraps another Exception), get the wrapped Exception out from
     * there and use that instead of the passed one.
     */ 
    public static AxisFault makeFault(Exception e)
    {
        if (e instanceof InvocationTargetException) {
            Throwable t = ((InvocationTargetException)e).getTargetException();
            if (t instanceof Exception) {
                e = (Exception)t;
            }
        }
        
        if (e instanceof AxisFault) {
            return (AxisFault)e;
        }
        
        return new AxisFault(e);
    }
    
    public AxisFault(String code, String str,
                     String actor, Element[] details) {
        super (str);
        setFaultCode( new QName(Constants.NS_URI_AXIS, code));
        setFaultString( str );
        setFaultActor( actor );
        setFaultDetail( details );
        if (details == null)
            initFromException(this);
    }

    public AxisFault(QName code, String str,
                     String actor, Element[] details) {
        super (str);
        setFaultCode( code );
        setFaultString( str );
        setFaultActor( actor );
        setFaultDetail( details );
        if (details == null)
            initFromException(this);
    }

    /**
     * Wrap an AxisFault around an existing Exception - this is private
     * to force everyone to use makeFault() above, which sanity-checks us.
     */ 
    protected AxisFault(Exception target) {
        super ("", target);

        setFaultCode( Constants.FAULT_SERVER_USER );
        
        initFromException(target);
    }

    public AxisFault(String message)
    {
        super (message);
        setFaultCode(Constants.FAULT_SERVER_GENERAL);
        setFaultString(message);
        initFromException(this);
    }

    /**
     * No-arg constructor for building one from an XML stream.
     */
    public AxisFault()
    {
        super();
        setFaultCode(Constants.FAULT_SERVER_GENERAL);     
        initFromException(this);
    }

    public AxisFault (String message, Throwable t)
    {
        super (message, t);
        setFaultCode(Constants.FAULT_SERVER_GENERAL);
        setFaultString(message);
    }

    private void initFromException(Exception target)
    {
        for (int i = 0; faultDetails != null && i < faultDetails.size(); i++) {
            Element element = (Element) faultDetails.elementAt(i);
            if ("stackTrace".equals(element.getLocalName()) &&
                Constants.NS_URI_AXIS.equals(element.getNamespaceURI())) {
                // ??? Should we replace it or just let it be?
                return;
            }
        }

        // Set the exception message (if any) as the fault string 
        setFaultString( target.toString() );
        
        if (faultDetails == null) faultDetails = new Vector();

        Element el;
        
        // If we're derived from AxisFault, then put the exception class
        // into the "exceptionName" element in the details.  This allows
        // us to get back a correct Java Exception class on the other side
        // (assuming they have it available).
        
        if ((target instanceof AxisFault) &&
            (target.getClass() != AxisFault.class)) {
            el = XMLUtils.StringToElement(Constants.NS_URI_AXIS, 
                                                  "exceptionName", 
                                                  target.getClass().getName());
            
            faultDetails.add(el);        
        }
        
        el =  XMLUtils.StringToElement(Constants.NS_URI_AXIS, 
                                       "stackTrace", 
                                       JavaUtils.stackToString(target));

        faultDetails.add(el);
    }
    
    public void dump()
    {
        log.debug(dumpToString());
    }

    public String dumpToString()
    {
        String details = new String();

        if (faultDetails != null) {
            for (int i=0; i < faultDetails.size(); i++) {
                Element e = (Element) faultDetails.get(i);
                Text text = (Text)e.getFirstChild();
                details += LS + "\t" +  e.getLocalName() + ": " + text.getData();
            }
        }
        
        return "AxisFault" + LS
            + " faultCode: " + faultCode + LS
            + " faultString: " + faultString + LS
            + " faultActor: " + faultActor + LS
            + " faultDetail: " + details + LS
            ;
    }

    public void setFaultCode(QName code) {
        faultCode = code ;
    }

    public void setFaultCode(String code) {
        faultCode = new QName(Constants.NS_URI_AXIS, code);
    }

    public QName getFaultCode() {
        return( faultCode );
    }

    public void setFaultString(String str) {
        if (str != null) {
            faultString = str ;
        } else {
            faultString = "";
        }
    }

    public String getFaultString() {
        return( faultString );
    }

    public void setFaultActor(String actor) {
        faultActor = actor ;
    }

    public String getFaultActor() {
        return( faultActor );
    }

    public void setFaultDetail(Element[] details) {
        if ( details == null ) return ;
        faultDetails = new Vector( details.length );
        for ( int loop = 0 ; loop < details.length ; loop++ )
            faultDetails.add( details[loop] );
    }

    public void setFaultDetailString(String details) {
        faultDetails = new Vector();
        Document doc = XMLUtils.newDocument();
        Element element = doc.createElement("string");
        Text text = doc.createTextNode(details);
        element.appendChild(text);
        faultDetails.add(element);
    }

    public Element[] getFaultDetails() {
        if (faultDetails == null) return null;
        Element result[] = new Element[faultDetails.size()];
        for (int i=0; i<result.length; i++)
            result[i] = (Element) faultDetails.elementAt(i);
        return result;
    }
    
    public void output(SerializationContext context) throws Exception {

        SOAPEnvelope envelope = new SOAPEnvelope();

        SOAPFaultElement fault =
                                new SOAPFaultElement(this);
        envelope.addBodyElement(fault);

        envelope.output(context);
    }

    public String toString() {
        return faultString;
    }

    public void printStackTrace(PrintStream ps) {
        ps.println(dumpToString());
        super.printStackTrace(ps);
    }

    public void printStackTrace(java.io.PrintWriter pw) {
        pw.println(dumpToString());
        super.printStackTrace(pw);
    }
};
