package test.wsdl.inout;

import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;
import test.wsdl.inout.holders.AddressHolder;
import test.wsdl.inout.holders.PhoneHolder;

import java.util.Date;

import org.apache.axis.holders.DateHolder;

public class InoutSOAPBindingImpl implements Inout
{
    public InoutSOAPBindingImpl() {
        expectedAddress = new Address();
        expectedPhone = new Phone();
        expectedDate = new Date(2002-1900, 6, 23);
        expectedAddress.setStreetNum(1);
        expectedAddress.setStreetName("University Drive");
        expectedAddress.setCity("West Lafayette");
        expectedAddress.setState("IN");
        expectedAddress.setZip(47907);
        expectedPhone.setAreaCode(765);
        expectedPhone.setExchange("494");
        expectedPhone.setNumber("4900");
        expectedAddress.setPhoneNumber(expectedPhone);

        returnAddress = new Address();
        returnPhone = new Phone();
        returnDate = new Date(1998-1900, 3, 9);
        returnAddress.setStreetNum(555);
        returnAddress.setStreetName("Monroe Street");
        returnAddress.setCity("Madison");
        returnAddress.setState("WI");
        returnAddress.setZip(54444);
        returnPhone.setAreaCode(999);
        returnPhone.setExchange("one");
        returnPhone.setNumber("two");
        returnAddress.setPhoneNumber(returnPhone);
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

    private Phone expectedPhone;
    private Address expectedAddress;
    private Date expectedDate;
    private int expectedNumber = 99;

    private Phone returnPhone;
    private Address returnAddress;
    private Date returnDate;
    private int returnNumber = 66;

    public void out0Inout0In0 () throws org.apache.axis.AxisFault, TestFailed
    {
    }

    public void out0Inout0In1 (String name) throws org.apache.axis.AxisFault, TestFailed
    {
        if (!"out0Inout0In1".equals (name))
        {
            System.err.println ("Test failure:  out0Inout0In1");
            System.err.println ("expected name = out0Inout0In1");
            System.err.println ("actual name = " + name);
            throw new TestFailed ();
        }
    }

    public void out0Inout0InMany (String name, Address address) throws org.apache.axis.AxisFault, TestFailed
    {
        if (!"out0Inout0InMany".equals (name) || !equals (address, expectedAddress))
        {
            System.err.println ("Test failure:  out0Inout0InMany");
            System.err.println ("expected name = out0Inout0InMany");
            System.err.println ("actual name = " + name);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            throw new TestFailed ();
        }
    }

    public void out0Inout1In0 (PhoneHolder phone) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone.value, expectedPhone))
            phone.value = returnPhone;
        else
        {
            System.err.println ("Test failure:  out0Inout1In0");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone.value));
            throw new TestFailed ();
        }
    }

    public void out0Inout1In1 (StringHolder name, Address address) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (address, expectedAddress) && "out0Inout1In1".equals (name.value))
            name.value = name.value + " yo ho ho!";
        else
        {
            System.err.println ("Test failure:  out0Inout1In1");
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected name = out0Inout1In1");
            System.err.println ("actual name = " + name.value);
            throw new TestFailed ();
        }
    }

    public void out0Inout1InMany (String name, Address address, PhoneHolder phone) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out0Inout1InMany".equals (name) && equals (address, expectedAddress) && equals (phone.value, expectedPhone))
            phone.value = returnPhone;
        else
        {
            System.err.println ("Test failure:  out0Inout1InMany");
            System.err.println ("expected name = out0Inout1InMany");
            System.err.println ("actual name = " + name);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone.value));
            throw new TestFailed ();
        }
    }

    public void out0InoutManyIn0 (StringHolder name, AddressHolder address) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out0InoutManyIn0".equals (name.value) && equals (address.value, expectedAddress))
        {
            name.value = name.value + " yo ho ho!";
            address.value = returnAddress;
        }
        else
        {
            System.err.println ("Test failure:  out0InoutManyIn0");
            System.err.println ("expected name = out0InoutManyIn0");
            System.err.println ("actual name = " + name.value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address.value));
            throw new TestFailed ();
        }
    }

    public void out0InoutManyIn1 (StringHolder name, AddressHolder address, Phone phone) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && "out0InoutManyIn1".equals (name.value) && equals (address.value, expectedAddress))
        {
            name.value = name.value + " yo ho ho!";
            address.value = returnAddress;
        }
        else
        {
            System.err.println ("Test failure:  out0InoutManyIn1");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected name = out0InoutManyIn1");
            System.err.println ("actual name = " + name.value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address.value));
            throw new TestFailed ();
        }
    }


    public void out0InoutManyInMany (StringHolder name, AddressHolder address, Phone phone, int number) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && expectedNumber == number && "out0InoutManyInMany".equals (name.value) && equals (address.value, expectedAddress))
        {
            name.value = name.value + " yo ho ho!";
            address.value = returnAddress;
        }
        else
        {
            System.err.println ("Test failure:  out0InoutManyInMany");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected number = " + expectedNumber);
            System.err.println ("actual number = " + number);
            System.err.println ("expected name = out0InoutManyInMany");
            System.err.println ("actual name = " + name.value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address.value));
            throw new TestFailed ();
        }
    }


    public int out1Inout0In0 () throws org.apache.axis.AxisFault, TestFailed
    {
        return returnNumber;
    }


    public int out1Inout0In1 (String name) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out1Inout0In1".equals (name))
        {
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  out1Inout0In1");
            System.err.println ("expected name = out1Inout0In1");
            System.err.println ("actual name = " + name);
            throw new TestFailed ();
        }
    }


    public int out1Inout0InMany (String name, Address address) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out1Inout0InMany".equals (name) && equals (address, expectedAddress))
        {
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  out1Inout0InMany");
            System.err.println ("expected name = out1Inout0InMany");
            System.err.println ("actual name = " + name);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            throw new TestFailed ();
        }
    }

    public Address out1Inout1In0 (StringHolder name) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out1Inout1In0".equals (name.value))
        {
            name.value = name.value + " yo ho ho!";
            return returnAddress;
        }
        else
        {
            System.err.println ("Test failure:  out1Inout1In0");
            System.err.println ("expected name = out1Inout1In0");
            System.err.println ("actual name = " + name.value);
            throw new TestFailed ();
        }
    }

    public String out1Inout1In1 (StringHolder name, Address address) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (address, expectedAddress) && "out1Inout1In1".equals (name.value))
        {
            name.value = name.value + " yo ho ho!";
            return "out1Inout1In1 arghhh!";
        }
        else
        {
            System.err.println ("Test failure:  out1Inout1In1");
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected name = out1Inout1In1");
            System.err.println ("actual name = " + name.value);
            throw new TestFailed ();
        }
    }


    public String out1Inout1InMany (StringHolder name, Address address, Phone phone) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (address, expectedAddress) && equals (phone, expectedPhone) && "out1Inout1InMany".equals (name.value))
        {
            name.value = name.value + " yo ho ho!";
            return "out1Inout1InMany arghhh!";
        }
        else
        {
            System.err.println ("Test failure:  out1Inout1InMany");
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected name = out1Inout1InMany");
            System.err.println ("actual name = " + name.value);
            throw new TestFailed ();
        }
    }


    public String out1InoutManyIn0 (StringHolder name, AddressHolder address) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out1InoutManyIn0".equals (name.value) && equals (address.value, expectedAddress))
        {
            name.value = name.value + " yo ho ho!";
            address.value = returnAddress;
            return "out1InoutManyIn0 arghhh!";
        }
        else
        {
            System.err.println ("Test failure:  out1InoutManyIn0");
            System.err.println ("expected name = out1InoutManyIn0");
            System.err.println ("actual name = " + name.value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address.value));
            throw new TestFailed ();
        }
    }


    public String out1InoutManyIn1 (StringHolder name, AddressHolder address, Phone phone) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && "out1InoutManyIn1".equals (name.value) && equals (address.value, expectedAddress))
        {
            name.value = name.value + " yo ho ho!";
            address.value = returnAddress;
            return "out1InoutManyIn1 arghhh!";
        }
        else
        {
            System.err.println ("Test failure:  out1InoutManyIn1");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected name = out1InoutManyIn1");
            System.err.println ("actual name = " + name.value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address.value));
            throw new TestFailed ();
        }
    }


    public String out1InoutManyInMany (StringHolder name, AddressHolder address, Phone phone, int number) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && expectedNumber == number && "out1InoutManyInMany".equals (name.value) && equals (address.value, expectedAddress))
        {
            name.value = name.value + " yo ho ho!";
            address.value = returnAddress;
            return "out1InoutManyInMany arghhh!";
        }
        else
        {
            System.err.println ("Test failure:  out1InoutManyInMany");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected number = " + expectedNumber);
            System.err.println ("actual number = " + number);
            System.err.println ("expected name = out1InoutManyInMany");
            System.err.println ("actual name = " + name.value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address.value));
            throw new TestFailed ();
        }
    }


    public void outManyInout0In0 (StringHolder name, AddressHolder address) throws org.apache.axis.AxisFault, TestFailed
    {
        if (name.value == null && address.value == null) {
            name.value = " arghhh!";
            address.value = returnAddress;
        }
        else
            throw new TestFailed ();
    }


    public void outManyInout0In1 (String name, IntHolder number, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("outManyInout0In1".equals (name) && otherName.value == null)
        {
            number.value = returnNumber;
            otherName.value = " yo ho ho!";
        }
        else
        {
            System.err.println ("Test failure:  outManyInout0In1");
            System.err.println ("expected name = outManyInout0In1");
            System.err.println ("actual name = " + name);
            throw new TestFailed ();
        }
    }


    public void outManyInout0InMany (String name, Address address, IntHolder number, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("outManyInout0InMany".equals (name) && equals (address, expectedAddress) && otherName.value == null)
        {
            number.value = returnNumber;
            otherName.value = " yo ho ho!";
        }
        else
        {
            System.err.println ("Test failure:  outManyInout0InMany");
            System.err.println ("expected name = outManyInout0InMany");
            System.err.println ("actual name = " + name);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            throw new TestFailed ();
        }
    }


    public void outManyInout1In0 (StringHolder name, IntHolder number, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("outManyInout1In0".equals (name.value) && otherName.value == null)
        {
            number.value = returnNumber;
            name.value = name.value + " yo ho ho!";
            otherName.value = " yo ho ho!";
        }
        else
        {
            System.err.println ("Test failure:  outManyInout1In0");
            System.err.println ("expected name = outManyInout1In0");
            System.err.println ("actual name = " + name.value);
            throw new TestFailed ();
        }
    }


    public void outManyInout1In1 (StringHolder name, Address address, IntHolder number, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (address, expectedAddress) && "outManyInout1In1".equals (name.value) && otherName.value == null)
        {
            number.value = returnNumber;
            name.value = name.value + " yo ho ho!";
            otherName.value = " yo ho ho!";
        }
        else
        {
            System.err.println ("Test failure:  outManyInout1In1");
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected name = out1InoutManyInMany");
            System.err.println ("actual name = " + name.value);
            System.err.println ("expected otherName = null");
            System.err.println ("actual otherName = " + otherName.value);
            throw new TestFailed ();
        }
    }


    public void outManyInout1InMany (String name, Address address, PhoneHolder phone, IntHolder number, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("outManyInout1InMany".equals (name) && equals (address, expectedAddress) && equals (phone.value, expectedPhone) && otherName.value == null)
        {
            phone.value = returnPhone;
            number.value = returnNumber;
            otherName.value = " yo ho ho!";
        }
        else
        {
            System.err.println ("Test failure:  outManyInout1InMany");
            System.err.println ("expected name = outManyInout1InMany");
            System.err.println ("actual name = " + name);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone.value));
            throw new TestFailed ();
        }
    }


    public void outManyInoutManyIn0 (StringHolder name, AddressHolder address, IntHolder number, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("outManyInoutManyIn0".equals (name.value) && equals (address.value, expectedAddress) && otherName.value == null)
        {
            name.value = name.value + " yo ho ho!";
            address.value = returnAddress;
            number.value = returnNumber;
            otherName.value = " yo ho ho!";
        }
        else
        {
            System.err.println ("Test failure:  outManyInoutManyIn0");
            System.err.println ("expected name = outManyInoutManyIn0");
            System.err.println ("actual name = " + name.value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address.value));
            throw new TestFailed ();
        }
    }


    public void outManyInoutManyIn1 (StringHolder name, AddressHolder address, Phone phone, IntHolder number, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && "outManyInoutManyIn1".equals (name.value) && equals (address.value, expectedAddress) && otherName.value == null)
        {
            name.value = name.value + " yo ho ho!";
            address.value = returnAddress;
            number.value = returnNumber;
            otherName.value = " yo ho ho!";
        }
        else
        {
            System.err.println ("Test failure:  outManyInoutManyIn1");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected name = out1InoutManyInMany");
            System.err.println ("actual name = " + name.value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address.value));
            throw new TestFailed ();
        }
    }


    public void outManyInoutManyInMany (StringHolder name, AddressHolder address, Phone phone, int otherNumber, IntHolder number, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && expectedNumber == otherNumber && "outManyInoutManyInMany".equals (name.value) && equals (address.value, expectedAddress) && otherName.value == null)
        {
            name.value = name.value + " yo ho ho!";
            address.value = returnAddress;
            number.value = returnNumber;
            otherName.value = " yo ho ho!";
        }
        else
        {
            System.err.println ("Test failure:  outManyInoutManyInMany");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected number = " + expectedNumber);
            System.err.println ("actual number = " + otherNumber);
            System.err.println ("expected name = outManyInoutManyInMany");
            System.err.println ("actual name = " + name.value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address.value));
            throw new TestFailed ();
        }
    }
    public void dateInout (DateHolder dateHolder) throws org.apache.axis.AxisFault, TestFailed
    {
        Date inDate = dateHolder.value;
        if (inDate.equals(expectedDate))
        {
            dateHolder.value = returnDate;
        } else {
            System.err.println ("Test failure:  dateInout");
            System.err.println ("expected Date = " + expectedDate);
            System.err.println ("actual Date = " + inDate);
            throw new TestFailed ();
        }
    }
    
}
