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

import junit.framework.AssertionFailedError;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.apache.axis.client.AdminClient;
import org.apache.axis.transport.http.SimpleAxisServer;
import org.apache.axis.utils.Options;
import org.apache.axis.wsdl.Emitter;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Set up the test suite for the tests.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 */
public class Wsdl2javaTestSuite extends TestSuite {
    private static final String COMPILE_TASK="compile";
    private static final String CLEAN_TASK="clean";
    private static final String WORK_DIR="./build/work/";
    private static Project testSuiteProject = null;
    private static List classNames = null;
    private static List fileNames = null;
    private static ClassLoader loader = null;

    public Wsdl2javaTestSuite() {
        super();
        this.setupTasks();
        this.prepareTests();
    }

    public Wsdl2javaTestSuite(String name) {
        super(name);

        this.setupTasks();
        this.prepareTests();
    } //public Wsdl2javaTestSuite(String Name_)

    private void prepareTests() {
        if (null ==  Wsdl2javaTestSuite.classNames) {
            Wsdl2javaTestSuite.classNames = new ArrayList();
            Wsdl2javaTestSuite.fileNames = new ArrayList();
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader()
                    .getResourceAsStream(this.getClass().getName().replace('.', '/') + ".list")));

            try {
                String curLine = reader.readLine();
                int testNum = 0;
                while (curLine != null) {
                    this.prepareTest(curLine, testNum);

                    //setup tests
                    Iterator names = ((List) Wsdl2javaTestSuite.classNames.get(testNum)).iterator();
                    while (names.hasNext()) {
                        String className = (String) names.next();
                        if (className.endsWith("TestCase")) {
                            try {
                                this.addTestSuite(loader.loadClass(className));
                            } catch (Exception e) {
                                System.err.println("Could not set up test '" + className + "' due to an error");
                                e.printStackTrace(System.err);
                                throw new AssertionFailedError(e.getMessage());
                            }
                        }
                    }
                    curLine = reader.readLine();
                    testNum++;
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
                throw new AssertionFailedError(e.getMessage());
            }
        }
    }

    private void setupTasks() {
        if (Wsdl2javaTestSuite.testSuiteProject == null) {
            File workDir = new File(Wsdl2javaTestSuite.WORK_DIR);
            workDir.mkdirs();

            testSuiteProject = new Project();
            testSuiteProject.init();
            testSuiteProject.setName("Wsdl2javaTestSuite");
            testSuiteProject.addReference("Wsdl2javaTestSuite", testSuiteProject);
            testSuiteProject.setBasedir("./");
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
            root.setPath(Wsdl2javaTestSuite.WORK_DIR);
            compile.setSrcdir(root);
            compile.setDestdir(workDir);
            compile.setVerbose(true);

            Target cleanup = new Target();
            cleanup.setName(Wsdl2javaTestSuite.CLEAN_TASK);
            testSuiteProject.addTarget(Wsdl2javaTestSuite.CLEAN_TASK, cleanup);
            Delete delete = (Delete) testSuiteProject.createTask("delete");
            delete.setLocation(new Location("Wsdl2javaTestSuite"));
            delete.setOwningTarget(cleanup);
            cleanup.addTask(delete);
            delete.init();

            delete.setDir(workDir);
            delete.setVerbose(true);
        }
    }

    protected void prepareTest(String fileName, int testNum) throws Exception {
        Emitter wsdl2java = new Emitter();
        wsdl2java.setPackageName("org.apache.axisttest" + testNum);
        wsdl2java.generatePackageName(true);
        wsdl2java.setOutputDir(Wsdl2javaTestSuite.WORK_DIR);
        wsdl2java.generateSkeleton(true);
        wsdl2java.verbose(true);
        wsdl2java.generateTestCase(true);

        wsdl2java.emit(fileName);

        Wsdl2javaTestSuite.classNames.add(testNum, wsdl2java.getGeneratedClassNames());
        Wsdl2javaTestSuite.fileNames.add(testNum, wsdl2java.getGeneratedFileNames());

        this.testSuiteProject.executeTarget(Wsdl2javaTestSuite.COMPILE_TASK);
    } //protected void prepareTest()

    private void cleanTest() {
        Iterator i = Wsdl2javaTestSuite.classNames.iterator();
        while (i.hasNext()) {
            ((List) i.next()).clear();
        }
        Wsdl2javaTestSuite.classNames.clear();
        Wsdl2javaTestSuite.classNames = null;

        i = Wsdl2javaTestSuite.fileNames.iterator();
        while (i.hasNext()) {
            ((List) i.next()).clear();
        }
        Wsdl2javaTestSuite.fileNames.clear();
        Wsdl2javaTestSuite.fileNames = null;
        testSuiteProject.executeTarget(Wsdl2javaTestSuite.CLEAN_TASK);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner runner = null;
        try {
            loader = new URLClassLoader(new URL[] {new File(Wsdl2javaTestSuite.WORK_DIR).toURL()},
                Wsdl2javaTestSuite.class.getClassLoader());
            runner = (junit.swingui.TestRunner) loader.loadClass("junit.swingui.TestRunner").newInstance();
        } catch (Exception e) {
            System.exit(1);
        }
        runner.start(new String[] {"-noloading", Wsdl2javaTestSuite.class.getName()});
        runner.runSuite();
    } //public static void main(String[] args)

    public void run(TestResult result) {
        System.out.println("Starting test http server.");
        SimpleAxisServer server = new SimpleAxisServer();

        try {
            Options opts = new Options(new String[]{});
            int port = opts.getPort();
            ServerSocket ss = new ServerSocket(port);
            server.setServerSocket(ss);
            server.run();

            Iterator testIterator = Wsdl2javaTestSuite.fileNames.iterator();
            while (testIterator.hasNext()) {
                String deploy = null;

                Iterator files = ((List) testIterator.next()).iterator();
                while (files.hasNext()) {
                    String fileName = (String) files.next();
                    if (fileName.endsWith("deploy.xml")) {
                        deploy = fileName;
                    }
                }
                //deploy
                String[] args = new String[] { deploy };
                AdminClient.main(args);
            }

            //run tests
            super.run(result);

            testIterator = Wsdl2javaTestSuite.fileNames.iterator();
            while (testIterator.hasNext()) {
                String undeploy = null;

                Iterator files = ((List) testIterator.next()).iterator();
                while (files.hasNext()) {
                    String fileName = (String) files.next();
                    if (fileName.endsWith("undeploy.xml")) {
                        undeploy = fileName;
                    }
                }
                //undeploy
                String[] args = new String[] { undeploy };
                AdminClient.main(args);
            }

            this.cleanTest();

            System.out.println("Stopping test http server.");
            server.stop();
        } catch (Exception e) {
            throw new AssertionFailedError("The test suite failed with the following exception: " + e.getMessage());
        }
    }

    public static final TestSuite suite() {
        return new Wsdl2javaTestSuite();
    }
} //public class Wsdl2javaTestSuite extends TestCase
