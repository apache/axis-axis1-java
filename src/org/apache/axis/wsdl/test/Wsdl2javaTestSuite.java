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
import org.apache.axis.utils.AxisClassLoader;
import org.apache.axis.wsdl.Emitter;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.FileSet;

import java.io.*;
import java.net.ServerSocket;
import java.net.URL;
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
    private static final AxisClassLoader loader = AxisClassLoader.getClassLoader();

    /**
     * Instantiate a new TestSuite with all the tasks necessary to collect, compile,
     * and prepare the tests.
     */
    public Wsdl2javaTestSuite() {
        super();
        this.setupTasks();
        this.prepareTests();
    }

    /**
     * Standard JUnit invocation.  This is not the standard entry point.
     */
    public Wsdl2javaTestSuite(String name) {
        super(name);

        this.setupTasks();
        this.prepareTests();
    } //public Wsdl2javaTestSuite(String Name_)

    /**
     * Setup the Ant Tasks to handle the compilation and cleanup of the test environment.
     * We programatically create a project instead of using an XML file.  The project is
     * a singleton, so we only perform this once.
     */
    private void setupTasks() {
        if (Wsdl2javaTestSuite.testSuiteProject == null) {
            File workDir = new File(Wsdl2javaTestSuite.WORK_DIR);
            workDir.mkdirs();

            /* Create the project.  We name it "Wsdl2javaTestSuite", and set the base
             * directory to the current directory.  This means if it is called from the
             * main ant task, it will find everything correctly.  We also set the
             * default target name to COMPILE_TASK.
             */
            testSuiteProject = new Project();
            testSuiteProject.init();
            testSuiteProject.setName("Wsdl2javaTestSuite");
            testSuiteProject.addReference("Wsdl2javaTestSuite", testSuiteProject);
            testSuiteProject.setBasedir("./");
            testSuiteProject.setDefaultTarget(Wsdl2javaTestSuite.COMPILE_TASK);

            /* Set up the default task (the compile task).  We add the "javac" target,
             * and all the options for it.
             */
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

            /* Set up the CLEAN_TASK.  It has the "delete" task, and will clean up all
             * the working files.
             */
            Target cleanup = new Target();
            cleanup.setName(Wsdl2javaTestSuite.CLEAN_TASK);
            testSuiteProject.addTarget(Wsdl2javaTestSuite.CLEAN_TASK, cleanup);
            Delete delete = (Delete) testSuiteProject.createTask("delete");
            delete.setLocation(new Location("Wsdl2javaTestSuite"));
            delete.setOwningTarget(cleanup);
            cleanup.addTask(delete);
            delete.init();

            delete.setDir(workDir);
        }
    }

    /**
     * Prepare the tests we will generate and run.  Here we gather all the WSDL files we will generate classes from.
     * Next, we run them through Wsdl2java with a standard set of options.  Then we run the COMPILE_TASK
     * from the previously created Ant Project.  Lastly, we iterate over all the generated classes, add
     * them to the ClassLoader, and add the ones that end in "TestCase" to the list of tests we will run.
     */
    private void prepareTests() {
        if (null ==  Wsdl2javaTestSuite.classNames) {
            Wsdl2javaTestSuite.classNames = new ArrayList();
            Wsdl2javaTestSuite.fileNames = new ArrayList();
            // The file is the same as this class name, but with the ".list" extension.
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader()
                    .getResourceAsStream(this.getClass().getName().replace('.', '/') + ".list")));

            try {
                // Each line is a new WSDL file.
                String curLine = reader.readLine();
                int testNum = 0;
                while (curLine != null) {
                    curLine = curLine.trim();
                    if ( "".equals(curLine) ) {
                        curLine = reader.readLine();
                        continue;
                    }
                    // Run Wsdl2java on the WSDL file.
                    // The test number is used to keep each WSDL file in a different package.
                    this.prepareTest(curLine, testNum);

                    // Setup Tests
                    Iterator names = ((List) Wsdl2javaTestSuite.classNames.get(testNum)).iterator();
                    while (names.hasNext()) {
                        String className = (String) names.next();

                        // Register all generated classes with the classloader.
                        if ( !loader.isClassRegistered(className) ) {
                            String classFile = Wsdl2javaTestSuite.WORK_DIR;
                            classFile += className.replace('.', File.separatorChar) + ".class";
                            loader.registerClass(className, classFile);
                        }

                        // Add all "TestCase" classes to the list of tests we run
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

    /**
     * Generate the classes using Wsdl2java.  Currently we use Emitter directly, but as Emitter gets redesigned, we will
     * have to make an acceptible wrapper class.  We generate the package name to be "org.apache.axisttest" with the
     * testNum appended to it.  We also enablt skeleton generation and testcase generation.  We also turn on verbosity.
     */
    protected void prepareTest(String fileName, int testNum) throws Exception {
        String packageName = fileName.replace('/', '.');
        Emitter wsdl2java = new Emitter();
        packageName = packageName.substring(0, fileName.lastIndexOf('/'));
        wsdl2java.setPackageName(packageName);
        wsdl2java.generatePackageName(true);
        wsdl2java.setOutputDir(Wsdl2javaTestSuite.WORK_DIR);
        wsdl2java.generateSkeleton(true);
        wsdl2java.generateTestCase(true);

        /* Copy concrete implementation files to the work directory.
         */
        File implDir = new File(fileName.substring(0, fileName.lastIndexOf('/')));
        if (implDir.isDirectory()) {
            File[] files = implDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith("Impl.java") || files[i].getName().endsWith("TestCase.java")) {
                    File subDir = new File(Wsdl2javaTestSuite.WORK_DIR, implDir.toString());
                    subDir.mkdirs();
                    File newFile = new File(subDir, files[i].getName());
                    BufferedInputStream is = new BufferedInputStream(new FileInputStream(files[i]));
                    BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(newFile));
                    byte[] buffer = new byte[1024];
                    int length = -1;
                    while ((length = is.read(buffer)) != -1) {
                        os.write(buffer, 0, length);
                    }
                    os.flush();
                    is.close();
                    os.close();
                }
            }
        }

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

    /**
     * Convenience method to run the test case from the command-line.
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.main(new String[] {"-noloading", Wsdl2javaTestSuite.class.getName()});
    } //public static void main(String[] args)

    /**
     * Override JUnit's <code>run(TestResult)</code> method.  Basically all we are doing is wrapping it
     * with code to start the SimpleAxisServer, deploy all the generated services, undeploy all the
     * generated services, stop the SimpleAxisServer, and clean up the test environment.
     */
    public void run(TestResult result) {
        // Get the SimpleAxisServer running--using the default port.
        System.out.println("Starting test http server.");
        SimpleAxisServer server = new SimpleAxisServer();

        try {
            Options opts = new Options(new String[]{});
            int port = opts.getPort();
            ServerSocket ss = new ServerSocket(port);
            server.setServerSocket(ss);
            Thread serverThread = new Thread(server);
            serverThread.setDaemon(true);
            serverThread.setContextClassLoader(loader);
            serverThread.start();

            // Find all the "deploy.xml" files and run them through the AdminClient.
            Iterator testIterator = Wsdl2javaTestSuite.fileNames.iterator();
            while (testIterator.hasNext()) {
                String deploy = null;

                Iterator files = ((List) testIterator.next()).iterator();
                while (files.hasNext()) {
                    String fileName = (String) files.next();
                    if (fileName.endsWith(File.separator + "deploy.xml")) {
                        deploy = fileName;
                    }
                }
                // Perform actual deployment
                String[] args = new String[] { Wsdl2javaTestSuite.WORK_DIR + deploy };
                AdminClient.main(args);
            }

            //AdminClient.main(new String[] {"list"});

            // Run the tests
            super.run(result);

            // Find all the "undeploy.xml" files and run them through the AdminClient.
            testIterator = Wsdl2javaTestSuite.fileNames.iterator();
            while (testIterator.hasNext()) {
                String undeploy = null;

                Iterator files = ((List) testIterator.next()).iterator();
                while (files.hasNext()) {
                    String fileName = (String) files.next();
                    if (fileName.endsWith(File.separator + "undeploy.xml")) {
                        undeploy = fileName;
                    }
                }
                // Perform actual undeployment
                String[] args = new String[] { Wsdl2javaTestSuite.WORK_DIR + undeploy };
                AdminClient.main(args);
            }

            //AdminClient.main(new String[] {"list"});

            // Clean up the test environment
            //this.cleanTest(); // commented out while debugging.

            // Stop the SimpleAxisServer
            System.out.println("Stopping test http server.");
            server.stop();
        } catch (Exception e) {
            throw new AssertionFailedError("The test suite failed with the following exception: " + e.getMessage());
        }
    }

    /**
     * Static method for JUnit to treat this like a TestSuite.
     */
    public static final TestSuite suite() {
        return new Wsdl2javaTestSuite();
    }
} //public class Wsdl2javaTestSuite extends TestCase
