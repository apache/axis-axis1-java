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
package org.apache.axis.message;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;

import java.io.IOException;

/** A Fault body element.
 *
 * @author Sam Ruby (rubys@us.ibm.com)
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Tom Jordahl (tomj@macromedia.com)
 */
public class SOAPFault extends SOAPBodyElement implements javax.xml.soap.SOAPFault
{
    protected AxisFault fault;

    public SOAPFault(String namespace, String localName, String prefix,
                            Attributes attrs, DeserializationContext context)
    {
        super(namespace, localName, prefix, attrs, context);
        this.fault = fault;
        namespaceURI = Constants.URI_SOAP11_ENV;
        name = Constants.ELEM_FAULT;
    }

    public SOAPFault(AxisFault fault)
    {
        this.fault = fault;
        namespaceURI = Constants.URI_SOAP11_ENV;
        name = Constants.ELEM_FAULT;
    }

    public void outputImpl(SerializationContext context)
        throws IOException
    {
        context.registerPrefixForURI(prefix, namespaceURI);
        context.startElement(new QName(this.getNamespaceURI(),
                                       this.getName()),
                             attributes);

        // XXX - Can fault be anything but an AxisFault here?
         if (fault instanceof AxisFault) {
            AxisFault axisFault = (AxisFault) fault;
            if (axisFault.getFaultCode() != null) {
                // Do this BEFORE starting the element, so the prefix gets
                // registered if needed.
                String faultCode = context.qName2String(axisFault.getFaultCode());
                context.startElement(Constants.QNAME_FAULTCODE, null);
                context.writeSafeString(faultCode);
                context.endElement();
            }

            if (axisFault.getFaultString() != null) {
                context.startElement(Constants.QNAME_FAULTSTRING, null);
                context.writeSafeString(axisFault.getFaultString());
                context.endElement();
            }

            if (axisFault.getFaultActor() != null) {
                context.startElement(Constants.QNAME_FAULTACTOR, null);
                context.writeSafeString(axisFault.getFaultActor());
                context.endElement();
            }

            Element[] faultDetails = axisFault.getFaultDetails();
            if (faultDetails != null) {
                context.startElement(Constants.QNAME_FAULTDETAILS, null);
                for (int i = 0; i < faultDetails.length; i++) {
                    context.writeDOMElement(faultDetails[i]);
                }
                context.endElement();
            }
        }

        context.endElement();
    }

    public AxisFault getFault()
    {
        return fault;
    }

    public void setFault(AxisFault fault)
    {
        this.fault = fault;
    }

    /**
     * Sets this <CODE>SOAPFaultException</CODE> object with the given
     *   fault code.
     *
     *   <P>Fault codes, which given information about the fault,
     *   are defined in the SOAP 1.1 specification.</P>
     * @param   faultCode a <CODE>String</CODE> giving
     *     the fault code to be set; must be one of the fault codes
     *     defined in the SOAP 1.1 specification
     * @throws  SOAPException if there was an error in
     *     adding the <CODE>faultCode</CODE> to the underlying XML
     *     tree.
     */
    public void setFaultCode(String faultCode) throws SOAPException {
        fault.setFaultCode(faultCode);
    }

    /**
     * Gets the fault code for this <CODE>SOAPFaultException</CODE>
     * object.
     * @return a <CODE>String</CODE> with the fault code
     */
    public String getFaultCode() {
        return fault.getFaultCode().getLocalPart();
    }

    /**
     *  Sets this <CODE>SOAPFaultException</CODE> object with the given
     *   fault actor.
     *
     *   <P>The fault actor is the recipient in the message path who
     *   caused the fault to happen.</P>
     * @param   faultActor a <CODE>String</CODE>
     *     identifying the actor that caused this <CODE>
     *     SOAPFaultException</CODE> object
     * @throws  SOAPException  if there was an error in
     *     adding the <CODE>faultActor</CODE> to the underlying XML
     *     tree.
     */
    public void setFaultActor(String faultActor) throws SOAPException {
        fault.setFaultActor(faultActor);
    }

    /**
     * Gets the fault actor for this <CODE>SOAPFaultException</CODE>
     * object.
     * @return  a <CODE>String</CODE> giving the actor in the message
     *     path that caused this <CODE>SOAPFaultException</CODE> object
     * @see #setFaultActor(java.lang.String) setFaultActor(java.lang.String)
     */
    public String getFaultActor() {
        return fault.getFaultActor();
    }

    /**
     * Sets the fault string for this <CODE>SOAPFaultException</CODE>
     * object to the given string.
     *
     * @param faultString a <CODE>String</CODE>
     *     giving an explanation of the fault
     * @throws  SOAPException  if there was an error in
     *     adding the <CODE>faultString</CODE> to the underlying XML
     *     tree.
     * @see #getFaultString() getFaultString()
     */
    public void setFaultString(String faultString) throws SOAPException {
        fault.setFaultString(faultString);
    }

    /**
     * Gets the fault string for this <CODE>SOAPFaultException</CODE>
     * object.
     * @return a <CODE>String</CODE> giving an explanation of the
     *     fault
     */
    public String getFaultString() {
        return fault.getFaultString();
    }

    /**
     * Returns the detail element for this <CODE>SOAPFaultException</CODE>
     *   object.
     *
     *   <P>A <CODE>Detail</CODE> object carries
     *   application-specific error information related to <CODE>
     *   SOAPBodyElement</CODE> objects.</P>
     * @return  a <CODE>Detail</CODE> object with
     *     application-specific error information
     */
    public javax.xml.soap.Detail getDetail() {
        if(this.getChildren()==null || this.getChildren().size()<=0)
            return null;
        return (javax.xml.soap.Detail) this.getChildren().get(0);
    }

    /**
     * Creates a <CODE>Detail</CODE> object and sets it as the
     *   <CODE>Detail</CODE> object for this <CODE>SOAPFaultException</CODE>
     *   object.
     *
     *   <P>It is illegal to add a detail when the fault already
     *   contains a detail. Therefore, this method should be called
     *   only after the existing detail has been removed.</P>
     * @return the new <CODE>Detail</CODE> object
     * @throws  SOAPException  if this
     *     <CODE>SOAPFaultException</CODE> object already contains a valid
     *     <CODE>Detail</CODE> object
     */
    public javax.xml.soap.Detail addDetail() throws SOAPException {
        if(getDetail()!=null){
            throw new SOAPException(JavaUtils.getMessage("valuePresent"));
        }
        Detail detail = new Detail(fault);
        addChildElement(detail);
        return detail;
    }
}
