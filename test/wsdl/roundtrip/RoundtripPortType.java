/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package test.wsdl.roundtrip;



/**
 * The RoundtripPortType interface defines the methods necessary when
 * when implementing this interface.  
 *
 * @version   1.00  06 Feb 2002
 * @author    Brent Ulbricht
 * @author    Rich Scheuerle
 */
// Some of the methods are inherited, to test inherted interface processing
public interface RoundtripPortType extends RoundtripPortTypeA, RoundtripPortTypeB {

    public boolean methodBoolean(boolean inBoolean)
        throws java.rmi.RemoteException;
    public byte methodByte(byte inByte)
        throws java.rmi.RemoteException;
    public short methodShort(short inShort)
        throws java.rmi.RemoteException;
    public int methodInt(int inInt)
        throws java.rmi.RemoteException;
    public long methodLong(long inLong)
        throws java.rmi.RemoteException;
    public float methodFloat(float inFloat)
        throws java.rmi.RemoteException;
    public double methodDouble(double inDouble)
        throws java.rmi.RemoteException;
} // RoundtripPortType

