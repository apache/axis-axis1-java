/*
 * Copyright 2002,2004 The Apache Software Foundation.
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

package org.apache.axis.tools.ant.axis;

import org.apache.axis.AxisFault;
import org.apache.axis.client.AdminClient;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import javax.xml.rpc.ServiceException;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Task to administer a local or remote Axis server. Remember, for remote admin,
 * the server has to be accept remote management calls.
 *
 * @ant.task category="axis" name="axis-admin"
 */
public class AdminClientTask extends MatchingTask {

    /**
     * flag to control action on execution trouble
     */
    private boolean failOnError = true;
    private String hostname;
    private int port = 0;
    private String servletPath;
    private File srcDir = null;
    private File xmlFile;
    private String transportChain;
    private String username;
    private String password;
    private String fileProtocol;
    private String action = "";
    private String url;
    private boolean debug;
    private String newPassword;
    private LinkedList argslist;
    private boolean fork = false;
    private Path classpath = null;

    /**
     * set a debug flag
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * set a new password; only valid if action=passwd
     *
     * @param newPassword
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * full url to the admin endpoint
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * specifies that a simple file protocol be used
     *
     * @param fileProtocol
     */
    public void setFileProtocol(String fileProtocol) {
        this.fileProtocol = fileProtocol;
    }

    /**
     * name the host to admin
     *
     * @param hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * the admin password
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * the port to connect to
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * the path to the AxisAdmin servlet
     *
     * @param servletPath
     */
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    /**
     * Set the source dir to find the source text files.
     */
    public void setSrcdir(File srcDir) {
        this.srcDir = srcDir;
    }

