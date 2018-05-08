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

package org.apache.axis.handlers;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Scope;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.utils.ClasspathUtils;
import org.apache.axis.utils.JWSClassLoader;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/** A <code>JWSHandler</code> sets the target service and JWS filename
 * in the context depending on the JWS configuration and the target URL.
 *
 * @author Glen Daniels (gdaniels@allaire.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 */
public class JWSHandler extends BasicHandler
{
    protected static Log log =
        LogFactory.getLog(JWSHandler.class.getName());

    public final String OPTION_JWS_FILE_EXTENSION = "extension";
    public final String DEFAULT_JWS_FILE_EXTENSION = Constants.JWS_DEFAULT_FILE_EXTENSION;

    private final Map/*<String,SOAPService>*/ soapServices = new HashMap();
    private final Map/*<String,ClassLoader>*/ classloaders = new Hashtable();

    /**
     * Just set up the service, the inner service will do the rest...
     */ 
    public void invoke(MessageContext msgContext) throws AxisFault
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: JWSHandler::invoke");
        }

        try {
            setupService(msgContext);
        } catch (Exception e) {
            log.error( Messages.getMessage("exception00"), e );
            throw AxisFault.makeFault(e);
        }
    }
    
    /**
     * If our path ends in the right file extension (*.jws), handle all the
     * work necessary to compile the source file if it needs it, and set
     * up the "proxy" RPC service surrounding it as the MessageContext's
     * active service.
     *
     */ 
    protected void setupService(MessageContext msgContext) throws Exception {
        // FORCE the targetService to be JWS if the URL is right.
        String realpath = msgContext.getStrProp(Constants.MC_REALPATH);
        String extension = (String)getOption(OPTION_JWS_FILE_EXTENSION);
        if (extension == null) extension = DEFAULT_JWS_FILE_EXTENSION;
        
        if ((realpath!=null) && (realpath.endsWith(extension))) {
            /* Grab the *.jws filename from the context - should have been */
            /* placed there by another handler (ie. HTTPActionHandler)     */
            /***************************************************************/
            String   jwsFile = realpath;
            String rel = msgContext.getStrProp(Constants.MC_RELATIVE_PATH);

            // Check for file existance, report error with
            // relative path to avoid giving out directory info.
            File  f2 = new File( jwsFile );
            if (!f2.exists()) {
                throw new FileNotFoundException(rel);
            }

            if (rel.charAt(0) == '/') {
                rel = rel.substring(1);
            }

            int lastSlash = rel.lastIndexOf('/');
            String dir = null;
            
            if (lastSlash > 0) {
                dir = rel.substring(0, lastSlash);
            }
            
            String file = rel.substring(lastSlash + 1);
            
            String outdir = msgContext.getStrProp( Constants.MC_JWS_CLASSDIR );
            if ( outdir == null ) outdir = "." ;
            
            // Build matching directory structure under the output
            // directory.  In other words, if we have:
            //    /webroot/jws1/Foo.jws
            //
            // That will be compiled to:
            //    .../jwsOutputDirectory/jws1/Foo.class
            if (dir != null) {
                outdir = outdir + File.separator + dir;
            }
            
            // Confirm output directory exists.  If not, create it IF we're
            // allowed to.
            // !!! TODO: add a switch to control this.
            File outDirectory = new File(outdir);
            if (!outDirectory.exists()) {
                outDirectory.mkdirs();
            }
            
            if (log.isDebugEnabled())
                log.debug("jwsFile: " + jwsFile );
            
            String   jFile   = outdir + File.separator + file.substring(0, file.length()-extension.length()+1) +
                    "java" ;
            String   cFile   = outdir + File.separator + file.substring(0, file.length()-extension.length()+1) +
                    "class" ;
            
            if (log.isDebugEnabled()) {
                log.debug("jFile: " + jFile );
                log.debug("cFile: " + cFile );
                log.debug("outdir: " + outdir);
            }
            
            File  f1 = new File( cFile );

            /* Get the class */
            /*****************/
            String clsName = null ;
            //clsName = msgContext.getStrProp(Constants.MC_RELATIVE_PATH);
            if ( clsName == null ) clsName = f2.getName();
            if ( clsName != null && clsName.charAt(0) == '/' )
                clsName = clsName.substring(1);
            
            clsName = clsName.substring( 0, clsName.length()-extension.length() );
            clsName = clsName.replace('/', '.');
            
            if (log.isDebugEnabled())
                log.debug("ClsName: " + clsName );
            
            /* Check to see if we need to recompile */
            /****************************************/
            if ( !f1.exists() || f2.lastModified() > f1.lastModified() ) {
                /* If the class file doesn't exist, or it's older than the */
                /* java file then recompile the java file.                 */
                /* Start by copying the *.jws file to *.java               */
                /***********************************************************/
                log.debug(Messages.getMessage("compiling00", jwsFile) );
                log.debug(Messages.getMessage("copy00", jwsFile, jFile) );
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
                log.debug("javac " + jFile );
                // Process proc = rt.exec( "javac " + jFile );
                // proc.waitFor();
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
                
                fileManager.setLocation(StandardLocation.CLASS_PATH, ClasspathUtils.getDefaultClasspath(msgContext));
                fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(new File(outdir)));
                
                DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
                CompilationTask task = compiler.getTask(null, fileManager, diagnosticCollector, null, null,
                        fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(new File(jFile))));
                
                boolean result = task.call();
                
                /* Delete the temporary *.java file and check return code */
                /**********************************************************/
                (new File(jFile)).delete();
                
                if ( !result ) {
                    /* Delete the *class file - sometimes it gets created even */
                    /* when there are errors - so erase it so it doesn't       */
                    /* confuse us.                                             */
                    /***********************************************************/
                    (new File(cFile)).delete();
                    
                    Document doc = XMLUtils.newDocument();
                    
                    Element         root = doc.createElementNS("", "Errors");
                    StringBuffer message = new StringBuffer("Error compiling ");
                    message.append(jFile);
                    message.append(":");

                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticCollector.getDiagnostics()) {
                        if (diagnostic.getKind() == Kind.ERROR) {
                            message.append("\nLine ");
                            message.append(diagnostic.getLineNumber());
                            message.append(", column ");
                            message.append(diagnostic.getStartPosition());
                            message.append(": ");
                            message.append(diagnostic.getMessage(null));
                        }
                    }
                    root.appendChild( doc.createTextNode( message.toString() ) );
                    throw new AxisFault( "Server.compileError",
                                         Messages.getMessage("badCompile00", jFile),
                                         null, new Element[] { root } );
                }
                classloaders.remove( clsName );
                // And clean out the cached service.
                soapServices.remove(clsName);
            }
            
            ClassLoader cl = (ClassLoader)classloaders.get(clsName);
            if (cl == null) {
                cl = new JWSClassLoader(clsName,
                                        msgContext.getClassLoader(),
                                        cFile);
                classloaders.put(clsName, cl);
            }
            
            msgContext.setClassLoader(cl);
            
            /* Create a new RPCProvider - this will be the "service"   */
            /* that we invoke.                                                */
            /******************************************************************/
            // Cache the rpc service created to handle the class.  The cache
            // is based on class name, so only one .jws/.jwr class can be active
            // in the system at a time.
            SOAPService rpc = (SOAPService)soapServices.get(clsName);
            if (rpc == null) {
                rpc = new SOAPService(new RPCProvider());
                rpc.setName(clsName);
                rpc.setOption(RPCProvider.OPTION_CLASSNAME, clsName );
                rpc.setEngine(msgContext.getAxisEngine());
                
                // Support specification of "allowedMethods" as a parameter.
                String allowed = (String)getOption(RPCProvider.OPTION_ALLOWEDMETHODS);
                if (allowed == null) allowed = "*";
                rpc.setOption(RPCProvider.OPTION_ALLOWEDMETHODS, allowed);
                // Take the setting for the scope option from the handler
                // parameter named "scope"
                String scope = (String)getOption(RPCProvider.OPTION_SCOPE);
                if (scope == null) scope = Scope.DEFAULT.getName();
                rpc.setOption(RPCProvider.OPTION_SCOPE, scope);
                
                rpc.getInitializedServiceDesc(msgContext);
                
                soapServices.put(clsName, rpc);                
            }
            
            // Set engine, which hooks up type mappings.
            rpc.setEngine(msgContext.getAxisEngine());
            
            rpc.init();   // ??

            // OK, this is now the destination service!
            msgContext.setService( rpc );
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: JWSHandler::invoke");
        }
    }
    
    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        try {
            setupService(msgContext);
        } catch (Exception e) {
            log.error( Messages.getMessage("exception00"), e );
            throw AxisFault.makeFault(e);
        }
    }
}
