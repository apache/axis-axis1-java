package test.inout;

import javax.xml.rpc.holders.StringHolder;

public class InoutSOAPBindingImpl implements Inout
{
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

    public void out0_inout0_in0 () throws org.apache.axis.AxisFault, TestFailed
    {
    }

    public void out0_inout0_in1 (String name) throws org.apache.axis.AxisFault, TestFailed
    {
        if (!"out0_inout0_in1".equals (name))
        {
            System.err.println ("Test failure:  out0_inout0_in1");
            System.err.println ("expected name = out0_inout0_in1");
            System.err.println ("actual name = " + name);
            throw new TestFailed ();
        }
    }

    public void out0_inout0_inMany (String name, Address address) throws org.apache.axis.AxisFault, TestFailed
    {
        if (!"out0_inout0_inMany".equals (name) || !equals (address, expectedAddress))
        {
            System.err.println ("Test failure:  out0_inout0_inMany");
            System.err.println ("expected name = out0_inout0_inMany");
            System.err.println ("actual name = " + name);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            throw new TestFailed ();
        }
    }

    public void out0_inout1_in0 (PhoneHolder phone) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone._value, expectedPhone))
            phone._value = returnPhone;
        else
        {
            System.err.println ("Test failure:  out0_inout1_in0");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone._value));
            throw new TestFailed ();
        }
    }

    public void out0_inout1_in1 (StringHolder name, Address address) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (address, expectedAddress) && "out0_inout1_in1".equals (name._value))
            name._value = name._value + " yo ho ho!";
        else
        {
            System.err.println ("Test failure:  out0_inout1_in1");
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected name = out0_inout1_in1");
            System.err.println ("actual name = " + name._value);
            throw new TestFailed ();
        }
    }

    public void out0_inout1_inMany (String name, Address address, PhoneHolder phone) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out0_inout1_inMany".equals (name) && equals (address, expectedAddress) && equals (phone._value, expectedPhone))
            phone._value = returnPhone;
        else
        {
            System.err.println ("Test failure:  out0_inout1_inMany");
            System.err.println ("expected name = out0_inout1_inMany");
            System.err.println ("actual name = " + name);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone._value));
            throw new TestFailed ();
        }
    }

    public void out0_inoutMany_in0 (StringHolder name, AddressHolder address) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out0_inoutMany_in0".equals (name._value) && equals (address._value, expectedAddress))
        {
            name._value = name._value + " yo ho ho!";
            address._value = returnAddress;
        }
        else
        {
            System.err.println ("Test failure:  out0_inoutMany_in0");
            System.err.println ("expected name = out0_inoutMany_in0");
            System.err.println ("actual name = " + name._value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address._value));
            throw new TestFailed ();
        }
    }

    public void out0_inoutMany_in1 (StringHolder name, AddressHolder address, Phone phone) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && "out0_inoutMany_in1".equals (name._value) && equals (address._value, expectedAddress))
        {
            name._value = name._value + " yo ho ho!";
            address._value = returnAddress;
        }
        else
        {
            System.err.println ("Test failure:  out0_inoutMany_in1");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected name = out0_inoutMany_in1");
            System.err.println ("actual name = " + name._value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address._value));
            throw new TestFailed ();
        }
    }


    public void out0_inoutMany_inMany (StringHolder name, AddressHolder address, Phone phone, int number) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && expectedNumber == number && "out0_inoutMany_inMany".equals (name._value) && equals (address._value, expectedAddress))
        {
            name._value = name._value + " yo ho ho!";
            address._value = returnAddress;
        }
        else
        {
            System.err.println ("Test failure:  out0_inoutMany_inMany");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected number = " + expectedNumber);
            System.err.println ("actual number = " + number);
            System.err.println ("expected name = out0_inoutMany_inMany");
            System.err.println ("actual name = " + name._value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address._value));
            throw new TestFailed ();
        }
    }


    public int out1_inout0_in0 () throws org.apache.axis.AxisFault, TestFailed
    {
        return returnNumber;
    }


    public int out1_inout0_in1 (String name) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out1_inout0_in1".equals (name))
        {
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  out1_inout0_in1");
            System.err.println ("expected name = out1_inout0_in1");
            System.err.println ("actual name = " + name);
            throw new TestFailed ();
        }
    }


    public int out1_inout0_inMany (String name, Address address) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out1_inout0_inMany".equals (name) && equals (address, expectedAddress))
        {
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  out1_inout0_inMany");
            System.err.println ("expected name = out1_inout0_inMany");
            System.err.println ("actual name = " + name);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            throw new TestFailed ();
        }
    }

    public Address out1_inout1_in0 (StringHolder name) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out1_inout1_in0".equals (name._value))
        {
            name._value = name._value + " yo ho ho!";
            return returnAddress;
        }
        else
        {
            System.err.println ("Test failure:  out1_inout1_in0");
            System.err.println ("expected name = out1_inout1_in0");
            System.err.println ("actual name = " + name._value);
            throw new TestFailed ();
        }
    }

    public String out1_inout1_in1 (StringHolder name, Address address) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (address, expectedAddress) && "out1_inout1_in1".equals (name._value))
        {
            name._value = name._value + " yo ho ho!";
            return "out1_inout1_in1 arghhh!";
        }
        else
        {
            System.err.println ("Test failure:  out1_inout1_in1");
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected name = out1_inout1_in1");
            System.err.println ("actual name = " + name._value);
            throw new TestFailed ();
        }
    }


    public String out1_inout1_inMany (StringHolder name, Address address, Phone phone) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (address, expectedAddress) && equals (phone, expectedPhone) && "out1_inout1_inMany".equals (name._value))
        {
            name._value = name._value + " yo ho ho!";
            return "out1_inout1_inMany arghhh!";
        }
        else
        {
            System.err.println ("Test failure:  out1_inout1_inMany");
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected name = out1_inout1_inMany");
            System.err.println ("actual name = " + name._value);
            throw new TestFailed ();
        }
    }


    public String out1_inoutMany_in0 (StringHolder name, AddressHolder address) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("out1_inoutMany_in0".equals (name._value) && equals (address._value, expectedAddress))
        {
            name._value = name._value + " yo ho ho!";
            address._value = returnAddress;
            return "out1_inoutMany_in0 arghhh!";
        }
        else
        {
            System.err.println ("Test failure:  out1_inoutMany_in0");
            System.err.println ("expected name = out1_inoutMany_in0");
            System.err.println ("actual name = " + name._value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address._value));
            throw new TestFailed ();
        }
    }


    public String out1_inoutMany_in1 (StringHolder name, AddressHolder address, Phone phone) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && "out1_inoutMany_in1".equals (name._value) && equals (address._value, expectedAddress))
        {
            name._value = name._value + " yo ho ho!";
            address._value = returnAddress;
            return "out1_inoutMany_in1 arghhh!";
        }
        else
        {
            System.err.println ("Test failure:  out1_inoutMany_in1");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected name = out1_inoutMany_in1");
            System.err.println ("actual name = " + name._value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address._value));
            throw new TestFailed ();
        }
    }


    public String out1_inoutMany_inMany (StringHolder name, AddressHolder address, Phone phone, int number) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && expectedNumber == number && "out1_inoutMany_inMany".equals (name._value) && equals (address._value, expectedAddress))
        {
            name._value = name._value + " yo ho ho!";
            address._value = returnAddress;
            return "out1_inoutMany_inMany arghhh!";
        }
        else
        {
            System.err.println ("Test failure:  out1_inoutMany_inMany");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected number = " + expectedNumber);
            System.err.println ("actual number = " + number);
            System.err.println ("expected name = out1_inoutMany_inMany");
            System.err.println ("actual name = " + name._value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address._value));
            throw new TestFailed ();
        }
    }


    public String outMany_inout0_in0 (AddressHolder address) throws org.apache.axis.AxisFault, TestFailed
    {
        if (address._value == null)
        {
            address._value = returnAddress;
            return " arghhh!";
        }
        else
            throw new TestFailed ();
    }


    public int outMany_inout0_in1 (String name, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("outMany_inout0_in1".equals (name) && otherName._value == null)
        {
            otherName._value = " yo ho ho!";
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  outMany_inout0_in1");
            System.err.println ("expected name = outMany_inout0_in1");
            System.err.println ("actual name = " + name);
            throw new TestFailed ();
        }
    }


    public int outMany_inout0_inMany (String name, Address address, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("outMany_inout0_inMany".equals (name) && equals (address, expectedAddress) && otherName._value == null)
        {
            otherName._value = " yo ho ho!";
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  outMany_inout0_inMany");
            System.err.println ("expected name = outMany_inout0_inMany");
            System.err.println ("actual name = " + name);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            throw new TestFailed ();
        }
    }


    public int outMany_inout1_in0 (StringHolder name, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("outMany_inout1_in0".equals (name._value) && otherName._value == null)
        {
            name._value = name._value + " yo ho ho!";
            otherName._value = " yo ho ho!";
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  outMany_inout1_in0");
            System.err.println ("expected name = outMany_inout1_in0");
            System.err.println ("actual name = " + name._value);
            throw new TestFailed ();
        }
    }


    public int outMany_inout1_in1 (StringHolder name, Address address, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (address, expectedAddress) && "outMany_inout1_in1".equals (name._value) && otherName._value == null)
        {
            name._value = name._value + " yo ho ho!";
            otherName._value = " yo ho ho!";
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  outMany_inout1_in1");
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected name = out1_inoutMany_inMany");
            System.err.println ("actual name = " + name._value);
            System.err.println ("expected otherName = null");
            System.err.println ("actual otherName = " + otherName._value);
            throw new TestFailed ();
        }
    }


    public int outMany_inout1_inMany (String name, Address address, PhoneHolder phone, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("outMany_inout1_inMany".equals (name) && equals (address, expectedAddress) && equals (phone._value, expectedPhone) && otherName._value == null)
        {
            phone._value = returnPhone;
            otherName._value = " yo ho ho!";
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  outMany_inout1_inMany");
            System.err.println ("expected name = outMany_inout1_inMany");
            System.err.println ("actual name = " + name);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address));
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone._value));
            throw new TestFailed ();
        }
    }


    public int outMany_inoutMany_in0 (StringHolder name, AddressHolder address, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if ("outMany_inoutMany_in0".equals (name._value) && equals (address._value, expectedAddress) && otherName._value == null)
        {
            name._value = name._value + " yo ho ho!";
            address._value = returnAddress;
            otherName._value = " yo ho ho!";
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  outMany_inoutMany_in0");
            System.err.println ("expected name = outMany_inoutMany_in0");
            System.err.println ("actual name = " + name._value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address._value));
            throw new TestFailed ();
        }
    }


    public int outMany_inoutMany_in1 (StringHolder name, AddressHolder address, Phone phone, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && "outMany_inoutMany_in1".equals (name._value) && equals (address._value, expectedAddress) && otherName._value == null)
        {
            name._value = name._value + " yo ho ho!";
            address._value = returnAddress;
            otherName._value = " yo ho ho!";
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  outMany_inoutMany_in1");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected name = out1_inoutMany_inMany");
            System.err.println ("actual name = " + name._value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address._value));
            throw new TestFailed ();
        }
    }


    public int outMany_inoutMany_inMany (StringHolder name, AddressHolder address, Phone phone, int otherNumber, StringHolder otherName) throws org.apache.axis.AxisFault, TestFailed
    {
        if (equals (phone, expectedPhone) && expectedNumber == otherNumber && "outMany_inoutMany_inMany".equals (name._value) && equals (address._value, expectedAddress) && otherName._value == null)
        {
            name._value = name._value + " yo ho ho!";
            address._value = returnAddress;
            otherName._value = " yo ho ho!";
            return returnNumber;
        }
        else
        {
            System.err.println ("Test failure:  outMany_inoutMany_inMany");
            System.err.println ("expected phone = " + printPhone (expectedPhone));
            System.err.println ("actual phone = " + printPhone (phone));
            System.err.println ("expected number = " + expectedNumber);
            System.err.println ("actual number = " + otherNumber);
            System.err.println ("expected name = outMany_inoutMany_inMany");
            System.err.println ("actual name = " + name._value);
            System.err.println ("expected address =\n" + printAddress (expectedAddress));
            System.err.println ("actual address =\n" + printAddress (address._value));
            throw new TestFailed ();
        }
    }
}
