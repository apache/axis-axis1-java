/*
 * Copyright 2001,2004 The Apache Software Foundation.
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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.CallTarget;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

/**
 * Ant task for starting / stopping servers and running junit in the middle.
 * Based on the Cactus org.apache.commons.cactus.ant package, heavily munged
 * and cruftily dumped into one file.
 * <p>
 * <i>For Axis development; there is no support or stability associated 
 *  with this task</i> 
 * @ant.task category="axis"
 * @author Rob Jellinghaus (robj@unrealities.com)
 */
public class RunAxisFunctionalTestsTask extends Task
{
    private String tcpServerTarget = null;
    private String httpServerTarget = null;
    private String testTarget;
    private String httpStopTarget = null;
    private URL url = null;

    /**
     * Executes the task.
     */
    public void execute() throws BuildException
    {
        try {
            callStart(tcpServerTarget);
            callStart(httpServerTarget);
            callTests();
        } finally {
            // Make sure we stop the server
            callStop();
        }
    }

    /**
     * Call the start server task
     */
    private void callStart(String startTarget)
    {
        if (startTarget == null) {
            return;
        }

        // Execute the ant target
        new Thread(new TaskRunnable(startTarget)).start();

        if (! startTarget.equals(tcpServerTarget))
            return;
        
        // try a ping for the TCP server
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
            try {
                sendOnSocket("ping\r\n");
                // if no exception, return
                System.out.println("RunAxisFunctionalTestsTask.callStart successfully pinged server.");
                return;
            } catch (Exception ex) {
                // loop & try again
            }
        }

        // NOTREACHED since the while loop returns if it successfully pings
    }

    /**
     * Call the run tests target
     */
    private void callTests()
    {
        antcall(testTarget);
    }

    /**
     * Call the stop server task
     */
    private void callStop()
    {
        try {
            // first, stop the tcp server
            if (tcpServerTarget != null) {
                sendOnSocket("quit\r\n");
            }
            
            
            // second, and more involvedly, stop the http server
            // Try connecting in case the server is already stopped.
            if (httpServerTarget != null) {
                URL url = new URL("http://localhost:8080/");
                try {
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.connect();
                    readFully(connection);
                    connection.disconnect();
                } catch (IOException e) {
                    // Server is not running. Make this task a no-op.
                    System.out.println("Error from HTTP read: " + e);
                    return;
                }
            }

            // Call the target that stops the server
            antcall(httpStopTarget);

            // Wait a few ms more (just to make sure)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new BuildException("Interruption during sleep", e);
            }

            /*
             // Continuously try calling the test URL until it fails
            while (true) {
System.out.println("Trying localhost:8080...");
                try {
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.connect();
                    this.readFully(connection);
                    connection.disconnect();
                } catch (IOException e) {
                    break;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ee) {
                    throw new BuildException("Interruption during sleep", ee);
                }

            }

            // Wait a few ms more (just to be sure !)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new BuildException("Interruption during sleep", e);
            }
             */
            System.out.println("RunAxisFunctionalTestsTask.callStop successfully sent quit message.");
        } catch (Exception ex) {
            // ignore; if socket not there, presume dead already
        }
    }


    /**
     * Call the selected ant task.
     */
    private void antcall (String taskName) {
        CallTarget callee;
        callee = (CallTarget)project.createTask("antcall");
        callee.setOwningTarget(target);
        callee.setTaskName(getTaskName());
        callee.setLocation(location);
        callee.init();
        callee.setTarget(taskName);
        callee.execute();
    }

    /**
     * Make a socket to the url, and send the given string
     */
    private void sendOnSocket (String str) throws Exception {
        if (url == null)
            return;
        
        Socket sock = null;
        try {
            sock = new Socket(url.getHost(), url.getPort());
            sock.getOutputStream().write(new String(str).getBytes());
            // get a single byte response
            int i = sock.getInputStream().read();
        } catch (Exception ex) {
            throw ex;
        }/* finally {
            if (sock != null) {
                try {
                    sock.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
         }*/
    }


    /**
     * Read all the contents that are to be read
     */
    static void readFully(HttpURLConnection connection) throws IOException
    {
        // finish reading it to prevent (harmless) server-side exceptions
        BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
        byte[] buffer = new byte[256];
        while((is.read(buffer)) > 0) {}
        is.close();
    }

    /**
     * Sets the target to call to start server 1.
     *
     * @param theStartTarget the Ant target to call
     */
    public void setTcpServerTarget(String theStartTarget)
    {
        tcpServerTarget = theStartTarget;
    }

    /**
     * Sets the target to call to start server 2.
     *
     * @param theStartTarget the Ant target to call
     */
    public void setHttpServerTarget(String theStartTarget)
    {
        httpServerTarget = theStartTarget;
    }

    /**
     * Sets the target to call to run the tests.
     *
     * @param theTestTarget the Ant target to call
     */
    public void setTestTarget(String theTestTarget)
    {
        testTarget = theTestTarget;
    }

    /**
     * Sets the stop target.  This is the target which does
     * a HTTP admin shutdown on the simple server.
     */
    public void setHttpStopTarget (String theStopTarget)
    {
        httpStopTarget = theStopTarget;
    }

    /**
     * Sets the target URL (just http://host:port)
     */
    public void setUrl (String theUrl) {
        try {
            url = new URL(theUrl);
        } catch (MalformedURLException ex) {
            System.err.println("Can't make URL from "+theUrl);
        }
    }


    /**
     * Helper class to execute a task in a thread.
     */
    public class TaskRunnable implements Runnable
    {
        String taskName;
        public TaskRunnable (String taskName) {
            this.taskName = taskName;
        }
        public void run () {
            antcall(taskName);
        }
    }
}


