/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *     "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *  not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *  nor may "Apache" appear in their name, without prior written
 *  permission of the Apache Software Foundation.
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

package org.apache.axis.handlers;

import java.io.* ;

import org.apache.axis.* ;
import org.apache.axis.utils.Debug ;
import org.apache.axis.utils.XMLUtils ;
import org.apache.axis.utils.AxisClassLoader ;
import org.apache.axis.providers.java.RPCProvider;

import sun.tools.javac.Main;

import org.w3c.dom.* ;

/**
 * This handler will use the MC_REALPATH property of the MsgContext to
 * locate a *.jws (JavaWebService) file.  If found it will copy it to a
 * *.java file, compile it and then run it using the RPCDispatchHandler.
 *
 * Todo:
 *   support msg instead of just rpc
 *   allow configurable handler (not just RPCDispatchHandler)
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class JWSProcessor extends BasicHandler
{
    static String errFile = "jws.err" ;

    public void invoke(MessageContext msgContext) throws AxisFault
    {
        Debug.Print( 1, "Enter: JWSProcessor::invoke" );
        try {
            /* Grab the *.jws filename from the context - should have been */
            /* placed there by another handler (ie. HTTPActionHandler)     */
            /***************************************************************/
            Runtime  rt      = Runtime.getRuntime();
            String   jwsFile = msgContext.getStrProp(Constants.MC_REALPATH);
            Debug.Print( 2, "jwsFile: " + jwsFile );
            String   jFile   = jwsFile.substring(0, jwsFile.length()-3) + "java" ;
            String   cFile   = jwsFile.substring(0, jwsFile.length()-3) + "class" ;
            Debug.Print( 2, "jFile: " + jFile );
            Debug.Print( 2, "cFile: " + cFile );

            File  f1 = new File( cFile );
            File  f2 = new File( jwsFile );

            /* Get the class */
            /*****************/
            String clsName = f2.getName();
            clsName = clsName.substring( 0, clsName.length()-4 );
            Debug.Print( 2, "ClsName: " + clsName );

            /* Check to see if we need to recompile */
            /****************************************/
            if ( !f1.exists() || f2.lastModified() > f1.lastModified() ) {
                /* If the class file doesn't exist, or it's older than the */
                /* java file then recompile the java file.                 */
                /* Start by copying the *.jws file to *.java               */
                /***********************************************************/
                Debug.Print(1, "Compiling: " + jwsFile );
                Debug.Print(3, "copy " + jwsFile + " " + jFile );
                FileReader fr = new FileReader( jwsFile );
                FileWriter fw = new FileWriter( jFile );
                char[] buf = new char[4096];
                int    rc ;
                while ( (rc = fr.read( buf, 0, 4095)) >= 0 )
                    fw.write( buf, 0, rc );
                fw.close();
                fr.close();

                /* Now run javac on the *.java file */
                /************************************/
                Debug.Print(2, "javac " + jFile );
                // Process proc = rt.exec( "javac " + jFile );
                // proc.waitFor();
                FileOutputStream  out      = new FileOutputStream( errFile );
                Main              compiler = new Main( out, "javac" );
                String            outdir   = f1.getParent();
                String[]          args     = null ;
                
                if (outdir == null) outdir=".";

                args = new String[] { "-d", outdir,
                          "-classpath",
                          System.getProperty("java.class.path" ),
                          jFile };
                boolean           result   = compiler.compile( args );

                /* Delete the temporary *.java file and check the return code */
                /**************************************************************/
                (new File(jFile)).delete();

                if ( !result ) {
                    /* Delete the *class file - sometimes it gets created even */
                    /* when there are errors - so erase it so it doesn't       */
                    /* confuse us.                                             */
                    /***********************************************************/
                    (new File(cFile)).delete();

                    Document doc = XMLUtils.newDocument();

                    Element         root = doc.createElement( "Errors" );
                    StringBuffer    sbuf = new StringBuffer();
                    FileReader      inp  = new FileReader( errFile );

                    buf = new char[4096];

                    while ( (rc = inp.read(buf, 0, 4096)) > 0 )
                        sbuf.append( buf, 0, rc );
                    inp.close();
                    root.appendChild( doc.createTextNode( sbuf.toString() ) );
                    (new File(errFile)).delete();
                    throw new AxisFault( "Server.compileError",
                        "Error while compiling: " + jFile,
                        null, new Element[] { root } );
                }
                (new File(errFile)).delete();

                AxisClassLoader.removeClassLoader( clsName );
            }
            AxisClassLoader cl = msgContext.getClassLoader( clsName );
            if ( !cl.isClassRegistered(clsName) )
                cl.registerClass( clsName, cFile );
            msgContext.setClassLoader( cl );

            if (msgContext.getProperty("is-http-get") != null) {
                Class c = cl.loadClass(clsName);
                msgContext.setProperty("JWSClass", c);
                return;
            }

            /* Create a new RPCProvider - this will be the "service"   */
            /* that we invoke.                                                */
            /******************************************************************/
            Handler rpc = new RPCProvider();
            msgContext.setServiceHandler( rpc );

            rpc.addOption( "className", clsName );
            
            /** For now, allow all methods - we probably want to have a way to
            * configure this in the future.
            */
            rpc.addOption( "methodName", "*");

            rpc.init();   // ??
            rpc.invoke( msgContext );
            rpc.cleanup();  // ??
        }
        catch( Exception e ) {
            Debug.Print( 1, e );
            if ( !(e instanceof AxisFault) ) e = new AxisFault( e );
            throw (AxisFault) e ;
        }

        Debug.Print( 1, "Exit : JWSProcessor::invoke" );
    }

    public void undo(MessageContext msgContext)
    {
        Debug.Print( 1, "Enter: JWSProcessor::undo" );
        Debug.Print( 1, "Exit: JWSProcessor::undo" );
    }
}
