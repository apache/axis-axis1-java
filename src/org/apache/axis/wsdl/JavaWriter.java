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
package org.apache.axis.wsdl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashMap;

import javax.wsdl.QName;

import org.w3c.dom.Element;

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

public abstract class JavaWriter implements Writer {
    protected Emitter     emitter;
    protected QName       qname;
    protected Namespaces  namespaces;
    protected String      className;
    protected String      fileName;
    protected String      packageName;
    protected PrintWriter pw;
    protected String      message;

    /**
     * Constructor.  Set up all the variables needed to write a file.
     */
    protected JavaWriter(
            Emitter emitter,
            QName qname,
            String suffix,
            String extension,
            String message) {
        this.emitter     = emitter;
        this.qname       = qname;
        this.namespaces  = emitter.getNamespaces();
        this.className   = qname.getLocalPart() + (suffix == null ? "" : suffix);
        this.fileName    = className + '.' + extension;
        this.packageName = namespaces.getCreate(qname.getNamespaceURI());
        this.message     = message;
    } // ctor

    /**
     * Create the file, write the header, write the body.
     */
    public void write() throws IOException {
        String packageDirName = namespaces.toDir(packageName);
        emitter.fileList.add(packageDirName + fileName);
        emitter.classList.add(packageName + "." + className);
        File file = new File(packageDirName, fileName);
        if (emitter.bVerbose) {
            System.out.println(message + file.getPath());
        }
        pw = new PrintWriter(new FileWriter(file));
        writeFileHeader();
        writeFileBody();
    } // write

    /**
     * Write a common header, including the package name.
     */
    protected void writeFileHeader() throws IOException {
        pw.println("/**");
        pw.println(" * " + fileName);
        pw.println(" *");
        pw.println(" * This file was auto-generated from WSDL");
        pw.println(" * by the Apache Axis Wsdl2java emitter.");
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

        if (element == null)
            return;

        String comment = element.getFirstChild().getNodeValue();

        // Strip out stuff that will really mess up our comments
        comment = comment.replace('\r', ' ');
        comment = comment.replace('\n', ' ');

        if (comment != null) {
            int start = 0;

            pw.println();  // blank line

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
                pw.println("    // " + comment.substring(start, end).trim());
                start = end + 1;
            }
        }
    } // writeComment

    /**
     * A simple map of the primitive types and their holder objects
     */
    private static HashMap TYPES = new HashMap(7);

    static {
        TYPES.put("int", "Integer");
        TYPES.put("float", "Float");
        TYPES.put("boolean", "Boolean");
        TYPES.put("double", "Double");
        TYPES.put("byte", "Byte");
        TYPES.put("short", "Short");
        TYPES.put("long", "Long");
    }

    /**
     * Return a string with "var" wrapped as an Object type if needed
     */
    protected String wrapPrimitiveType(String type, String var) {
        String objType = (String) TYPES.get(type);
        if (objType != null) {
            return "new " + objType + "(" + var + ")";
        }
        else {
            return var;
        }
    } // wrapPrimitiveType

    /**
     * Return the Object variable 'var' cast to the appropriate type
     * doing the right thing for the primitive types.
     */
    protected String getResponseString(String type, String var) {
        String objType = (String) TYPES.get(type);
        if (objType != null) {
            return "((" + objType + ") " + var + ")." + type + "Value();";
        }
        else if (type.equals("void")) {
            return ";";
        }
        else {
            return "(" + type + ") " + var + ";";
        }
    } // getResponseString

    protected boolean isPrimitiveType(String type) {
        return TYPES.get(type) != null;
    }

    /**
     * Initialize the deployment document, spit out preamble comments
     * and opening tag.
     */
    protected void initializeDeploymentDoc(String deploymentOpName) throws IOException {
        pw.println("<!--                                         " +
                "                    -->");
        pw.println("<!--Use this file to " + deploymentOpName +
                " some handlers/chains and services  -->");
        pw.println("<!--Two ways to do this:                     " +
                "                    -->");
        pw.println("<!--  java org.apache.axis.utils.Admin " +
                deploymentOpName + ".xml              -->");
        pw.println("<!--     from the same dir that the Axis " +
                "engine runs             -->");
        pw.println("<!--or                                     " +
                "                      -->");
        pw.println("<!--  java org.apache.axis.client.AdminClient " +
                deploymentOpName + ".xml       -->");
        pw.println("<!--     after the axis server is running    " +
                "                    -->");
        pw.println("<!--This file will be replaced by WSDD once " +
                "it's ready           -->");
        pw.println();
        pw.println("<m:" + deploymentOpName + " xmlns:m=\"AdminService\">");
    } // initializeDeploymentDoc

    /**
     * Does the given file already exist?
     */
    protected boolean fileExists (String name, String namespace) throws IOException
    {
        String packageName = emitter.getNamespaces().getAsDir(namespace);
        String fullName = packageName + name;
        return new File (fullName).exists();
    } // fileExists

    /**
     * Write the body of the file.  This is what extenders of this class must
     * implement
     */
    protected abstract void writeFileBody() throws IOException;

} // abstract class JavaWriter
