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
package org.apache.axis.wsdl.test;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.framework.AssertionFailedError;
import org.apache.axis.wsdl.Emitter;
import org.apache.axis.transport.http.SimpleAxisServer;
import org.apache.axis.utils.Options;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.net.ServerSocket;

/**
 * Set up the test suite for the tests.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 */
public class Wsdl2javaTestSuite extends TestSuite {
    private static final String COMPILE_TASK="compile";
    private static final String CLEAN_TASK="clean";
    private Project testSuiteProject;
    private List classNames = null;

    public Wsdl2javaTestSuite(String Name) {
        super(Name);

        testSuiteProject = new Project();
        testSuiteProject.init();
        testSuiteProject.setName("Wsdl2javaTestSuite");
        testSuiteProject.addReference("Wsdl2javaTestSuite", testSuiteProject);
        testSuiteProject.setBasedir("/temp/");
        testSuiteProject.setDefaultTarget("default");

        Target defaultTarget = new Target();
        defaultTarget.setName(Wsdl2javaTestSuite.COMPILE_TASK);
        testSuiteProject.addTarget(Wsdl2javaTestSuite.COMPILE_TASK, defaultTarget);

        Javac compile = (Javac) testSuiteProject.createTask("javac");
        compile.setLocation(new Location("Wsdl2javaTestSuite"));
        compile.setOwningTarget(defaultTarget);
        defaultTarget.addTask(compile);
        compile.init();

        compile.setDebug(true);
        Path root = new Path(testSuiteProject);
        root.setPath("/temp/");
        compile.setSrcdir(root);
        compile.setDestdir(new File("/temp/"));
        compile.setVerbose(true);

        Target cleanup = new Target();
        cleanup.setName(Wsdl2javaTestSuite.CLEAN_TASK);
        testSuiteProject.addTarget(Wsdl2javaTestSuite.CLEAN_TASK, cleanup);
        Delete delete = (Delete) testSuiteProject.createTask("delete");
        delete.setLocation(new Location("Wsdl2javaTestSuite"));
        delete.setOwningTarget(cleanup);
        cleanup.addTask(delete);
        delete.init();

        delete.setDir(new File("/temp/"));
        delete.setVerbose(true);
    } //public Wsdl2javaTestSuite(String Name_)

    protected void prepareTest(String fileName) throws Exception {
        Emitter wsdl2java = new Emitter();
        wsdl2java.setPackageName("org.apache.axisttest");
        wsdl2java.generatePackageName(true);
        wsdl2java.setOutputDir("/temp");
        wsdl2java.generateSkeleton(true);
        wsdl2java.verbose(true);
        wsdl2java.generateTestCase(true);

        wsdl2java.emit(fileName);

        this.classNames = wsdl2java.getGeneratedClassNames();

        this.testSuiteProject.executeTarget(Wsdl2javaTestSuite.COMPILE_TASK);
    } //protected void prepareTest()

    private void cleanTest() {
        this.classNames = null;
        testSuiteProject.executeTarget(Wsdl2javaTestSuite.CLEAN_TASK);
    }

    public static void main(String[] args) {
        String[] testSuiteName = {Wsdl2javaTestSuite.class.getName()};
        junit.swingui.TestRunner.main(testSuiteName);
    } //public static void main(String[] args)

    public void run(TestResult result) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader()
                .getResourceAsStream(this.getClass().getName().replace('.', '/') + ".list")));

        System.out.println("Starting test http server.");
        SimpleAxisServer server = new SimpleAxisServer();

        try {
            Options opts = new Options(new String[]{});
            int port = opts.getPort();
            ServerSocket ss = new ServerSocket(port);
            server.setServerSocket(ss);
            server.run();

            String curLine = reader.readLine();
            while (curLine != null) {
                this.prepareTest(curLine);
                //deploy

                Iterator names = this.classNames.iterator();
                while (names.hasNext()) {
                    String className = (String) names.next();
                    if (className.endsWith("TestCase")) {
                        try {
                            Class clazz = this.getClass().getClassLoader().loadClass(className);

                            Method[] methods = clazz.getMethods();
                            for (int i = 0; i < methods.length; i++) {
                                String testName = methods[i].getName();
                                if (Modifier.isPublic(methods[i].getModifiers())
                                        && !Modifier.isStatic(methods[i].getModifiers())) {
                                    if (testName.startsWith("test")) {
                                        Test test = (Test) clazz.getConstructor(new Class[] {String.class})
                                                .newInstance(new Object[] {testName});
                                        test.run(result);
                                    }
                                }
                            }

                        } catch (Exception e) {
                            System.err.println("Could not run test '" + className + "' due to an error");
                            e.printStackTrace(System.err);
                        }
                    }
                }
                //undeploy
                this.cleanTest();
            }

            System.out.println("Stopping test http server.");
            server.stop();

            reader.close();
        } catch (Exception e) {
            throw new AssertionFailedError("The test suite failed with the following exception: " + e.getMessage());
        }

    }
} //public class Wsdl2javaTestSuite extends TestCase
