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
package org.apache.axis.wsdl.toJava;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.wsdl.QName;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.deployment.wsdd.WSDDConstants;

import org.apache.axis.wsdl.gen.Generator;

import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
* All of Wsdl2java's Writer implementations do some common stuff.  All this
* common stuff resides in this abstract base class.  All that extensions to
* this class have to do is implement writeFileBody.
*
* Emitter knows about WSDL writers, one each for PortType, Binding, Service,
* Definition, Type.  But for some of these WSDL types, Wsdl2java generates
* multiple files.  Each of these files has a corresponding writer that extends
* JavaWriter.  So the Java WSDL writers (JavaPortTypeWriter, JavaBindingWriter,
* etc.) each calls a file writer (JavaStubWriter, JavaSkelWriter, etc.) for
* each file that that WSDL generates.
*
* For example, when Emitter calls JavaWriterFactory for a Binding Writer, it
* returns a JavaBindingWriter.  JavaBindingWriter, in turn, contains a
* JavaStubWriter, JavaSkelWriter, and JavaImplWriter since a Binding may cause
* a stub, skeleton, and impl template to be generated.
*
* Note that the writers that are given to Emitter by JavaWriterFactory DO NOT
* extend JavaWriter.  They simply implement Writer and delegate the actual
* task of writing to extensions of JavaWriter.
*/

public abstract class JavaWriter implements Generator {
    protected Emitter     emitter;
    protected QName       qname;
    protected Namespaces  namespaces;
    protected String      rootName; // No suffix...
    protected String      className;
    protected String      fileName;
    protected String      packageName;
    protected PrintWriter pw;
    protected String      message;
    protected String      type;
    protected boolean     embeddedCode = false;

    /**
     * Constructor.  Use this one to pass in a Type.  Type contains QName and java name.
     */
    protected JavaWriter(
            Emitter emitter,
            SymTabEntry entry,
            String suffix,
            String extension,
            String message, 
            String type) {
        this.emitter     = emitter;
        this.qname       = entry.getQName();
        this.namespaces  = emitter.getNamespaces();
        this.rootName    = Utils.getJavaLocalName(entry.getName());
        this.className   = rootName + (suffix == null ? "" : suffix);
        this.fileName    = className + '.' + extension;
        this.packageName = Utils.getJavaPackageName(entry.getName());
        this.message     = message;
        this.type        = type;
    } // ctor


    /**
     * Constructor.  Use Set up all the variables needed to write a file.
     */
    protected JavaWriter(
            Emitter emitter,
            QName qname,
            String suffix,
            String extension,
            String message, 
            String type) {
        this.emitter     = emitter;
        this.qname       = qname;
        this.namespaces  = emitter.getNamespaces();
        this.className   = qname.getLocalPart() + (suffix == null ? "" : suffix);
        this.fileName    = className + '.' + extension;
        this.packageName = namespaces.getCreate(qname.getNamespaceURI());
        this.message     = message;
        this.type        = type;
    } // ctor

    /**
     * Generate into an existing class with PrinterWriter pw
     */
    public void generate(PrintWriter pw) throws IOException {
        embeddedCode = true;  // Indicated embedded
        String packageDirName = namespaces.toDir(packageName);
        String path = packageDirName + fileName;
        String fqClass = packageName + "." + className;
        
        // Check for duplicates, probably the result of namespace mapping
        if (emitter.getGeneratedClassNames().contains(fqClass)) {
            throw new IOException(JavaUtils.getMessage("duplicateClass00", fqClass));
        }
        if (emitter.getGeneratedFileNames().contains(path)) {
            throw new IOException(JavaUtils.getMessage("duplicateFile00", path));
        }
        
        emitter.getGeneratedFileInfo().add(path, fqClass, type);
        this.pw = pw;
        writeFileBody();
    }

