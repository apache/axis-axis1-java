/**
 * Nested2BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.nested;
import org.apache.axis.message.MessageElement;
import test.wsdl.nested.holders._PE_ADDRESSHolder;
import test.wsdl.nested.holders._RETURNHolder;

import javax.xml.namespace.QName;

public class Nested2BindingImpl implements test.wsdl.nested.Nested2PortType {
    public void nestedSvc2(java.lang.String cUSTOMERNO, java.lang.String pIDISTRCHAN, java.lang.String pIDIVISION, java.lang.String pIPASSBUFFER, java.lang.String pISALESORG, _PE_ADDRESSHolder _PE_ADDRESS, _RETURNHolder rETURN) throws java.rmi.RemoteException {
        _PE_ADDRESS address = new _PE_ADDRESS();
        address.setFORM_OF_AD("Company");
        address.setFIRST_NAME("");
        address.setNAME("Becker Berlin");
        address.setNAME_3("");
        address.setNAME_4("");
        address.setDATE_BIRTH("0000-00-00");
        address.setSTREET("Calvinstr. 36");
        address.setPOSTL_CODE("13467");
        address.setCITY("Berlin");
        address.setREGION("");
        address.setCOUNTRY("");
        address.setCOUNTRNISO("");
        address.setCOUNTRAISO("");
        address.setINTERNET("");
        address.setFAX_NUMBER("030-8853-999");
        address.setTELEPHONE("030-8853-0");
        address.setTELEPHONE2("");
        address.setLANGU("D");
        address.setLANGU_ISO("DE");
        address.setCURRENCY("");
        address.setCURRENCY_ISO("");
        address.setCOUNTRYISO("DE");
        address.setONLY_CHANGE_COMADDRESS("X");
        MessageElement me = new MessageElement(new QName("foo", "bar"),
                                               "Test Any");
        address.set_any(new MessageElement [] { me });

        _RETURN ret = new _RETURN();
        ret.setTYPE("");
        ret.setCODE("");
        ret.setMESSAGE("");
        ret.setLOG_NO("");
        ret.setLOG_MSG_NO("123456");
        ret.setMESSAGE_V1("");
        ret.setMESSAGE_V2("");
        ret.setMESSAGE_V3("");
        ret.setMESSAGE_V4("");

        _PE_ADDRESS.value = address;
        rETURN.value = ret;
    }

}
