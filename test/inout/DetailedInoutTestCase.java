package test.inout;

import java.net.URL;

import javax.xml.rpc.holders.StringHolder;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

import org.apache.axis.client.AdminClient;

import org.apache.axis.utils.Options;

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;
import junit.swingui.TestRunner;

/**
 * This class shows how to use the ServiceClient's ability to
 * become session aware.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Sanjiva Weerawarana <sanjiva@watson.ibm.com>
 */
public class DetailedInoutTestCase extends TestCase
{
    private static Inout io = new InoutService().getInoutService();
    
    public DetailedInoutTestCase(String name) {
        super(name);
    }

    private String printAddress (Address ad) {
        String out;
        if (ad == null)
            out = "\t[ADDRESS NOT FOUND!]";
        else
            out ="\t" + ad.getStreetNum () + " " + ad.getStreetName () + "\n\t" + ad.getCity () + ", " + ad.getState () + " " + ad.getZip () + "\n\t" + printPhone (ad.getPhoneNumber ());
        return out;
    }

    private String printPhone (Phone ph)
    {
        String out;
        if (ph == null)
            out = "[PHONE NUMBER NOT FOUND!}";
        else
            out ="Phone: (" + ph.getAreaCode () + ") " + ph.getExchange () + "-" + ph.getNumber ();
        return out;
    }

    public boolean equals (Address a1, Address a2)
    {
        try
        {
            return a1.getStreetNum() == a2.getStreetNum() && a1.getZip() == a2.getZip() && equals (a1.getPhoneNumber(), a2.getPhoneNumber()) && ((a1.getStreetName() == null && a2.getStreetName() == null) || a1.getStreetName().equals (a2.getStreetName())) && ((a1.getCity() == null && a2.getCity() == null) || a1.getCity().equals (a2.getCity())) && ((a1.getState() == null && a2.getState() == null) || a1.getState().equals (a2.getState()));
        }
        catch (Throwable t)
        {
            return false;
        }
    }

    public boolean equals (Phone p1, Phone p2)
    {
        try
        {
            return p1.getAreaCode() == p2.getAreaCode() && ((p1.getExchange() == null && p2.getExchange() == null) || p1.getExchange().equals (p2.getExchange())) && ((p1.getNumber() == null && p2.getNumber() == null) || p1.getNumber().equals (p2.getNumber()));
        }
        catch (Throwable t)
        {
            return false;
        }
    }

    private Phone expectedPhone = new Phone (765, "494", "4900");
    private Address expectedAddress = new Address (1, "University Drive", "West Lafayette", "IN", 47907, expectedPhone);
    private int expectedNumber = 99;

    private Phone returnPhone = new Phone (999, "one", "two");
    private Address returnAddress = new Address (555, "Monroe Street", "Madison", "WI", 54444, returnPhone);
    private int returnNumber = 66;

    public void testDeploy() {
        String[] args = {"test/inout/deploy.xml"};
        AdminClient.main(args);
    } // doTestDeploy
    
    public void testOut0_inout0_in0 ()
    {
        try
        {
            io.out0_inout0_in0 ();
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure: out0_inout0_in0: " + t.getMessage());
        }
    }

    public void testOut0_inout0_in1 ()
    {
        try
        {
            io.out0_inout0_in1 ("out0_inout0_in1");
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0_inout0_in1" + t.getMessage());
        }
    }

    public void testOut0_inout0_inMany ()
    {
        try
        {
            io.out0_inout0_inMany ("out0_inout0_inMany", expectedAddress);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0_inout0_inMany" + t.getMessage());
        }
    }

