package org.apache.axis.message;

import org.xml.sax.*;
import org.apache.axis.MessageContext;
import org.apache.axis.utils.XMLUtils;
import javax.xml.parsers.SAXParser;

/** This class is an adapter for the Axis SAX-event system
 * which uses a SAX parser to parse on its own thread, synchronizing
 * when appropriate with the control thread.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class ThreadedSAXAdapter extends SOAPSAXHandler
{
    private static final boolean DEBUG_LOG = false;
    
    private Object _semaphore = new Object();
                                           
    private SAXParser _parser;
    InputSource inputSource;

    private Thread parseThread = null;

    private boolean doneParsing = false;
    
    /** The actual Runnable for the parse thread
     */   
    class ParseRunnable implements Runnable
    {
        public void run()
        {
            try {
                _parser.parse(inputSource, ThreadedSAXAdapter.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (DEBUG_LOG) {
                System.out.println("ThreadedSAXAdapter done.");
            }
            doneParsing = true;
            state = FINISHED;
            
            synchronized (_semaphore) {
                _semaphore.notify();
            }
        }
    }
    
    public ThreadedSAXAdapter(InputSource inputSource,
                              MessageContext msgContext)
    {
        super(msgContext);
        _parser = XMLUtils.getSAXParser();
        this.inputSource = inputSource;
    }
    
    /*******************************************************************
     * Threading stuff
     */
    public void startParse()
    {
        // This should be using some threading infrastructure...
        parseThread = new Thread(new ParseRunnable());
        parseThread.start();
    }
    
    public void parse()
    {
        if (getState() == FINISHED) {
            return;
        }
        
        if (parseThread == null) {
            startParse();
        }
        
        // Kick the parse thread
        continueParsing();
        
        // Wait until it's done (i.e. paused or finished)
        synchronized (_semaphore) {
            while (!doneParsing) {
                try {
                    //System.out.println("parseHeaders() waiting...");
                    _semaphore.wait();
                    //System.out.println("parseHeaders() waking...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
    
    /** Called by the control thread; let the parsing thread
     * continue.
     * 
     */
    protected synchronized void continueParsing()
    {
        if (DEBUG_LOG) {
            System.out.println("continueParsing()");
        }
        doneParsing = false;
        notify();
    }
    
    /** Called by the parse thread once it hits a desired
     * event (found the element the control thread is looking
     * for, finished parsing the headers, etc).  Suspend
     * execution until we're told to go again.
     */
    protected void pauseParsing() throws SAXException
    {
        if (parsingToEnd)
            return;
        
        if (DEBUG_LOG) {
            System.out.println("pauseParsing()");
        }
        
        synchronized (this) {
            doneParsing = true;
        }

        /** Anyone who's waiting on us will be hanging out on
         * the semaphore.
         */
        synchronized (_semaphore) {
            _semaphore.notify();
        }
        
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new SAXException("SOAPSAXHandler was interrupted!");
            }
        }
    }

}
