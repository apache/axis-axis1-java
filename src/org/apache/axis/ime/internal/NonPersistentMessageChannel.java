package org.apache.axis.ime.internal;

import java.util.Vector;
import org.apache.axis.ime.MessageChannel;
import org.apache.axis.ime.MessageExchangeContext;

/**
 * Creates a non-persistent message channel.  Queued messages
 * are stored in memory. If the Channel instance is destroyed,
 * so is the Queue.
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class NonPersistentMessageChannel
  implements MessageChannel {

  private final KeyedQueue messages = new KeyedQueue();

  private MessageWorkerGroup WORKERS;
  
  public NonPersistentMessageChannel(
    MessageWorkerGroup workers) {
      this.WORKERS = workers;
  }

  public MessageExchangeContext peek() {
    KeyedNode node = null;
    synchronized(messages) {
      node = messages.peek();
    }
    if (node != null) {
      return (MessageExchangeContext)node.value;
    } else {
      return null;
    }
  }

  public void put(
    Object key, 
    MessageExchangeContext context) {

    if (key == null ||
        context == null)
          throw new IllegalArgumentException();

    synchronized(messages) {
      messages.put(new KeyedNode(key,context));
      messages.notify();
    }
  }

  public MessageExchangeContext cancel(Object key) {
    if (key == null)
      throw new IllegalArgumentException();
    MessageExchangeContext context = null;
    synchronized(messages) {
      KeyedNode node = messages.select(key); // will attempt to find an remove
      if (node != null) 
        context = (MessageExchangeContext)node.value;
      node.key = null;
      node.value = null;
    }
    return context;
  }

  public MessageExchangeContext[] selectAll() {
    Vector v = new Vector();
    KeyedNode node = null;
    synchronized(messages) {
      while ((node = messages.select()) != null) {
        v.add(node.value);
        node.key = null;
        node.value = null;
      }
    }
    MessageExchangeContext[] contexts = new 
      MessageExchangeContext[v.size()];
    v.copyInto(contexts);
    return contexts;
  }

  public MessageExchangeContext select()
    throws InterruptedException {
    for (;;) {
      if (WORKERS.isShuttingDown())
        throw new IllegalStateException();
      KeyedNode node = null;
      synchronized(messages) {
        node = messages.select();
      }
      if (node != null) {
        MessageExchangeContext context = (MessageExchangeContext)node.value;
        node.key = null;
        node.value = null;
        return context;
      } else {
        messages.wait();
      }
    }
  }
  
  public MessageExchangeContext select(long timeout)
    throws InterruptedException {
    for (;;) {
      if (WORKERS.isShuttingDown())
        throw new IllegalStateException();
      KeyedNode node = null;
      synchronized(messages) {
        node = messages.select();
      }
      if (node != null) {
        MessageExchangeContext context = (MessageExchangeContext)node.value;
        node.key = null;
        node.value = null;
        return context;
      } else {
        messages.wait(timeout);
      }
    }
  }
  
  public MessageExchangeContext select(Object key)
    throws InterruptedException {
    for (;;) {
      if (WORKERS.isShuttingDown())
        throw new IllegalStateException();
      KeyedNode node = null;
      synchronized(messages) {
        node = messages.select(key);
      }
      if (node != null) {
        MessageExchangeContext context = (MessageExchangeContext)node.value;
        node.key = null;
        node.value = null;
        return context;
      } else {
        messages.wait();
      }
    }
  }

  public MessageExchangeContext select(Object key, long timeout)
    throws InterruptedException {
    for (;;) {
      if (WORKERS.isShuttingDown())
        throw new IllegalStateException();
      KeyedNode node = null;
      synchronized(messages) {
        node = messages.select(key);
      }
      if (node != null) {
        MessageExchangeContext context = (MessageExchangeContext)node.value;
        node.key = null;
        node.value = null;
        return context;
      } else {
        messages.wait(timeout);
      }
    }
  }

/// Support Classes ///
  protected static class KeyedNode {
    public Object key;
    public Object value;
    public KeyedNode next;
    public KeyedNode() {}
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
        this(key,value);
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
      for (KeyedNode node = head;node != null;node = node.next) {
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
    
  }
    
}
