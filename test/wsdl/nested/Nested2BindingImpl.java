/**
 * Nested2BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.nested;
import test.wsdl.nested.holders.PEADDRESSHolder;
import test.wsdl.nested.holders.RETURNHolder;

public class Nested2BindingImpl implements test.wsdl.nested.Nested2PortType {
    public void nestedSvc2(java.lang.String cUSTOMERNO, java.lang.String pIDISTRCHAN, java.lang.String pIDIVISION, java.lang.String pIPASSBUFFER, java.lang.String pISALESORG, PEADDRESSHolder pEADDRESS, RETURNHolder rETURN) throws java.rmi.RemoteException {
        PEADDRESS address = new PEADDRESS();
        address.setFORMOFAD("Company");
        address.setFIRSTNAME("");
        address.setNAME("Becker Berlin");
        address.setNAME3("");
        address.setNAME4("");
        address.setDATEBIRTH("0000-00-00");
        address.setSTREET("Calvinstr. 36");
        address.setPOSTLCODE("13467");
        address.setCITY("Berlin");
        address.setREGION("");
        address.setCOUNTRY("");
        address.setCOUNTRNISO("");
        address.setCOUNTRAISO("");
        address.setINTERNET("");
        address.setFAXNUMBER("030-8853-999");
        address.setTELEPHONE("030-8853-0");
        address.setTELEPHONE2("");
        address.setLANGU("D");
        address.setLANGUISO("DE");
        address.setCURRENCY("");
        address.setCURRENCYISO("");
        address.setCOUNTRYISO("DE");
        address.setONLYCHANGECOMADDRESS("X");

        RETURN ret = new RETURN();
        ret.setTYPE("");
        ret.setCODE("");
        ret.setMESSAGE("");
        ret.setLOGNO("");
        ret.setLOGMSGNO("123456");
        ret.setMESSAGEV1("");
        ret.setMESSAGEV2("");
        ret.setMESSAGEV3("");
        ret.setMESSAGEV4("");

        pEADDRESS.value = address;
        rETURN.value = ret;
    }

}
