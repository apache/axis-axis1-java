package org.apache.axis.resolver.sd.schema;

import org.apache.axis.Handler;
import org.apache.axis.utils.QName;

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
