/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

package org.apache.axis;

import org.apache.axis.client.Call;
import org.apache.axis.utils.Messages;

/**
 * Little utility to get the version and build date of the axis.jar.
 * 
 * The messages referenced here are automatically kept up-to-date by the
 * build.xml.
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 */ 
public class Version {
    public static String getVersion()
    {
        return Messages.getMessage("axisVersion") + "\n" +
               Messages.getMessage("builtOn");
    }
    
    /**
     *  Returns the Axis Version number and build date.
     *
     *  Example output: 1.1 Jul 08, 2003 (09:00:12 EDT)
     **/
    public static String getVersionText()
    {
        return Messages.getMessage("axisVersionRaw") + " " + Messages.getMessage("axisBuiltOnRaw");
    }

    /**
     * Entry point.
     * 
     * Calling this with no arguments returns the version of the client-side
     * axis.jar.  Passing a URL which points to a remote Axis server will
     * attempt to retrieve the version of the server via a SOAP call.
     */ 
    public static void main(String[] args) {
        if (args.length != 1)
            System.out.println(getVersion());
        else
            try {
                Call call = new Call(args[0]);
                String result = (String)call.invoke("Version", "getVersion", 
                                                    null);
                System.out.println(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
