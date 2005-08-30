/**
 * SessionTestSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */
package test.wsdl.session;

/**
 * Class SessionTestSoapBindingImpl
 */
public class SessionTestSoapBindingImpl
        implements test.wsdl.session.SessionTestServer {

    /**
     * Method doSomething
     *
     * @return
     *
     * @throws java.rmi.RemoteException
     */
    public boolean doSomething() throws java.rmi.RemoteException {

        // if this is my session only then the data will be 0
        boolean succeeded = true;
        int count = countUp();

        if (count != 1) {
            System.out.println("Failed with count=" + count);
            succeeded = false;
        }
        try {
            // simulate some busy processing
            Thread.currentThread().sleep(999);
        } catch (InterruptedException e) {
            // ignore
        }

        // check exit count
        count = countDown();
        if (count != 0) {
            System.out.println("Failed with count=" + count);
            succeeded = false;
        }
        return succeeded;
    }

    /**
     * Count one caller
     *
     * @return Number of simultaneous session callers.
     */
    private synchronized int countUp() {
        sessionCallers++;
        return sessionCallers;
    }

    /**
     * Count down one caller
     *
     * @return Number of callers left.
     */
    private synchronized int countDown() {
        sessionCallers--;
        return sessionCallers;
    }

    /** count simultaneous session callers           */
    private int sessionCallers = 0;
}
