package test.utils;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class ConsumerPipe implements Runnable
{
    PipedInputStream consumer = null;
    String result;
    private static int pipecount = 0; //thread counter for debuggers and stack traces.
    boolean done = false;
    
    public ConsumerPipe(PipedOutputStream out) throws IOException {
        consumer = new PipedInputStream(out);
        pipecount++;
    }
    
    public void run()
    {
        try
        {
            char[] chars = new char[consumer.available()];
            for (int i =0; i < chars.length; i++)
            {
                chars[i] = (char)consumer.read();
            }
            result = new String(chars);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally {
            try {
                consumer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            done = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    /**
     * Starts this object as a thread, which results in the run() method being
     * invoked and work being done.
     */
    public String getResult() {
        Thread reader = new Thread(this, "Piper-"+pipecount);
        reader.start();
        while (!done) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return result;
    }
}
