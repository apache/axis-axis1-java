package test.inout;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.swingui.TestRunner;
import org.apache.axis.utils.Options;

import javax.xml.rpc.holders.StringHolder;

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

    public void testOut0Inout0In0 ()
    {
        try
        {
            io.out0Inout0In0 ();
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure: out0Inout0In0: " + t.getMessage());
        }
    }

    public void testOut0Inout0In1 ()
    {
        try
        {
            io.out0Inout0In1 ("out0Inout0In1");
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0Inout0In1" + t.getMessage());
        }
    }

    public void testOut0Inout0InMany ()
    {
        try
        {
            io.out0Inout0InMany ("out0Inout0InMany", expectedAddress);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0Inout0InMany" + t.getMessage());
        }
    }

    public void testOut0Inout1In0 ()
    {
        PhoneHolder ph = new PhoneHolder (expectedPhone);
        try
        {
            io.out0Inout1In0 (ph);
            assertTrue("out0Inout1In0 returned bad value", equals(ph._value, returnPhone));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0Inout1In0\nexpected phone = "
                                           + printPhone (returnPhone) + "\nactual phone = "
                                           + printPhone (ph._value) + t.getMessage());
        }
    }

    public void testOut0Inout1In1 ()
    {
        StringHolder sh = new StringHolder ("out0Inout1In1");
        try
        {
            io.out0Inout1In1 (sh, expectedAddress);
            assertEquals("StringHolder returned bad value", "out0Inout1In1 yo ho ho!", sh._value);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0Inout1In1\nexpected string = out0Inout1In1 yo ho ho!\nactual string = "
                                           + sh._value + t.getMessage());
        }
    }

    public void testOut0Inout1InMany ()
    {
        PhoneHolder ph = new PhoneHolder (expectedPhone);
        try
        {
            io.out0Inout1InMany ("out0Inout1InMany", expectedAddress, ph);
            assertTrue("out0Inout1InMany returned bad value", equals(ph._value, returnPhone));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0Inout1InMany\nexpected phone = "
                                           + printPhone (returnPhone) + "\nactual phone = "
                                           + printPhone (ph._value) + t.getMessage());
        }
    }

    public void testOut0InoutManyIn0 ()
    {
        StringHolder sh = new StringHolder ("out0InoutManyIn0");
        AddressHolder ah = new AddressHolder (expectedAddress);
        try
        {
            io.out0InoutManyIn0 (sh, ah);
            assertEquals("out0InoutManyIn0 yo ho ho!", sh._value);
            assertTrue("out0InoutManyIn0 returned bad value", equals (ah._value, returnAddress));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0InoutManyIn0\nexpected string = out0InoutManyIn0 yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected address =\n" + printAddress (returnAddress)
                                           + "\nactual address =\n" + printAddress (ah._value) + t.getMessage());
        }
    }

    public void testOut0InoutManyIn1 ()
    {
        try
        {
            StringHolder sh = new StringHolder ("out0InoutManyIn1");
            AddressHolder ah = new AddressHolder (expectedAddress);
            io.out0InoutManyIn1 (sh, ah, expectedPhone);
            assertEquals("out0InoutManyIn1 yo ho ho!", sh._value);
            assertTrue("testOut0InoutManyIn1 returned bad value", equals (ah._value, returnAddress));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0InoutManyIn1\n" + t.getMessage());
        }
    }

    public void testOut0InoutManyInMany ()
    {
        StringHolder sh = new StringHolder ("out0InoutManyInMany");
        AddressHolder ah = new AddressHolder (expectedAddress);
        try
        {
            io.out0InoutManyInMany (sh, ah, expectedPhone, expectedNumber);
            assertEquals("out0InoutManyInMany yo ho ho!", sh._value);
            assertTrue(equals (ah._value, returnAddress));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out0InoutManyInMany\nexpected string = out0InoutManyInMany yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected address =\n" + printAddress (returnAddress)
                                           + "\nactual address =\n" + printAddress (ah._value) + t.getMessage());
        }
    }

    public void testOut1Inout0In0 ()
    {
        int ret = 0;
        try
        {
            ret = io.out1Inout0In0 ();
            assertEquals("out1Inout0In0 returned wrong value", returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1Inout0In0\nexpected number = "
                                           + returnNumber + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOut1Inout0In1 ()
    {
        int ret = 0;
        try
        {
            ret = io.out1Inout0In1 ("out1Inout0In1");
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1Inout0In1\nexpected number = "
                                           + returnNumber + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOut1Inout0InMany ()
    {
        int ret = 0;
        try
        {
            ret = io.out1Inout0InMany ("out1Inout0InMany", expectedAddress);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1Inout0InMany\nexpected number = "
                                           + returnNumber + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOut1Inout1In0 ()
    {
        StringHolder sh = new StringHolder ("out1Inout1In0");
        Address ret = null;
        try
        {
            ret = io.out1Inout1In0 (sh);
            assertEquals("out1Inout1In0 yo ho ho!", sh._value);
            assertTrue(equals (ret, returnAddress));
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1Inout1In0\nexpected string = out1Inout1In0 yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected address =\n" + printAddress (returnAddress)
                                           + "\nactual address =\n" + printAddress (ret) + t.getMessage());
        }
    }

    public void testOut1Inout1In1 ()
    {
        StringHolder sh = new StringHolder ("out1Inout1In1");
        String ret = null;
        try
        {
            ret = io.out1Inout1In1 (sh, expectedAddress);
            assertEquals("out1Inout1In1 yo ho ho!", sh._value);
            assertEquals("out1Inout1In1 arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1Inout1In1\nexpected string1 = out1Inout1In1 yo ho ho!\nactual string1 = "
                                           + sh._value + "\nexpected string2 = out1Inout1In1 arghhh!\nactual string2 = " + ret);
        }
    }

    public void testOut1Inout1InMany ()
    {
        StringHolder sh = new StringHolder ("out1Inout1InMany");
        String ret = null;
        try
        {
            ret = io.out1Inout1InMany (sh, expectedAddress, expectedPhone);
            assertEquals("out1Inout1InMany yo ho ho!", sh._value);
            assertEquals("out1Inout1InMany arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1Inout1InMany\nexpected string1 = out1Inout1InMany yo ho ho!\nactual string1 = "
                                           + sh._value + "\nexpected string2 = out1Inout1InMany arghhh!\nactual string2 = " + ret + t.getMessage());
        }
    }

    public void testOut1InoutManyIn0 ()
    {
        StringHolder sh = new StringHolder ("out1InoutManyIn0");
        AddressHolder ah = new AddressHolder (expectedAddress);
        String ret = null;
        try
        {
            ret = io.out1InoutManyIn0 (sh, ah);
            assertEquals("out1InoutManyIn0 yo ho ho!", sh._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals("out1InoutManyIn0 arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1InoutManyIn0\nexpected string1 = out1InoutManyIn0 yo ho ho!\nactual string1 = "
                                           + sh._value + "\nexpected address = " + printAddress (returnAddress)
                                           + "\nactual address = " + printAddress (ah._value)
                                           + "\nexpected string2 = out1InoutManyIn0 arghhh!\nactual string2 = " + ret + t.getMessage());
        }
    }

    public void testOut1InoutManyIn1 ()
    {
        StringHolder sh = new StringHolder ("out1InoutManyIn1");
        AddressHolder ah = new AddressHolder (expectedAddress);
        String ret = null;
        try
        {
            ret = io.out1InoutManyIn1 (sh, ah, expectedPhone);
            assertEquals("out1InoutManyIn1 yo ho ho!", sh._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals("out1InoutManyIn1 arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1InoutManyIn1\nexpected string1 = out1InoutManyIn1 yo ho ho!\nactual string1 = "
                                           + sh._value + "\nexpected address = " + printAddress (returnAddress)
                                           + "\nactual address = " + printAddress (ah._value)
                                           + "\nexpected string2 = out1InoutManyIn1 arghhh!\nactual string2 = " + ret + t.getMessage());
        }
    }

    public void testOut1InoutManyInMany ()
    {
        StringHolder sh = new StringHolder ("out1InoutManyInMany");
        AddressHolder ah = new AddressHolder (expectedAddress);
        String ret = null;
        try
        {
            ret = io.out1InoutManyInMany (sh, ah, expectedPhone, expectedNumber);
            assertEquals("out1InoutManyInMany yo ho ho!", sh._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals("out1InoutManyInMany arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  out1InoutManyInMany\nexpected string1 = out1InoutManyInMany yo ho ho!\nactual string1 = "
                                           + sh._value + "\nexpected address = " + printAddress (returnAddress)
                                           + "\nactual address = " + printAddress (ah._value)
                                           + "\nexpected string2 = out1InoutManyInMany arghhh!\nactual string2 = " + ret + t.getMessage());
        }
    }

    public void testOutManyInout0In0 ()
    {
        AddressHolder ah = new AddressHolder (expectedAddress);
        String ret = null;
        try
        {
            ret = io.outManyInout0In0 (ah);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals(" arghhh!", ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outManyInout0In0\nexpected address = "
                                           + printAddress (returnAddress) + "\nactual address = "
                                           + printAddress (ah._value) + "\nexpected string =  arghhh!\nactual string = "
                                           + ret + t.getMessage());
        }
    }

    public void testOutManyInout0In1 ()
    {
        StringHolder sh = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outManyInout0In1 ("outManyInout0In1", sh);
            assertEquals(" yo ho ho!", sh._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outManyInout0In1\nexpected string =  yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutManyInout0InMany ()
    {
        StringHolder sh = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outManyInout0InMany ("outManyInout0InMany", expectedAddress, sh);
            assertEquals(" yo ho ho!", sh._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outManyInout0InMany\nexpected string =  yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutManyInout1In0 ()
    {
        StringHolder shinout = new StringHolder ("outManyInout1In0");
        StringHolder shout = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outManyInout1In0 (shinout, shout);
            assertEquals("outManyInout1In0 yo ho ho!", shinout._value);
            assertEquals(" yo ho ho!", shout._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outManyInout1In0\nexpected string1 = outManyInout1In0 yo ho ho!\nactual string1 = "
                                           + shinout._value + "\nexpected string2 =  yo ho ho!\nactual string2 = "
                                           + shout._value + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutManyInout1In1 ()
    {
        StringHolder shinout = new StringHolder ("outManyInout1In1");
        StringHolder shout = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outManyInout1In1 (shinout, expectedAddress, shout);
            assertEquals("outManyInout1In1 yo ho ho!", shinout._value);
            assertEquals(" yo ho ho!", shout._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outManyInout1In1\nexpected string1 = outManyInout1In1 yo ho ho!\nactual string = "
                                           + shinout._value + "\nexpected string2 =  yo ho ho!\nactual string2 = "
                                           + shout._value + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutManyInout1InMany ()
    {
        PhoneHolder ph = new PhoneHolder (expectedPhone);
        StringHolder sh = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outManyInout1InMany ("outManyInout1InMany", expectedAddress, ph, sh);
            assertTrue(equals (ph._value, returnPhone));
            assertEquals(" yo ho ho!", sh._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outManyInout1InMany\nexpected phone = "
                                           + printPhone (returnPhone) + "\nactual phone = "
                                           + printPhone (ph._value) + "\nexpected string =  yo ho ho!\nactual string = "
                                           + sh._value + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutManyInoutManyIn0 ()
    {
        StringHolder shinout = new StringHolder ("outManyInoutManyIn0");
        AddressHolder ah = new AddressHolder (expectedAddress);
        StringHolder shout = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outManyInoutManyIn0 (shinout, ah, shout);
            assertEquals("outManyInoutManyIn0 yo ho ho!", shinout._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals(" yo ho ho!", shout._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outManyInoutManyIn0\nexpected string1 = outManyInoutManyIn0 yo ho ho!\nactual string1 = "
                                           + shinout._value + "\nexpected address = " + printAddress (returnAddress)
                                           + "\nactual address = " + printAddress (ah._value) + "\nexpected string2 =  yo ho ho!\nactual string2 = "
                                           + shout._value + "\nexpected number = " + returnNumber + "\nactual number = "
                                           + ret + t.getMessage());
        }
    }

    public void testOutManyInoutManyIn1 ()
    {
        StringHolder shinout = new StringHolder ("outManyInoutManyIn1");
        AddressHolder ah = new AddressHolder (expectedAddress);
        StringHolder shout = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outManyInoutManyIn1 (shinout, ah, expectedPhone, shout);
            assertEquals("outManyInoutManyIn1 yo ho ho!", shinout._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals(" yo ho ho!", shout._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outManyInoutManyIn1\nexpected string1 = outManyInoutManyIn1 yo ho ho!\nactual string1 = "
                                           + shinout._value + "\nexpected address = " + printAddress (returnAddress)
                                           + "\nactual address = " + printAddress (ah._value)
                                           + "\nexpected string2 =  yo ho ho!\nactual string2 = " + shout._value
                                           + "\nexpected number = " + returnNumber
                                           + "\nactual number = " + ret + t.getMessage());
        }
    }

    public void testOutManyInoutManyInMany ()
    {
        StringHolder shinout = new StringHolder ("outManyInoutManyInMany");
        AddressHolder ah = new AddressHolder (expectedAddress);
        StringHolder shout = new StringHolder ();
        int ret = 0;
        try
        {
            ret = io.outManyInoutManyInMany (shinout, ah, expectedPhone, expectedNumber, shout);
            assertEquals("outManyInoutManyInMany yo ho ho!", shinout._value);
            assertTrue(equals (ah._value, returnAddress));
            assertEquals(" yo ho ho!", shout._value);
            assertEquals(returnNumber, ret);
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError("Test failure:  outManyInoutManyInMany\nexpected string1 = outManyInoutManyInMany yo ho ho!\nactual string1 = "
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
