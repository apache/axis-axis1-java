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
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.utils.Options;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.message.SOAPBodyElement;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.rpc.ParameterMode;
import javax.xml.namespace.QName;
import javax.activation.DataHandler;

import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.io.ByteArrayInputStream;
import java.io.File;
import org.w3c.dom.*;


/**
 *
 * @author Rick Rineholt 
 */

/**
 * An example of sending an attachment via messages.
 *  The main purpose is to validate the different types of attachment references
 *  by content Id, content location both absolute and relative.
 * 
 *  Creates 5 separate attachments referenced  differently by a SOAP document.
 *  Each attachment contains a string that is assembled and tested to see if
 *  if the attachments are correctly sent and referenced.  Each attachment also
 *  contains a mime header indicating its position and validated on the server
 *  to see if mime headers are correctly sent with attachments.
 *
 *  Sends the same message again however the second attachments are placed in the
 *  stream in reverse to see if they are still referenced ok.
 *
 *
 *  The return SOAP document references a single attachment which is the a Java
 *  serialized vector holding strings to the individual attachments sent.
 *
 *  Demos using attachments directly.
 *  
 */
public class TestRef {

    Options opts = null;
    public static final String positionHTTPHeader="Ordinal";
    public static final String TheKey= "Now is the time for all good men to come to the aid of their country.";


    public TestRef( Options opts) {
        this.opts = opts;
    }


