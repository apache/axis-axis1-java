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

package org.apache.axis.ime.internal.util;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.threadpool.ThreadPool;
import org.apache.axis.i18n.Messages;
import org.apache.commons.logging.Log;

import java.util.Iterator;
import java.util.Vector;

/**
 * Creates a non-persistent KeyedBuffer.  Queued messages
 * are stored in memory. If the buffer instance is destroyed,
 * so is the Queue.
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class NonPersistentKeyedBuffer
        implements KeyedBuffer {

    protected static Log log =
        LogFactory.getLog(NonPersistentKeyedBuffer.class.getName());

    private final KeyedQueue messages = new KeyedQueue();

    private ThreadPool WORKERS;

    public NonPersistentKeyedBuffer(
            ThreadPool workers) {
        this.WORKERS = workers;
    }

    public Object peek() {
        KeyedNode node = null;
        synchronized (messages) {
            node = messages.peek();
        }
        if (node != null) {
            return node.value;
        } else {
            return null;
        }
    }


    public Object[] peekAll() {
        if (log.isDebugEnabled()) {
            log.debug("Enter: KeyedBuffer::selectAll");
        }
        Vector v = new Vector();
        KeyedNode node = null;
        synchronized (messages) {
            for (Iterator i = messages.iterator(); i.hasNext();) {
              v.add(i.next());
            }
        }
        Object[] objects = new
                Object[v.size()];
        v.copyInto(objects);
        if (log.isDebugEnabled()) {
            log.debug("Exit: KeyedBuffer::selectAll");
        }
        return objects;
    }


    public void put(
            Object key,
            Object object) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: KeyedBuffer::put");
        }
        if (key == null ||
                object == null)
            throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));

        synchronized (messages) {
            messages.put(new KeyedNode(key, object));
            messages.notify();
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: KeyedBuffer::put");
        }
    }

    public Object cancel(Object key) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: KeyedBuffer::cancel");
        }
        if (key == null)
            throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
        Object object = null;
        synchronized (messages) {
            KeyedNode node = messages.select(key); // will attempt to find and remove
            if (node != null)
                object = node.value;
            node.key = null;
            node.value = null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: KeyedBuffer::cancel");
        }
        return object;
    }

    public Object[] selectAll() {
        if (log.isDebugEnabled()) {
            log.debug("Enter: KeyedBuffer::selectAll");
        }
        Vector v = new Vector();
        KeyedNode node = null;
        synchronized (messages) {
            while ((node = messages.select()) != null) {
                v.add(node.value);
                node.key = null;
                node.value = null;
            }
        }
        Object[] objects = new
                Object[v.size()];
        v.copyInto(objects);
        if (log.isDebugEnabled()) {
            log.debug("Exit: KeyedBuffer::selectAll");
        }
        return objects;
    }

    public Object select()
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: KeyedBuffer::select");
        }
        for (; ;) {
            if (WORKERS.isShuttingDown())
                throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
            KeyedNode node = null;
            synchronized (messages) {
                node = messages.select();
            }
            if (node != null) {
                Object object = node.value;
                node.key = null;
                node.value = null;
                if (log.isDebugEnabled()) {
                    log.debug("Exit: KeyedBuffer::select");
                }
                return object;
            } else {
                messages.wait();
            }
        }
    }

    public Object select(long timeout)
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: KeyedBuffer::select");
        }
        for (; ;) {
            if (WORKERS.isShuttingDown())
                throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
            KeyedNode node = null;
            synchronized (messages) {
                node = messages.select();
            }
            if (node != null) {
                Object object = node.value;
                node.key = null;
                node.value = null;
                if (log.isDebugEnabled()) {
                    log.debug("Exit: KeyedBuffer::select");
                }
                return object;
            } else {
                messages.wait(timeout);
            }
        }
    }

    public Object select(Object key)
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: KeyedBuffer::select");
        }
        for (; ;) {
            if (WORKERS.isShuttingDown())
                throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
            KeyedNode node = null;
            synchronized (messages) {
                node = messages.select(key);
            }
            if (node != null) {
                Object object = node.value;
                node.key = null;
                node.value = null;
                if (log.isDebugEnabled()) {
                    log.debug("Exit: KeyedBuffer::select");
                }
                return object;
            } else {
                messages.wait();
            }
        }
    }

    public Object select(Object key, long timeout)
            throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: KeyedBuffer::select");
        }
        for (; ;) {
            if (WORKERS.isShuttingDown())
                throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
            KeyedNode node = null;
            synchronized (messages) {
                node = messages.select(key);
            }
            if (node != null) {
                Object object = node.value;
                node.key = null;
                node.value = null;
                if (log.isDebugEnabled()) {
                    log.debug("Exit: KeyedBuffer::select");
                }
                return object;
            } else {
                messages.wait(timeout);
            }
        }
    }

    public Object get() {
        if (log.isDebugEnabled()) {
            log.debug("Enter: KeyedBuffer::get");
        }
        KeyedNode node = null;
        Object object = null;
        synchronized (messages) {
            node = messages.select();
        }
        if (node != null) {
            object = node.value;
            node.key = null;
            node.value = null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: KeyedBuffer::get");
        }
        return object;
    }

    public Object get(Object key) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: KeyedBuffer::get");
        }
        KeyedNode node = null;
        Object object = null;
        synchronized (messages) {
            node = messages.select(key);
        }
        if (node != null) {
            object = node.value;
            node.key = null;
            node.value = null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: KeyedBuffer::get");
        }
        return object;
    }
    
/// Support Classes ///
    protected static class KeyedNode {
        public Object key;
        public Object value;
        public KeyedNode next;

        public KeyedNode() {
        }

        public KeyedNode(
                Object key,
                Object value) {
            this.key = key;
            this.value = value;
        }

        public KeyedNode(
                Object key,
                Object value,
                KeyedNode next) {
            this(key, value);
            this.next = next;
        }
    }

    protected static class KeyedQueue {

        protected KeyedNode head;
        protected KeyedNode last;

        protected void put(KeyedNode node) {
            if (last == null) {
                last = head = node;
            } else {
                last = last.next = node;
            }
        }

        protected KeyedNode select() {
            KeyedNode node = head;
            if (node != null && (head = node.next) == null) {
                last = null;
            }
            if (node != null)
                node.next = null;
            return node;
        }

        protected KeyedNode select(Object key) {
            KeyedNode previous = null;
            for (KeyedNode node = head; node != null; node = node.next) {
                if (node.key.equals(key)) {
                    if (previous != null)
                        previous.next = node.next;
                    node.next = null;
                    return node;
                }
                previous = node;
            }
            return null;
        }

        protected KeyedNode peek() {
            KeyedNode node = head;
            return node;
        }

        protected Iterator iterator() {
          return new KeyedQueueIterator(head);
        }
    }

    protected static class KeyedQueueIterator
      implements Iterator {
        protected KeyedNode current;
        protected KeyedNode next;
        public KeyedQueueIterator(KeyedNode node) {
          this.next = node;
        }
        
        public boolean hasNext() {
          return (next != null);
        }

        public Object next() {
          KeyedNode node = null;
          if (next != null) {
            node = next.next;
          }
          current = next;
          next = node;
          return current;
        }

        public void remove() {}
    }
}
