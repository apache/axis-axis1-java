/**
 * TerraServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.terra;

import org.apache.axis.AxisFault;

import java.net.ConnectException;

public class TerraServiceTestCase extends junit.framework.TestCase {
    public TerraServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void test11TerraServiceSoapGetPlaceList() {
        TerraServiceSoap binding;
        try {
            binding = new TerraServiceLocator().getTerraServiceSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            ArrayOfPlaceFacts value = null;
            value = binding.getPlaceList(new java.lang.String("Boston"), 5, true);
            PlaceFacts[] facts = value.getPlaceFacts();
            for(int i=0;i<facts.length;i++){
                System.out.println("City      :" + facts[i].getPlace().getCity());
                System.out.println("State     :" + facts[i].getPlace().getState());
                System.out.println("Country   :" + facts[i].getPlace().getCountry());
                System.out.println("Lat       :" + facts[i].getCenter().getLon());
                System.out.println("Long      :" + facts[i].getCenter().getLat());
                System.out.println("Theme     :" + facts[i].getAvailableThemeMask());
                System.out.println("PlaceType :" + facts[i].getPlaceTypeId().getValue());
                System.out.println("Population:" + facts[i].getPopulation());
                System.out.println("---------------------------");
            }
        }
        catch (java.rmi.RemoteException re) {
            if (re instanceof AxisFault) {
                AxisFault fault = (AxisFault) re;
                if (fault.detail instanceof ConnectException ||
                    fault.getFaultCode().getLocalPart().equals("HTTP")) {
                    System.err.println("TerraService HTTP error: " + fault);
                    return;
                }
            }
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }
}
