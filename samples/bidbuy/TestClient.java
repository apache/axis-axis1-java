/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package samples.bidbuy ;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.utils.Options;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.net.URL;
import java.util.Calendar;


/**
 * Test Client for the echo interop service.  See the main entrypoint
 * for more details on usage.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class TestClient {

    private static Service service         = null ;
    private static Call    call            = null;

    /**
     * Test an echo method.  Declares success if the response returns
     * true with an Object.equal comparison with the object to be sent.
     * @param method name of the method to invoke
     * @param toSend object of the correct type to be sent
     */
    private static void test(String method, Object toSend) {

    }

    /**
     * Main entry point.  Tests a variety of echo methods and reports
     * on their results.
     *
     * Arguments are of the form:
     *   -h localhost -p 8080 -s /soap/servlet/rpcrouter
     */
    public static void main(String args[]) throws Exception {
        // set up the call object
        Options opts = new Options(args);
        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress( new URL(opts.getURL()) );
        call.setUseSOAPAction(true);
        call.setSOAPActionURI("http://www.soapinterop.org/Bid");

        // register the PurchaseOrder class
        QName poqn = new QName("http://www.soapinterop.org/Bid",
                               "PurchaseOrder");
        Class cls = PurchaseOrder.class;
        call.registerTypeMapping(cls, poqn, BeanSerializerFactory.class, BeanDeserializerFactory.class);

        // register the Address class
        QName aqn = new QName("http://www.soapinterop.org/Bid", "Address");
        cls = Address.class;
        call.registerTypeMapping(cls, aqn, BeanSerializerFactory.class, BeanDeserializerFactory.class);

        // register the LineItem class
        QName liqn = new QName("http://www.soapinterop.org/Bid", "LineItem");
        cls = LineItem.class;
        call.registerTypeMapping(cls, liqn, BeanSerializerFactory.class, BeanDeserializerFactory.class);

        try {
            // Default return type based on what we expect
            call.setOperationName( new QName("http://www.soapinterop.org/Bid", "Buy" ));
            call.addParameter( "PO", poqn, ParameterMode.IN );
            call.setReturnType( XMLType.XSD_STRING );

            LineItem[] li = new LineItem[2];
            li[0] = new LineItem("Tricorder", 1, "2500.95");
            li[1] = new LineItem("Phasor", 3, "7250.95");

            PurchaseOrder po = new PurchaseOrder(
              "NCC-1701",
              Calendar.getInstance(),
              new Address("Sam Ruby", "Home", "Raleigh", "NC", "27676"),
              new Address("Lou Gerstner", "Work", "Armonk", "NY", "15222"),
              li
            );

            // issue the request
            String receipt = (String) call.invoke( new Object[] {po} );

            System.out.println(receipt);
        } catch (Exception e) {
           System.out.println("Buy failed: " + e);
            throw e;
        }
    }

}
