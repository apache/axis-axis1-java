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
package org.apache.axis.wsdl.toJava;

import org.apache.axis.Version;
import org.apache.axis.utils.Messages;

import java.io.File;
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
* Many of the files generated are .java files, so this abstract class -
* JavaClassWriter - exists.  It extends JavaWriter and adds a bit of Java-
* relative behaviour.  This behaviour is primarily placed within the generate
* method.  The generate method calls, in succession (note:  the starred methods
* are the ones you are probably most interested in):
* <dl>
*   <dt> getFileName
*   <dd> This method is abstract in JavaWriter, but JavaClassWriter implements
*        this method.  Subclasses should have no need to override it.  It
*        returns the fully-qualified file name based on the fully-qualified
*        classname + ".java".
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
*   <dt> writeFileHeader(pw)
*   <dd> JavaClassWriter implements this method, so you should not need to
*        override it.  This method generates a javadoc giving the filename and
*        a comment stating that this file is generated by WSDL2Java, and it
*        generates the class definition including the opening curly brace..
*   <dt> * writeFileBody(pw)
*   <dd> This is an abstract method that must be implemented by the subclass.
*        This is where the body of a file is generated.
*   <dt> * writeFileFooter(pw)
*   <dd> JavaClassWriter implements this method, so you should not need to
*        override it.  It generates the closing curly brace for the class.
*   <dt> closePrintWriter(pw)
*   <dd> You should not need to override this method.  It simply closes the
*        PrintWriter.
* </dl>
*
* Additional behaviour that JavaClassWriter introduces beyond JavaWriter is
* related to the class header and definition:
* <dl>
*   <dt> writeHeaderComments
*   <dd> Write the header comments, such as the file name and that the file was
*        generated by WSDL2Java.  You need not override this method unless you
*        want a tailored comment.
*   <dt> writePackage
*   <dd> Write the package statement, if necessary.  You should not need to
*        override this method.
*   <dt> getClassModifiers
*   <dd> Modifiers, such as "public", "final", "abstract" would be returned by
*        this method.  The default implementation only generates "public ", so
*        any subclass that needs more must override this method.
*   <dt> getClassText
*   <dd> This simply returns "class ".  If anything else is desired, for
*        instance, JavaInterfaceWriter prefers "interface ", then this method
*        must be overridden.
*   <dt> getExtendsText
*   <dd> The default implementation returns "".  If a subclass desires to list
*        a set of classes this one extends, then this method must be overridden.
*   <dt> getImplementsText
*   <dd> Same as getExtendsText except for the implements clause.
* </dl>
*/
public abstract class JavaClassWriter extends JavaWriter {
    protected Namespaces namespaces;
    protected String     className;
    protected String     packageName;

    /**
     * Constructor.
     * @param emitter The emitter instance
     * @param fullClassName The fully qualified class name of the class
     *        to be generated.
     * @param type
     */
    protected JavaClassWriter(
            Emitter emitter,
            String fullClassName,
            String type) {
        super(emitter, type);
        this.namespaces = emitter.getNamespaces();
        this.packageName = Utils.getJavaPackageName(fullClassName);
        this.className = Utils.getJavaLocalName(fullClassName);
    } // ctor

    /**
     * Return the file name as a string of the form:
     * "<directory-ized fully-qualified classname>.java"
     */
    protected String getFileName() {
        return namespaces.toDir(packageName) + className + ".java";
    } // getFileName

    /**
     * You should not need to override this method.
     * It registers the given file by calling
     * emitter.getGeneratedFileInfo().add(...).
     * JavaClassWriter overrides this method from JavaWriter because
     * it add class name to the registration information.
     */
    protected void registerFile(String file) {
        String fqClass = getPackage() + '.' + getClassName();
        emitter.getGeneratedFileInfo().add(file, fqClass, type);
    } // registerFile

    /**
     * Write a common header, including the package name, the class
     * declaration, and the opening curly brace.
     */
    protected void writeFileHeader(PrintWriter pw) throws IOException {
        writeHeaderComments(pw);
        writePackage(pw);

        // print class declaration
        pw.println(getClassModifiers() + getClassText() + getClassName() + ' ' + getExtendsText() + getImplementsText() + "{");
    } // writeFileHeader

    /**
     * Write the header comments.
     */
    protected void writeHeaderComments(PrintWriter pw) throws IOException {
        String localFile = getFileName();
        int lastSepChar = localFile.lastIndexOf(File.separatorChar);
        if (lastSepChar >= 0) {
            localFile = localFile.substring(lastSepChar + 1);
        }
        pw.println("/**");
        pw.println(" * " + localFile);
        pw.println(" *");
        pw.println(" * " + Messages.getMessage("wsdlGenLine00"));
        pw.println(" * " + Messages.getMessage("wsdlGenLine01",Version.getVersionText()));
        pw.println(" */");
        pw.println();
    } // writeHeaderComments

    /**
     * Write the package declaration statement.
     */
    protected void writePackage(PrintWriter pw) throws IOException {
        if (getPackage() != null) {
            pw.println("package " + getPackage() + ";");
            pw.println();
        }

    } // writePackage

    /**
     * Return "public ".  If more modifiers are needed, this method must be
     * overridden.
     */
    protected String getClassModifiers() {
        return "public ";
    } // getClassModifiers

    /**
     * Return "class ".  If "interface " is needed instead, this method must be
     * overridden.
     */
    protected String getClassText() {
        return "class ";
    } // getClassString

    /**
     * Returns the appropriate extends clause.  This default implementation
     * simply returns "", but if you want "extends <class/interface list> "
     * then you must override this method.
     * @return ""
     */
    protected String getExtendsText() {
        return "";
    } // getExtendsText

    /**
     * Returns the appropriate implements clause.  This default implementation
     * simply returns "", but if you want "implements <interface list> " then
     * you must override this method.
     * @return ""
     */
    protected String getImplementsText() {
        return "";
    } // getImplementsText

    /**
     * Returns the package name.
     */
    protected String getPackage() {
        return packageName;
    } // getPackage

    /**
     * Returns the class name.
     */
    protected String getClassName() {
        return className;
    } // getClassName

    /**
     * Generate the closing curly brace.
     */
    protected void writeFileFooter(PrintWriter pw) throws IOException {
        super.writeFileFooter(pw);
        pw.println('}');
    } // writeFileFooter

} // abstract class JavaClassWriter
