package org.apache.axis.resolver.sd.schema;

import org.apache.axis.Handler;
import javax.xml.rpc.namespace.QName;

/**
 * @author James Snell (jasnell@us.ibm.com)
 */

public class Fault extends HandlerList {

    private QName faultCode;
    
    public QName getFaultCode() {
        return this.faultCode;
    }
    
    public void setFaultCode(QName faultCode) {
        this.faultCode = faultCode;
    }
    
}
