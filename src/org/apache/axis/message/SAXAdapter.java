package org.apache.axis.message;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.axis.MessageContext;
import org.apache.axis.utils.XMLUtils;
import javax.xml.parsers.*;
import org.apache.axis.encoding.ServiceDescription;

/** This class is an adapter for the Axis SAX-event system
 * which uses a SAX parser to parse on its own thread, synchronizing
 * when appropriate with the control thread.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class SAXAdapter extends SOAPSAXHandler
{
    private static final boolean DEBUG_LOG = false;
    private SAXParser _parser;
    InputSource inputSource;

    public SAXAdapter(InputSource inputSource,
                      MessageContext msgContext,
                      String messageType)
    {
        super(msgContext, messageType);
        _parser = XMLUtils.getSAXParser();
        this.inputSource = inputSource;
    }
    
    public SAXAdapter(InputSource inputSource,
                      MessageContext msgContext)
    {
        this(inputSource, msgContext, ServiceDescription.REQUEST);
    }
    
    /*******************************************************************
     * Threading stuff
     */
    public void parse() throws SAXException
    {
      try {
        _parser.parse(inputSource, this);
      } catch (java.io.IOException ioe) {
        throw new SAXException(ioe);
      }
    }
    
    /** Called by the control thread; let the parsing thread
     * continue.
     * 
     */
    protected void continueParsing()
    {
    }
    
    /** Called by the parse thread once it hits a desired
     * event (found the element the control thread is looking
     * for, finished parsing the headers, etc).  Suspend
     * execution until we're told to go again.
     */
    protected void pauseParsing() throws SAXException
    {
    }

}