    /**
     * Create the file, write the header, write the body.
     */
    public void generate() throws IOException {
        String packageDirName = namespaces.toDir(packageName);
        String path = packageDirName + fileName;
        String fqClass = packageName + "." + className;

        // Check for duplicates, probably the result of namespace mapping
        if (emitter.getGeneratedClassNames().contains(fqClass)) {
            throw new IOException(JavaUtils.getMessage("duplicateClass00", fqClass));
        }
        if (emitter.getGeneratedFileNames().contains(path)) {
            throw new IOException(JavaUtils.getMessage("duplicateFile00", path));
        }
        
        emitter.getGeneratedFileInfo().add(path, fqClass, type);
        namespaces.mkdir(packageName);
        File file = new File(packageDirName, fileName);
        if (emitter.isVerbose()) {
            System.out.println(message + ":  " + file.getPath());
        }
        pw = new PrintWriter(new FileWriter(file));
        writeFileHeader();
        writeFileBody();
    } // generate

    /**
     * Write a common header, including the package name.
     */
    protected void writeFileHeader() throws IOException {
        pw.println("/**");
        pw.println(" * " + fileName);
        pw.println(" *");
        pw.println(" * " + JavaUtils.getMessage("wsdlGenLine00"));
        pw.println(" * " + JavaUtils.getMessage("wsdlGenLine01"));
        pw.println(" */");
        pw.println();

        // print package declaration
        pw.println("package " + packageName + ";");
        pw.println();
    } // writeFileHeader

    /**
     * output documentation element as a Java comment.
     */
    protected void writeComment(PrintWriter pw, Element element) {
        // This controls how many characters per line
        final int LINE_LENGTH = 65;

        if (element == null) {
            return;
        }

        Node child = element.getFirstChild();
        if (child == null) {
            return;
        }

        String comment = child.getNodeValue();

        // Strip out stuff that will really mess up our comments
        comment = comment.replace('\r', ' ');
        comment = comment.replace('\n', ' ');

        if (comment != null) {
            int start = 0;

            pw.println();  // blank line
            pw.println("    /**");

            // make the comment look pretty
            while (start < comment.length()) {
                int end = start + LINE_LENGTH;
                if (end > comment.length())
                    end = comment.length();
                // look for next whitespace
                while (end < comment.length() &&
                        !Character.isWhitespace(comment.charAt(end))) {
                    end++;
                }
                pw.println("     * " + comment.substring(start, end).trim());
                start = end + 1;
            }
            pw.println("     */");
        }
    } // writeComment

    /**
     * Initialize the deployment document, spit out preamble comments
     * and opening tag.
     */
    protected void initializeDeploymentDoc(String deploymentOpName) throws IOException {
        if ("deploy".equals(deploymentOpName)) {
            pw.println(JavaUtils.getMessage("deploy00"));
        }
        else {
            pw.println(JavaUtils.getMessage("deploy01"));
        }
        pw.println(JavaUtils.getMessage("deploy02"));
        if ("deploy".equals(deploymentOpName)) {
            pw.println(JavaUtils.getMessage("deploy03"));
        }
        else {
            pw.println(JavaUtils.getMessage("deploy04"));
        }
        pw.println(JavaUtils.getMessage("deploy05"));
        pw.println(JavaUtils.getMessage("deploy06"));
        if ("deploy".equals(deploymentOpName)) {
            pw.println(JavaUtils.getMessage("deploy07"));
        }
        else {
            pw.println(JavaUtils.getMessage("deploy08"));
        }
        pw.println(JavaUtils.getMessage("deploy09"));
        pw.println();
        if ("deploy".equals(deploymentOpName)) {
            pw.println("<deployment");
            pw.println("    xmlns=\"" + WSDDConstants.URI_WSDD +"\"");
            pw.println("    xmlns:" + WSDDConstants.NS_PREFIX_WSDD_JAVA + "=\"" +
                       WSDDConstants.URI_WSDD_JAVA +"\">");
        }
        else {
            pw.println("<undeployment");
            pw.println("    xmlns=\"" + WSDDConstants.URI_WSDD +"\">");
        }
    } // initializeDeploymentDoc

    /**
     * Write the body of the file.  This is what extenders of this class must
     * implement
     */
    protected abstract void writeFileBody() throws IOException;

} // abstract class JavaWriter
