/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package samples.echo ;

import java.lang.reflect.Array;
import java.util.Hashtable;

import org.apache.axis.AxisFault ;
import org.apache.axis.client.ServiceClient ;
import org.apache.axis.client.http.HTTPTransport ;
import org.apache.axis.encoding.BeanSerializer;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.RPCParam;
import org.apache.axis.utils.Options ;
import org.apache.axis.utils.QName ;

/**
 * Test Client for the echo interop service.  See the main entrypoint
 * for more details on usage.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class TestClient {

    private static ServiceClient call;
    private static TypeMappingRegistry map = new SOAPTypeMappingRegistry();
    private static boolean addMethodToAction = false;
    private static String soapAction = "http://soapinterop.org/";

    /**
     *
     */
    private static boolean equals(Object obj1, Object obj2) {
       if (obj1 == null) return (obj2 == null);
       if (obj1.equals(obj2)) return true;
       if (!obj2.getClass().isArray()) return false;
       if (!obj1.getClass().isArray()) return false;
       if (Array.getLength(obj1) != Array.getLength(obj2)) return false;
       for (int i=0; i<Array.getLength(obj1); i++)
           if (!equals(Array.get(obj1,i),Array.get(obj2,i))) return false;
       return true;
    }

    /**
     * Test an echo method.  Declares success if the response returns
     * true with an Object.equal comparison with the object to be sent.
     * @param method name of the method to invoke
     * @param toSend object of the correct type to be sent
     */
    private static void test(String type, Object toSend) {

        String method = "echo" + type;
        String arg = "input" + type;
        String resultName = "output" + type;
        RPCParam paramToSend = new RPCParam(arg, toSend);
        
        try {
            // Default return type based on what we expect
            ServiceDescription sd = new ServiceDescription(method, true);
            sd.addOutputParam(resultName, map.getTypeQName(toSend.getClass()));
            sd.addOutputParam("Return", map.getTypeQName(toSend.getClass()));
            call.setServiceDescription(sd);
            
            String action = soapAction;
            if (addMethodToAction) {
                action += method;
            }
            call.set(HTTPTransport.ACTION, action);

            // issue the request
            Object gotBack = call.invoke(
                "http://soapinterop.org/", method, new Object[] {paramToSend} );

            // verify the result
            if (equals(toSend,gotBack)) {
                System.out.println(method + "\t OK");
            } else {
                System.out.println(method + "\t FAIL: " + gotBack);
            }
        } catch (Exception e) {
           System.out.println(method + "\t FAIL: " + e);
        }
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
        
        addMethodToAction = (opts.isFlagSet('m') > 0);
        
        String action = opts.isValueSet('a');
        if (action != null)
            soapAction = action;
        
        call = new ServiceClient(new HTTPTransport());
        call.set(HTTPTransport.URL, opts.getURL());
        call.set(HTTPTransport.ACTION, "http://soapinterop.org/");

        // register the SOAPStruct class
        QName ssqn = new QName("http://soapinterop.org/xsd", "SOAPStruct");
        Class cls = SOAPStruct.class;
        call.addSerializer(cls, ssqn, new BeanSerializer(cls));
        call.addDeserializerFactory(ssqn, cls, BeanSerializer.getFactory(cls));

        // execute the tests
        test("String", "abcdefg");
        test("StringArray", new String[] {"abc", "def"});
        test("Integer", new Integer(42));
        test("IntegerArray", new Integer[] {new Integer(42)});
        test("Float", new Float(3.7F));
        test("FloatArray", new Float[] {new Float(3.7F), new Float(7F)});
        test("Struct", new SOAPStruct(5, "Hello", 10.3F));
        test("StructArray", new SOAPStruct[] {
          new SOAPStruct(1, "one", 1.1F),
          new SOAPStruct(2, "two", 2.2F),
          new SOAPStruct(3, "three", 3.3F)});
    }

}
