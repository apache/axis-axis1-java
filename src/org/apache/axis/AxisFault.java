/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * An exception which maps cleanly to a SOAP fault.
 * This is a base class for exceptions which are mapped to faults.
 * SOAP faults contain
 * <ol>
 * <li>A fault string
 * <li>A fault code
 * <li>A fault actor
 * <li>Fault details; an xml tree of fault specific stuff
 * </ol>
 * @author Doug Davis (dug@us.ibm.com)
 * @author James Snell (jasnell@us.ibm.com)
 * @author Steve Loughran
 */

public class AxisFault extends java.rmi.RemoteException {
    protected static Log log =
        LogFactory.getLog(AxisFault.class.getName());

    protected QName     faultCode ;
    /** SOAP1.2 addition: subcodes of faults; a Vector of QNames */
    protected Vector    faultSubCode ;
    protected String    faultString = "";
    protected String    faultActor ;
    protected Vector    faultDetails ;  // vector of Element's
    protected String    faultNode ;

    /** SOAP headers which should be serialized with the Fault */
    protected ArrayList faultHeaders = null;

    /** wrapped remote exception */
    protected Throwable detail2 = null;
    
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

    /**
     * make a fault
     * @param code fault code which will be passed into the Axis namespace
     * @param faultString fault string
     * @param actor fault actor
     * @param details details; if null the current stack trace and classname is
     * inserted into the details.
     */
    public AxisFault(String code, String faultString,
                     String actor, Element[] details) {
        this(new QName(Constants.NS_URI_AXIS, code),
                faultString, actor, details);
    }

    /**
     * make a fault in any namespace
     * @param code fault code which will be passed into the Axis namespace
     * @param faultString fault string
     * @param actor fault actor
     * @param details details; if null the current stack trace and classname is
     * inserted into the details.
     */
    public AxisFault(QName code, String faultString,
                     String actor, Element[] details) {
        super (faultString);
        setFaultCode( code );
        setFaultString( faultString );
        setFaultActor( actor );
        setFaultDetail( details );
        if (details == null) {
            initFromException(this);
        }
    }

    /**
     * make a fault in any namespace
     * @param code fault code which will be passed into the Axis namespace
     * @param subcodes fault subcodes which will be pased into the Axis namespace
     * @param faultString fault string
     * @param actor fault actor, same as fault role in SOAP 1.2
     * @param node which node caused the fault on the SOAP path
     * @param details details; if null the current stack trace and classname is
     * inserted into the details.
     * @since axis1.1
     */
    public AxisFault(QName code, QName[] subcodes, String faultString,
                     String actor, String node, Element[] details) {
        super (faultString);
        setFaultCode( code );
        if (subcodes != null) {
            for (int i = 0; i < subcodes.length; i++) {
                addFaultSubCode( subcodes[i] );
            }
        }
        setFaultString( faultString );
        setFaultActor( actor );
        setFaultNode( node );
        setFaultDetail( details );
        if (details == null) {
            initFromException(this);
        }
    }

    /**
     * Wrap an AxisFault around an existing Exception - this is private
     * to force everyone to use makeFault() above, which sanity-checks us.
     */
    protected AxisFault(Exception target) {
        super ("", target);
        // ? SOAP 1.2 or 1.1 ?
        setFaultCodeAsString( Constants.FAULT_SERVER_USER );
        detail2 = target;
        initFromException(target);
    }

    /**
     * create a simple axis fault from the message. Classname and stack trace
     * go into the fault details.
     * @param message
     */
    public AxisFault(String message)
    {
        super (message);
        setFaultCodeAsString(Constants.FAULT_SERVER_GENERAL);
        setFaultString(message);
        initFromException(this);
    }

    /**
     * No-arg constructor for building one from an XML stream.
     */
    public AxisFault()
    {
        super();
        setFaultCodeAsString(Constants.FAULT_SERVER_GENERAL);
        initFromException(this);
    }

    /**
     * create a fault from any throwable;
     * When faulting a throwable (as opposed to an exception),
     * stack trace information does not go into the fault.
     * @param message any extra text to with the fault
     * @param t whatever is to be turned into a fault
     */
    public AxisFault (String message, Throwable t)
    {
        super (message, t);
        setFaultCodeAsString(Constants.FAULT_SERVER_GENERAL);
        setFaultString(getMessage());
    }

