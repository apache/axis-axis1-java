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
package org.apache.axis.wsdl;

/**
 * Command line interface to the wsdl2java utility
 *
 * @author Tom Jordahl (tjordahl@macromedia.com)
 */
public class Wsdl2java
{
    /**
     * print usage message
     */
    private static void usage() {
        System.out.println ("Usage: java org.apache.axis.wsdl.Wsdl2java [-verbose] [-skeleton [-messageContext]] WSDL-URI");
        System.out.println ("Switches:");
        System.out.println ("   -verbose - Turn on verbose output");
        System.out.println ("   -skeleton - emit skeleton class for web service");
        System.out.println ("   -messageContext - emit a MessageContext parameter in skeleton");
        System.exit(-1);
    }


    public static void main(String[] args) {
        try {
            boolean bSkeleton, bVerbose, bMessageContext;
            bSkeleton = bVerbose = bMessageContext = false;

            int argcount = args.length;
            int arg = 0;
            while ( arg < (args.length-1)) {
                if ( args[arg].startsWith("-v")) {
                    bVerbose = true;
                    --argcount;
                }
               if( args[arg].startsWith("-skel") )      {
                    bSkeleton = true;
                   --argcount;
                }
               if( args[arg].startsWith("-messageContext") )        {
                    bMessageContext = true;
                   --argcount;
                }
                ++arg;
            }
            if (argcount != 1 )
                usage();

            String uri = args[arg];
            if (uri.startsWith("-"))
                usage();
            if (bMessageContext && !bSkeleton) {
                System.out.println("Error: -messageContext switch only valid with -skeleton");
                usage();
            }

            Emitter emitter = new Emitter ();
            emitter.verbose(bVerbose);
            emitter.generateSkeleton(bSkeleton);
            emitter.generateMessageContext(bMessageContext);
            emitter.emit (uri);
        }
        catch (Throwable t) {
            t.printStackTrace ();
        }
    } // main

}
