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
package samples.integrationGuide.example2;

import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaWriter;

import javax.wsdl.Definition;
import java.io.IOException;
import java.io.PrintWriter;

public class MyDeployWriter extends JavaWriter {

    private String filename;

    public MyDeployWriter(Emitter emitter, Definition definition,
            SymbolTable symbolTable) {
        super(emitter, "deploy");

        // Create the fully-qualified file name
        String dir = emitter.getNamespaces().getAsDir(
                definition.getTargetNamespace());
        filename = dir + "deploy.useless";
    } // ctor

    public void generate() throws IOException {
        if (emitter.isServerSide()) {
            super.generate();
        }
    } // generate

    protected String getFileName() {
        return filename;
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
        MyEmitter myEmitter = (MyEmitter) emitter;
        if (myEmitter.getSong() == MyEmitter.RUM) {
            pw.println("Yo!  Ho!  Ho!  And a bottle of rum.");
        }
        else if (myEmitter.getSong() == MyEmitter.WORK) {
            pw.println("Hi ho!  Hi ho!  It's off to work we go.");
        }
        else {
            pw.println("Feelings...  Nothing more than feelings...");
        }
    } // writeFileBody
} // class MyDeployWriter
