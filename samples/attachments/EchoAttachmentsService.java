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

package samples.attachments;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.activation.DataHandler;


/**
 * @author Rick Rineholt 
 */

/**
 * An example of 
 * This class has a main method that beside the standard arguments
 * allows you to specify an attachment that will be sent to a 
 * service which will then send it back.
 *  
 */
public class EchoAttachmentsService {

    /**
     * This method implements a web service that sends back
     * any attachment it receives.
     */
    public DataHandler echo( DataHandler dh) {
        System.err.println("In echo");

        //Attachments are sent by default back as a MIME stream if no attachments were
        // received.  If attachments are received the same format that was received will
        // be the default stream type for any attachments sent.

        //The following two commented lines would force any attachments sent back.
        //  to be in DIME format.

        //Message rspmsg=AxisEngine.getCurrentMessageContext().getResponseMessage();
        //rspmsg.getAttachmentsImpl().setSendType(org.apache.axis.attachments.Attachments.SEND_TYPE_DIME);
        
        if (dh == null ) System.err.println("dh is null");
        else System.err.println("Received \""+dh.getClass().getName()+"\".");
        return dh;
    }

    /**
     * This method implements a web service that sends back
     * an array of attachment it receives.
     */
    public DataHandler[] echoDir( DataHandler[] attachments) {
        System.err.println("In echoDir");

        //Attachments are sent by default back as a MIME stream if no attachments were
        // received.  If attachments are received the same format that was received will
        // be the default stream type for any attachments sent.

        //The following two commented lines would force any attachments sent back.
        //  to be in DIME format.

        //Message rspmsg=AxisEngine.getCurrentMessageContext().getResponseMessage();
        //rspmsg.getAttachmentsImpl().setSendType(org.apache.axis.attachments.Attachments.SEND_TYPE_DIME);

        if (attachments == null ) System.err.println("attachments is null!");
        else System.err.println("Got " + attachments.length + " attachments!");
        return attachments;
    }

    public Document attachments( Document xml)
      throws org.apache.axis.AxisFault,java.io.IOException, org.xml.sax.SAXException,
      java.awt.datatransfer.UnsupportedFlavorException,javax.xml.parsers.ParserConfigurationException,
      java.lang.ClassNotFoundException,javax.xml.soap.SOAPException  {
      System.err.println("In message handling attachments directly.");
      org.apache.axis.MessageContext msgContext= org.apache.axis.MessageContext.getCurrentContext(); 

      org.apache.axis.Message reqMsg= msgContext.getRequestMessage();

      org.apache.axis.attachments.Attachments attachments=reqMsg.getAttachmentsImpl();  

      if(null == attachments){
         throw new org.apache.axis.AxisFault("No support for attachments" );
      }

      Element rootEl= xml.getDocumentElement();

      Element caEl= getNextFirstChildElement(rootEl);
      StringBuffer fullmsg= new StringBuffer();
      java.util.Vector reply= new java.util.Vector();


      for(int count=1 ;caEl != null; caEl= getNextSiblingElement(caEl), ++count){
        String href= caEl.getAttribute("href");
        org.apache.axis.Part p= attachments.getAttachmentByReference(href);
        if(null == p)
         throw new org.apache.axis.AxisFault("Attachment for ref='"+href+"' not found." );
         String ordinalStr =getOrdinalHeaders(p);
         if( null == ordinalStr || ordinalStr.trim().length()==0)
           throw new org.apache.axis.AxisFault("Ordinal for attachment  ref='"+href+"' not found." );
         int ordinal= Integer.parseInt(ordinalStr);
         if(count != ordinal)
           throw new org.apache.axis.AxisFault("Ordinal for attachment  ref='"+href+"' excpected" + count + " got " + ordinal +"." );

          //check content type.
          if(!"text/plain".equals(p.getContentType()))
             throw new org.apache.axis.AxisFault("Attachment  ref='"+href+"' bad content-type:'"+p.getContentType()+"'." );

         //now get at the data...
          DataHandler dh= ((org.apache.axis.attachments.AttachmentPart)p).getDataHandler();
          String pmsg=(String )dh.getContent();
          fullmsg.append(pmsg);
          reply.add(pmsg);
      }
      if(!(samples.attachments.TestRef .TheKey.equals(fullmsg.toString())))
        throw new org.apache.axis.AxisFault("Fullmsg not correct'"+fullmsg +"'." );
      System.out.println(fullmsg.toString());

      //Now lets Java serialize the reply...
      java.io.ByteArrayOutputStream byteStream = new java.io.ByteArrayOutputStream();
      java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(byteStream);
      oos.writeObject(reply);
      oos.close();
      byte[] replyJavaSerialized= byteStream.toByteArray();
      byteStream=null; oos= null;

      org.apache.axis.attachments.AttachmentPart replyPart= new 
          org.apache.axis.attachments.AttachmentPart(
        new DataHandler( new MemoryOnlyDataSource(replyJavaSerialized,
         java.awt.datatransfer.DataFlavor.javaSerializedObjectMimeType+"; class=\"" 
          + reply.getClass().getName()+"\"")));

      //Now lets add the attachment to the response message.
      org.apache.axis.Message rspMsg= msgContext.getResponseMessage();
      rspMsg.addAttachmentPart(replyPart);

      //Iterate over the attachments... not by reference.
      String ordinalPattern=""; 
      for(java.util.Iterator ai=reqMsg.getAttachments(); ai.hasNext();){
        org.apache.axis.Part p= (org.apache.axis.Part) ai.next();
        ordinalPattern += getOrdinalHeaders(p);
      }

      //Now build the return document in a string buffer... 
      StringBuffer msgBody = new StringBuffer("\n<attachments xmlns=\"");
          msgBody.append(rootEl.getNamespaceURI())
          .append("\">\n")
          .append("\t<attachment href=\"")
          .append(replyPart.getContentIdRef())
          .append("\" ordinalPattern=\"")
          .append(ordinalPattern)
          .append("\"/>\n")
          .append("</attachments>\n");

      //Convert the string buffer to an XML document and return it.
      return 
        org.apache.axis.utils.XMLUtils.newDocument(
          new org.xml.sax.InputSource(new java.io.ByteArrayInputStream(
            msgBody.toString().getBytes())));
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
    String getOrdinalHeaders( org.apache.axis.Part p){
      StringBuffer ret= new StringBuffer();
      for(java.util.Iterator i= p.getMatchingMimeHeaders( new String[]{samples.attachments.TestRef.positionHTTPHeader});
          i.hasNext();){
          javax.xml.soap.MimeHeader mh= (javax.xml.soap.MimeHeader) i.next();
          String v= mh.getValue();  
          if(v != null) ret.append(v.trim());
      }
      return ret.toString();
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

