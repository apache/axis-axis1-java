/**
 * SalesRankNPrice_ServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 * Edited by hand to test a .NET web service on the internet.
 *
 *@author Tom Jordahl (tomj@macromedia.com)
 */

package test.wsdl.literal;

import java.io.IOException;
import java.io.File;

public class SalesRankNPrice_ServiceTestCase extends junit.framework.TestCase {
    public SalesRankNPrice_ServiceTestCase(String name) {
        super(name);
    }
    
    private void printit(String result) {
        System.out.println("Result: " + result);
    }
    
    private void printit(SalesRankNPrice_Type r) {
        System.out.println("price: " + r.getPrice());
        System.out.println("rank: " + r.getSalesRank());
    }
    
    private void printit(Prices p) {
        System.out.println(
                " Amazon price:" + p.getAmazonPrice() + "\n" +
                " BN price:" + p.getBNPrice() );
    }
    
    private void printit(SalesRanks s) {
        System.out.println(
                " Amazon rank:" + s.getAmazonSalesRank() + "\n" + 
                " BN rank:" + s.getBNSalesRank());
    }
    
    private void printit(All all) {
        System.out.println(
                " Amazon price:" + all.getAmazonPrice() + "\n" +
                " Amazon rank:" + all.getAmazonSalesRank() + "\n" + 
                " BN price:" + all.getBNPrice() +  "\n" +
                " BN rank:" + all.getBNSalesRank());
        
    }
    
    // List of files which should be generated
    private static String[] shouldExist= new String[] {
        "All.java",
        "Prices.java",
        "SalesRankNPrice_Service.java",
        "SalesRankNPrice_Type.java",
        "SalesRankNPriceSoap.java",
        "SalesRankNPriceSoapStub.java",
        "SalesRanks.java"
        
    };
    
    // List of files which should NOT be generated
    private static String[] shouldNotExist= new String[] {
        "GetAmazonSalesRank.java",
        "GetAmazonSalesRankResponse.java",
        "GetAmazonUKSalesRank.java",
        "GetAmazonUKSalesRankResponse.java",
        "GetBNSalesRank.java",
        "GetBNSalesRankResponse.java",
        "GetAmazonPrice.java",
        "GetAmazonPriceReponse.java",
        "GetAmazonUKPrice.java",
        "GetAmazonUKPriceResponse.java",
        "GetBNPrice.java",
        "GetBNPriceResponse.java",
        "GetAmazonSalesRankNPrice.java",
        "GetAmazonSalesRankNPriceResponse.java",
        "GetBNSalesRankNPrice.java",
        "GetBNSalesRankNPriceResponse.java",
        "GetAmazonAndBNSalesRank.java",
        "GetAmazonAndBNSalesRankResponse.java",
        "GetAmazonAndBNPrice.java",
        "GetAmazonAndBNPriceResponse.java",
        "GetAll.java",
        "GetAllResponse.java"
    };

    public void testFileGen() throws IOException {
        String rootDir = "build"+ File.separator + "work" + File.separator + 
                "test" + File.separator + "wsdl" + File.separator + "literal";
        // open up the output directory and check what files exist.
        File outputDir = new File(rootDir);
        
        String[] files = outputDir.list();
        
        for (int i=0; i < shouldExist.length; i++) {
            File f = new File(rootDir, shouldExist[i]);
            assertTrue("File does not exist (and it should): " + shouldExist[i], f.exists()); 
        }
        
        for (int i=0; i < shouldNotExist.length; i++) {
            File f = new File(rootDir, shouldNotExist[i]);
            assertTrue("File exist (and it should NOT): " + shouldNotExist[i], !f.exists()); 
        }
    }
    
    public void testSalesRankNPriceSoap() {
        // This is the book to look up
        java.lang.String ISBN = "1861005466";
        
        boolean debug = true;
        
        SalesRankNPriceSoap binding;
        try {
            binding = new SalesRankNPrice_Service().getSalesRankNPriceSoap();
        } catch (javax.xml.rpc.JAXRPCException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC Exception caught: " + jre );
        }
        assertTrue("binding is null", binding != null);
        try {
            java.lang.String value = null;
            value = binding.getAmazonSalesRank(ISBN);
            printit(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.String value = null;
            value = binding.getAmazonUKSalesRank(ISBN);
            printit(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.String value = null;
            value = binding.getBNSalesRank(ISBN);
            printit(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.String value = null;
            value = binding.getAmazonPrice(ISBN);
            printit(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.String value = null;
            value = binding.getAmazonUKPrice(ISBN);
            printit(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            java.lang.String value = null;
            value = binding.getBNPrice(ISBN);
            printit(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            SalesRankNPrice_Type value = null;
            value = binding.getAmazonSalesRankNPrice(ISBN);
            printit(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            SalesRankNPrice_Type value = null;
            value = binding.getBNSalesRankNPrice(ISBN);
            printit(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            SalesRanks value = null;
            value = binding.getAmazonAndBNSalesRank(ISBN);
            printit(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
           Prices value = null;
            value = binding.getAmazonAndBNPrice(ISBN);
            printit(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            All value = null;
            value = binding.getAll(ISBN);
            printit(value);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
    }
}

