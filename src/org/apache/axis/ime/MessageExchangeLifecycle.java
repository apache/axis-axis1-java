package org.apache.axis.ime;

/**
 * Interface that may be provided by MessageExchange impl's
 * to allow users to control the lifecycle of the "stuff"
 * going on under the covers
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageExchangeLifecycle {

    public void init();
  
    public void shutdown();
  
    public void shutdown(boolean force);
  
    public void awaitShutdown()
        throws InterruptedException;
  
    public void awaitShutdown(long timeout)
        throws InterruptedException;

}
