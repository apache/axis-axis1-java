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
package samples.integrationGuide.example1;

import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaWriter;
import org.apache.axis.wsdl.toJava.Utils;

import javax.wsdl.Port;
import javax.wsdl.Service;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
* This is my example of a class that writes a list of a service's
* ports to a file named <serviceName>Lst.lst.
*
* Note:  because of a name clash problem, I add the suffix "Lst".
* I hope to remove this in a future version of this example.
*
* Details of the JavaWriter bug:  JavaWriter looks to make sure a
* class doesn't already exist before creating a file, but not all
* files that we generate are .class files!  This works with
* deploy.wsdd and undeploy.wsdd because these files just happen
* to begin with lowercase letters, where Java classes begin with
* uppercase letters.  But this example shows the problem quite
* well.  I would LIKE to call the file <serviceName>.lst, but
* JavaWriter sees that we already have a class called
* <serviceName> and won't let me proceed.
*/
public class MyListPortsWriter extends JavaWriter {
    private Service service;
    private String fileName;

    /**
     * Constructor.
     */
    public MyListPortsWriter(
            Emitter emitter,
            ServiceEntry sEntry,
            SymbolTable symbolTable) {
        super(emitter, "service list");
        this.service = sEntry.getService();

        // Create the fully-qualified file name
        String javaName = sEntry.getName();
        fileName = emitter.getNamespaces().toDir(
                Utils.getJavaPackageName(javaName))
                + Utils.getJavaLocalName(javaName) + ".lst";
    } // ctor

    protected String getFileName() {
        return fileName;
    } // getFileName

    /**
     * Override the common JavaWriter header to a no-op.
     */
    protected void writeFileHeader(PrintWriter pw) throws IOException {
    } // writeFileHeader

    /**
     * Write the service list file.
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        Map portMap = service.getPorts();
        Iterator portIterator = portMap.values().iterator();

        while (portIterator.hasNext()) {
            Port p = (Port) portIterator.next();
            pw.println(p.getName());
        }
        pw.close(); // Note:  this really should be done in JavaWriter.
    } // writeFileBody

} // class MyListPortsWriter
