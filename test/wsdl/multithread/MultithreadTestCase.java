package test.wsdl.multithread;

import java.net.ConnectException;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.axis.AxisFault;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import samples.addr.AddressBook;
import samples.addr.AddressBookServiceLocator;
import samples.addr.AddressBookSOAPBindingStub;
import samples.addr.Address;
import samples.addr.Phone;
import samples.addr.StateType;

/**
* This test calls the stub multiple times from multiple threads.  Before the
* stub was made threadsafe, there was a good chance this test would fail with an
* IllegalStateException or "javax.xml.rpc.ServiceException: Number of parameters
* passed in (2) doesn't match the number of IN/INOUT parameters (4) from the
* addParameter() calls" or something else just as cryptic.
*/

public class MultithreadTestCase extends TestCase {
    private static Log log =
            LogFactory.getLog(MultithreadTestCase.class.getName());

    private AddressBook binding;

    public MultithreadTestCase(String name) {
        super(name);
    }

    private String printAddress (Address ad) {
        String out;
        if (ad == null)
            out = "\t[ADDRESS NOT FOUND!]";
        else
            out ="\t" + ad.getStreetNum () + " " + ad.getStreetName () + "\n\t" + ad.getCity () + ", " + ad.getState () + " " + ad.getZip () + "\n\t" + printPhone (ad.getPhoneNumber ());
        return out;
    } // printAddress

    private String printPhone (Phone ph)
    {
        String out;
        if (ph == null)
            out = "[PHONE NUMBER NOT FOUND!]";
        else
            out ="Phone: (" + ph.getAreaCode () + ") " + ph.getExchange () + "-" + ph.getNumber ();
        return out;
    } // printPhone

    private AssertionFailedError error = null;

    private synchronized void setError(AssertionFailedError error) {
        if (this.error == null) {
            this.error = error;
        }
    } // setError

    private static int var = 0;

    public class Run implements Runnable {
        public void run() {
            try {
                for (int i = 0; i < 4; ++i) {
                    Address address = new Address();
                    Phone phone = new Phone();
                    address.setStreetNum(var++);
                    address.setStreetName("2");
                    address.setCity("3");
                    address.setState(StateType.TX);
                    address.setZip(var++);
                    phone.setAreaCode(11);
                    phone.setExchange("22");
                    phone.setNumber("33");
                    address.setPhoneNumber(phone);
                    
                    binding.addEntry("hi", address); 
                    Address addressRet = binding.getAddressFromName("hi");
                }
            } catch (Throwable t) {
                // There are bound to be connection refused exceptions when the
                // server socket is busy) in a multithreaded environment.  I
                // don't want to deal with those.  Only grab exceptions that are
                // likely to have something to do with bad AXIS runtime.
                if (!(t instanceof AxisFault &&
                        ((AxisFault) t).detail instanceof ConnectException)) {

                    // Log a stack trace as we may not be so lucky next time!
                    log.fatal("Throwable caught: ", t);

                    setError(new AssertionFailedError("Throwable caught: " + t));
                }
            }
        } // run
    } // class Run

    public void testMultithreading() {
        try {
            binding = new AddressBookServiceLocator().getAddressBook();
        }
        catch (ServiceException jre) {
            throw new AssertionFailedError("ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);
        ((AddressBookSOAPBindingStub) binding).setMaintainSession(true);
        Thread[] threads = new Thread[100];
        for (int i = 0; i < 100; ++i) {
            threads[i] = new Thread(new Run());
            threads[i].start();
        }
        for (int i = 0; i < 100; ++i) {
            try {
                threads[i].join();
            }
            catch (InterruptedException ie) {
            }
        }
        if (error != null) {
            throw error;
        }
    } // testMultithreading
} // class MultithreadTestCase

