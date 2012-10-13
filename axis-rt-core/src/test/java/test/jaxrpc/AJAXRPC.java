package test.jaxrpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerInfo;

import junit.framework.TestCase;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.HandlerInfoChainFactory;


/**
 *
 *
 * @author Guillaume Sauthier
 */
public abstract class AJAXRPC extends TestCase {

    HandlerInfo handlerInfo0 = null;
    HandlerInfo handlerInfo1 = null;
    HandlerInfo handlerInfo2 = null;
    Map handler0Config = null;
    protected Map handler1Config = null;
    protected Map handler2Config = null;

    /**
     * Sets up the handlerInfo and handlerConfigs for all tests.
     * Each test has 3 JAX-RPC Handlers of the same type to keep things
     * simple.
     * 
     * @throws Exception 
     */
    protected void setUp() throws Exception {
        handlerInfo0 = new HandlerInfo();
        handlerInfo0.setHandlerClass(AAAHandler.class);
        handlerInfo1 = new HandlerInfo();
        handlerInfo1.setHandlerClass(AAAHandler.class);
        handlerInfo2 = new HandlerInfo();
        handlerInfo2.setHandlerClass(AAAHandler.class);
        handler0Config = new HashMap();
        handler1Config = new HashMap();
        handler2Config = new HashMap();
        handlerInfo0.setHandlerConfig(handler0Config);
        handlerInfo1.setHandlerConfig(handler1Config);
        handlerInfo2.setHandlerConfig(handler2Config);
    }

    /**
     * Convenience method to organize all test checking for a particular
     * handler.
     * <p/>
     * Checks to see if the expected number of calls to handleRequest, handleResponse
     * and handleFault reconciles with actuals
     * 
     * @param message           
     * @param handler           the target handler to reconcile
     * @param numHandleRequest  # of expected calls to handleRequest
     * @param numHandleResponse # of expected calls to handleResponse
     * @param numHandleFault    # of expected call to handleFault
     */
    protected void assertHandlerRuntime(String message, AAAHandler handler, int numHandleRequest, int numHandleResponse, int numHandleFault) {
        assertEquals(message + ": handleRequest", numHandleRequest,
                handler.getHandleRequestInvocations());
        assertEquals(message + ": handleResponse", numHandleResponse,
                handler.getHandleResponseInvocations());
        assertEquals(message + ": handleFault", numHandleFault,
                handler.getHandleFaultInvocations());
    }

    /**
     * Convenience method to create a HandlerInfoChainFactory
     * 
     * @return 
     */
    protected TestHandlerInfoChainFactory buildInfoChainFactory() {
        List handlerInfos = new ArrayList();
        handlerInfos.add(handlerInfo0);
        handlerInfos.add(handlerInfo1);
        handlerInfos.add(handlerInfo2);
        TestHandlerInfoChainFactory factory = new TestHandlerInfoChainFactory(
                handlerInfos);
        return factory;
    }
    /**
     * Mock Service Handler used to simulate a service object throwing
     * an AxisFault.
     */
    protected class MockServiceHandler extends BasicHandler {
        public void invoke(MessageContext msgContext) throws AxisFault {
            throw new AxisFault();
        }
    }

    /**
     * Helper class for keeping references to the JAX-RPC Handlers
     * that are created by the Factory.
     * <p/>
     * This class allows us to access the individual handlers after
     * the test case has been executed in order to make sure that
     * the expected methods of the handler instance have been invoked.
     */
    protected class TestHandlerInfoChainFactory extends HandlerInfoChainFactory {
        AAAHandler[] handlers;

        public TestHandlerInfoChainFactory(List handlerInfos) {
            super(handlerInfos);
        }

        public HandlerChain createHandlerChain() {
            HandlerChain chain = super.createHandlerChain();
            handlers = new AAAHandler[chain.size()];
            for (int i = 0; i < chain.size(); i++) {
                handlers[i] = (AAAHandler) chain.get(i);
            }
            return chain;
        }

        public AAAHandler[] getHandlers() {
            return handlers;
        }

    }

}
