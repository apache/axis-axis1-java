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

package org.apache.axis.transport.mail;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.i18n.Messages;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.net.pop3.POP3Client;
import org.apache.commons.net.pop3.POP3MessageInfo;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * This is a simple implementation of an SMTP/POP3 server for processing
 * SOAP requests via Apache's xml-axis.  This is not intended for production
 * use.  Its intended uses are for demos, debugging, and performance
 * profiling.
 *
 * @author Davanum Srinivas <dims@yahoo.com>
 * @author Rob Jellinghaus (robj@unrealities.com)
 */

public class MailServer implements Runnable {
    protected static Log log =
            LogFactory.getLog(MailServer.class.getName());

    private String host;
    private int port;
    private String userid;
    private String password;

    public MailServer(String host, int port, String userid, String password) {
        this.host = host;
        this.port = port;
        this.userid = userid;
        this.password = password;
    }

    // Are we doing threads?
    private static boolean doThreads = true;

    public void setDoThreads(boolean value) {
        doThreads = value;
    }

    public boolean getDoThreads() {
        return doThreads;
    }

    public String getHost() {
        return host;
    }

    // Axis server (shared between instances)
    private static AxisServer myAxisServer = null;

    protected static synchronized AxisServer getAxisServer() {
        if (myAxisServer == null) {
            myAxisServer = new AxisServer();
        }
        return myAxisServer;
    }

    // are we stopped?
    // latch to true if stop() is called
    private boolean stopped = false;

    /**
     * Accept requests from a given TCP port and send them through the
     * Axis engine for processing.
     */
    public void run() {
        log.info(Messages.getMessage("start00", "MailServer", host + ":" + port));

        // Accept and process requests from the socket
        while (!stopped) {
            try {
                pop3.connect(host, port);
                pop3.login(userid, password);

                POP3MessageInfo[] messages = pop3.listMessages();
                if (messages != null && messages.length > 0) {
                    for (int i = 0; i < messages.length; i++) {
                        Reader reader = pop3.retrieveMessage(messages[i].number);
                        if (reader == null) {
                            continue;
                        }

                        StringBuffer buffer = new StringBuffer();
                        BufferedReader bufferedReader =
                                new BufferedReader(reader);
                        int ch;
                        while ((ch = bufferedReader.read()) != -1) {
                            buffer.append((char) ch);
                        }
                        bufferedReader.close();
                        ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString().getBytes());
                        Properties prop = new Properties();
                        Session session = Session.getDefaultInstance(prop, null);

                        MimeMessage mimeMsg = new MimeMessage(session, bais);
                        pop3.deleteMessage(messages[i].number);
                        if (mimeMsg != null) {
                            MailWorker worker = new MailWorker(this, mimeMsg);
                            if (doThreads) {
                                Thread thread = new Thread(worker);
                                thread.setDaemon(true);
                                thread.start();
                            } else {
                                worker.run();
                            }
                        }
                    }
                }
            } catch (java.io.InterruptedIOException iie) {
            } catch (Exception e) {
                log.debug(Messages.getMessage("exception00"), e);
                break;
            } finally {
                try {
                    pop3.logout();
                    pop3.disconnect();
                    Thread.sleep(3000);
                } catch (Exception e) {
                    log.error(Messages.getMessage("exception00"), e);
                }
            }
        }
        log.info(Messages.getMessage("quit00", "MailServer"));
    }

    /**
     * POP3 connection
     */
    private POP3Client pop3;

    /**
     * Obtain the serverSocket that that MailServer is listening on.
     */
    public POP3Client getPOP3() {
        return pop3;
    }

    /**
     * Set the serverSocket this server should listen on.
     * (note : changing this will not affect a running server, but if you
     *  stop() and then start() the server, the new socket will be used).
     */
    public void setPOP3(POP3Client pop3) {
        this.pop3 = pop3;
    }

    /**
     * Start this server.
     *
     * Spawns a worker thread to listen for HTTP requests.
     *
     * @param daemon a boolean indicating if the thread should be a daemon.
     */
    public void start(boolean daemon) throws Exception {
        if (doThreads) {
            Thread thread = new Thread(this);
            thread.setDaemon(daemon);
            thread.start();
        } else {
            run();
        }
    }

    /**
     * Start this server as a NON-daemon.
     */
    public void start() throws Exception {
        start(false);
    }

    /**
     * Stop this server.
     *
     * This will interrupt any pending accept().
     */
    public void stop() throws Exception {
        /* 
         * Close the server socket cleanly, but avoid fresh accepts while
         * the socket is closing.
         */
        stopped = true;
        log.info(Messages.getMessage("quit00", "MailServer"));

        // Kill the JVM, which will interrupt pending accepts even on linux.
        System.exit(0);
    }

    /**
     * Server process.
     */
    public static void main(String args[]) {
        Options opts = null;
        try {
            opts = new Options(args);
        } catch (MalformedURLException e) {
            log.error(Messages.getMessage("malformedURLException00"), e);
            return;
        }

        try {
            doThreads = (opts.isFlagSet('t') > 0);
            String host = opts.getHost();
            int port = ((opts.isFlagSet('p') > 0) ? opts.getPort() : 110);
            POP3Client pop3 = new POP3Client();
            MailServer sas = new MailServer(host, port, opts.getUser(), opts.getPassword());

            sas.setPOP3(pop3);
            sas.start();
        } catch (Exception e) {
            log.error(Messages.getMessage("exception00"), e);
            return;
        }

    }
}
