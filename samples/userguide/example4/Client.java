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

package samples.userguide.example4;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.utils.Options;

import javax.xml.namespace.QName;

public class Client
{
    public static void main(String [] args)
    {
        try {
            Options options = new Options(args);
            
            String endpointURL = options.getURL();
            
            Service  service = new Service();
            Call     call    = (Call) service.createCall();

            call.setTargetEndpointAddress( new java.net.URL(endpointURL) );
            call.setOperationName( new QName("LogTestService", "testMethod") );

            String res = (String) call.invoke( new Object[] {} );

            System.out.println( res );
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
