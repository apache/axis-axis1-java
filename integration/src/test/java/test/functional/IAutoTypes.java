/*
 * Copyright 2002,2004 The Apache Software Foundation.
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

package test.functional;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Interface which will be used as the client side proxy 
 * view of the Web Service.
 *
 * @author Patrick Martin
 */
public interface IAutoTypes extends Remote {
    String ping() throws RemoteException;
    SimpleBean getBean() throws RemoteException;
    void setBean(SimpleBean bean) throws RemoteException;
    SimpleBean echoBean(SimpleBean bean) throws RemoteException;
    SimpleBean[] getBeanArray(int count) throws RemoteException;
    void setBeanArray(SimpleBean[] beans) throws RemoteException;
    SimpleBean[] echoBeanArray(SimpleBean[] beans) throws RemoteException;
    ArrayList getBeanArrayList(int count) throws RemoteException;
    int setBeanArrayList(ArrayList beansArr) throws RemoteException;
    NestedBean getNestedBean() throws RemoteException;
    void setNestedBean(NestedBean value) throws RemoteException;
    NestedBean echoNestedBean(NestedBean value) throws RemoteException;
}
