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
 * 4. The names "SOAP" and "Apache Software Foundation" must
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2000, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package test.wsdl.addrNoImplSEI;

import org.apache.axis.AxisFault;

import org.apache.axis.utils.Options;

import java.net.URL;



/**
 * This class shows how to use the Call object's ability to
 * become session aware.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Sanjiva Weerawarana <sanjiva@watson.ibm.com>
 */
public class Main {
    static String name1;
    static Address addr1;
    static Phone phone1;
    
    static {
        name1 = "Purdue Boilermaker";
        addr1 = new Address();
        phone1 = new Phone();
        addr1.setStreetNum(1);
        addr1.setStreetName("University Drive");
        addr1.setCity("West Lafayette");
        addr1.setState(StateType.IN);
        addr1.setZip(47907);
        phone1.setAreaCode(765);
        phone1.setExchange("494");
        phone1.setNumber("4900");
        addr1.setPhoneNumber(phone1);
        
    }
    private static void printAddress (Address ad) {
        if (ad == null) {
            System.err.println ("\t[ADDRESS NOT FOUND!]");
            return;
        }
        System.err.println ("\t" + ad.getStreetNum() + " " +
                                ad.getStreetName());
        System.err.println ("\t" + ad.getCity() + ", " + ad.getState() + " " +
                                ad.getZip());
        Phone ph = ad.getPhoneNumber();
        System.err.println ("\tPhone: (" + ph.getAreaCode() + ") " +
                                ph.getExchange() + "-" + ph.getNumber());
    }
    
    private static Object doit (AddressBookNoImplSEI ab) throws Exception {
        System.err.println (">> Storing address for '" + name1 + "'");
        ab.addEntry (name1, addr1);
        System.err.println (">> Querying address for '" + name1 + "'");
        Address resp = ab.getAddressFromName (name1);
        System.err.println (">> Response is:");
        printAddress (resp);
        
        // if we are NOT maintaining session, resp must be == null.
        // If we ARE, resp must be != null.
        
        System.err.println (">> Querying address for '" + name1 + "' again");
        resp = ab.getAddressFromName (name1);
        System.err.println (">> Response is:");
        printAddress (resp);
        
        // Test NPE
        try {
            ab.addEntry(null, null);
            throw new Exception("Expected exception when calling addEntry with null params");
        } catch (java.rmi.RemoteException e) {
            if (e.detail instanceof java.lang.IllegalArgumentException) {
                // Good! Expected this!
            } else {
                throw e;  // This is not right!
            }            
        }
        
        return resp;
    }
    
    public static void main (String[] args) throws Exception {
        try {
            Options opts = new Options(args);

            System.err.println ("Using proxy without session maintenance.");
            System.err.println ("(queries without session should say:  \"ADDRESS NOT FOUND!\")");

            AddressBookNoImplSEIService abs = new AddressBookNoImplSEIServiceLocator();
            opts.setDefaultURL( abs.getAddressBookNoImplSEIAddress() );
            URL serviceURL = new URL(opts.getURL());

            AddressBookNoImplSEI ab1 = null;
            if (serviceURL == null) {
                ab1 = abs.getAddressBookNoImplSEI();
            }
            else {
                ab1 = abs.getAddressBookNoImplSEI(serviceURL);
            }
            Object ret = doit (ab1);
            if (ret != null) {
                throw new Exception("non-session test expected null response, got "+ret);
            }

            System.err.println ("\n\nUsing proxy with session maintenance.");
            AddressBookNoImplSEI ab2 = null;
            if (serviceURL == null) {
                ab2 = abs.getAddressBookNoImplSEI();
            }
            else {
                ab2 = abs.getAddressBookNoImplSEI(serviceURL);
            }
            ((AddressBookNoImplSEISoapBindingStub) ab2).setMaintainSession (true);
            ret = doit (ab2);
            if (ret == null) {
                throw new Exception("session test expected non-null response, got "+ret);
            }
        }
        catch (Exception e) {
            if (e instanceof AxisFault) {
                System.err.println(((AxisFault) e).dumpToString());
            }
            else {
                e.printStackTrace();
            }
        }
    }
}
