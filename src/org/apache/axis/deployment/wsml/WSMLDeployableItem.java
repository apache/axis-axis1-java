package org.apache.axis.deployment.wsml;

import org.apache.axis.Handler;
import org.apache.axis.deployment.DeployableItem;
import javax.xml.rpc.namespace.QName;

public class WSMLDeployableItem implements DeployableItem { 
        
    public QName getQName() {
        return null;
    }

    public Handler newInstance(org.apache.axis.deployment.DeploymentRegistry registry) {
        return null;
    }
}
