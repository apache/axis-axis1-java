/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package org.apache.axis.components.threadpool;

import java.util.LinkedList;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.axis.utils.Messages;

/**
 * Pooled TaskManager implementation. 
 * Uses pool of threads to execute the tasks. The pools of threads grows at
 * specified rate and shrinks with time if not being used.
 */
public class PooledTaskManager implements TaskManager {

    private static final int DEFAULT_NOTIFICATION_EXPONENT = 2;

    protected static Log log =
        LogFactory.getLog(PooledTaskManager.class.getName());

    private Queue queue;
    private ArrayList threads;
    private double exponent;
    private int computedSize;
    
    public PooledTaskManager() {
        this(DEFAULT_NOTIFICATION_EXPONENT);
    }

    public PooledTaskManager(double exponent) {
        this.exponent = exponent;
        this.queue = new Queue();
        this.threads = new ArrayList();
        this.computedSize = 0;
    }
        
    public void execute(Runnable r) {
        int size = this.queue.enqueue(r);
        addThread(size);
    }
        
    private synchronized void computeSize() {
        this.computedSize = 
            (int)Math.pow(this.threads.size(), this.exponent);
    }
    
    private synchronized void addThread(int size) {
        if (size > this.computedSize) {
            NotificationThread thread = new NotificationThread();
            thread.setDaemon(true);
            this.threads.add(thread);
            thread.start();
            computeSize();
        }
    }
    
    private synchronized void removeThread(Thread thread) {
            this.threads.remove(thread);
            computeSize();
    }
    
    
    private class NotificationThread extends Thread {

        public void run() {
            Runnable r = null;
            while(true) {
                try {
                    r = (Runnable)queue.dequeue();
                } catch (InterruptedException e) {
                    break;
                }
                if (r == null) {
                    break;
                }
                try {
                    r.run();
                } catch (Throwable e) {
                    log.debug(Messages.getMessage("exceptionPrinting"), e);
                }
            }
            removeThread(this);
        }
    }
    
    private static class Queue {
        
        private LinkedList queue = new LinkedList();
        
        public synchronized int enqueue(Runnable r) {
            queue.add(r);
            notify();
            return queue.size();
        }
        
        public synchronized Object dequeue() throws InterruptedException {
            if (queue.isEmpty()) {
                wait(1000 * 60 * 2);
            }
            return (queue.isEmpty()) ? null : queue.removeFirst();
        }
        
    }
       
}
