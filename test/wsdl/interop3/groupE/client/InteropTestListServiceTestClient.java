/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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

package test.wsdl.interop3.groupE.client;

import org.apache.axis.AxisFault;
import org.apache.axis.encoding.Hex;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Options;
import org.apache.axis.client.Call;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import javax.xml.rpc.Service;
import javax.xml.namespace.QName;
import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.net.URL;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Test Client for part of interop group 3E.  See the main entrypoint
 * for more details on usage.
 *
 * @author Glyn Normington <glyn@apache.org>
 */
public abstract class InteropTestListServiceTestClient {
    public static URL url;

    private static final String NS =
        "http://soapinterop.org/WSDLInteropTestList";
    private static final QName OPQN = new QName(NS, "echoLinkedList");
    private static final String LISTNS = "http://soapinterop.org/xsd";
    private static final QName LISTQN = new QName(LISTNS, "List");

    private boolean addMethodToAction = false;
    private String soapAction = "http://soapinterop.org/";
    private org.apache.axis.client.Call call = null;
    private Definition wsdl = null;
    private QName serviceQN = null;

    /**
     * Determine if two objects are equal.  Handles nulls and recursively
     * verifies arrays are equal.  Accepts dates within a tolerance of
     * 999 milliseconds.
     */
    protected boolean equals(Object obj1, Object obj2) {
       if (obj1 == null || obj2 == null) return (obj1 == obj2);
       if (obj1.equals(obj2)) return true;

       // For comparison purposes, get the array of bytes representing
       // the Hex object.
       if (obj1 instanceof Hex) {
           obj1 = ((Hex) obj1).getBytes();
       }
       if (obj2 instanceof Hex) {
           obj2 = ((Hex) obj2).getBytes();
       }

       if (obj1 instanceof Date && obj2 instanceof Date)
           if (Math.abs(((Date)obj1).getTime()-((Date)obj2).getTime())<1000)
               return true;

       if ((obj1 instanceof Map) && (obj2 instanceof Map)) {
           Map map1 = (Map)obj1;
           Map map2 = (Map)obj2;
           Set keys1 = map1.keySet();
           Set keys2 = map2.keySet();
           if (!(keys1.equals(keys2))) return false;

           // Check map1 is a subset of map2.
           Iterator i = keys1.iterator();
           while (i.hasNext()) {
               Object key = i.next();
               if (!equals(map1.get(key), map2.get(key)))
                   return false;
           }

           // Check map2 is a subset of map1.
           Iterator j = keys2.iterator();
           while (j.hasNext()) {
               Object key = j.next();
               if (!equals(map1.get(key), map2.get(key)))
                   return false;
           }
           return true;
       }
       
       if ((obj1 instanceof List) && (obj2 instanceof List)) {
           List l1 = (List)obj1;
           List l2 = (List)obj2;

           if (l1.getVarInt() != l2.getVarInt()) return false;
           
           if (l1.getVarString() == null) {
               if (l2.getVarString() != null) return false;
           } else {
               if (!l1.getVarString().equals(l2.getVarString())) return false;
           }

           if (l1.getChild() == null) {
               if (l2.getChild() != null) return false;
           } else {
               if (!equals(l1.getChild(), l2.getChild())) return false;
           }
       } else {
           return false; // type mismatch or unsupported type
       }
       return true;
    }

    /**
     * Set up the call object.
     */
    public void setURL(String url) throws AxisFault {
        try {
            Document doc = XMLUtils.newDocument(new URL(url).toString());
            WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            wsdl = reader.readWSDL(null, doc);

            Service service = new org.apache.axis.client.
                Service(new URL(url), getServiceQName());

            call = (org.apache.axis.client.Call)service.
                createCall(getPortQName(wsdl), OPQN);
            
            call.registerTypeMapping(List.class,
                                     LISTQN,
                                     BeanSerializerFactory.class,
                                     BeanDeserializerFactory.class,
                                     false);
            call.setReturnType(LISTQN);
        } catch (Exception exp) {
            throw AxisFault.makeFault(exp);
        }
    }

