/*
 * Copyright 2002-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.wsdl.interop3.groupE;

/**
 * This test is part of the SOAP Builders round III interoperability testing
 * effort described at http://www.whitemesa.net/r3/plan.html.
 *
 * The test is in group E which requires a service (this class) coding by hand
 * which implements the operations, parameters, and binding style/use described
 * in a WSDL file available from the above web site. The WSDL file is used
 * only as a pseudo-code description of the service.
 *
 * Next WSDL is generated from the service and the WSDL used to create a
 * client which is then used to invoke the service. Other vendors should
 * also be able to use the same, generated WSDL to invoke the service.
 *
 * This interface is a JAX-RPC service definition interface as defined
 * by the JAX-RPC spec., especially chapter 5.
 *
 * @author Glyn Normington <glyn@apache.org> 
 */
public interface InteropTestList extends java.rmi.Remote {

    public List echoLinkedList(List param0) throws java.rmi.RemoteException;
}
