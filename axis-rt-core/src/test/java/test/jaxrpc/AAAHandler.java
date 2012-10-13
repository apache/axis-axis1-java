package test.jaxrpc;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import java.util.Map;

public class AAAHandler implements Handler {
    private int handleRequestInvocations = 0;
    private int handleResponseInvocations = 0;
    private int handleFaultInvocations = 0;

    public Object handleRequestReturnValue = null;
    public Object handleResponseReturnValue = null;
    public Object handleFaultReturnValue = null;

    public boolean handleRequest(MessageContext context) {
        handleRequestInvocations++;
        return returnAppropriateValue(handleRequestReturnValue);
    }

    public boolean handleResponse(MessageContext context) {
        handleResponseInvocations++;
        return returnAppropriateValue(handleResponseReturnValue);
    }

    public boolean handleFault(MessageContext context) {
        handleFaultInvocations++;
        return returnAppropriateValue(handleFaultReturnValue);
    }

    private boolean returnAppropriateValue(Object returnValue) {
        if (returnValue == null)
            return true;
        else if (returnValue instanceof Boolean)
            return ((Boolean) returnValue).booleanValue();
        else if (returnValue instanceof RuntimeException)
            throw (RuntimeException) returnValue;
        else {
            throw new RuntimeException();
        }
    }

    public void init(HandlerInfo config) {
        Map map = config.getHandlerConfig();
        handleRequestReturnValue = map.get("HANDLE_REQUEST_RETURN_VALUE");
        handleResponseReturnValue = map.get("HANDLE_RESPONSE_RETURN_VALUE");
        handleFaultReturnValue = map.get("HANDLE_FAULT_RETURN_VALUE");
    }

    public void destroy() {
    }

    public QName[] getHeaders() {
        return new QName[0];
    }

    public int getHandleRequestInvocations() {
        return handleRequestInvocations;
    }

    public int getHandleResponseInvocations() {
        return handleResponseInvocations;
    }

    public int getHandleFaultInvocations() {
        return handleFaultInvocations;
    }

    public Object getHandleRequestReturnValue() {
        return handleRequestReturnValue;
    }

    public void setHandleRequestReturnValue(Object handleRequestReturnValue) {
        this.handleRequestReturnValue = handleRequestReturnValue;
    }

    public Object getHandleResponseReturnValue() {
        return handleResponseReturnValue;
    }

    public void setHandleResponseReturnValue(Object handleResponseReturnValue) {
        this.handleResponseReturnValue = handleResponseReturnValue;
    }

    public Object getHandleFaultReturnValue() {
        return handleFaultReturnValue;
    }

    public void setHandleFaultReturnValue(Object handleFaultReturnValue) {
        this.handleFaultReturnValue = handleFaultReturnValue;
    }

}