package org.apache.axis.resolver.sd.schema;

import java.util.HashMap;
import org.apache.axis.utils.QName;
import org.apache.axis.Handler;
import org.apache.axis.SimpleTargetedChain;

/**
 * @author James Snell (jasnell@us.ibm.com)
 */

public class Service extends SDElement {

    private HandlerList request;
    private Provider provider;
    private HandlerList response;
    private HashMap faults;
    private TypeMappings typeMappings;
    
    public HandlerList getRequest() {
        return this.request;
    }
    
    public void setRequest(HandlerList request) {
        this.request = request;
    }
    
    public Provider getProvider() {
        return this.provider;
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    
    public HandlerList getResponse() {
        return this.response;
    }
    
    public void setResponse(HandlerList response) {
        this.response = response;
    }
    
    public Fault getFault(QName faultCode) {
        if (faults == null) return null;
        return (Fault)faults.get(faultCode);
    }
    
    public void setFault(QName faultCode, Fault fault) {
        if (faults == null) faults = new HashMap();
        faults.put(faultCode, fault);
    }
    
    public HashMap getFaults() {
        return this.faults;
    }
    
    public Handler newInstance() {
        SimpleTargetedChain service = new SimpleTargetedChain();
        if (request != null) service.setRequestHandler(request.newInstance());
        if (provider != null) service.setPivotHandler(provider.newInstance());
        if (response != null) service.setResponseHandler(response.newInstance());
        return service;
    }
}
