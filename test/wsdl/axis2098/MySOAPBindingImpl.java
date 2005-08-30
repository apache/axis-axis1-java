package test.wsdl.axis2098;

public class MySOAPBindingImpl implements test.wsdl.axis2098.MyPort {

    public test.wsdl.axis2098.MyResponseType helloWorld(test.wsdl.axis2098.MyRequestType body)
            throws java.rmi.RemoteException {

        MyResponseType resp = new MyResponseType();
        resp.setHelloworld(body.getHelloworld());
        return resp;
    }
}