    /**
     * the name of the XML file containing deployment information
     *
     * @param xmlFile
     */
    public void setXmlFile(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    /**
     * set the transport chain to use
     *
     * @param transportChain
     */
    public void setTransportChain(String transportChain) {
        this.transportChain = transportChain;
    }

    /**
     * username to log in as
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Whether or not the build should halt if this task fails.
     * Defaults to <code>true</code>.
     */
    public void setFailOnError(boolean fail) {
        failOnError = fail;
    }

    /**
     * If true, forks the ant invocation.
     *
     * @param f "true|false|on|off|yes|no"
     */
    public void setFork(boolean f) {
        fork = f;
    }

    /**
     * validation code
     *
     * @throws org.apache.tools.ant.BuildException
     *          if validation failed
     */
    protected void validate()
            throws BuildException {
        if (srcDir != null) {
            if (!srcDir.exists()) {
                throw new BuildException("srcdir does not exist!");
            }
            if (!srcDir.isDirectory()) {
                throw new BuildException("srcdir is not a directory!");
            }
        }
    }

    /**
     * trace out parameters
     *
     * @param logLevel to log at
     * @see org.apache.tools.ant.Project#log
     */
    public void traceParams(int logLevel) {
        log("Running axis-admin with parameters:", logLevel);
        log("  action:" + action, logLevel);
        log("  url:" + url, logLevel);
        log("  hostname:" + hostname, logLevel);
        log("  port:" + port, logLevel);
        log("  servletPath:" + servletPath, logLevel);
        log("  fileProtocol:" + fileProtocol, logLevel);
        log("  username:" + username, logLevel);
        log("  password:" + password, logLevel);
        log("  transportChain:" + transportChain, logLevel);
        log("  debug:" + debug, logLevel);
    }

    /**
     * <p>Processes a set of administration commands.</p>
     * <p>The following commands are available:</p>
     * <ul>
     * <li><code>-l<i>url</i></code> sets the AxisServlet URL</li>
     * <li><code>-h<i>hostName</i></code> sets the AxisServlet host</li>
     * <li><code>-p<i>portNumber</i></code> sets the AxisServlet port</li>
     * <li><code>-s<i>servletPath</i></code> sets the path to the
     * AxisServlet</li>
     * <li><code>-f<i>fileName</i></code> specifies that a simple file
     * protocol should be used</li>
     * <li><code>-u<i>username</i></code> sets the username</li>
     * <li><code>-w<i>password</i></code> sets the password</li>
     * <li><code>-d</code> sets the debug flag (for instance, -ddd would
     * set it to 3)</li>
     * <li><code>-t<i>name</i></code> sets the transport chain touse</li>
     * <li><code>list</code> will list the currently deployed services</li>
     * <li><code>quit</code> will quit (???)</li>
     * <li><code>passwd <i>value</i></code> changes the admin password</li>
     * <li><code><i>xmlConfigFile</i></code> deploys or undeploys
     * Axis components and web services</li>
     * </ul>
     * <p>If <code>-l</code> or <code>-h -p -s</code> are not set, the
     * AdminClient will invoke
     * <code>http://localhost:8080/axis/servlet/AxisServlet</code>.
     * <p>
     * outputs XML result or null in case of failure. In the case of multiple
     * commands, the XML results will be concatenated, separated by \n
     *
     * @throws BuildException something went wrong
     */
    public void execute() throws BuildException {
        traceParams(Project.MSG_VERBOSE);
        validate();
        argslist = new LinkedList();

        //build an array of args
        addArgs("-l", url, url != null);
        addArgs("-h", hostname, hostname != null);
        addArgs("-p", Integer.toString(port), port != 0);
        addArgs("-s", servletPath, servletPath != null);
        addArgs("-f", fileProtocol, fileProtocol != null);
        addArgs("-u", username, username != null);
        addArgs("-w", password, password != null);
        addArgs("-t", transportChain, transportChain != null);
        addArg("-d", debug);
        //action
        addArg(action, action != null);
        //action extras
        if ("passwd".equals(action)) {
            if (newPassword == null) {
                throw new BuildException("No newpassword set for passwd");
            }
            addArg(newPassword);
        } else {
            if (newPassword != null) {
                throw new BuildException(
                        "newpassword is only used when action=passwd");
            }
        }

        //final param is the xml file(s)
        if (xmlFile != null) {
            if (!xmlFile.exists()) {
                throw new BuildException("File " + xmlFile + " no found");
            }
            addArg(xmlFile.toString());
        }
        if (srcDir != null) {
            DirectoryScanner ds = super.getDirectoryScanner(srcDir);
            String[] files = ds.getIncludedFiles();
            for (int i = 0; i < files.length; i++) {
                File srcFile = new File(srcDir, files[i]);
                if (!srcFile.exists()) {
                    throw new BuildException("File " + srcFile + " no found");
                }
                addArg(srcFile.getAbsolutePath());
            }
        }
        
        //turn the list into an array
        int counter = 0;
        String[] args = new String[argslist.size()];
        Iterator it = argslist.iterator();
        while (it.hasNext()) {
            String arg = (String) it.next();
            args[counter] = arg;
            counter++;
        }
        if (fork) {
            executeInForkedVM(args);
        } else {
            executeInCurrentVM(args);
        }
    }

    private void executeInForkedVM(String[] args) {
        try {
            // Create an instance of the compiler, redirecting output to
            // the project log
            Java java = (Java) (getProject().createTask("java"));
            getProject().log("using classpath: " + classpath,
                    Project.MSG_DEBUG);
            java.setClasspath(classpath);
            java.setClassname("org.apache.axis.client.AdminClient");
            for (int i = 0; i < args.length; i++) {
                java.createArg().setValue(args[i]);
            }
            java.setFailonerror(failOnError);
            //we are forking here to be sure that if AdminClient calls
            //System.exit() it doesn't halt the build
            java.setFork(true);
            java.setTaskName("AdminClient");
            java.execute();
        } catch (BuildException e) {
            //rethrow these
            throw e;
        } catch (Exception e) {
            throw new BuildException("Exception in " + getTaskName(), e);
        }
    }

    private void executeInCurrentVM(String[] args) {
        //now create a client and invoke it
        AdminClient admin = null;
        try {
            admin = new AdminClient(true);
        } catch (ServiceException e) {
            throw new BuildException("failed to start the axis engine", e);
        }
        String result = null;
        try {
            result = admin.process(args);
            if (result != null) {
                log(result);
            } else {
                logOrThrow(getTaskName() + " got a null response");
            }
        } catch (AxisFault fault) {
            log(fault.dumpToString(), Project.MSG_ERR);
            traceParams(Project.MSG_ERR);
            logOrThrow(getTaskName()
                    + " failed with  "
                    + fault.getFaultCode().toString()
                    + " " + fault.getFaultString());
        } catch (BuildException e) {
            //rethrow these
            throw e;
        } catch (Exception e) {
            throw new BuildException("Exception in " + getTaskName(), e);
        }
    }

    private void logOrThrow(String text) throws BuildException {
        if (failOnError) {
            throw new BuildException(text);
        } else {
            log(text, Project.MSG_ERR);
        }
    }

    /**
     * add one arg
     *
     * @param argument
     */
    protected void addArg(String argument) {
        argslist.add(argument);
    }

    /**
     * add one arg
     *
     * @param argument
     */
    protected void addArg(String argument, boolean test) {
        if (test) {
            argslist.add(argument);
        }
    }

    /**
     * add an arg pair
     *
     * @param argument
     * @param param
     */
    protected void addArgs(String argument, String param) {
        addArg(argument);
        addArg(param);
    }

    /**
     * add an arg pair if the test is true
     *
     * @param argument first arg
     * @param param    param to accompany
     * @param test     test to trigger argument add
     */
    protected void addArgs(String argument, String param, boolean test) {
        if (test) {
            addArg(argument);
            addArg(param);
        }
    }

    /**
     * Set the optional classpath 
     *
     * @param classpath the classpath to use when loading class
     */
    public void setClasspath(Path classpath) {
        createClasspath().append(classpath);
    }

    /**
     * Set the optional classpath 
     *
     * @return a path instance to be configured by the Ant core.
     */
    public Path createClasspath() {
        if (classpath == null) {
            classpath = new Path(getProject());
        }
        return classpath.createPath();
    }

    /**
     * Set the reference to an optional classpath 
     *
     * @param r the id of the Ant path instance to act as the classpath
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }
}
