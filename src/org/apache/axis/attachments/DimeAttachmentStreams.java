
package org.apache.axis.attachments;

import java.io.IOException;

import org.apache.axis.AxisFault;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;

/**
 * 
 * This is the concrete implementation of the IncomingAttachmentStreams class
 * and is used to parse data that is in the DIME format. This class will make
 * use of Axis’ DimeDelimitedInputStream to parse the data in the HTTP stream
 * which will give this class the capability of creating
 * IncomingAttachmentInputStream objects at each marker within the HTTP stream.
 * 
 * @author David Wong
 * @author Brian Husted
 *
 */
public final class DimeAttachmentStreams extends IncomingAttachmentStreams
{
   private DimeDelimitedInputStream _delimitedStream = null;
   
   public DimeAttachmentStreams(DimeDelimitedInputStream stream)
      throws AxisFault
   {
      if (stream == null)
      {
         throw new AxisFault(Messages.getMessage("nullDelimitedStream"));
      }
      _delimitedStream = stream;
   }
   
   /* (non-Javadoc)
    * @see org.apache.axis.attachments.IncomingAttachmentStreams#getNextStream()
    */
   public IncomingAttachmentInputStream getNextStream() throws AxisFault
   {
      IncomingAttachmentInputStream stream = null;
      
      if (!isReadyToGetNextStream())
      {
         throw new IllegalStateException(Messages.getMessage("nextStreamNotReady"));
      }
      try
      {
         _delimitedStream = _delimitedStream.getNextStream();
         if (_delimitedStream == null)
         {
            return null;
         }
         stream = new IncomingAttachmentInputStream(_delimitedStream);
      }
      catch (IOException e)
      {
         throw new AxisFault(Messages.getMessage("failedToGetDelimitedAttachmentStream"), e);
      }

      String value = _delimitedStream.getContentId();
      if (value != null && value.length() > 0)
      {
         stream.addHeader(HTTPConstants.HEADER_CONTENT_ID, value);
      }
      value = _delimitedStream.getType();
      if (value != null && value.length() > 0)
      {
         stream.addHeader(HTTPConstants.HEADER_CONTENT_TYPE, value);
      }
      setReadyToGetNextStream(false);
      return stream;
   }

}
