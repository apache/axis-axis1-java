package org.apache.axis.server;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.configuration.FileProvider;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;

/**
 * @author Glen Daniels (gdaniels@macromedia.com)
 */ 

public interface AxisServerFactory {    
    public AxisServer getServer(Map environment)
        throws AxisFault;
}
