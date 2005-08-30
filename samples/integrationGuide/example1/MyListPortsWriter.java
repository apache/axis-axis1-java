/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
