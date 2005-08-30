/**
 * TestdateSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2alpha Dec 06, 2003 (10:46:24 EST) WSDL2Java emitter.
 */

package test.wsdl.date;

import java.util.*;

public class TestdateSoapBindingImpl implements test.wsdl.date.MyService{
    public test.wsdl.date.MyBean getInfo() throws java.rmi.RemoteException {
        MyBean b = new MyBean();
        Calendar xmas = new GregorianCalendar(1998, Calendar.DECEMBER, 25);
        Date date = xmas.getTime();
        b.setDate(new Date());
        return b;
    }

}
