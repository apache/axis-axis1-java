/**
 * LoggingFacilityLogPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.logging;

public interface LoggingFacilityLogPortType extends java.rmi.Remote {

    // Append an entry to the system log.
    public void logEvent(org.apache.axis.wsi.scm.logging.LogEventRequestType document) throws java.rmi.RemoteException;

    // Retrieve entries from the system log with the specified userId.
    public org.apache.axis.wsi.scm.logging.GetEventsResponseType getEvents(org.apache.axis.wsi.scm.logging.GetEventsRequestType document) throws java.rmi.RemoteException, org.apache.axis.wsi.scm.logging.GetEventsFaultType;
}
