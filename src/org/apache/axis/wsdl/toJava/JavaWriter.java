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

import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.gen.Generator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
* Emitter knows about WSDL writers, one each for PortType, Binding, Service,
* Definition, Type.  But for some of these WSDL types, Wsdl2java generates
* multiple files.  Each of these files has a corresponding writer that extends
* JavaWriter.  So the Java WSDL writers (JavaPortTypeWriter, JavaBindingWriter,
* etc.) each calls a file writer (JavaStubWriter, JavaSkelWriter, etc.) for
* each file that that WSDL generates.
*
* <p>For example, when Emitter calls JavaWriterFactory for a Binding Writer, it
* returns a JavaBindingWriter.  JavaBindingWriter, in turn, contains a
* JavaStubWriter, JavaSkelWriter, and JavaImplWriter since a Binding may cause
* a stub, skeleton, and impl template to be generated.
*
* <p>Note that the writers that are given to Emitter by JavaWriterFactory DO NOT
* extend JavaWriter.  They simply implement Writer and delegate the actual
* task of writing to extensions of JavaWriter.
*
* <p>All of Wsdl2java's Writer implementations follow a common behaviour.
* JavaWriter is the abstract base class that dictates this common behaviour.
* This behaviour is primarily placed within the generate method.  The generate
* method calls, in succession (note:  the starred methods are the ones you are
* probably most interested in):
* <dl>
*   <dt> * getFileName
*   <dd> This is an abstract method that must be implemented by the subclass.
*        It returns the fully-qualified file name.
*   <dt> isFileGenerated(file)
*   <dd> You should not need to override this method.  It checks to see whether
*        this file is in the List returned by emitter.getGeneratedFileNames.
*   <dt> registerFile(file)
*   <dd> You should not need to override this method.  It registers this file by
*        calling emitter.getGeneratedFileInfo().add(...).
*   <dt> * verboseMessage(file)
*   <dd> You may override this method if you want to provide more information.
*        The generate method only calls verboseMessage if verbose is turned on.
*   <dt> getPrintWriter(file)
*   <dd> You should not need to override this method.  Given the file name, it
*        creates a PrintWriter for it.
*   <dt> * writeFileHeader(pw)
*   <dd> You may want to override this method.  The default implementation
*        generates nothing.
*   <dt> * writeFileBody(pw)
*   <dd> This is an abstract method that must be implemented by the subclass.
*        This is where the body of a file is generated.
*   <dt> * writeFileFooter(pw)
*   <dd> You may want to override this method.  The default implementation
*        generates nothing.
*   <dt> closePrintWriter(pw)
*   <dd> You should not need to override this method.  It simply closes the
*        PrintWriter.
* </dl>
*/
public abstract class JavaWriter implements Generator {
    protected Emitter emitter;
    protected String  type;

    /**
     * Constructor.
     */
    protected JavaWriter(Emitter emitter, String type) {
        this.emitter = emitter;
        this.type    = type;
    } // ctor

    /**
     * Generate a file.
     */
    public void generate() throws IOException {
        String file = getFileName();
        if (isFileGenerated(file)) {
            throw new DuplicateFileException(Messages.getMessage("duplicateFile00", file), file);
        }
        registerFile(file);
        if (emitter.isVerbose()) {
            String msg = verboseMessage(file);
            if (msg != null) {
                System.out.println(msg);
            }
        }
        PrintWriter pw = getPrintWriter(file);
        writeFileHeader(pw);
        writeFileBody(pw);
        writeFileFooter(pw);
        closePrintWriter(pw);
    } // generate

    /**
     * This method must be implemented by a subclass.  It
     * returns the fully-qualified name of the file to be
     * generated.
     */
    protected abstract String getFileName();

    /**
     * You should not need to override this method. It checks
     * to see whether the given file is in the List returned
     * by emitter.getGeneratedFileNames.
     */
    protected boolean isFileGenerated(String file) {
        return emitter.getGeneratedFileNames().contains(file);
    } // isFileGenerated

    /**
     * You should not need to override this method.
     * It registers the given file by calling
     * emitter.getGeneratedFileInfo().add(...).
     */
    protected void registerFile(String file) {
        emitter.getGeneratedFileInfo().add(file, null, type);
    } // registerFile

    /**
     * Return the string:  "Generating <file>".  Override this
     * method if you want to provide more information.
     */
    protected String verboseMessage(String file) {
        return Messages.getMessage("generating", file);
    } // verboseMessage

    /**
     * You should not need to override this method.
     * Given the file name, it creates a PrintWriter for it.
     */
    protected PrintWriter getPrintWriter(String filename) throws IOException {
        File file = new File(filename);
        File parent = new File(file.getParent());
        parent.mkdirs();
        return new PrintWriter(new FileWriter(file));
    } // getPrintWriter

    /**
     * This method is intended to be overridden as necessary
     * to generate file header information.  This default
     * implementation does nothing.
     */
    protected void writeFileHeader(PrintWriter pw) throws IOException {
    } // writeFileHeader

    /**
     * This method must be implemented by a subclass.  This
     * is where the body of a file is generated.
     */
    protected abstract void writeFileBody(PrintWriter pw) throws IOException;

    /**
     * You may want to override this method.  This default
     * implementation generates nothing.
     */
    protected void writeFileFooter(PrintWriter pw) throws IOException {
    } // writeFileFooter

    /**
     * Close the print writer.
     */
    protected void closePrintWriter(PrintWriter pw) {
        pw.close();
    } // closePrintWriter

    /**
     * Output a documentation element as a Java comment.
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

} // abstract class JavaWriter
