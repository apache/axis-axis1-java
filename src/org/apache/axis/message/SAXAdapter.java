package org.apache.axis.message;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.axis.MessageContext;
import org.apache.axis.utils.XMLUtils;
import javax.xml.parsers.*;

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
                      MessageContext msgContext)
    {
        super(msgContext);
        _parser = XMLUtils.getSAXParser();
        this.inputSource = inputSource;
    }
    
    /*******************************************************************
     * Threading stuff
     */
    public void parse()
    {
      try {
        _parser.parse(inputSource, this);
      } catch (Exception e) {
        e.printStackTrace();
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
