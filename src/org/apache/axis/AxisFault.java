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
import org.apache.axis.utils.QFault;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Vector;
import java.rmi.RemoteException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An exception which maps cleanly to a SOAP fault.
 * This is a base class for exceptions which are mapped to faults.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author James Snell (jasnell@us.ibm.com)
 */

public class AxisFault extends java.rmi.RemoteException {
    protected QFault    faultCode ;
    protected String    faultString ;
    protected String    faultActor ;
    protected Vector    faultDetails ;  // vector of Element's

    public AxisFault(String code, String str,
                     String actor, Element[] details) {
        super (str);
        setFaultCode( new QFault(Constants.AXIS_NS, code));
        setFaultString( str );
        setFaultActor( actor );
        setFaultDetail( details );
    }

    public AxisFault(QFault code, String str,
                     String actor, Element[] details) {
        super (str);
        setFaultCode( code );
        setFaultString( str );
        setFaultActor( actor );
        setFaultDetail( details );
    }

    public AxisFault(Exception e) {
        super ("", e);
        String  str ;

        // If this is a exception thrown by the web service, we need to pass
        // that back to the client in the fault detail
        if (e instanceof InvocationTargetException) {
            setFaultCode( Constants.FAULT_SERVER_USER );

            // This is a wrapped exception, get the original.
            InvocationTargetException ite = (InvocationTargetException) e;
            Throwable target = ite.getTargetException();

            // Set the exception message (if any) as the fault string 
            setFaultString( target.getMessage() );

            // get the stack trace of the target exception
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream( stream );
            target.printStackTrace(ps);
            ps.close();
        
            // Set the exception name and stack trace in the details
            // TODO: we should serialize any exception data into detail also
            Element[] detailsArray = new Element[2];
            detailsArray[0] = 
                    org.apache.axis.utils.XMLUtils.StringToElement(
                            Constants.AXIS_NS, "exceptionName", target.getClass().getName());
            detailsArray[1] = 
                    org.apache.axis.utils.XMLUtils.StringToElement(
                            Constants.AXIS_NS, "stackTrace", stream.toString());
            setFaultDetail(detailsArray);

        } else {
            setFaultCode( Constants.FAULT_SERVER_GENERAL );

            // put the stack trace in the FaultString
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream( stream );
            e.printStackTrace(ps);
            ps.close();
            Element[] detailsArray = new Element[1];
            detailsArray[0] = 
                    org.apache.axis.utils.XMLUtils.StringToElement(
                            Constants.AXIS_NS, "stackTrace", stream.toString());
            setFaultDetail(detailsArray);
        }
    }

    public AxisFault(String message)
    {
        super (message);
        setFaultCode(Constants.FAULT_SERVER_GENERAL);
        setFaultString(message);
    }

    /**
     * No-arg constructor for building one from an XML stream.
     */
    public AxisFault()
    {
        super ();
    }

    public AxisFault (String message, Throwable t)
    {
        super (message, t);
        setFaultCode(Constants.FAULT_SERVER_GENERAL);
        setFaultString(message);
    }

    public void dump() {
        System.out.println( toString() );
    }

    public void setFaultCode(QFault code) {
        faultCode = code ;
    }

    public void setFaultCode(String code) {
        faultCode = new QFault(Constants.AXIS_NS, code);
    }

    public QFault getFaultCode() {
        return( faultCode );
    }

    public void setFaultString(String str) {
        faultString = str ;
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
        String details = new String();
        if (faultDetails != null) {
            for (int i=0; i < faultDetails.size(); i++) {
                Element e = (Element) faultDetails.get(i);
                Text text = (Text)e.getFirstChild();
                details += "\n\t" +  e.getLocalName() + ": " + text.getData();
            }
        }
        
        return( "AxisFault\n" +
                "  faultCode: " + faultCode + "\n" +
                "  faultString: " + faultString + "\n" +
                "  faultActor: " + faultActor + "\n" +
                "  faultDetail: " + details + "\n"  );
    }
};
