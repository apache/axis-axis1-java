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

import java.io.File;
import java.io.IOException;

public class SalesRankNPrice_ServiceTestCase extends junit.framework.TestCase {
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
        String rootDir = "target" + File.separator + "work" + File.separator + 
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
}
