package test.wsdl.multithread;

import junit.framework.TestCase;
import samples.addr.AddressBook;
import samples.addr.AddressBookSOAPBindingStub;
import samples.addr.AddressBookServiceLocator;
import test.HttpTestUtil;

public class MultithreadTestCase extends TestCase {
    /**
     * This test calls a single stub multiple times from multiple threads.  Before the
     * stub was made threadsafe, there was a good chance this test would fail with an
     * IllegalStateException or "javax.xml.rpc.ServiceException: Number of parameters
     * passed in (2) doesn't match the number of IN/INOUT parameters (4) from the
     * addParameter() calls" or something else just as cryptic.
     */
    public void testSingleStub() throws Throwable {
        AddressBookServiceLocator loc = new AddressBookServiceLocator();
        final AddressBook binding = loc.getAddressBook(HttpTestUtil.getTestEndpoint(loc.getAddressBookAddress()));
        ((AddressBookSOAPBindingStub) binding).setMaintainSession(true);
        testMultithreading(new StubSupplier() {
            public AddressBook getStub() throws Exception {
                return binding;
            }
        });
    }

    /**
     * Tests concurrent invocations of different stubs (one per thread) created from a single
     * locator. This tests the scenario described in <a
     * href="https://issues.apache.org/jira/browse/AXIS-2498">AXIS-2498</a>.
     */
    public void testSingleLocator() throws Throwable {
        final AddressBookServiceLocator loc = new AddressBookServiceLocator();
        testMultithreading(new StubSupplier() {
            public AddressBook getStub() throws Exception {
                AddressBook binding = loc.getAddressBook(HttpTestUtil.getTestEndpoint(loc.getAddressBookAddress()));
                ((AddressBookSOAPBindingStub) binding).setMaintainSession(true);
                return binding;
            }
        });
    }
    
    private void testMultithreading(StubSupplier stubSupplier) throws Throwable {
        Report report = new Report();
        int NUM_THREADS = 50;
        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; ++i) {
            threads[i] = new Thread(new Invoker(stubSupplier.getStub(), report));
            threads[i].start();
        }
        for (int i = 0; i < NUM_THREADS; ++i) {
            try {
                threads[i].join();
            }
            catch (InterruptedException ie) {
            }
        }
        Throwable error = report.getError();
        if (error != null) {
            throw error;
        }
        assertEquals("number of successes", NUM_THREADS * 4, report.getSuccessCount());
    } // testMultithreading
} // class MultithreadTestCase

