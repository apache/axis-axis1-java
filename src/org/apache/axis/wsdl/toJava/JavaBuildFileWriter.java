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

package org.apache.axis.wsdl.toJava;

import org.apache.axis.wsdl.symbolTable.SymbolTable;

import javax.wsdl.Definition;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * <p>This is Wsdl2java's build file Writer.  It writes the build.xml file.
 * The build.xml file is a ant build file. After run the WSDL2Java and filling
 * the implementation the user just have to cd to the out dir and type
 * and "ant" (of course you must have ant installed). Then the ant will genarate a
 * jar file which named after the wsdl file you used for WSDL2Java.
 * (named after wsdl file ??? I do not get anything better .. the wsdl file may have
 * more than one service ect ... so we can use them.)</p>
 *
 * <p>This build file work on the where it is created ... User can not move the genarated code
 * to another mechine and try to build. (class path is broken). But of cource user can
 * move genarated build file at his will.</p>
 *
 * <p>deploy the webservice using the AdminClient and drop the jar to servlet Container.
 * We might even add another task to deploy the WS as well.</p>
 *
 * <p>This feature can be on and off using the -B option default is off</p>
 * @author Srinath Perera(hemapani@opensource.lk)
 */
public class JavaBuildFileWriter extends JavaWriter
{
    protected Definition definition;

    /** Field symbolTable */
    protected SymbolTable symbolTable;


    /**
     * Constructor
     */
    public JavaBuildFileWriter(Emitter emitter, Definition definition,
                               SymbolTable symbolTable) {

        super(emitter, "build");

        this.definition = definition;
        this.symbolTable = symbolTable;
    }

    protected String getFileName() {
        String dir = emitter.getOutputDir();
        return dir + "/build.xml";
    }

    protected void writeFileBody(PrintWriter out) throws IOException {
        out.write("<?xml version=\"1.0\"?>\n");

        out.write("<project basedir=\".\" default=\"jar\">\n");
        out.write("	<property name=\"src\" location=\".\"/>\n");
        out.write("	<property name=\"build.classes\" location=\"classes\"/>\n");

        out.write("	<path id=\"classpath\">\n");
        StringTokenizer tok = getClasspathComponets();
        while (tok.hasMoreTokens()) {
            out.write("		<pathelement location=\"" + tok.nextToken() + "\"/>\n");
        }
        out.write("	</path>\n");

        out.write("	<target name=\"compile\">\n");
        out.write("	   <mkdir dir=\"${build.classes}\"/>\n");
        out.write("		<javac destdir=\"${build.classes}\" debug=\"on\">\n");
        out.write("			<classpath refid=\"classpath\" />\n");
        out.write("			<src path=\"${src}\"/>\n");
        out.write("		</javac>\n");
        out.write("	</target>\n");

        out.write("	<target name=\"jar\" depends=\"compile\">\n");
        out.write("        <copy todir=\"${build.classes}\">\n");
        out.write("            <fileset dir=\".\" casesensitive=\"yes\" >\n");
        out.write("                <include name=\"**/*.wsdd\"/>\n");
        out.write("            </fileset>\n");
        out.write("        </copy>\n");

        out.write("		<jar jarfile=\"" + getJarFileName(symbolTable.getWSDLURI()) + ".jar\" basedir=\"${build.classes}\" >\n");
        out.write("		<include name=\"**\" />\n");
        out.write("		<manifest>\n");
        out.write("			<section name=\"org/apache/ws4j2ee\">\n");
        out.write("			<attribute name=\"Implementation-Title\" value=\"Apache Axis\"/>\n");
        out.write("			<attribute name=\"Implementation-Vendor\" value=\"Apache Web Services\"/>\n");
        out.write("			</section>\n");
        out.write("		</manifest>\n");
        out.write("		</jar>\n");
        out.write("		<delete dir=\"${build.classes}\"/>\n");
        out.write("	</target>\n");
        out.write("</project>\n");
        out.close();
    }

    private StringTokenizer getClasspathComponets() {
        String classpath = System.getProperty("java.class.path");
        String spearator = ";";
        if (classpath.indexOf(';') < 0) {
            //t hen it is UNIX
            spearator = ":";
        }

        return new StringTokenizer(classpath, spearator);
    }

    private String getJarFileName(String wsdlFile) {
        int index = 0;
        if ((index = wsdlFile.lastIndexOf("/")) > 0) {
            wsdlFile = wsdlFile.substring(index + 1);
        }
        return wsdlFile.substring(0, wsdlFile.indexOf('.'));
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.gen.Generator#generate()
     */
    public void generate() throws IOException {
        if (emitter.isBuildFileWanted()) {
            super.generate();
        }
    }

}
