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

package samples.attachments;


import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.utils.Options;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.namespace.QName;

import java.net.URL;


/**
 *
 * @author Rick Rineholt 
 */

/**
 * An example of sending an attachment via RPC.
 * This class has a main method that beside the standard arguments
 * allows you to specify an attachment that will be sent to a 
 * service which will then send it back.
 *  WORK IN PROGRESS!
 *  
 */
public class EchoAttachment {

    Options opts = null;

    EchoAttachment( Options opts) {
        this.opts = opts;
    }

    /**
     * This method sends a file as an attachment then 
     *  receives it as a return.  The returned file is
     *  compared to the source.
     *  @param The filename that is the source to send.
     *  @return True if sent and compared.
     */
    public boolean echo(String filename) throws Exception {

        //javax.activation.MimetypesFileTypeMap map= (javax.activation.MimetypesFileTypeMap)javax.activation.MimetypesFileTypeMap.getDefaultFileTypeMap();
        //map.addMimeTypes("application/x-org-apache-axis-wsdd wsdd");


        //Create the data for the attached file.
        DataHandler dhSource = new DataHandler( new FileDataSource( filename ));

        Service  service = new Service();

        Call     call    = (Call) service.createCall();

        call.setTargetEndpointAddress( new URL(opts.getURL()) ); //Set the target service host and service location, 

        call.setOperationName(new QName("urn:EchoAttachmentsService","echo") ); //This is the target services method to invoke.

        QName qnameAttachment = new QName("urn:EchoAttachmentsService", "DataHandler");

        call.registerTypeMapping(dhSource.getClass(),//Add serializer for attachment. 
                                 qnameAttachment,
                                 JAFDataHandlerSerializerFactory.class,
                                 JAFDataHandlerDeserializerFactory.class);

                                 
        call.addParameter( "source", qnameAttachment,
                ParameterMode.IN ); //Add the file.

        call.setReturnType( qnameAttachment);

        call.setUsername( opts.getUser());

        call.setPassword( opts.getPassword() );


        Object ret = call.invoke( new Object[] {
                    dhSource
                } 
            ); //Add the attachment.

        if (null == ret) {
            System.out.println("Received null ");
            throw new AxisFault("", "Received null", null, null);
        }

        if (ret instanceof String) {
            System.out.println("Received problem response from server: " + ret);
            throw new AxisFault("", (String) ret, null, null);
        }

        if (!(ret instanceof DataHandler)) {
            //The wrong type of object that what was expected.
            System.out.println("Received problem response from server:" +
                ret.getClass().getName());
            throw new AxisFault("", "Received problem response from server:" +
                    ret.getClass().getName(), null, null);

        }
        //Still here, so far so good.
        //Now lets brute force compare the source attachment
        // to the one we received.
        DataHandler rdh = (DataHandler) ret;

        //From here we'll just treat the data resource as file.
        String receivedfileName = rdh.getName();//Get the filename. 

        if ( receivedfileName == null) {
            System.err.println("Could not get the file name.");
            throw new AxisFault("", "Could not get the file name.", null, null);
        }


        System.out.println("Going to compare the files..");
        boolean retv = compareFiles( filename,  receivedfileName);

        java.io.File receivedFile = new java.io.File(receivedfileName);

        receivedFile.delete();

        return retv;
    }

    /**
     * This method sends all the files in a directory. 
     *  @param The directory that is the source to send.
     *  @return True if sent and compared.
     */
    public boolean echoDir(String filename) throws Exception {
        boolean rc = true;


        DataHandler[] attachments = getAttachmentsFromDir(filename); //Get the attachments from the directory.

        if ( attachments.length == 0) {
            throw new java.lang.IllegalArgumentException(
                    "The directory \"" + filename + "\" has no files to send.");
        }

        Service  service = new Service(); //A new axis Service.

        Call     call    = (Call) service.createCall(); //Create a call to the service.

        call.setTargetEndpointAddress( new URL(opts.getURL()) ); //Set the target service host and service location, 

        call.setOperationName( new QName("urn:EchoAttachmentsService", "echoDir") ); //This is the target services method to invoke.

        QName qnameAttachment = new QName("urn:EchoAttachmentsService", "DataHandler");

        call.registerTypeMapping(attachments[0].getClass(),//Add serializer for attachment. 
                                 qnameAttachment,
                                 JAFDataHandlerSerializerFactory.class,
                                 JAFDataHandlerDeserializerFactory.class);

        call.addParameter( "source",   XMLType.SOAP_ARRAY , // new XMLType(qnameAttachment),
            ParameterMode.IN ); //Add the file.

        call.setReturnType(XMLType.SOAP_ARRAY); // new XMLType(qnameAttachment));

        call.setUsername( opts.getUser());

        call.setPassword( opts.getPassword() );

        Object ret = call.invoke( new Object[] {
                    attachments
                } 
            ); //Add the attachment.

        if (null == ret) {
            System.out.println("Received null ");
            throw new AxisFault("", "Received null", null, null);
        }

        if (ret instanceof String) {
            System.out.println("Received problem response from server: " + ret);
            throw new AxisFault("", (String) ret, null, null);
        }

        if (!(ret instanceof javax.activation.DataHandler[])) {
            //The wrong type of object that what was expected.
            System.out.println("Received unexpected type :" +
                ret.getClass().getName());
            throw new AxisFault("", "Received unexpected type:" +
                    ret.getClass().getName(), null, null);

        }
        //Still here, so far so good.
        //Now lets brute force compare the source attachment
        // to the one we received.
        javax.activation.DataHandler[] received = (javax.activation.DataHandler[] ) ret;

        int i=0;
        for (  i=0; i< received.length && i < attachments.length; ++i ) {
            DataHandler recDH =  received[i];
            DataHandler orginalDH = attachments[i];

            if (!compareFiles( filename + java.io.File.separator + orginalDH.getName(), recDH.getName())) {
                System.err.println("The attachment with the file name: \"" + orginalDH.getName() +
                    "\" was not received the same!.");
                rc = false;
            }
            java.io.File receivedFile = new java.io.File(recDH.getName());

            receivedFile.delete();
        }

        if (i < received.length) {
            System.err.println("There are more file received than sent!!!!");

            rc = false;
        }
        if ( i < attachments.length ) {
            System.err.println("Not all the files were received!");
            rc = false;
        }


        return rc;
    }

