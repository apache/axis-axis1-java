package test.wsdl.extension2;

public class ArrayExtensionTestBindingImpl implements test.wsdl.extension2.ArrayExtensionTestPortType{
    public test.wsdl.extension2.Data[] echoData(test.wsdl.extension2.ManagedObject _this, test.wsdl.extension2.Data[] data) throws java.rmi.RemoteException {

        if (data == null) System.out.println("data was null");
        else {

            System.out.println("data.length: " + data.length);
            for (int i = 0;i < data.length;i++) {

                if (data[i] == null) System.out.println("data[" + i + "]: null");
                else if (data[i] instanceof MoreData) System.out.println("data[" + i + "].name: " + ((MoreData)data[i]).getName() + ", size: " + ((MoreData)data[i]).getSize());
                else System.out.println("data[" + i + "].name: " + data[i].getName());
            }
        }
        return data;
    }

}
