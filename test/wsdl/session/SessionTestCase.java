/**
 * SessionTestServerServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */
package test.wsdl.session;

/**
 * Class SessionTestCase
 */
public class SessionTestCase extends junit.framework.TestCase {

    /**
     * Constructor SessionTestCase
     * @param name
     */
    public SessionTestCase(java.lang.String name) {
        super(name);
    }

    /**
     * Method test1SessionTestDoSomething
     */
    public void test1SessionTestDoSomething() {
        // Threads array
        SessionTest[] clients = new SessionTest[numThreads];

        for (int i = 0; i < numThreads; i++) {
            clients[i] = new SessionTest();
        }
        for (int j = 0; j < numThreads; j++) {
            clients[j].start();
            try {
                Thread.currentThread().sleep(150);
            } catch (InterruptedException e) {
                System.out.println("Threads interrupted");
            }
        }
        try {
            synchronized (lock) {
                while (count != 0) {
                    lock.wait();
                }
            }
        } catch (InterruptedException ie) {
            System.out.println("Threads interrupted");
        }
        System.out.println("Succeeded " + succeeded + " times.");
        System.out.println("Failed " + failed + " times.");
        assertTrue("found session failures", (failed == 0));
    }

    /**
     * Class SessionTest
     */
    public class SessionTest extends Thread {
        /**
         * run the thread until done.
         */
        public void run() {
            try {
                // Create an instance of the Web service interface.
                SessionTestServerServiceLocator wsloc =
                        new SessionTestServerServiceLocator();
                SessionTestServer ws = wsloc.getSessionTest();

                // Maintain sessions for test calls.
                ((org.apache.axis.client.Stub) ws).setMaintainSession(true);
                for (int i = 0; i < NO_OF_CALLS; i++) {
                    if (ws.doSomething() == false) {
                        synchronized (testLock) {
                            failed++;
                        }
                    } else {
                        synchronized (testLock) {
                            succeeded++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // checkout
            synchronized (lock) {
                count--;
                lock.notifyAll();
            }
        }
    }

    /** Field lock           */
    private static Object lock = new Object();

    /** Field testLock           */
    private static Object testLock = new Object();

    /** Field NO_OF_THREADS           */
    private static final int NO_OF_THREADS = 3;

    /** Field NO_OF_CALLS           */
    private static final int NO_OF_CALLS = 6;

    /** Field numThreads           */
    private static int numThreads = NO_OF_THREADS;

    /** Field count           */
    private static int count = NO_OF_THREADS;

    /** Field failed           */
    private static int failed = 0;

    /** Field succeeded           */
    private static int succeeded = 0;

    /**
     * Main entry point for the application.
     * Takes number of threads as argument.
     *
     * @param args
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        numThreads = count = Integer.parseInt(args[0]);
        SessionTestCase testCase = new SessionTestCase("SessionTestCase");
        testCase.test1SessionTestDoSomething();
    }
}
