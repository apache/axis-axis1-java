package test.wsdl.extension2;

import java.net.URL;

import javax.xml.rpc.*;

import arrayExtensionTest.*;

public class ArrayExtensionTestClient {

    public static void main(String[] args) throws Exception {
        String url = args[0];

        ArrayExtensionTestPortType port = null;
		try
		{
    		ServiceFactory sf = ServiceFactory.newInstance();
    		ArrayExtensionTestService service = (ArrayExtensionTestService)sf.loadService(ArrayExtensionTestService.class);

            port = service.getArrayExtensionTestPort();

            ((Stub)port)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, url);
		}
		catch(Exception e)
		{
			System.out.println("Exception message : "+e.getMessage());
			e.printStackTrace();
		}


       ManagedObject mo = new ManagedObject("aHandle");
       mo.setType("DataContainer");

       Data data = new Data();
       data.setName("data");

       Data[] result = port.echoData(mo, new Data[] { data });
       printResult(result);

       MoreData moreData = new MoreData();
       moreData.setName("moreData");
       moreData.setSize(11);

       result = port.echoData(mo, new Data[] { moreData });
       printResult(result);

       result = port.echoData(mo, new MoreData[] { moreData });
       printResult(result);
    }

    public static void printResult(Data[] data) {
        if (data == null) System.out.println("data was null");
        else {

            System.out.println("data.length: " + data.length);
            for (int i = 0;i < data.length;i++) {

                if (data[i] == null) System.out.println("data[" + i + "]: null");
                else if (data[i] instanceof MoreData) System.out.println("data[" + i + "].name: " + ((MoreData)data[i]).getName() + ", size: " + ((MoreData)data[i]).getSize());
                else System.out.println("data[" + i + "].name: " + data[i].getName());
            }
        }
    }

}