    /**
     * This method sends all the files in a directory. 
     *  @param The directory that is the source to send.
     *  @return True if sent and compared.
     */
    public boolean testit() throws Exception {
        boolean rc = true;
        String baseLoc= "http://axis.org/attachTest";
        Vector refs= new Vector();  //holds a string of references to attachments.

        Service  service = new Service(); //A new axis Service.

        Call     call    = (Call) service.createCall(); //Create a call to the service.

        /*Un comment the below statement to do HTTP/1.1 protocol*/
      //call.setScopedProperty(MessageContext.HTTP_TRANSPORT_VERSION,HTTPConstants.HEADER_PROTOCOL_V11);
        Hashtable myhttp= new Hashtable();
        myhttp.put(HTTPConstants.HEADER_CONTENT_LOCATION, baseLoc);     //Send extra soap headers

        /*Un comment the below to do http chunking to avoid the need to calculate content-length. (Needs HTTP/1.1)*/
      //myhttp.put(HTTPConstants.HEADER_TRANSFER_ENCODING, HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED);

        /*Un comment the below to force a 100-Continue... This will cause  httpsender to wait for
         * this response on a post.  If HTTP 1.1 and this is not set, *SOME* servers *MAY* reply with this anyway.
         *  Currently httpsender won't handle this situation, this will require the resp. which it will handle.
         */
      //myhttp.put(HTTPConstants.HEADER_EXPECT, HTTPConstants.HEADER_EXPECT_100_Continue);
        call.setScopedProperty(HTTPConstants.REQUEST_HEADERS,myhttp);

        call.setTargetEndpointAddress( new URL(opts.getURL()) ); //Set the target service host and service location, 

        java.util.Stack rev= new java.util.Stack();
        //Create an attachment referenced by a generated contentId.
        AttachmentPart ap= new AttachmentPart(new javax.activation.DataHandler(
          "Now is the time", "text/plain" ));
         refs.add(ap.getContentIdRef()); //reference the attachment by contentId.
         ap.setMimeHeader(positionHTTPHeader, ""+refs.size() ); //create a MIME header indicating postion.
        call.addAttachmentPart(ap);
        rev.push(ap);

        //Create an attachment referenced by a set contentId.
        String setContentId="rick_did_this";
        ap= new AttachmentPart(new DataHandler(" for all good", "text/plain" ));
          //new MemoryOnlyDataSource(
         ap.setContentId(setContentId);  
         refs.add("cid:" + setContentId); //reference the attachment by contentId.
         ap.setMimeHeader(positionHTTPHeader, ""+refs.size() ); //create a MIME header indicating postion.
        call.addAttachmentPart(ap);
        rev.push(ap);

        //Create an attachment referenced by a absolute contentLocation.
        ap= new AttachmentPart(new DataHandler( " men to", "text/plain" ));
          //new MemoryOnlyDataSource( " men to", "text/plain" )));
        ap.setContentLocation(baseLoc+ "/firstLoc");  
         refs.add(baseLoc+ "/firstLoc"); //reference the attachment by contentId.
         ap.setMimeHeader(positionHTTPHeader, ""+refs.size() ); //create a MIME header indicating postion.
        call.addAttachmentPart(ap);
        rev.push(ap);

        //Create an attachment referenced by relative location to a absolute location. 
        ap= new AttachmentPart(new DataHandler( " come to", "text/plain" ));
          // new MemoryOnlyDataSource( " come to", "text/plain" )));
        ap.setContentLocation(baseLoc+ "/secondLoc");  
        refs.add("secondLoc"); //reference the attachment by contentId.
        ap.setMimeHeader(positionHTTPHeader, ""+refs.size() ); //create a MIME header indicating postion.
        call.addAttachmentPart(ap);
        rev.push(ap);

        //Create an attachment referenced by relative location to a relative location. 
        ap= new AttachmentPart(new DataHandler( " the aid of their country.", "text/plain" ));
          // new MemoryOnlyDataSource( " the aid of their country.", "text/plain" )));
        ap.setContentLocation("thirdLoc");  
        refs.add("thirdLoc"); //reference the attachment by contentId.
        ap.setMimeHeader(positionHTTPHeader, ""+refs.size() ); //create a MIME header indicating postion.
        call.addAttachmentPart(ap);
        rev.push(ap);


        //Now build the message....
        String namespace="urn:attachmentsTestRef"; //needs to match name of service. 

        StringBuffer msg = new StringBuffer("\n<attachments xmlns=\"" +namespace +"\">\n");
        for (java.util.Iterator i = refs.iterator(); i.hasNext() ; )
          msg.append("    <attachment href=\"" + (String) i.next()  + "\"/>\n");

        msg.append(  "</attachments>");

        call.setUsername( opts.getUser());

        call.setPassword( opts.getPassword() );

        call.setOperationStyle(org.apache.axis.enum.Style.DOCUMENT_STR);

        //Now do the call....
        Object ret = call.invoke(new Object[]{
           new SOAPBodyElement(new ByteArrayInputStream(msg.toString().getBytes("UTF-8"))) } ); 

        validate(ret, call, "12345");   

        //Note: that even though the attachments are sent in reverse they are still
        // retreived by reference so the ordinal will still match.
        int revc=1;
        for( ap= (AttachmentPart)rev.pop(); ap!=null ;ap= rev.empty()? null :  (AttachmentPart)rev.pop()){
          call.addAttachmentPart(ap);
        }

        //Now do the call....
        ret = call.invoke(new Object[]{
           new SOAPBodyElement(new ByteArrayInputStream(msg.toString().getBytes("UTF-8"))) } ); 

        validate(ret, call, "54321");   


        return rc;
    }

