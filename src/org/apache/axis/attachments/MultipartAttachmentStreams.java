package org.apache.axis.attachments;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeUtility;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;

/**
 * The MultipartAttachmentStreams class is used to create
 * IncomingAttachmentInputStream objects when the HTTP stream shows a marked
 * separation between the SOAP and each attachment parts. Unlike the DIME
 * version, this class will use the BoundaryDelimitedStream to parse data in the
 * SwA format. Another difference between the two is that the
 * MultipartAttachmentStreams class must also provide a way to hold attachment
 * parts parsed prior to where the SOAP part appears in the HTTP stream (i.e.
 * the root part of the multipart-related message). Our DIME counterpart didn’t
 * have to worry about this since the SOAP part is guaranteed to be the first in
 * the stream. But since SwA has no such guarantee, we must fall back to caching
 * these first parts. Afterwards, we can stream the rest of the attachments that
 * are after the SOAP part of the request message.
 * 
 * @author David Wong
 * @author Brian Husted
 *  
 */
public final class MultipartAttachmentStreams extends IncomingAttachmentStreams {
    private BoundaryDelimitedStream _delimitedStream = null;

    private Iterator _attachmentParts = null;

    public MultipartAttachmentStreams(BoundaryDelimitedStream delimitedStream)
            throws AxisFault {
        this(delimitedStream, null);
    }

    public MultipartAttachmentStreams(BoundaryDelimitedStream delimitedStream,
            Collection priorParts) throws AxisFault {
        if (delimitedStream == null) {
            throw new AxisFault(Messages.getMessage("nullDelimitedStream"));
        }
        _delimitedStream = delimitedStream;
        if (priorParts != null) {
            setAttachmentsPriorToSoapPart(priorParts.iterator());
        }
    }

    public void setAttachmentsPriorToSoapPart(Iterator iterator) {
        _attachmentParts = iterator;
    }

    /**
     * 
     * @see org.apache.axis.attachments.IncomingAttachmentStreams#getNextStream()
     */
    public IncomingAttachmentInputStream getNextStream() throws AxisFault {
        IncomingAttachmentInputStream stream;
        if (!isReadyToGetNextStream()) {
            throw new IllegalStateException(Messages
                    .getMessage("nextStreamNotReady"));
        }
        if (_attachmentParts != null && _attachmentParts.hasNext()) {
            AttachmentPart part = (AttachmentPart) _attachmentParts.next();

            try {
                stream = new IncomingAttachmentInputStream(part
                        .getDataHandler().getInputStream());
            } catch (IOException e) {
                throw new AxisFault(Messages
                        .getMessage("failedToGetAttachmentPartStream"), e);
            } catch (SOAPException e) {
                throw new AxisFault(Messages
                        .getMessage("failedToGetAttachmentPartStream"), e);
            }
            stream.addHeader(HTTPConstants.HEADER_CONTENT_ID, part
                    .getContentId());
            stream.addHeader(HTTPConstants.HEADER_CONTENT_LOCATION, part
                    .getContentLocation());
            stream.addHeader(HTTPConstants.HEADER_CONTENT_TYPE, part
                    .getContentType());
        } else {
            InternetHeaders headers;

            try {
                _delimitedStream = _delimitedStream.getNextStream();
                if (_delimitedStream == null) {
                    return null;
                }
                headers = new InternetHeaders(_delimitedStream);
                String delimiter = null; // null for the first header
                String encoding = headers.getHeader(
                        HTTPConstants.HEADER_CONTENT_TRANSFER_ENCODING,
                        delimiter);
                if (encoding != null && encoding.length() > 0) {
                    encoding = encoding.trim();
                    stream = new IncomingAttachmentInputStream(MimeUtility
                            .decode(_delimitedStream, encoding));
                    stream.addHeader(
                            HTTPConstants.HEADER_CONTENT_TRANSFER_ENCODING,
                            encoding);
                } else {
                    stream = new IncomingAttachmentInputStream(_delimitedStream);
                }
            } catch (IOException e) {
                throw new AxisFault(Messages
                        .getMessage("failedToGetDelimitedAttachmentStream"), e);
            } catch (MessagingException e) {
                throw new AxisFault(Messages
                        .getMessage("failedToGetDelimitedAttachmentStream"), e);
            }
            Header header;
            String name;
            String value;
            Enumeration e = headers.getAllHeaders();
            while (e != null && e.hasMoreElements()) {
                header = (Header) e.nextElement();
                name = header.getName();
                value = header.getValue();
                if (HTTPConstants.HEADER_CONTENT_ID.equals(name)
                        || HTTPConstants.HEADER_CONTENT_TYPE.equals(name)
                        || HTTPConstants.HEADER_CONTENT_LOCATION.equals(name)) {
                    value = value.trim();
                    if ((HTTPConstants.HEADER_CONTENT_ID.equals(name) || HTTPConstants.HEADER_CONTENT_LOCATION
                            .equals(name))
                            && (name.indexOf('>') > 0 || name.indexOf('<') > 0)) {
                        value = new StringTokenizer(value, "<>").nextToken();
                    }
                }
                stream.addHeader(name, value);
            }
        }
        setReadyToGetNextStream(false);
        return stream;
    }

}