    /**
     * fill in soap fault details from the exception, unless
     * this object already has a stack trace in its details. Which, given
     * the way this private method is invoked, is a pretty hard situation to ever achieve.
     * This method adds classname of the exception and the stack trace.
     * @param target what went wrong
     */
    private void initFromException(Exception target)
    {
        //look for old stack trace
        Element oldStackTrace=lookupFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE);
        if(oldStackTrace!=null) {
            // todo: Should we replace it or just let it be?
            return;
        }

        // Set the exception message (if any) as the fault string
        setFaultString( target.toString() );


        // Put the exception class into the AXIS SPECIFIC HACK
        //  "exceptionName" element in the details.  This allows
        // us to get back a correct Java Exception class on the other side
        // (assuming they have it available).
        // NOTE: This hack is obsolete!  We now serialize exception data
        // and the other side uses *that* QName to figure out what exception
        // to use, because the class name may be completly different on the
        // client.
        if ((target instanceof AxisFault) &&
            (target.getClass() != AxisFault.class)) {
          addFaultDetail(Constants.QNAME_FAULTDETAIL_EXCEPTIONNAME,
                    target.getClass().getName());
        }

        //add stack trace
        addFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE,
                JavaUtils.stackToString(target));
    }

    /**
     * init the fault details data structure; does nothing
     * if this exists already
     */
    private void initFaultDetails() {
        if (faultDetails == null) {
            faultDetails = new Vector();
        }
    }

    /**
     * clear the fault details list
     */
    public void clearFaultDetails() {
        faultDetails=null;
    }

    /**
     * dump the fault info to the log at debug level
     */
    public void dump()
    {
        log.debug(dumpToString());
    }


    /**
     * turn the fault and details into a string, with XML escaping.
     * subclassers: for security (cross-site-scripting) reasons,
     * escape everything that could contain caller-supplied data.
     * @return stringified fault details
     */
    public String dumpToString()
    {
        String details = new String();

        if (faultDetails != null) {
            for (int i=0; i < faultDetails.size(); i++) {
                Element e = (Element) faultDetails.get(i);
                String namespace= e.getNamespaceURI();
                if(namespace==null) {
                    namespace="";
                }
                String partname= e.getLocalName();
                if(partname==null) {
                    partname=e.getNodeName();
                }
                details += JavaUtils.LS
                          + "\t{" + namespace + "}"
                          + partname + ": "
                          + XMLUtils.getInnerXMLString(e);
            }
        }

        String subCodes = new String();
        if (faultSubCode != null) {
            for (int i = 0; i < faultSubCode.size(); i++) {
                subCodes += JavaUtils.LS
                            + (QName)faultSubCode.elementAt(i);
            }
        }
        //encode everything except details and subcodes, which are already
        //dealt with one way or another.
        String code= XMLUtils.xmlEncodeString(faultCode.toString());
        String errorString= XMLUtils.xmlEncodeString(faultString);
        String actor= XMLUtils.xmlEncodeString(faultActor);
        String node= XMLUtils.xmlEncodeString(faultNode);


        return "AxisFault" + JavaUtils.LS
            + " faultCode: " + code + JavaUtils.LS
            + " faultSubcode: " + subCodes + JavaUtils.LS
            + " faultString: " + errorString + JavaUtils.LS
            + " faultActor: " + actor + JavaUtils.LS
            + " faultNode: " + node + JavaUtils.LS
            + " faultDetail: " + details + JavaUtils.LS
            ;
    }

    /**
     * set the fault code
     * @param code a new fault code
     */
    public void setFaultCode(QName code) {
        faultCode = code ;
    }

    /**
     * Set the fault code (as a String).
     *
     * @deprecated expect to see this go away after 1.1, use
     *             setFaultCodeAsString instead!
     */

    public void setFaultCode(String code) {
        setFaultCodeAsString(code);
    }

    /**
     * set a fault code string that is turned into a qname
     * in the SOAP 1.1 or 1.2 namespace, depending on the current context
     * @param code fault code
     */
    public void setFaultCodeAsString(String code) {
        SOAPConstants soapConstants = MessageContext.getCurrentContext() == null ?
                                        SOAPConstants.SOAP11_CONSTANTS :
                                        MessageContext.getCurrentContext().getSOAPConstants();

        faultCode = new QName(soapConstants.getEnvelopeURI(), code);
    }

    /**
     * get the fault code
     * @return fault code QName or null if there is none yet.
     */
    public QName getFaultCode() {
        return( faultCode );
    }

    /**
     * This is new in SOAP 1.2, ignored in SOAP 1.1
     * @since axis1.1
     */
    public void addFaultSubCodeAsString(String code) {
        initFaultSubCodes();
        faultSubCode.add(new QName(Constants.NS_URI_AXIS, code));
    }

    /**
     * do whatever is needed to create the fault subcodes
     * data structure, if it is needed
     */
    protected void initFaultSubCodes() {
        if (faultSubCode == null) {
            faultSubCode = new Vector();
        }
    }

    /**
     * This is new in SOAP 1.2, ignored in SOAP 1.1
     * @since axis1.1
     */
    public void addFaultSubCode(QName code) {
        initFaultSubCodes();
        faultSubCode.add(code);
    }

    /**
     * This is new in SOAP 1.2, ignored in SOAP 1.1
     *
     * @since axis1.1
     */
    public void clearFaultSubCodes() {
        faultSubCode = null;
    }

    /**
     * get the fault subcode list; only used in SOAP 1.2
     * @since axis1.1
     * @return null for no subcodes, or a QName array
     */
    public QName[] getFaultSubCodes() {
        if (faultSubCode == null) {
            return null;
        }
        QName[] q = new QName[faultSubCode.size()];
        return (QName[])faultSubCode.toArray(q);
    }


    /**
     * set a fault string;
     * @param str new fault string; null is turned into ""
     */
    public void setFaultString(String str) {
        if (str != null) {
            faultString = str ;
        } else {
            faultString = "";
        }
    }

    /**
     * get the fault string; this will never be null but may be the
     * empty string
     * @return a fault string
     */
    public String getFaultString() {
        return( faultString );
    }

    /**
     * This is SOAP 1.2 equivalent of {@link #setFaultString(java.lang.String)}
     * @since axis1.1
     */
    public void setFaultReason(String str) {
        setFaultString(str);
    }

    /**
     * This is SOAP 1.2 equivalent of {@link #getFaultString()}
     * @since axis1.1
     * @return
     */
    public String getFaultReason() {
        return getFaultString();
    }

    /**
     * set the fault actor
     * @param actor fault actor
     */
    public void setFaultActor(String actor) {
        faultActor = actor ;
    }

    /**
     * get the fault actor
     * @return actor or null
     */
    public String getFaultActor() {
        return( faultActor );
    }

    /**
     * This is SOAP 1.2 equivalent of {@link #getFaultActor()}
     * @since axis1.1
     * @return
     */
    public String getFaultRole() {
        return getFaultActor();
    }

    /**
     * This is SOAP 1.2 equivalent of {@link #setFaultActor(java.lang.String)}
     * @since axis1.1
     */
    public void setFaultRole(String role) {
        setFaultActor(role);
    }

    /**
     * This is new in SOAP 1.2
     * @since axis1.1
     * @return
     */
    public String getFaultNode() {
        return( faultNode );
    }

    /**
     * This is new in SOAP 1.2
     * @since axis1.1
     */
    public void setFaultNode(String node) {
        faultNode = node;
    }

    /**
     * set the fault detail element to the arrary of details
     * @param details list of detail elements, can be null
     */
    public void setFaultDetail(Element[] details) {
        if ( details == null ) {
            faultDetails=null;
            return ;
        }
        faultDetails = new Vector( details.length );
        for ( int loop = 0 ; loop < details.length ; loop++ ) {
            faultDetails.add( details[loop] );
        }
    }

    /**
     * set the fault details to a string element.
     * @param details XML fragment
     */
    public void setFaultDetailString(String details) {
        clearFaultDetails();
        addFaultDetailString(details);
    }

    /**
     * add a string tag to the fault details.
     * @param detail XML fragment
     */
    public void addFaultDetailString(String detail) {
        initFaultDetails();
        try {
            Document doc = XMLUtils.newDocument();
            Element element = doc.createElement("string");
            Text text = doc.createTextNode(detail);
            element.appendChild(text);
            faultDetails.add(element);
        } catch (ParserConfigurationException e) {
            // This should not occur
            throw new InternalException(e);
        }
    }

    /**
     * append an element to the fault detail list
     * @param detail the new element to add
     * @since Axis1.1
     */
    public void addFaultDetail(Element detail) {
        initFaultDetails();
        faultDetails.add(detail);
    }

    /**
     * create an element of the given qname and add it to the details
     * @param qname qname of the element
     * @param body string to use as body
     */
    public void addFaultDetail(QName qname,String body) {
        Element detail = XMLUtils.StringToElement(qname.getNamespaceURI(),
                qname.getLocalPart(),
                body);

        addFaultDetail(detail);
    }
    /**
     * get all the fault details
     * @return an array of fault details, or null for none
     */
    public Element[] getFaultDetails() {
        if (faultDetails == null) {
            return null;
        }
        Element result[] = new Element[faultDetails.size()];
        for (int i=0; i<result.length; i++)
            result[i] = (Element) faultDetails.elementAt(i);
        return result;
    }

    /**
     * Find a fault detail element by its qname
     * @param qname name of the node to look for
     * @return the matching element or null
     * @since axis1.1
     */
    public Element lookupFaultDetail(QName qname) {
        if (faultDetails != null) {
            //extract details from the qname. the empty namespace is represented
            //by the empty string
            String searchNamespace = qname.getNamespaceURI();
            String searchLocalpart = qname.getLocalPart();
            //now spin through the elements, seeking a match
            Iterator it=faultDetails.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String localpart= e.getLocalName();
                if(localpart==null) {
                    localpart=e.getNodeName();
                }
                String namespace= e.getNamespaceURI();
                if(namespace==null) {
                    namespace="";
                }
                //we match on matching namespace and local part; empty namespace
                //in an element may be null, which matches QName's ""
                if(searchNamespace.equals(namespace)
                    && searchLocalpart.equals(localpart)) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * find and remove a specified fault detail element
     * @param qname qualified name of detail
     * @return true if it was found and removed
     * @since axis1.1
     */
    public boolean removeFaultDetail(QName qname) {
        Element elt=lookupFaultDetail(qname);
        if(elt==null) {
            return false;
        } else {
            return faultDetails.remove(elt);
        }
    }

    /**
     * add this fault and any needed headers to the output context
     * @param context
     * @throws Exception
     */
    public void output(SerializationContext context) throws Exception {

        SOAPConstants soapConstants = Constants.DEFAULT_SOAP_VERSION;
        if (context.getMessageContext() != null)
            soapConstants = context.getMessageContext().getSOAPConstants();

        SOAPEnvelope envelope = new SOAPEnvelope(soapConstants);

        SOAPFault fault = new SOAPFault(this);
        envelope.addBodyElement(fault);

        // add any headers we need
        if (faultHeaders != null) {
            for (Iterator i = faultHeaders.iterator(); i.hasNext();) {
                SOAPHeaderElement header = (SOAPHeaderElement) i.next();
                envelope.addHeader(header);
            }
        }

        envelope.output(context);
    }

    /**
     * string operator
     * @return the current fault string; may be empty but never null
     */
    public String toString() {
        return faultString;
    }

    /**
     * The override of the base class method prints out the
     * fault info before the stack trace
     * @param ps where to print
     */
    public void printStackTrace(PrintStream ps) {
        ps.println(dumpToString());
        super.printStackTrace(ps);
    }

    /**
     * The override of the base class method prints out the
     * fault info before the stack trace
     * @param pw where to print
     */
    public void printStackTrace(java.io.PrintWriter pw) {
        pw.println(dumpToString());
        super.printStackTrace(pw);
    }

    /**
     * Add a SOAP header which should be serialized along with the
     * fault.
     *
     * @param header a SOAPHeaderElement containing some fault-relevant stuff
     */
    public void addHeader(SOAPHeaderElement header) {
        if (faultHeaders == null) {
            faultHeaders = new ArrayList();
        }
        faultHeaders.add(header);
    }

    /**
     * Get the SOAP headers associated with this fault.
     *
     * @return an ArrayList containing any headers associated with this fault
     */
    public ArrayList getHeaders() {
        return faultHeaders;
    }

    /**
     * clear all fault headers
     */
    public void clearHeaders() {
        faultHeaders = null;
    }


    /**
     * Writes any exception data to the faultDetails
     * This can be overrided (and is) by emitted exception clases.
     * The base implementation will attempt to serialize exception data
     * the fault was created from an Exception and a type mapping is found for it.
     */
    public void writeDetails(QName qname, SerializationContext context) throws java.io.IOException {
        Object detailObject = this.detail;
        if (detailObject == null)
            return;

        boolean haveSerializer = false;
        try {
            if (context.getTypeMapping().getSerializer(detailObject.getClass()) != null) {
                haveSerializer = true;
            }
        } catch (Exception e) {
            // swallow this exception, it means that we don't know how to serialize
            // the details.
        }
        if (haveSerializer) {
            boolean oldMR = context.getDoMultiRefs();
            context.setDoMultiRefs(false);
            context.serialize(qname, null, detailObject);
            context.setDoMultiRefs(oldMR);
        }
    }

    /**
     * Returns the wrapped remote exception (the <i>cause</i>).
     *
     * @return  the wrapped remote exception, which may be <tt>null</tt>.
     */
    public Throwable getWrapped() {
        return detail2;
    }
}
