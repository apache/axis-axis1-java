
package org.apache.axis.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.AxisFault;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;

/**
 * Similiar in concept to an iterator over the delimited streams inside
 * of the HTTP stream. One difference between this class and a full fledge
 * iterator is that the class is unable to tell if there are more streams until
 * the last one has been fully read. It will however, return null when the end
 * of the HTTP stream has been reached. Since the HTTP stream can contain data
 * in different formats (e.g. DIME or SwA), the IncomingAttachmentStreams class
 * will be an abstract class letting its derivatives handle the specifics to
 * parsing out the HTTP stream. However, the class will implement methods that
 * keep track of when each of the delimited streams are completely read. This is
 * necessary since the next stream cannot be created until the previous stream
 * has been fully read due to the fact that we are actually dealing with a
 * single stream delimited by markers.
 * 
 * @author David Wong
 * @author Brian Husted
 */
public abstract class IncomingAttachmentStreams {
    private boolean _readyToGetNextStream = true;

    /**
     * @return The next delimited stream or null if no additional streams are
     *         left.
     */
    public abstract IncomingAttachmentInputStream getNextStream()
            throws AxisFault;

    /**
     * @return True if the next stream can be read, false otherwise.
     */
    public final boolean isReadyToGetNextStream() {
        return _readyToGetNextStream;
    }

    /**
     * Set the ready flag. Intended for the inner class to use.
     * 
     * @param ready
     */
    protected final void setReadyToGetNextStream(boolean ready) {
        _readyToGetNextStream = ready;
    }

    public final class IncomingAttachmentInputStream extends InputStream {
        private HashMap _headers = null;

        private InputStream _stream = null;

        /**
         * @param in
         */
        public IncomingAttachmentInputStream(InputStream in) {
            _stream = in;
        }

        /**
         * @return MIME headers for this attachment. May be null if no headers
         *         were set.
         */
        public Map getHeaders() {
            return _headers;
        }

        /**
         * Add a header.
         * 
         * @param name
         * @param value
         */
        public void addHeader(String name, String value) {
            if (_headers == null) {
                _headers = new HashMap();
            }
            _headers.put(name, value);
        }

        /**
         * Get a header value.
         * 
         * @param name
         * @return The header found or null if not found.
         */
        public String getHeader(String name) {
            Object header = null;
            if (_headers == null || (header = _headers.get(name)) == null) {
                return null;
            }
            return header.toString();
        }

        /**
         * @return The header with HTTPConstants.HEADER_CONTENT_ID as the key.
         */
        public String getContentId() {
            return getHeader(HTTPConstants.HEADER_CONTENT_ID);
        }

        /**
         * @return The header with HTTPConstants.HEADER_CONTENT_LOCATION as the
         *         key.
         */
        public String getContentLocation() {
            return getHeader(HTTPConstants.HEADER_CONTENT_LOCATION);
        }

        /**
         * @return The header with HTTPConstants.HEADER_CONTENT_TYPE as the key.
         */
        public String getContentType() {
            return getHeader(HTTPConstants.HEADER_CONTENT_TYPE);
        }

        /**
         * Don't want to support mark and reset since this may get us into
         * concurrency problem when different pieces of software may have a
         * handle to the underlying InputStream.
         */
        public boolean markSupported() {
            return false;
        }

        public void reset() throws IOException {
            throw new IOException(Messages.getMessage("markNotSupported"));
        }

        public void mark(int readLimit) {
            // do nothing
        }

        public int read() throws IOException {
            int retval = _stream.read();
            IncomingAttachmentStreams.this
                    .setReadyToGetNextStream(retval == -1);
            return retval;
        }

        public int read(byte[] b) throws IOException {
            int retval = _stream.read(b);
            IncomingAttachmentStreams.this
                    .setReadyToGetNextStream(retval == -1);
            return retval;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            int retval = _stream.read(b, off, len);
            IncomingAttachmentStreams.this
                    .setReadyToGetNextStream(retval == -1);
            return retval;
        }
    }
}