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

package test.wsdl.addrNoImplSEI;

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
    static _Phone phone1;
    
    static {
        name1 = "Purdue Boilermaker";
        addr1 = new Address();
        phone1 = new _Phone();
        addr1.setStreetNum(1);
        addr1.setStreetName("University Drive");
        addr1.setCity("West Lafayette");
        addr1.setState(StateType.IN);
        addr1.setZip(47907);
        phone1.setAreaCode(765);
        phone1.setExchange("494");
        phone1.setNumber("4900");
        addr1.setPhone(phone1);
        
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
        _Phone ph = ad.getPhone();
        System.err.println ("\tPhone: (" + ph.getAreaCode() + ") " +
                                ph.getExchange() + "-" + ph.getNumber());
    }
    
    private static Object doit (AddressBookNoImplSEI ab) throws Exception {
        ab.addEntry (name1, addr1);
        Address resp = ab.getAddressFromName (name1);
        
        // if we are NOT maintaining session, resp must be == null.
        // If we ARE, resp must be != null.
        
        resp = ab.getAddressFromName (name1);
        
        // Test NPE
        try {
            ab.addEntry(null, null);
            throw new Exception("Expected exception when calling addEntry with null params");
        } catch (org.apache.axis.AxisFault e) {
            if ("java.lang.IllegalArgumentException".equals(e.getFaultString())) {
                // Good! Expected this!
            } else {
                throw e;  // This is not right!
            }            
        }
        
        return resp;
    }
    
    public static void main (String[] args) throws Exception {
        Options opts = new Options(args);

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
}