    public void testOut0_inout1_in0 ()
    {
        PhoneHolder ph = new PhoneHolder (expectedPhone);
        try
        {
            io.out0_inout1_in0 (ph);
            assertTrue("out0_inout1_in0 returned bad value", equals(ph._value, returnPhone));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0_inout1_in0\nexpected phone = "
                                           + printPhone (returnPhone) + "\nactual phone = "
                                           + printPhone (ph._value) + t.getMessage());
        }
    }

    public void testOut0_inout1_in1 ()
    {
        StringHolder sh = new StringHolder ("out0_inout1_in1");
        try
        {
            io.out0_inout1_in1 (sh, expectedAddress);
            assertEquals("StringHolder returned bad value", "out0_inout1_in1 yo ho ho!", sh._value);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0_inout1_in1\nexpected string = out0_inout1_in1 yo ho ho!\nactual string = "
                                           + sh._value + t.getMessage());
        }
    }

    public void testOut0_inout1_inMany ()
    {
        PhoneHolder ph = new PhoneHolder (expectedPhone);
        try
        {
            io.out0_inout1_inMany ("out0_inout1_inMany", expectedAddress, ph);
            assertTrue("out0_inout1_inMany returned bad value", equals(ph._value, returnPhone));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0_inout1_inMany\nexpected phone = "
                                           + printPhone (returnPhone) + "\nactual phone = "
                                           + printPhone (ph._value) + t.getMessage());
        }
    }

    public void testOut0_inoutMany_in0 ()
    {
        StringHolder sh = new StringHolder ("out0_inoutMany_in0");
        AddressHolder ah = new AddressHolder (expectedAddress);
        try
        {
            io.out0_inoutMany_in0 (sh, ah);
            assertEquals("out0_inoutMany_in0 yo ho ho!", sh._value);
            assertTrue("out0_inoutMany_in0 returned bad value", equals (ah._value, returnAddress));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0_inoutMany_in0\nexpected string = out0_inoutMany_in0 yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected address =\n" + printAddress (returnAddress)
                                           + "\nactual address =\n" + printAddress (ah._value) + t.getMessage());
        }
    }

    public void testOut0_inoutMany_in1 ()
    {
        try
        {
            StringHolder sh = new StringHolder ("out0_inoutMany_in1");
            AddressHolder ah = new AddressHolder (expectedAddress);
            io.out0_inoutMany_in1 (sh, ah, expectedPhone);
            assertEquals("out0_inoutMany_in1 yo ho ho!", sh._value);
            assertTrue("testOut0_inoutMany_in1 returned bad value", equals (ah._value, returnAddress));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0_inoutMany_in1\n" + t.getMessage());
        }
    }

    public void testOut0_inoutMany_inMany ()
    {
        StringHolder sh = new StringHolder ("out0_inoutMany_inMany");
        AddressHolder ah = new AddressHolder (expectedAddress);
        try
        {
            io.out0_inoutMany_inMany (sh, ah, expectedPhone, expectedNumber);
            assertEquals("out0_inoutMany_inMany yo ho ho!", sh._value);
            assertTrue(equals (ah._value, returnAddress));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0_inoutMany_inMany\nexpected string = out0_inoutMany_inMany yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected address =\n" + printAddress (returnAddress)
                                           + "\nactual address =\n" + printAddress (ah._value) + t.getMessage());
        }
    }

    public void testOut1_inout0_in0 ()
    {
        int ret = 0;
        try
        {
            ret = io.out1_inout0_in0 ();
            assertEquals("out1_inout0_in0 returned wrong value", returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1_inout0_in0\nexpected number = "
                                           + returnNumber + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOut1_inout0_in1 ()
    {
        int ret = 0;
        try
        {
            ret = io.out1_inout0_in1 ("out1_inout0_in1");
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1_inout0_in1\nexpected number = "
                                           + returnNumber + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOut1_inout0_inMany ()
    {
        int ret = 0;
        try
        {
            ret = io.out1_inout0_inMany ("out1_inout0_inMany", expectedAddress);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1_inout0_inMany\nexpected number = "
                                           + returnNumber + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOut1_inout1_in0 ()
    {
        StringHolder sh = new StringHolder ("out1_inout1_in0");
        Address ret = null;
        try
        {
            ret = io.out1_inout1_in0 (sh);
            assertEquals("out1_inout1_in0 yo ho ho!", sh._value);
            assertTrue(equals (ret, returnAddress));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1_inout1_in0\nexpected string = out1_inout1_in0 yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected address =\n" + printAddress (returnAddress)
                                           + "\nactual address =\n" + printAddress (ret) + t.getMessage());
        }
    }

    public void testOut1_inout1_in1 ()
    {
        StringHolder sh = new StringHolder ("out1_inout1_in1");
        String ret = null;
        try
        {
            ret = io.out1_inout1_in1 (sh, expectedAddress);
            assertEquals("out1_inout1_in1 yo ho ho!", sh._value);
            assertEquals("out1_inout1_in1 arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1_inout1_in1\nexpected string1 = out1_inout1_in1 yo ho ho!\nactual string1 = "
                                           + sh._value + "\nexpected string2 = out1_inout1_in1 arghhh!\nactual string2 = " + ret);
        }
    }

    public void testOut1_inout1_inMany ()
    {
        StringHolder sh = new StringHolder ("out1_inout1_inMany");
        String ret = null;
        try
        {
            ret = io.out1_inout1_inMany (sh, expectedAddress, expectedPhone);
            assertEquals("out1_inout1_inMany yo ho ho!", sh._value);
            assertEquals("out1_inout1_inMany arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1_inout1_inMany\nexpected string1 = out1_inout1_inMany yo ho ho!\nactual string1 = "
                                           + sh._value + "\nexpected string2 = out1_inout1_inMany arghhh!\nactual string2 = " + ret + t.getMessage());
        }
    }

    public void testOut1_inoutMany_in0 ()
    {
        StringHolder sh = new StringHolder ("out1_inoutMany_in0");
        AddressHolder ah = new AddressHolder (expectedAddress);
        String ret = null;
        try
        {
            ret = io.out1_inoutMany_in0 (sh, ah);
            assertEquals("out1_inoutMany_in0 yo ho ho!", sh._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals("out1_inoutMany_in0 arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1_inoutMany_in0\nexpected string1 = out1_inoutMany_in0 yo ho ho!\nactual string1 = "
                                           + sh._value + "\nexpected address = " + printAddress (returnAddress)
                                           + "\nactual address = " + printAddress (ah._value)
                                           + "\nexpected string2 = out1_inoutMany_in0 arghhh!\nactual string2 = " + ret + t.getMessage());
        }
    }

    public void testOut1_inoutMany_in1 ()
    {
        StringHolder sh = new StringHolder ("out1_inoutMany_in1");
        AddressHolder ah = new AddressHolder (expectedAddress);
        String ret = null;
        try
        {
            ret = io.out1_inoutMany_in1 (sh, ah, expectedPhone);
            assertEquals("out1_inoutMany_in1 yo ho ho!", sh._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals("out1_inoutMany_in1 arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1_inoutMany_in1\nexpected string1 = out1_inoutMany_in1 yo ho ho!\nactual string1 = "
                                           + sh._value + "\nexpected address = " + printAddress (returnAddress)
                                           + "\nactual address = " + printAddress (ah._value)
                                           + "\nexpected string2 = out1_inoutMany_in1 arghhh!\nactual string2 = " + ret + t.getMessage());
        }
    }

    public void testOut1_inoutMany_inMany ()
    {
        StringHolder sh = new StringHolder ("out1_inoutMany_inMany");
        AddressHolder ah = new AddressHolder (expectedAddress);
        String ret = null;
        try
        {
            ret = io.out1_inoutMany_inMany (sh, ah, expectedPhone, expectedNumber);
            assertEquals("out1_inoutMany_inMany yo ho ho!", sh._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals("out1_inoutMany_inMany arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1_inoutMany_inMany\nexpected string1 = out1_inoutMany_inMany yo ho ho!\nactual string1 = "
                                           + sh._value + "\nexpected address = " + printAddress (returnAddress)
                                           + "\nactual address = " + printAddress (ah._value)
                                           + "\nexpected string2 = out1_inoutMany_inMany arghhh!\nactual string2 = " + ret + t.getMessage());
        }
    }

    public void testOutMany_inout0_in0 ()
    {
        AddressHolder ah = new AddressHolder (expectedAddress);
        String ret = null;
        try
        {
            ret = io.outMany_inout0_in0 (ah);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals(" arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outMany_inout0_in0\nexpected address = "
                                           + printAddress (returnAddress) + "\nactual address = "
                                           + printAddress (ah._value) + "\nexpected string =  arghhh!\nactual string = "
                                           + ret + t.getMessage());
        }
    }

    public void testOutMany_inout0_in1 ()
    {
        StringHolder sh = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outMany_inout0_in1 ("outMany_inout0_in1", sh);
            assertEquals(" yo ho ho!", sh._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outMany_inout0_in1\nexpected string =  yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutMany_inout0_inMany ()
    {
        StringHolder sh = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outMany_inout0_inMany ("outMany_inout0_inMany", expectedAddress, sh);
            assertEquals(" yo ho ho!", sh._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outMany_inout0_inMany\nexpected string =  yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutMany_inout1_in0 ()
    {
        StringHolder shinout = new StringHolder ("outMany_inout1_in0");
        StringHolder shout = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outMany_inout1_in0 (shinout, shout);
            assertEquals("outMany_inout1_in0 yo ho ho!", shinout._value);
            assertEquals(" yo ho ho!", shout._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outMany_inout1_in0\nexpected string1 = outMany_inout1_in0 yo ho ho!\nactual string1 = "
                                           + shinout._value + "\nexpected string2 =  yo ho ho!\nactual string2 = "
                                           + shout._value + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutMany_inout1_in1 ()
    {
        StringHolder shinout = new StringHolder ("outMany_inout1_in1");
        StringHolder shout = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outMany_inout1_in1 (shinout, expectedAddress, shout);
            assertEquals("outMany_inout1_in1 yo ho ho!", shinout._value);
            assertEquals(" yo ho ho!", shout._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outMany_inout1_in1\nexpected string1 = outMany_inout1_in1 yo ho ho!\nactual string = "
                                           + shinout._value + "\nexpected string2 =  yo ho ho!\nactual string2 = "
                                           + shout._value + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutMany_inout1_inMany ()
    {
        PhoneHolder ph = new PhoneHolder (expectedPhone);
        StringHolder sh = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outMany_inout1_inMany ("outMany_inout1_inMany", expectedAddress, ph, sh);
            assertTrue(equals (ph._value, returnPhone));
            assertEquals(" yo ho ho!", sh._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outMany_inout1_inMany\nexpected phone = "
                                           + printPhone (returnPhone) + "\nactual phone = "
                                           + printPhone (ph._value) + "\nexpected string =  yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutMany_inoutMany_in0 ()
    {
        StringHolder shinout = new StringHolder ("outMany_inoutMany_in0");
        AddressHolder ah = new AddressHolder (expectedAddress);
        StringHolder shout = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outMany_inoutMany_in0 (shinout, ah, shout);
            assertEquals("outMany_inoutMany_in0 yo ho ho!", shinout._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals(" yo ho ho!", shout._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outMany_inoutMany_in0\nexpected string1 = outMany_inoutMany_in0 yo ho ho!\nactual string1 = "
                                           + shinout._value + "\nexpected address = " + printAddress (returnAddress)
                                           + "\nactual address = " + printAddress (ah._value) + "\nexpected string2 =  yo ho ho!\nactual string2 = "
                                           + shout._value + "\nexpected number = " + returnNumber + "\nactual number = "
                                           + ret + t.getMessage());
        }
    }

    public void testOutMany_inoutMany_in1 ()
    {
        StringHolder shinout = new StringHolder ("outMany_inoutMany_in1");
        AddressHolder ah = new AddressHolder (expectedAddress);
        StringHolder shout = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outMany_inoutMany_in1 (shinout, ah, expectedPhone, shout);
            assertEquals("outMany_inoutMany_in1 yo ho ho!", shinout._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals(" yo ho ho!", shout._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outMany_inoutMany_in1\nexpected string1 = outMany_inoutMany_in1 yo ho ho!\nactual string1 = "
                                           + shinout._value + "\nexpected address = " + printAddress (returnAddress)
                                           + "\nactual address = " + printAddress (ah._value)
                                           + "\nexpected string2 =  yo ho ho!\nactual string2 = " + shout._value
                                           + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutMany_inoutMany_inMany ()
    {
        StringHolder shinout = new StringHolder ("outMany_inoutMany_inMany");
        AddressHolder ah = new AddressHolder (expectedAddress);
        StringHolder shout = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outMany_inoutMany_inMany (shinout, ah, expectedPhone, expectedNumber, shout);
            assertEquals("outMany_inoutMany_inMany yo ho ho!", shinout._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals(" yo ho ho!", shout._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outMany_inoutMany_inMany\nexpected string1 = outMany_inoutMany_inMany yo ho ho!\nactual string1 = "
                                           + shinout._value + "\nexpected address = " + printAddress (returnAddress)
                                           + "\nactual address = " + printAddress (ah._value)
                                           + "\nexpected string2 =  yo ho ho!\nactual string2 = " + shout._value
                                           + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public static void main (String[] args) throws Exception {
        try
        {
            Options opts = new Options(args);
//            Category category = Category.getInstance(org.apache.axis.transport.http.HTTPSender.class.getName());
//            category.setPriority (Priority.DEBUG);

            TestRunner.main(new String[] {DetailedInoutTestCase.class.getName()});
        }
        catch (Throwable t)
        {
            t.printStackTrace (System.err);
        }
    }
}
