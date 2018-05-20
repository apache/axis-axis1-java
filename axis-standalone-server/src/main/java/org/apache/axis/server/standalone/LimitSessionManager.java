/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.server.standalone;

import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.jetty.servlet.HashSessionManager;

/**
 * {@link HashSessionManager} extension that limits the number of concurrently active session.
 * 
 * @author Andreas Veithen
 */
final class LimitSessionManager extends HashSessionManager {
    // This is only needed to get access to some protected methods/fields.
    class Session extends HashSessionManager.Session {
        private static final long serialVersionUID = -6648322281268846583L;

        Session(HttpServletRequest request) {
            super(request);
        }
        
        long accessed() {
            return _accessed;
        }

        protected void timeout() {
            super.timeout();
        }
    }
    
    private final int maxSessions;
    private ScheduledExecutorService executor;

    LimitSessionManager(int maxSessions) {
        this.maxSessions = maxSessions;
    }

    public void doStart() throws Exception {
        super.doStart();
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                scavenge();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
    
    protected AbstractSessionManager.Session newSession(HttpServletRequest request) {
        return new Session(request);
    }

    void scavenge() {
        while (true) {
            Session sessionToRemove = null;
            synchronized (this) {
                if (_sessions.size() <= maxSessions) {
                    break;
                }
                long minAccessed = Long.MAX_VALUE;
                for (Iterator it = _sessions.values().iterator(); it.hasNext(); ) {
                    Session session = (Session)it.next();
                    long accessed = session.accessed();
                    if (accessed < minAccessed) {
                        minAccessed = accessed;
                        sessionToRemove = session;
                    }
                }
            }
            sessionToRemove.timeout();
        }
    }
    
    public void doStop() throws Exception {
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        super.doStop();
    }
}
