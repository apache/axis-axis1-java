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

import org.apache.axis.AxisFault;
import org.apache.axis.encoding.Hex;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Options;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.StringWriter;
import java.io.PrintWriter;

import javax.xml.rpc.holders.StringHolder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.FloatHolder;


/**
 * Test Client for the echo interop service.  See the main entrypoint
 * for more details on usage.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public abstract class TestClient {

    private static boolean addMethodToAction = false;
    private static String soapAction = "http://soapinterop.org/";
    private static EchoServicePortType binding = null;

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
       
       if (obj1 instanceof List)
         obj1 = JavaUtils.convert(obj1, Object[].class);
       if (obj2 instanceof List)
         obj2 = JavaUtils.convert(obj2, Object[].class);

       if (!obj2.getClass().isArray()) return false;
       if (!obj1.getClass().isArray()) return false;
       if (Array.getLength(obj1) != Array.getLength(obj2)) return false;
       for (int i=0; i<Array.getLength(obj1); i++)
           if (!equals(Array.get(obj1,i),Array.get(obj2,i))) return false;
       return true;
    }

    /**
     * Set up the call object.
     */
    public void setURL(String url)
        throws AxisFault
    {
        try {
            binding = new EchoServiceAccessLocator().
                getEchoServicePortType(new java.net.URL(url)); 
            ((EchoServiceBindingStub) binding).soapAction = soapAction;
            ((EchoServiceBindingStub) binding).addMethodToAction = addMethodToAction;
        } catch (Exception exp) {
            throw AxisFault.makeFault(exp);
        }
    }

    /**
     * Execute all the 2A tests
     */
    public void executeAll() throws Exception {
        execute2A();
        execute2B();
    }

    /**
     * Execute the 2A tests
     */
    public void execute2A() throws Exception {
        // execute the tests
        Object output = null;

        {
            String input = "abccdefg";
            try {
                output = binding.echoString(input);
                verify("echoString", input, output);
            } catch (Exception e) {
                verify("echoString", input, e);
            }
        }
        
        {
            String[] input = new String[] {"abc", "def"};
            try {
                output = binding.echoStringArray(input);
                verify("echoStringArray", input, output);
            } catch (Exception e) {
                verify("echoStringArray", input, e);
            }
        }
        
        {
            Integer input = new Integer(42);
            try {
                output = new Integer( binding.echoInteger(input.intValue()));
                verify("echoInteger", input, output);
            } catch (Exception e) {
                verify("echoInteger", input, e);
            }
        }
        
        {
            int[] input = new int[] {42};
            try {
                output = binding.echoIntegerArray(input);
                verify("echoIntegerArray", input, output);
            } catch (Exception e) {
                verify("echoIntegerArray", input, e);
            }
        }
        
        {
            Float input = new Float(3.7F);
            try {
                output = new Float(binding.echoFloat(input.floatValue()));
                verify("echoFloat", input, output);
            } catch (Exception e) {
                verify("echoFloat", input, e);
            }
        }

        {
            float[] input = new float[] {3.7F, 7F};
            try {
                output = binding.echoFloatArray(input);
                verify("echoFloatArray", input, output);
            } catch (Exception e) {
                verify("echoFloatArray", input, e);
            }
        }

        {
            SOAPStruct input = new SOAPStruct(5, "Hello", 103F);
            try {
                output = binding.echoStruct(input);
                verify("echoStruct", input, output);
            } catch (Exception e) {
                verify("echoStruct", input, e);
            }
        }
        
        {
            SOAPStruct[] input = new SOAPStruct[] {
                new SOAPStruct(1, "one", 1.1F),
                new SOAPStruct(2, "two", 2.2F),
                new SOAPStruct(3, "three", 3.3F)};
            try {
                output = binding.echoStructArray(input);
                verify("echoStructArray", input, output);
            } catch (Exception e) {
                verify("echoStructArray", input, e);
            }
        }

        {
            try {
                binding.echoVoid();
                verify("echoVoid", null, null);
            } catch (Exception e) {
                verify("echoVoid", null, e);
            }
        }

        {
            byte[] input = "Base64".getBytes();
            try {
                output = binding.echoBase64(input);
                verify("echoBase64", input, output);
            } catch (Exception e) {
                verify("echoBase64", input, e);
            }
        }
        
        {
            Hex input = new Hex("3344");
            try {
                output = binding.echoHexBinary(input.getBytes());
                verify("echoHexBinary", input, output);
            } catch (Exception e) {
                verify("echoHexBinary", input, e);
            }
        }
        
        {
            Date input = new Date();
            try {
                output = binding.echoDate(input);
                verify("echoDate", input, output);
            } catch (Exception e) {
                verify("echoDate", input, e);
            }
        }
        
        {
            BigDecimal input = new BigDecimal("3.14159");
            try {
                output = binding.echoDecimal(input);
                verify("echoDecimal", input, output);
            } catch (Exception e) {
                verify("echoDecimal", input, e);
            }
        }
        
        {
            Boolean input = Boolean.TRUE;
            try {
                output = new Boolean( binding.echoBoolean(input.booleanValue()));
                verify("echoBoolean", input, output);
            } catch (Exception e) {
                verify("echoBoolean", input, e);
            }
        }
        
        HashMap map = new HashMap();
        map.put(new Integer(5), "String Value");
        map.put("String Key", new Date());
        {
            HashMap input = map;
            try {
                output = binding.echoMap(input);
                verify("echoMap", input, output);
            } catch (Exception e) {
                verify("echoMap", input, e);
            }
        }

        HashMap map2 = new HashMap();
        map2.put("this is the second map", new Boolean(true));
        map2.put("test", new Float(411));
        {
            HashMap[] input = new HashMap [] {map, map2 };
            try {
                output = binding.echoMapArray(input);
                verify("echoMapArray", input, output);
            } catch (Exception e) {
                verify("echoMapArray", input, e);
            }
        }
    }

    /**
     * Execute the 2B tests
     */
    public void execute2B() throws Exception {
        // execute the tests
        Object output = null;
        {
            SOAPStruct input = new SOAPStruct(5, "Hello", 103F);
            try {
                StringHolder outputString = new StringHolder();
                IntHolder outputInteger = new IntHolder();
                FloatHolder outputFloat = new FloatHolder();
                binding.echoStructAsSimpleTypes(input, outputString, outputInteger, outputFloat);
                output = new SOAPStruct(outputInteger.value,
                                        outputString.value,
                                        outputFloat.value);
                verify("echoStructAsSimpleTypes", 
                       input, output);
            } catch (Exception e) {
                verify("echoStructAsSimpleTypes", input, e);
            }
        }

        {
            SOAPStruct input = new SOAPStruct(5, "Hello", 103F);
            try {
                output = binding.echoSimpleTypesAsStruct(
                   input.getVarString(), input.getVarInt(), input.getVarFloat());
                verify("echoSimpleTypesAsStruct", 
                       input, 
                       output);
            } catch (Exception e) {
                verify("echoSimpleTypesAsStruct", input, e);
            }
        }

        {
            String[][] input = new String[2][2];
            input[0][0] = "00";
            input[0][1] = "01";
            input[1][0] = "10";
            input[1][1] = "11";
            try {
                output = binding.echo2DStringArray(input);
                verify("echo2DStringArray", 
                       input, 
                       output);
            } catch (Exception e) {
                verify("echo2DStringArray", input, e);
            }
        }

        {
            SOAPStructStruct input = new SOAPStructStruct("AXIS",
                                                          1,
                                                          3F,
                                                          new SOAPStruct(5, "Hello", 103F));
            try {
                output = binding.echoNestedStruct(input);
                verify("echoNestedStruct", input, output);
            } catch (Exception e) {
                verify("echoNestedStruct", input, e);
            }
        }
        {
            SOAPArrayStruct input = new SOAPArrayStruct("AXIS",
                                                        1,
                                                        3F,
                                                        new String[] {"one", "two", "three"});
            try {
                output = binding.echoNestedArray(input);
                verify("echoNestedArray", input, output);
            } catch (Exception e) {
                verify("echoNestedArray", input, e);
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
     * -h indicats the host
     */
    public static void main(String args[]) throws Exception {
        Options opts = new Options(args);

        boolean testPerformance = opts.isFlagSet('k') > 0;
        boolean allTests = opts.isFlagSet('A') > 0;

        // set up tests so that the results are sent to System.out
        TestClient client;

        if (testPerformance) {
            client = new TestClient() {
               public void verify(String method, Object sent, Object gotBack) {
               }
            };
        } else {
            client = new TestClient() {
            public void verify(String method, Object sent, Object gotBack) {
                String message;
                if (this.equals(sent, gotBack)) {
                    message = "OK";
                } else {
                    if (gotBack instanceof Exception) {
                        if (gotBack instanceof AxisFault) {
                            message = "Fault: " +
                                ((AxisFault)gotBack).getFaultString();
                        } else {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            message = "Exception: ";
                            ((Exception)gotBack).printStackTrace(pw);
                            message += sw.getBuffer().toString();
                        }
                    } else {
                        message = "Fail:" + gotBack + " expected " + sent;
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

        // support for tests with non-compliant applications
        client.addMethodToAction = (opts.isFlagSet('m') > 0);

        String action = opts.isValueSet('a');
        if (action != null) client.soapAction = action;

        if (testPerformance) {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                if (allTests) {
                    client.executeAll();
                } else {
                    client.execute2A();
                }
            }
            long stopTime = System.currentTimeMillis();
            System.out.println("That took " + (stopTime - startTime) + " milliseconds");
        } else {
            if (allTests) {
                client.executeAll();
            } else {
                client.execute2A();
            }
        }
    }
}
