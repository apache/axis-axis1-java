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

package test.wsdl.inheritance;

import javax.xml.rpc.ServiceException;

/**
 * This class contains the methods necessary for testing that the use inherited methods
 * function in the Java2WSDL tool works as specified.
 * 
 * When using the Java2WSDL tool with the use inherited methods switch on, the tool
 * should generate the appropriate classes to include all of the inherited methods
 * of the specified interface (in addition to the actual methods in the interface).  
 *
 * @version   1.00  21 Jan 2002
 * @author    Brent Ulbricht
 */
public class InheritanceTestCase extends junit.framework.TestCase {

    /**
     *  Constructor used in all tests utilizing the Junit Framework.
     */
    public InheritanceTestCase(String name) {
        super(name);
    } // Constructor

    /**
     *  This method insures that two methods (getLastTradePrice and getRealtimeLastTradePrice)
     *  can be called, and they return the expected stock values.  The main goal is to verify
     *  that the getLastTradePrice method does not cause any compile errors and returns the
     *  expected stock value.  
     *
     *  The getLastTradePrice method originates from the test/wsdl/inheritance/StockQuoteProvider
     *  interface.  The InheritancePortType interface extends the StockQuoteProvider interface.
     *
     *  When the WSDL is generated for the InheritancePortType interface and the use inherited
     *  methods switch is used, all methods from the StockQuoteProvider and InheritancePortType
     *  interfaces should be available for service.
     */
    public void testInheritanceTest() {
        test.wsdl.inheritance.InheritancePortType binding;
        try {
            binding = new InheritanceTestLocator().getInheritanceTest();
        }
        catch (ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // The getLastTradePrice method should return a value of 20.25 when sent the tickerSymbol
        // "SOAP".
        try {
            float expected = 20.25F;
            float actual = binding.getLastTradePrice(new java.lang.String("SOAP"));
            float delta = 0.0F;
            assertEquals("The actual and expected values did not match.", expected, actual, delta);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }

        // The getRealtimeLastTradePrice method should return a value of 21.75 when sent the 
        // tickerSymbol "AXIS".
        try {
            float expected = 21.75F;
            float actual = binding.getRealtimeLastTradePrice(new java.lang.String("AXIS"));
            float delta = 0.0F;
            assertEquals("The actual and expected values did not match.", expected, actual, delta);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }

    } // testInheritanceTest

} // InheritanceTestCase

