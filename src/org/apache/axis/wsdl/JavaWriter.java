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

import javax.wsdl.QName;

import org.w3c.dom.Element;

/**
* All of Wsdl2java's Writer implementations do some common stuff.  All this
* common stuff resides in this abstract base class.  All that extensions to
* this class have to do is implement writeFileBody.
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
     * Write the body of the file.  This is what extenders of this class must
     * implement
     */
    protected abstract void writeFileBody() throws IOException;

} // abstract class JavaWriter