    /**
     * Introspect the WSDL to obtain a service name.
     * The WSDL must define one service.
     */
    private QName getServiceQName() {
        serviceQN = (QName)wsdl.getServices().
            keySet().iterator().next();
        return new QName(serviceQN.getNamespaceURI(),
                         serviceQN.getLocalPart());
    }

    /**
     * Introspect the specified WSDL to obtain a port name.
     * The WSDL must define one port name.
     */
    private QName getPortQName(Definition wsdl) {
        if (serviceQN == null) {
            getServiceQName();
        }
        String port = (String)wsdl.getService(serviceQN).getPorts().keySet().
            iterator().next();
        return new QName(serviceQN.getNamespaceURI(), port);
    }

    /**
     * Execute the test
     */
    public void execute() throws Exception {

        {
            Object input = null;
            try {
                List node1 = new List();
                node1.setVarInt(1);
                node1.setVarString("last");
                List node2 = new List();
                node2.setVarInt(2);
                node2.setVarString("middle");
                node2.setChild(node1);
                List list = new List();
                list.setVarInt(3);
                list.setVarString("first");
                list.setChild(node2);
                input = list;

                Object output = call.invoke(new Object[] {input});
                verify("echoLinkedList", input, output);
            } catch (Exception e) {
                verify("echoLinkedList", input, e);
            }
        }
    }

    /**
     * Verify that the object sent was, indeed, the one you got back.
     * Subclasses are sent to override this with their own output.
     */
    protected abstract void verify(String method, Object sent, Object gotBack);

    /**
     * Main entry point.  Tests a variety of echo methods and reports
     * on their results.
     *
     * Arguments are of the form:
     *   -h localhost -p 8080 -s /soap/servlet/rpcrouter 
     * -h indicates the host
     * -p indicates the port
     * -s indicates the service part of the URI
     *
     * Alternatively a URI may be passed thus:
     *   -l completeuri
     */
    public static void main(String args[]) throws Exception {
        Options opts = new Options(args);
        
        boolean testPerformance = opts.isFlagSet('k') > 0;
        
        // set up tests so that the results are sent to System.out
        InteropTestListServiceTestClient client;
        
        if (testPerformance) {
            client = 
                new InteropTestListServiceTestClient() {
                        public void verify(String method, 
                                           Object sent, 
                                           Object gotBack) {
                        }
                };
        } else {
            client =
                new InteropTestListServiceTestClient() {
                        public void verify(String method,
                                           Object sent,
                                           Object gotBack) {
                            String message;
                            if (this.equals(sent, gotBack)) {
                                message = "OK";
                            } else {
                                if (gotBack instanceof Exception) {
                                    if (gotBack instanceof AxisFault) {
                                        message = "Fault: " +
                                            ((AxisFault)gotBack).
                                            getFaultString();
                                    } else {
                                        StringWriter sw = new StringWriter();
                                        PrintWriter pw = new PrintWriter(sw);
                                        message = "Exception: ";
                                        ((Exception)gotBack).
                                            printStackTrace(pw);
                                        message += sw.getBuffer().toString();
                                    }
                                } else {
                                    message = "Fail:" + gotBack +
                                        " expected " + sent;
                                }
                            }
                            // Line up the output
                            String tab = "";
                            int l = method.length();
                            while (l < 25) {
                                tab += " ";
                                l++;
                            }
                            System.out.println(method + tab + " " + message);
                        }
                    };
        }

        // set up the call object
        client.setURL(opts.getURL());

        if (testPerformance) {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                    client.execute();
            }
            long stopTime = System.currentTimeMillis();
            System.out.println("That took " + 
                               (stopTime - startTime) + 
                               " milliseconds");
        } else {
            client.execute();
        }
    }
}
