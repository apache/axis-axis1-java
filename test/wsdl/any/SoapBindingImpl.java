/**
 * SoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis axis-1_2_1 Jun 07, 2005 (05:25:16 IST) WSDL2Java emitter.
 */

package test.wsdl.any;

import org.apache.axis.message.MessageElement;

public class SoapBindingImpl implements test.wsdl.any.Soap{
    public test.wsdl.any.QueryResult query(java.lang.String queryString) throws java.rmi.RemoteException {
        QueryResult qr = new QueryResult();
        qr.setDone(true);
        
        SObject record = new SObject();
        record.setType("Contact");
        record.setId("00330000006jryXAAQ");
        
        try{
        MessageElement m1 = new MessageElement("Id", "sf", "urn:partner.soap.sforce.com");
        MessageElement m2 = new MessageElement("FirstName", "sf", "urn:partner.soap.sforce.com");
        MessageElement m3 = new MessageElement("LastName", "sf", "urn:partner.soap.sforce.com");
        m1.addTextNode("00330000006jryXAAQ");
        m2.addTextNode("Fred");
        m3.addTextNode("A>B");
        MessageElement[] me = new MessageElement[]{m1, m2, m3};
        record.set_any(me);
        } catch(javax.xml.soap.SOAPException e){
        	
        }
        
        SObject[] records = new SObject[]{record};
        qr.setRecords(records);
        
        qr.setSize(1);
        
    	return qr;
    }

}