    void validate(Object ret, Call call, final String expOrdPattern) throws Exception{   
        if (null == ret) {
            System.out.println("Received null ");
            throw new AxisFault("", "Received null", null, null);
        }

        if (ret instanceof String) {
            System.out.println("Received problem response from server: " + ret);
            throw new AxisFault("", (String) ret, null, null);
        }

        Vector vret= (Vector) ret;

        if (!(ret instanceof java.util.Vector )) {
            //The wrong type of object that what was expected.
            System.out.println("Received unexpected type :" +
                ret.getClass().getName());
            throw new AxisFault("", "Received unexpected type:" +
                    ret.getClass().getName(), null, null);

        }

        org.apache.axis.message.RPCElement retrpc= (org.apache.axis.message.RPCElement )
          ((Vector)ret).elementAt(0);

        Document retDoc= org.apache.axis.utils.XMLUtils.newDocument(
          new org.xml.sax.InputSource(new java.io.ByteArrayInputStream(
            retrpc.toString().getBytes())));

        //get at the attachments.    
        org.apache.axis.attachments.Attachments attachments= 
          call.getResponseMessage().getAttachmentsImpl();

        //Still here, so far so good.
        Element rootEl= retDoc.getDocumentElement();

        Element caEl= getNextFirstChildElement(rootEl);
        //this should be the only child element with the ref to our attachment
        // response.
        String href= caEl.getAttribute("href");
        org.apache.axis.Part p= attachments.getAttachmentByReference(href);
        if(null == p)
         throw new org.apache.axis.AxisFault("Attachment for ref='"+href+"' not found." );

         //Check to see the the attachment were sent in order
        String ordPattern= caEl.getAttribute("ordinalPattern");
        if(!expOrdPattern.equals(ordPattern))
          throw new org.apache.axis.AxisFault(
           "Attachments sent out of order expected:'" +expOrdPattern + "', got:'"+ordPattern+"'."  );

         //now get at the data...
         DataHandler dh= ((org.apache.axis.attachments.AttachmentPart)p).getDataHandler();
         System.err.println("content-type:" + dh.getContentType());

       java.util.Vector rspVector= null;
       Object rspObject =  dh.getContent();//This SHOULD just return the vector but reality strikes...
       if(rspObject == null)
           throw new AxisFault("", "Received unexpected object:null", null, null);
       else if(rspObject instanceof java.util.Vector) rspVector= (java.util.Vector)rspObject;
       else if(rspObject instanceof java.io.InputStream)
          rspVector= (java.util.Vector)
           new java.io.ObjectInputStream((java.io.InputStream)rspObject ).readObject();
       else    
           throw new AxisFault("", "Received unexpected object:" +
                    rspObject.getClass().getName(), null, null);

      StringBuffer fullmsg= new StringBuffer();
      for(java.util.Iterator ri= rspVector.iterator(); ri.hasNext();){
        String part= (String)ri.next();
        fullmsg.append(part);
        System.out.print(part);
      }
      System.out.println("");

      if(!(TheKey.equals (fullmsg.toString())))
        throw new org.apache.axis.AxisFault("Fullmsg not correct'"+fullmsg +"'." );
    }

    Element getNextFirstChildElement(Node n) {
        if(n== null) return null;
        n= n.getFirstChild();
        for(; n!= null && !(n instanceof Element); n= n.getNextSibling());
        return (Element)n;
    }

    Element getNextSiblingElement(Node n) {
        if(n== null) return null;
        n= n.getNextSibling();
        for(; n!= null && !(n instanceof Element); n= n.getNextSibling());
        return (Element)n;
    }

    /**
     * Give a single file to send or name a directory
     * to send an array of attachments of the files in
     * that directory.
     */
    public static void main(String args[]) {
        try {

            Options opts = new Options(args);
            TestRef echoattachment = new TestRef(opts);

            args = opts.getRemainingArgs();
            int argpos=0;

            if (echoattachment.testit()) {
                System.out.println("Attachments sent and received ok!");
                System.exit(0);
            }
        }
        catch ( Exception e ) {
            System.err.println(e);
             e.printStackTrace();
        }
        System.exit(18);
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

    /**This class should store all attachment data in memory */
    static class MemoryOnlyDataSource extends org.apache.axis.attachments.ManagedMemoryDataSource{
    
       MemoryOnlyDataSource( byte [] in, String contentType) throws java.io.IOException{
         super( new java.io.ByteArrayInputStream( in) , Integer.MAX_VALUE -2, contentType, true); 
       }
       MemoryOnlyDataSource( String in, String contentType)throws java.io.IOException{
         this( in.getBytes() ,  contentType); 
       }
    }
}
