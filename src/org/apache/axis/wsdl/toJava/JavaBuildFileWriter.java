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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.wsdl.Definition;

import org.apache.axis.wsdl.symbolTable.SymbolTable;
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
public class JavaBuildFileWriter extends JavaWriter {
	protected Definition definition;

	 /** Field symbolTable */
	 protected SymbolTable symbolTable;

    /**
     * @param emitter
     * @param type
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
		while(tok.hasMoreTokens()){
			out.write("		<pathelement location=\""+tok.nextToken()+"\"/>\n");
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

		out.write("		<jar jarfile=\""+getJarFileName(symbolTable.getWSDLURI())+".jar\" basedir=\"${build.classes}\" >\n");
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
	
	private StringTokenizer getClasspathComponets(){
		String classpath = System.getProperty("java.class.path");
		String spearator  = ";";
		if(classpath.indexOf(';') < 0){
			//then it is UNIX
			spearator = ":";
		}
		
		return new StringTokenizer(classpath,spearator);
	}
	
	private String getJarFileName(String wsdlFile){
		int index = 0;
		if((index = wsdlFile.lastIndexOf("/"))>0){
			wsdlFile = wsdlFile.substring(index+1);
		}
		return wsdlFile.substring(0,wsdlFile.indexOf('.'));
	}
    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.gen.Generator#generate()
     */
    public void generate() throws IOException {
		if(emitter.isBuildFileWanted()){
        	super.generate();
		}
    }

}
