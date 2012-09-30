package test.wsdl.qnameser;

import javax.xml.namespace.QName;

import java.rmi.RemoteException;

public class PlanService {
    
    public static final QName Q_1 = new QName("http://foo", "1");
    public static final QName Q_2 = new QName("http://tempuri.org/", "2");
    public static final QName Q_3 = new QName("", "3");

    public GetPlanResponse getPlan(QName parameters) 
        throws RemoteException {
        String localName = parameters.getLocalPart();
        String namespace = null;
        if (localName.equals(Q_1.getLocalPart())) {
            namespace = Q_1.getNamespaceURI();
        } else if (localName.equals(Q_2.getLocalPart())) {
            namespace = Q_2.getNamespaceURI();
        } else if (localName.equals(Q_3.getLocalPart())) {
            namespace = Q_3.getNamespaceURI();
        } else {
            throw new RemoteException("invalid localname:" + localName);
        }
        
        if (!parameters.getNamespaceURI().equals(namespace)) {
            throw new RemoteException("Expected: " + namespace + " but got: " +
                                      parameters.getNamespaceURI());
        }

        return new GetPlanResponse();
    }

    public GetMPlanResponse getMPlan(GetMPlan in)
        throws RemoteException {
        QName [] list = in.getList();
        for (int i=0;i<list.length;i++) {
            getPlan(list[i]);
        }
        return new GetMPlanResponse();
    }

}
