package test.wsdl.parameterOrder;

import test.wsdl.parameterOrder.ParameterOrderService;
import test.wsdl.parameterOrder.ParameterOrderTest;

/**
* This class is taken from the generated TestCase.  The generated test case is still generated
* to verify that the generated test case is always compilable.  THIS test case exists
* because I know this is correct.  If the generation of the bindings changes, it's likely that
* the generation of the TestCase will change as well, so we wouldn't know whether they changed
* for the worse if they all changed the same.  This test case should fail to compile if generated
* stuff changed for the worse.
*/

public class VerifyTestCase extends junit.framework.TestCase {
    public VerifyTestCase(String name) {
        super(name);
    }

    public void testParameterOrder() {
        test.wsdl.parameterOrder.ParameterOrderTest binding;
        try {
            binding = new ParameterOrderServiceLocator().getParameterOrder();
        } catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);
        try {
            binding.oneIn(0);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.oneInout(new javax.xml.rpc.holders.IntHolder(0));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.oneOut(new javax.xml.rpc.holders.IntHolder());
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.fiveInNoOrder(0, 0, 0, 0, 0);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.fiveInoutNoOrder(new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.IntHolder(0));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.fiveOutNoOrder(new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder());
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.fiveIn(0, 0, 0, 0, 0);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.fiveInout(new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.IntHolder(0));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.fiveOut(new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder());
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            int value = -3;
            value = binding.someInoutPartialOrder1(new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.IntHolder(0));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.someInoutPartialOrder2(new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder());
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            binding.fourOutPartialOrder(new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder());
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            int value = -3;
            value = binding.oneReturn(new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.IntHolder());
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
    }
}

