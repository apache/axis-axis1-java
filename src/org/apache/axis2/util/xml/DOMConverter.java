/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.axis2.util.xml ;

import java.io.Reader ;
import java.io.IOException ;
import java.io.StringReader ;
import java.io.StringWriter ;
import org.w3c.dom.Node ;
import org.w3c.dom.Element ;
import org.w3c.dom.Document ;
import org.xml.sax.ErrorHandler ;
import org.xml.sax.InputSource ;
import org.xml.sax.SAXException ;
import org.xml.sax.SAXParseException ;
import org.apache.xml.serialize.XMLSerializer ;
import org.apache.xml.serialize.OutputFormat ;
import org.apache.axis2.util.Logger ;

final public class DOMConverter {
    static Document parse(String url)
        throws IOException, SAXException
    {
        org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser() ;
        parser.setFeature("http://xml.org/sax/features/namespaces", true) ;
        parser.setErrorHandler(new ErrorHandlerImpl()) ;
        Logger.normal("Parsing the URL '" + url + "'...", Logger.MIN_LEVEL) ;
        parser.parse(url) ;
        Logger.normal("Done.", Logger.MIN_LEVEL) ;
        return parser.getDocument() ;
    }

    static Document parse(Reader in)
        throws IOException, SAXException
    {
        org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser() ;
        parser.setFeature("http://xml.org/sax/features/namespaces", true) ;
        parser.setErrorHandler(new ErrorHandlerImpl()) ;
        Logger.normal("Parsing an XML stream...", Logger.MIN_LEVEL) ;
        parser.parse(new InputSource(in)) ;
        Logger.normal("Done.", Logger.MIN_LEVEL) ;
        return parser.getDocument() ;
    }

    private DOMConverter() {}

    /**
     * Converts the XML string to DOM.
     * @param xml the XML to be converted
     * @return the document
     */
    public static Document toDOM(String xml) throws SAXException {
        try {
            StringReader in = new StringReader(xml) ;
            StringWriter out = new StringWriter() ;
            Document dom = parse(in) ;
            in.close() ;
            out.close() ;
            return dom ;
        } catch (IOException e) {
            throw new UnknownError(e.getMessage());
        }
    }

    public static Document toDOM(String string, boolean isURL)
        throws SAXException, IOException
    {
        return (isURL ? parse(string) : toDOM(string)) ;
    }

    /**
     * Converts the XML string to DOM.
     * @param in the XML reader
     * @return the document
     */
    public static Document toDOM(Reader in) throws IOException, SAXException {
        return parse(in) ;
    }


    /**
     * Converts the DOM to XML string.
     * @param node the DOM node.
     * @return the XML string
     */
    public static String toString(Node node) {
        return toString(node, null) ;
    }

    /**
     * Converts the DOM to XML string.
     * @param node the DOM node.
     * @param encoding Java character encoding in use by writer
     * @return the XML string
     */
    public static String toString(Node node, String encoding) {
        try {
            OutputFormat format = new OutputFormat() ;
            format.setPreserveSpace(true) ;
            format.setOmitDocumentType(true) ;
            if (encoding != null)
                format.setEncoding(encoding) ;
            XMLSerializer ser = new XMLSerializer(format);
            StringWriter out = new StringWriter() ;
            ser.setOutputCharStream(out);
            ser.asDOMSerializer();
            switch (node.getNodeType()) {
            case Node.DOCUMENT_NODE:
                ser.serialize((Document)node);
                break ;
            case Node.ELEMENT_NODE:
                format.setOmitXMLDeclaration(true) ;
                ser.serialize((Element)node);
                break ;
            default:
                throw new IllegalArgumentException("Unsupported node type: " + node.getNodeType()) ;
            }
            out.flush() ;
            return out.getBuffer().toString() ;
        } catch (IOException e) {
            e.printStackTrace() ;
            throw new UnknownError(e.getMessage()) ;
        }
    }
}

class ErrorHandlerImpl implements ErrorHandler {
    int errorCount = 0 ;
    public int getErrorCount() { return errorCount ; }
    public void warning(SAXParseException ex) {
        System.err.println("[Warning] " + getLocationString(ex)+": "+ ex.getMessage()) ;
    }
    public void error(SAXParseException ex) {
        System.err.println("[Error] " + getLocationString(ex) + ": " + ex.getMessage()) ;
        errorCount++ ;
    }
    public void fatalError(SAXParseException ex) throws SAXException {
        System.err.println("[Fatal Error] " + getLocationString(ex) + ": " + ex.getMessage()) ;
        errorCount++ ;
        throw ex ;
    }
    
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer() ;
    
        String systemId = ex.getSystemId() ;
        if (systemId != null) {
            int index = systemId.lastIndexOf('/') ;
            if (index != -1) 
                systemId = systemId.substring(index + 1) ;
            str.append(systemId) ;
        }
        str.append(':') ;
        str.append(ex.getLineNumber()) ;
        str.append(':') ;
        str.append(ex.getColumnNumber()) ;
    
        return str.toString() ;
    }
}
