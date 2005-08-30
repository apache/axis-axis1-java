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

package samples.userguide.example1;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import javax.xml.namespace.QName;

public class TestClient
{
   public static void main(String [] args) {
       try {
           String endpoint = 
                    "http://nagoya.apache.org:5049/axis/services/echo";
     
           Service  service = new Service();
           Call     call    = (Call) service.createCall();

           call.setTargetEndpointAddress( new java.net.URL(endpoint) );
           call.setOperationName(new QName("http://soapinterop.org/", "echoString") );

           // Call to addParameter/setReturnType as described in user-guide.html
           //call.addParameter("testParam",
           //                  org.apache.axis.Constants.XSD_STRING,
           //                  javax.xml.rpc.ParameterMode.IN);
           //call.setReturnType(org.apache.axis.Constants.XSD_STRING);

           String ret = (String) call.invoke( new Object[] { "Hello!" } );

           System.out.println("Sent 'Hello!', got '" + ret + "'");
       } catch (Exception e) {
           System.err.println(e.toString());
       }
   }
}