    /**
     * Give a single file to send or name a directory
     * to send an array of attachments of the files in
     * that directory.
     */
    public static void main(String args[]) {
        try {

            Options opts = new Options(args);
            EchoAttachment echoattachment = new EchoAttachment(opts);

            args = opts.getRemainingArgs();

            if(args == null || args.length == 0){
                System.err.println("Need a file or directory argument.");
                System.exit(8);
            }

            String argFile = args[0];

            java.io.File source = new java.io.File(argFile);

            if(!source.exists()){
                System.err.println("Error \""+ argFile + "\" does not exist!");
                System.exit(8);
            }

            if (source.isFile()) {
                if (echoattachment.echo(args[0])) {
                    System.out.println("Attachment sent and received ok!");
                    System.exit(0);
                }
                else {
                    System.err.println("Problem in matching attachments");
                    System.exit(8);
                }
            }
            else { //a directory?
                if (echoattachment.echoDir(args[0])) {
                    System.out.println("Attachments sent and received ok!");
                    System.exit(0);
                }
                else {
                    System.err.println("Problem in matching attachments");
                    System.exit(8);
                }
            }
        }
        catch ( Exception e ) {
            if ( e instanceof AxisFault ) {
                ((AxisFault) e).dump();
            } 
            System.err.println(e);
             e.printStackTrace();
        }
        System.exit(18);
    }

    /**
     * Quick and unsophisticated method to compare two file's
     * byte stream.
     * @param The first file to compare.
     * @param The second file to compare.
     * @return True if the bytestreams do compare, false for
     *    any other reason.
     */
    protected boolean compareFiles( String one, String other )
    throws java.io.FileNotFoundException, java.io.IOException {

        java.io.BufferedInputStream oneStream = null;
        java.io.BufferedInputStream otherStream = null;

        try {
            oneStream = new java.io.BufferedInputStream(
                new java.io.FileInputStream(one), 1024 * 64);
            otherStream = new java.io.BufferedInputStream(
                new java.io.FileInputStream(other), 1024 * 64);

            byte[] bufOne = new byte[1024 * 64];
            byte[] bufOther = new byte[1024 * 64];
            int breadOne = -1;
            int breadOther = -1;
            int available = 0;

            do {
                available = oneStream.available();
                available = Math.min( available, otherStream.available());
                available = Math.min( available, bufOther.length );

                if (0 != available) {
                    java.util.Arrays.fill( bufOne, (byte) 0);
                    java.util.Arrays.fill( bufOther, (byte) 0);

                    breadOne = oneStream.read(bufOne, 0, available);
                    breadOther = otherStream.read(bufOther, 0, available);
                    if (breadOne != breadOther) throw new RuntimeException(
                                "Sorry couldn't really read whats available!");
                    if (!java.util.Arrays.equals( bufOne, bufOther)) {
                        return false;
                    }
                }

            }
            while ( available != 0 && breadOne != -1 && breadOther != -1);
            if ( available != 0 && (breadOne != -1 || breadOther != -1)) {
                return false;
            }
            return true;
        }
        finally {
            if (null != oneStream) oneStream.close();
            if (null != otherStream) otherStream.close();
        }
    }

    /**
     *  Return an array of datahandlers for each file in the dir. 
     *  @param the name of the directory
     *  @return return an array of datahandlers.
     */

    protected DataHandler[] getAttachmentsFromDir(String dirName) {
        java.util.LinkedList retList = new java.util.LinkedList();
        DataHandler[] ret = new DataHandler[0];// empty

        java.io.File sourceDir = new java.io.File(dirName);

        java.io.File[] files = sourceDir.listFiles();

        for ( int i = files.length - 1; i >= 0; --i) {
            java.io.File cf = files[i];

            if (cf.isFile() && cf.canRead()) {
                String fname = null;

                try {
                    fname = cf.getAbsoluteFile().getCanonicalPath();
                }
                catch ( java.io.IOException e) {
                    System.err.println("Couldn't get file \"" + fname + "\" skipping...");
                    continue;
                }
                retList.add( new DataHandler( new FileDataSource( fname )));
            }
        }
        if (!retList.isEmpty()) {
            ret = new DataHandler[ retList.size()];
            ret = (DataHandler[]) retList.toArray(ret);
        }

        return ret;
    }
}
