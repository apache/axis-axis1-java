package org.apache.axis.ime.internal;

import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import org.apache.axis.ime.MessageChannel;
import org.apache.axis.ime.MessageExchangeContextListener;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class MessageWorkerGroup {

  protected Map threads = new Hashtable();
  protected boolean interrupt;
  protected long threadcount;
  public boolean _shutdown;

  /**
   * Returns true if all workers have been shutdown
   */
  public boolean isShutdown() {
    synchronized(this) {
      return _shutdown && threadcount == 0;
    }
  }

  /**
   * Returns true if all workers are in the process of shutting down
   */  
  public boolean isShuttingDown() {
    synchronized(this) {
      return _shutdown;
    }
  }

  /**
   * Returns the total number of currently active workers
   */  
  public long getWorkerCount() {
    synchronized(this) {
      return threadcount;
    }
  }
  
  /**
   * Adds a new worker to the pool
   */
  public void addWorker(
    MessageChannel channel, 
    MessageExchangeContextListener listener) {
      if (_shutdown)
        throw new IllegalStateException();
      MessageWorker worker =
        new MessageWorker(this, channel, listener);
      Thread thread = new Thread(worker);
      threads.put(worker,thread);
      threadcount++;
      thread.start();
  }

  /**
   * Forcefully interrupt all workers
   */
  public void interruptAll() {
    synchronized(threads) {
      for (Iterator i = threads.values().iterator(); i.hasNext();) {
        Thread t = (Thread)i.next();
        t.interrupt();
      }
    }
  }
  
  /**
   * Forcefully shutdown the pool
   */
  public void shutdown() {
    synchronized(this) {
      _shutdown = true;
    }
    interruptAll();
  }

  /**
   * Forcefully shutdown the pool
   */
  public void safeShutdown() {
    synchronized(this) {
      _shutdown = true;
    }
  }
  
  /**
   * Await shutdown of the worker
   */  
  public synchronized void awaitShutdown()
    throws InterruptedException {
      if (!_shutdown)
        throw new IllegalStateException();
      while (threadcount > 0)
        wait();
  }
  
  /**
   * Await shutdown of the worker
   */
  public synchronized boolean awaitShutdown(long timeout)
    throws InterruptedException {
      if (!_shutdown)
        throw new IllegalStateException();
      if (threadcount == 0) 
        return true;
      long waittime = timeout;
      if (waittime <= 0) 
        return false;
      long start = System.currentTimeMillis();
      for (;;) {
        wait(waittime);
        if (threadcount == 0) 
          return true;
        waittime = timeout - System.currentTimeMillis();
        if (waittime <= 0)
          return false;
      }
  }
  
  /**
   * Used by MessageWorkers to notify the pool that it is done
   */
  protected synchronized void workerDone(
    MessageWorker worker) {
      threads.remove(worker);
      if (--threadcount == 0 && _shutdown) {
        notifyAll();
      }
      if (!_shutdown) {
        addWorker(
          worker.getMessageChannel(),
          worker.getMessageExchangeContextListener());
      }
  }
}

