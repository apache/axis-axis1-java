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

package org.apache.axis.components.threadpool;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.i18n.Messages;
import org.apache.commons.logging.Log;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class ThreadPool {

    protected static Log log =
        LogFactory.getLog(ThreadPool.class.getName());

    public static final long MAX_THREADS = 100;
    
    protected Map threads = new Hashtable();
    protected long threadcount;
    public boolean _shutdown;

    public void cleanup()
        throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: ThreadPool::cleanup");
        }
        if (!isShutdown()) {
          safeShutdown();
          awaitShutdown();
        }
        synchronized(this) {
          threads.clear();
          _shutdown = false;
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: ThreadPool::cleanup");
        }
    }

    /**
     * Returns true if all workers have been shutdown
     */
    public boolean isShutdown() {
        synchronized (this) {
            return _shutdown && threadcount == 0;
        }
    }

    /**
     * Returns true if all workers are in the process of shutting down
     */
    public boolean isShuttingDown() {
        synchronized (this) {
            return _shutdown;
        }
    }

    /**
     * Returns the total number of currently active workers
     */
    public long getWorkerCount() {
        synchronized (this) {
            return threadcount;
        }
    }

    /**
     * Adds a new worker to the pool
     */
    public void addWorker(
            Runnable worker) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: ThreadPool::addWorker");
        }
        if (_shutdown ||
            threadcount == MAX_THREADS)
            throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
        Thread thread = new Thread(worker);
        threads.put(worker, thread);
        threadcount++;
        thread.start();
        if (log.isDebugEnabled()) {
            log.debug("Exit: ThreadPool::addWorker");
        }
    }

    /**
     * Forcefully interrupt all workers
     */
    public void interruptAll() {
        if (log.isDebugEnabled()) {
            log.debug("Enter: ThreadPool::interruptAll");
        }
        synchronized (threads) {
            for (Iterator i = threads.values().iterator(); i.hasNext();) {
                Thread t = (Thread) i.next();
                t.interrupt();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: ThreadPool::interruptAll");
        }
    }

    /**
     * Forcefully shutdown the pool
     */
    public void shutdown() {
        if (log.isDebugEnabled()) {
            log.debug("Enter: ThreadPool::shutdown");
        }
        synchronized (this) {
            _shutdown = true;
        }
        interruptAll();
        if (log.isDebugEnabled()) {
            log.debug("Exit: ThreadPool::shutdown");
        }
    }

    /**
     * Forcefully shutdown the pool
     */
    public void safeShutdown() {
        if (log.isDebugEnabled()) {
            log.debug("Enter: ThreadPool::safeShutdown");
        }
        synchronized (this) {
            _shutdown = true;
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: ThreadPool::safeShutdown");
        }
    }

    /**
     * Await shutdown of the worker
     */
    public synchronized void awaitShutdown()
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: ThreadPool::awaitShutdown");
        }
        if (!_shutdown)
            throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
        while (threadcount > 0)
            wait();
        if (log.isDebugEnabled()) {
            log.debug("Exit: ThreadPool::awaitShutdown");
        }
    }

    /**
     * Await shutdown of the worker
     */
    public synchronized boolean awaitShutdown(long timeout)
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: ThreadPool::awaitShutdown");
        }
        if (!_shutdown)
            throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
        if (threadcount == 0) {
            if (log.isDebugEnabled()) {
                log.debug("Exit: ThreadPool::awaitShutdown");
            }
            return true;
        }
        long waittime = timeout;
        if (waittime <= 0) {
            if (log.isDebugEnabled()) {
                log.debug("Exit: ThreadPool::awaitShutdown");
            }
            return false;
        }
        long start = System.currentTimeMillis();
        for (; ;) {
            wait(waittime);
            if (threadcount == 0) {
                if (log.isDebugEnabled()) {
                    log.debug("Exit: ThreadPool::awaitShutdown");
                }
                return true;
            }
            waittime = timeout - System.currentTimeMillis();
            if (waittime <= 0) {
                if (log.isDebugEnabled()) {
                    log.debug("Exit: ThreadPool::awaitShutdown");
                }
                return false;
            }
        }
    }

    /**
     * Used by MessageWorkers to notify the pool that it is done
     */
    public void workerDone(
            Runnable worker, 
            boolean restart) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: ThreadPool::workerDone");
        }
        synchronized(this) {
            threads.remove(worker);
            if (--threadcount == 0 && _shutdown) {
                notifyAll();
            }
            if (!_shutdown && restart) {
                addWorker(worker);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: ThreadPool::workerDone");
        }        
    }
}

