/*
 * The Apache Software License, Version 1.1
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
package test.functional.ant;

import java.net.*;
import java.io.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;

/**
 * Ant task for starting / stopping servers and running junit in the middle.
 * Based on the Cactus org.apache.commons.cactus.ant package, heavily munged
 * and cruftily dumped into one file.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 */
public class RunAxisFunctionalTestsTask extends Task
{
    private String startTarget1;
    private String startTarget2;
    private String testTarget;
    private String stopTarget;
    private URL url;
    
    /**
     * Executes the task.
     */
    public void execute() throws BuildException
    {
        try {
            callStart(startTarget1);
            callStart(startTarget2);
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
        new Thread(new TaskRunnable(startTarget)).start();
        // try a ping
        while (true) {
            try {
                Thread.currentThread().sleep(500);
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
            sendOnSocket("quit\r\n");
            
            // second, and more involvedly, stop the http server
            // Try connecting in case the server is already stopped.
            URL url = new URL("http://localhost:8080/");
            try {
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                this.readFully(connection);
                connection.disconnect();
            } catch (IOException e) {
                // Server is not running. Make this task a no-op.
                System.out.println(e);
                return;
            }
            
            // Call the target that stops the server
            antcall(stopTarget);
            
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
    public void setStartTarget1(String theStartTarget)
    {
        startTarget1 = theStartTarget;
    }
    
    /**
     * Sets the target to call to start server 2.
     *
     * @param theStartTarget the Ant target to call
     */
    public void setStartTarget2(String theStartTarget)
    {
        startTarget2 = theStartTarget;
    }
    
    /**
     * Sets the target to call to run the tests.
     *
     * @param theTerstTarget the Ant target to call
     */
    public void setTestTarget(String theTestTarget)
    {
        testTarget = theTestTarget;
    }
    
    /**
     * Sets the stop target.  This is the target which does
     * a HTTP admin shutdown on the simple server.
     */
    public void setStopTarget (String theStopTarget)
    {
        stopTarget = theStopTarget;
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


