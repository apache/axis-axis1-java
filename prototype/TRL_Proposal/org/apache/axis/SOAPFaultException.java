package org.apache.axis;

import org.apache.axis.SOAPException;

public class SOAPFaultException extends SOAPException {
    MessageContext faultMessage ;
    public SOAPFaultException() { }

    public SOAPFaultException(MessageContext faultMessage) {
        super("A SOAP Fault occured") ;
        setFaultMessage(faultMessage) ;
    }

    public void setFaultMessage(MessageContext faultMessage) {
        this.faultMessage = faultMessage ;
    }
    public MessageContext getFaultMessage() { return faultMessage ; }

    public String toString() {
        return (super.toString() + "\nThe SOAP Fault message contained in the exception is as follows:\n" + faultMessage) ;
    }
}
