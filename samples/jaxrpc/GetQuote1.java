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

package samples.jaxrpc;

import java.net.URL;

import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.XMLType; // This should probably be javax.xml.rpc.encoding.XMLType if we're told that class is approved (ie., it gets into the spec rather than just in the RI).

import org.apache.axis.utils.Options;

/**
 * This version of the ever so popular GetQuote is a near-duplicate of
 * the GetQuote1 method in samples/stock which shows how to use the AXIS
 * client APIs with and without WSDL.  This version is strictly JAX-RPC
 * compliant.  It uses no AXIS enhancements.
 *
 * This sample supports the use of the standard options too (-p ...)
 *
 * @author Russell Butek (butek@us.ibm.com)
 */
public class GetQuote1 {
    public  String symbol;

    /**
     * This will use the WSDL to prefill all of the info needed to make
     * the call.  All that's left is filling in the args to invoke().
     */
    public float getQuote1(String args[]) throws Exception {
        Options opts = new Options(args);

        args = opts.getRemainingArgs();

        if (args == null) {
            System.err.println("Usage: GetQuote <symbol>");
            System.exit(1);
        }

        /* Define the service QName and port QName */
        /*******************************************/
        QName servQN = new QName("urn:xmltoday-delayed-quotes",
                "GetQuoteService");
        QName portQN = new QName("urn:xmltoday-delayed-quotes", "GetQuote");

        /* Now use those QNames as pointers into the WSDL doc */
        /******************************************************/
        Service service = ServiceFactory.newInstance().createService(
                new URL("file:samples/stock/GetQuote.wsdl"), servQN);
        Call call = service.createCall(portQN, "getQuote");

        /* Strange - but allows the user to change just certain portions of */
        /* the URL we're gonna use to invoke the service.  Useful when you  */
        /* want to run it thru tcpmon (ie. put  -p81 on the cmd line).      */
        /********************************************************************/
        opts.setDefaultURL(call.getTargetEndpointAddress());
        call.setTargetEndpointAddress(opts.getURL());

        /* Define some service specific properties */
        /*******************************************/
        call.setProperty(Call.USERNAME_PROPERTY, opts.getUser());
        call.setProperty(Call.PASSWORD_PROPERTY, opts.getPassword());

        /* Get symbol and invoke the service */
        /*************************************/
        Object result = call.invoke(new Object[] {symbol = args[0]});

        return ((Float) result).floatValue();
    } // getQuote1

    /**
     * This will do everything manually (ie. no WSDL).
     */
    public float getQuote2(String args[]) throws Exception {
        Options opts = new Options(args);

        args = opts.getRemainingArgs();

        if (args == null) {
            System.err.println("Usage: GetQuote <symbol>");
            System.exit(1);
        }

        /* Create default/empty Service and Call object */
        /************************************************/
        Service service = ServiceFactory.newInstance().createService(null);
        Call call = service.createCall();

        /* Strange - but allows the user to change just certain portions of */
        /* the URL we're gonna use to invoke the service.  Useful when you  */
        /* want to run it thru tcpmon (ie. put  -p81 on the cmd line).      */
        /********************************************************************/
        opts.setDefaultURL("http://localhost:8080/axis/servlet/AxisServlet");

        /* Set all of the stuff that would normally come from WSDL */
        /***********************************************************/
        call.setTargetEndpointAddress(opts.getURL());
        call.setProperty(Call.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
        call.setProperty(Call.SOAPACTION_URI_PROPERTY, "getQuote");
        call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY,
                "http://schemas.xmlsoap.org/soap/encoding/");
        call.setOperationName(new QName("urn:xmltoday-delayed-quotes", "getQuote"));
        call.addParameter("symbol", XMLType.XSD_STRING, ParameterMode.IN);
        call.setReturnType(XMLType.XSD_FLOAT);

        /* Define some service specific properties */
        /*******************************************/
        call.setProperty(Call.USERNAME_PROPERTY, opts.getUser());
        call.setProperty(Call.PASSWORD_PROPERTY, opts.getPassword());

        /* Get symbol and invoke the service */
        /*************************************/
        Object result = call.invoke(new Object[] {symbol = args[0]});

        return ((Float) result).floatValue();
    } // getQuote2

    /**
     * This method does the same thing that getQuote1 does, but in
     * addition it reuses the Call object to make another call.
     */
    public float getQuote3(String args[]) throws Exception {
        Options opts = new Options(args);

        args = opts.getRemainingArgs();

        if (args == null) {
            System.err.println("Usage: GetQuote <symbol>");
            System.exit(1);
        }

        /* Define the service QName and port QName */
        /*******************************************/
        QName servQN = new QName("urn:xmltoday-delayed-quotes",
                "GetQuoteService");
        QName portQN = new QName("urn:xmltoday-delayed-quotes", "GetQuote");

        /* Now use those QNames as pointers into the WSDL doc */
        /******************************************************/
        Service service = ServiceFactory.newInstance().createService(
                new URL("file:samples/stock/GetQuote.wsdl"), servQN);
        Call call = service.createCall(portQN, "getQuote");

        /* Strange - but allows the user to change just certain portions of */
        /* the URL we're gonna use to invoke the service.  Useful when you  */
        /* want to run it thru tcpmon (ie. put  -p81 on the cmd line).      */
        /********************************************************************/
        opts.setDefaultURL(call.getTargetEndpointAddress());
        call.setTargetEndpointAddress(opts.getURL());

        /* Define some service specific properties */
        /*******************************************/
        call.setProperty(Call.USERNAME_PROPERTY, opts.getUser());
        call.setProperty(Call.PASSWORD_PROPERTY, opts.getPassword());

        /* Get symbol and invoke the service */
        /*************************************/
        Object result = call.invoke(new Object[] {symbol = args[0]});

        /* Reuse the Call object for a different call */
        /**********************************************/
        call.setOperationName(new QName("urn:xmltoday-delayed-quotes", "test"));
        call.removeAllParameters();
        call.setReturnType(XMLType.XSD_STRING);

        System.out.println(call.invoke(new Object[]{}));
        return ((Float) result).floatValue();
    } // getQuote3

    public static void main(String args[]) throws Exception {
        String    save_args[] = new String[args.length];
        float     val;
        GetQuote1 gq = new GetQuote1();

        /* Call the getQuote() that uses the WDSL */
        /******************************************/
        System.out.println("Using WSDL");
        System.arraycopy(args, 0, save_args, 0, args.length);
        val = gq.getQuote1(args);
        System.out.println(gq.symbol + ": " + val);

        /* Call the getQuote() that does it all manually */
        /*************************************************/
        System.out.println("Manually");
        System.arraycopy(save_args, 0, args, 0, args.length);
        val = gq.getQuote2(args);
        System.out.println(gq.symbol + ": " + val);

        /* Call the getQuote() that uses Axis's generated WSDL */
        /*******************************************************/
        System.out.println("WSDL + Reuse Call");
        System.arraycopy(save_args, 0, args, 0, args.length);
        val = gq.getQuote3(args);
        System.out.println(gq.symbol + ": " + val);
    } // main
}
