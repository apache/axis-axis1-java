package test.wsdl.multithread;

import java.util.concurrent.CountDownLatch;

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
        final int numThreads = 50;
        final int numInvocations = 4;
        CountDownLatch readyLatch = new CountDownLatch(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; ++i) {
            threads[i] = new Thread(new Invoker(stubSupplier.getStub(), readyLatch, startLatch, report, numInvocations));
            threads[i].start();
        }
        readyLatch.await();
        startLatch.countDown();
        for (int i = 0; i < numThreads; ++i) {
            threads[i].join(30000);
            StackTraceElement[] stack = threads[i].getStackTrace();
            if (stack.length > 0) {
                Throwable t = new Throwable("Hanging thread detected");
                t.setStackTrace(stack);
                throw t;
            }
        }
        Throwable error = report.getError();
        if (error != null) {
            throw error;
        }
        assertEquals("number of successes", numThreads * numInvocations, report.getSuccessCount());
    } // testMultithreading
} // class MultithreadTestCase

