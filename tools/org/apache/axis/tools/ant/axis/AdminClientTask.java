/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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

package org.apache.axis.tools.ant.axis;

import org.apache.tools.ant.*;
import org.apache.axis.client.AdminClient;

import java.util.LinkedList;
import java.util.Iterator;
import java.io.File;

/**
 * Task to offer axis to the Axis AdminClient
 * @ant.task category="axis"
 */
public class AdminClientTask extends Task {

    /**
     *  flag to control action on execution trouble
     */
    private boolean failOnError = true;

    private String hostname;

    private int port = 0;

    private String servletPath;

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


    /**
     * set a debug flag
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * set a new password; only valid if action=passwd
     * @param newPassword
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * full url to the admin endpoint
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }


    /**
     * specifies that a simple file protocol be used
     * @param fileProtocol
     */
    public void setFileProtocol(String fileProtocol) {
        this.fileProtocol = fileProtocol;
    }

    /**
     * name the host to admin
     * @param hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * the admin password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * the port to connect to
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * the path to the AxisAdmin servlet
     * @param servletPath
     */
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    /**
     * the name of the XML file containing deployment information
     * @param xmlFile
     */
    public void setXmlFile(File xmlFile) {
        this.xmlFile = xmlFile;
    }


    /**
     *  set the transport chain to use
     * @param transportChain
     */
    public void setTransportChain(String transportChain) {
        this.transportChain = transportChain;
    }

    /**
     * username to log in as
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
     * <p>Processes a set of administration commands.</p>
     * <p>The following commands are available:</p>
     * <ul>
     *   <li><code>-l<i>url</i></code> sets the AxisServlet URL</li>
     *   <li><code>-h<i>hostName</i></code> sets the AxisServlet host</li>
     *   <li><code>-p<i>portNumber</i></code> sets the AxisServlet port</li>
     *   <li><code>-s<i>servletPath</i></code> sets the path to the
     *              AxisServlet</li>
     *   <li><code>-f<i>fileName</i></code> specifies that a simple file
     *              protocol should be used</li>
     *   <li><code>-u<i>username</i></code> sets the username</li>
     *   <li><code>-p<i>password</i></code> sets the password</li>
     *   <li><code>-d</code> sets the debug flag (for instance, -ddd would
     *      set it to 3)</li>
     *   <li><code>-t<i>name</i></code> sets the transport chain touse</li>
     *   <li><code>list</code> will list the currently deployed services</li>
     *   <li><code>quit</code> will quit (???)</li>
     *   <li><code>passwd <i>value</i></code> changes the admin password</li>
     *   <li><code><i>xmlConfigFile</i></code> deploys or undeploys
     *       Axis components and web services</li>
     * </ul>
     * <p>If <code>-l</code> or <code>-h -p -s</code> are not set, the
     * AdminClient will invoke
     * <code>http://localhost:8080/axis/servlet/AxisServlet</code>.</p>
     *
     * @return XML result or null in case of failure. In the case of multiple
     * commands, the XML results will be concatenated, separated by \n
     * @exception BuildException something went wrong
     */

    public void execute() throws BuildException {
        argslist = new LinkedList();

        //build an array of args
        addArgs("-l", url, url != null);
        addArgs("-h", hostname, hostname != null);
        addArgs("-p", Integer.toString(port), port != 0);
        addArgs("-s", servletPath, servletPath != null);
        addArgs("-f", fileProtocol, fileProtocol != null);
        addArgs("-u", username, username != null);
        addArgs("-p", password, password != null);
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


        //final param is the xml file
        if (xmlFile != null) {
            if (!xmlFile.exists()) {
                throw new BuildException("File " + xmlFile + " no found");
            }
            addArg(xmlFile.toString());
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

        //now create a client and invoke it
        try {
            AdminClient admin = new AdminClient();
            String result = admin.process(args);

            if (result != null) {
                log(result);
            } else {
                String text = "Something seems to have gone wrong";
                if (failOnError) {
                    throw new BuildException(text);
                } else {
                    log(text, Project.MSG_ERR);
                }
            }
        } catch (Exception e) {
            throw new BuildException("While calling the AdminClient", e);
        }
    }


    /**
     * add one arg
     * @param argument
     */

    protected void addArg(String argument) {
        argslist.add(argument);
    }

    /**
     * add one arg
     * @param argument
     */

    protected void addArg(String argument, boolean test) {
        if (test) {
            argslist.add(argument);
        }
    }

    /**
     * add an arg pair
     * @param argument
     * @param param
     */
    protected void addArgs(String argument, String param) {
        addArg(argument);
        addArg(param);
    }

    /**
     * add an arg pair if the test is true
     * @param argument first arg
     * @param param param to accompany
     * @param test test to trigger argument add
     */
    protected void addArgs(String argument, String param, boolean test) {
        if (test) {
            addArg(argument);
            addArg(param);
        }
    }

}
