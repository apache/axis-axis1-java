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

package org.apache.axis.utils ;

/**
 * General purpose command line options parser.
 * If this is used outside of Axis just remove the Axis specific sections.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */

import org.apache.log4j.Category;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Priority;

import java.net.* ;
import java.util.* ;

public class Options {
    static Category category =
            Category.getInstance(Options.class.getName());

    static {
        BasicConfigurator.configure();
        Category.getRoot().setPriority(Priority.FATAL);
    }

    String  args[] = null ;

    //////////////////////////////////////////////////////////////////////////
    // SOASS (Start of Axis Specific Stuff)

    String  host ;      // -h    also -l (url)
    String  port ;      // -p
    String  servlet ;   // -s    also -f (file)
    String  protocol ;

    String  user ;      // -u
    String  passwd ;    // -w

    // EOASS
    //////////////////////////////////////////////////////////////////////////

    /**
     * Constructor - just pass in the <b>args</b> from the command line.
     */
    public Options(String _args[]) throws MalformedURLException {
        args = _args ;
        
        ///////////////////////////////////////////////////////////////////////
        // SOASS

        /* Process these well known options first */
        /******************************************/
        try {
            getURL();
        } catch( MalformedURLException e ) {
            category.error( "getURL failed to correctly process URL; protocol not " +
                            "supported" );
            throw e ;
        }
        getUser();
        getPassword();

        // EOASS
        ///////////////////////////////////////////////////////////////////////
    }

    /**
     * Returns an int specifying the number of times that the flag was
     * specified on the command line.  Once this flag is looked for you
     * must save the result because if you call it again for the same
     * flag you'll get zero.
     */
    public int isFlagSet(char optChar) {
        int  value = 0 ;
        int  loop ;
        int  i ;

        for ( loop = 0 ; loop < args.length ; loop++ ) {
            if ( args[loop] == null || args[loop].length() == 0 ) continue ;
            if ( args[loop].charAt(0) != '-' ) continue ;
            while ( args[loop] != null && (i = args[loop].indexOf(optChar)) != -1 ) {
                args[loop] = args[loop].substring(0, i) + args[loop].substring(i+1) ;
                if ( args[loop].length() == 1 ) 
                    args[loop] = null ;
                value++ ;
            }
        }
        return( value );
    }

    /**
     * Returns a string (or null) specifying the value for the passed
     * option.  If the option isn't there then null is returned.  The
     * option's value can be specified one of two ways:
     *   -x value
     *   -xvalue
     * Note that:  -ax value
     * is not value (meaning flag 'a' followed by option 'x'.
     * Options with values must be the first char after the '-'.
     * If the option is specified more than once then the last one wins.
     */
    public String isValueSet(char optChar) {
        String  value = null ;
        int     loop ;
        int     i ;

        for ( loop = 0 ; loop < args.length ; loop++ ) {
            if ( args[loop] == null || args[loop].length() == 0 ) continue ;
            if ( args[loop].charAt(0) != '-' ) continue ;
            i = args[loop].indexOf( optChar );
            if ( i != 1 ) continue ;
            if ( i != args[loop].length()-1 ) {
                // Not at end of arg, so use rest of arg as value 
                value = args[loop].substring(i+1) ;
                args[loop] = args[loop].substring(0, i);
            }
            else {
                // Remove the char from the current arg
                args[loop] = args[loop].substring(0, i);

                // Nothing after char so use next arg
                if ( loop+1 < args.length && args[loop+1] != null ) {
                    // Next arg is there and non-null
                    if ( args[loop+1].charAt(0) != '-' ) {
                        value = args[loop+1];
                        args[loop+1] = null ;
                    }
                }
                else {
                    // Next is null or not there - do nothing
                    // value = null ;
                }
            }
            if ( args[loop].length() == 1 ) 
                args[loop] = null ;
            // For now, keep looping to get that last on there
            // break ; 
        }
        return( value );
    }

    /**
     * This just returns a string with the unprocessed flags - mainly
     * for error reporting - so you can report the unknown flags.
     */
    public String getRemainingFlags() {
        StringBuffer sb = null ;
        int          loop ;

        for ( loop = 0 ; loop < args.length ; loop++ ) {
            if ( args[loop] == null || args[loop].length() == 0 ) continue ;
            if ( args[loop].charAt(0) != '-' ) continue ;
            if ( sb == null ) sb = new StringBuffer();
            sb.append( args[loop].substring(1) );
        }
        return( sb == null ? null : sb.toString() );
    }

    /**
     * This returns an array of unused args - these are the non-option
     * args from the command line.
     */
    public String[] getRemainingArgs() {
        ArrayList  al = null ;
        int        loop ;

        for ( loop = 0 ; loop < args.length ; loop++ ) {
            if ( args[loop] == null || args[loop].length() == 0 ) continue ;
            if ( args[loop].charAt(0) == '-' ) continue ;
            if ( al == null ) al = new ArrayList();
            al.add( (String) args[loop] );
        }
        if ( al == null ) return( null );
        String a[] = new String[ al.size() ];
        for ( loop = 0 ; loop < al.size() ; loop++ )
            a[loop] = (String) al.get(loop);
        return( a );
    }

    //////////////////////////////////////////////////////////////////////////
    // SOASS
    public String getURL() throws MalformedURLException {
        String  tmp ;
        URL     url = null ;
        
        // Just in case...
        org.apache.axis.client.ServiceClient.initialize();

        if ( (tmp = isValueSet( 'l' )) != null ) {
            url = new URL( tmp );
            host = url.getHost();
            port = "" + url.getPort();
            servlet = url.getFile();
            protocol = url.getProtocol();
        }

        if ( (tmp = isValueSet( 'f' )) != null ) {
            host = "";
            port = "-1";
            servlet = tmp;
            protocol = "file";
        }

        tmp = isValueSet( 'h' ); if ( host == null ) host = tmp ;
        tmp = isValueSet( 'p' ); if ( port == null ) port = tmp ;
        tmp = isValueSet( 's' ); if ( servlet == null ) servlet = tmp ;

        if ( host == null ) host = "localhost" ;
        if ( port == null ) port = "8080" ;
        if ( servlet == null ) servlet = "/axis/servlet/AxisServlet" ;
        else
            if ( servlet.length()>0 && servlet.charAt(0)!='/' ) 
                servlet = "/" + servlet ;

        if (url == null) {
            if (protocol == null) protocol = "http";
            tmp = protocol + "://" + host ;
            if ( port != null && !port.equals("-1")) tmp += ":" + port ;
            if ( servlet != null ) tmp += servlet ;
        } else tmp = url.toString();
        category.debug( "getURL returned: " + tmp );
        return( tmp );
    }
    
    public int getPort() {
        return Integer.parseInt(port);
    }

    public String getUser() {
        if ( user == null ) user = isValueSet( 'u' );
        return( user );
    }

    public String getPassword() {
        if ( passwd == null ) passwd = isValueSet( 'w' );
        return( passwd );
    }
    // EOASS
    //////////////////////////////////////////////////////////////////////////
}
