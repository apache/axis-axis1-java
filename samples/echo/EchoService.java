/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package samples.echo ;

import java.util.*;

/**
 * Test implementation of the echo interop service.  Original description of
 * this was found at http://www.xmethods.net/ilab/ .  The current definition
 * can be found at http://www.whitemesa.com/interop.htm .
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */

public class EchoService {

    /**
     * This method accepts a single string and echoes it back to the client.
     */
    public String echoString(String input) {
        return input;    
    }

    /**
     * This method accepts an array of strings and echoes it back to the client.
     */
    public String[] echoStringArray(String[] input) {
        return input;
    }
    
    /**
     * This method accepts an single integer and echoes it back to the client.
     */
    public Integer echoInteger(Integer input) {
        return input;
    }

    /**
     * This method accepts an array of integers and echoes it back to the 
     * client.
     */
    public Integer[] echoIntegerArray(Integer[] input) {
        return input;
    }

    /**
     * This method accepts a single float and echoes it back to the client.
     */
    public Float echoFloat(Float input) {
        return input;
    }

    /**
     * This method accepts an array of floats and echoes it back to the client.
     */
    public Float[] echoFloatArray(Float[] input) {
        return input;
    }

    /**
     * This method accepts a single structure and echoes it back to the 
     * client.  
     */
    public SOAPStruct echoStruct(SOAPStruct input) {
        return input;
    }

    /**
     * This method accepts an array of structures and echoes it back to the 
     * client.  The structure used is the same defined in the description of 
     * the "echoStruct" method.
     */
    public SOAPStruct[] echoStructArray(SOAPStruct[] input) {
        return input;
    }

    /**
     * This method exists to test the "void" return case.  It accepts no 
     * arguments, and returns no arguments.
     */
    public void echoVoid() {
    }

    /**
     * This methods accepts a binary object and echoes it back to the client.
     */
    public byte[] echo(byte[] input) {
        return input;
    }

    /**
     * This method accepts a Date/Time and echoes it back to the client.
     */
    public Date echoDate(Date input) {
        return input;
    }
}
