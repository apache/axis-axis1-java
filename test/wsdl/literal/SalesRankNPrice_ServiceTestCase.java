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

import org.apache.axis.AxisFault;
import java.io.IOException;
import java.io.File;
import java.net.URL;

public class SalesRankNPrice_ServiceTestCase extends junit.framework.TestCase {
    
    public static URL url;
    
    public SalesRankNPrice_ServiceTestCase(String name) {
        super(name);
    }
    
    private void printit(String result) {
        System.out.println("Result: " + result);
    }
    
    private void printit(SalesRankNPrice1 r) {
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
        "SalesRankNPrice1.java",
        "SalesRanks.java",
        "Prices.java",
        "All.java",
        "SalesRankNPriceSoap.java",
        "SalesRankNPriceSoapStub.java",
        "SalesRankNPrice.java",
        "SalesRankNPriceLocator.java"
        
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
        // This is the book to look up...
        // "Building Web Services With Java" :)
        java.lang.String ISBN = "0672321815";
        
        boolean debug = true;
        
        SalesRankNPriceSoap binding;
        try {
            if (url != null)
                binding = new SalesRankNPriceLocator().getSalesRankNPriceSoap(url);
            else
                binding = new SalesRankNPriceLocator().getSalesRankNPriceSoap();
        } catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre );
        }

        assertTrue("binding is null", binding != null);

        try {
            printit(binding.getAmazonSalesRank(ISBN));
            printit(binding.getAmazonUKSalesRank(ISBN));
            //printit(binding.getBNSalesRank(ISBN));
            printit(binding.getAmazonPrice(ISBN));
            printit(binding.getAmazonUKPrice(ISBN));
            printit(binding.getBNPrice(ISBN));
            printit(binding.getAmazonSalesRankNPrice(ISBN));
            printit(binding.getBNSalesRankNPrice(ISBN));
            printit(binding.getAmazonAndBNSalesRank(ISBN));
            printit(binding.getAmazonAndBNPrice(ISBN));
            printit(binding.getAll(ISBN));
        } catch (java.rmi.RemoteException re) {
            if (!(re instanceof AxisFault &&
                  ((AxisFault) re).detail instanceof java.net.ConnectException)) {
                throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
            } else {
                // A connection exception has been detected so report this and make the test succeed.
                printit("Connect failure caused some of SalesRankNPrice_ServiceTestCase to be skipped.");
            }
        }
    }
    
    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                url = new URL(args[0]);
            } catch (Exception e) {
            }
        }

        junit.textui.TestRunner.run(new junit.framework.TestSuite(SalesRankNPrice_ServiceTestCase.class));
    } // main
    
}
