package org.apache.axis.message;

import org.xml.sax.*;

/** This class is an adapter for the Axis SAX-event system
 * which uses a SAX parser to parse on its own thread, synchronizing
 * when appropriate with the control thread.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class SAXAdapter extends SOAPSAXHandler
{
    private static final boolean DEBUG_LOG = false;
    private XMLReader _parser;
    InputSource inputSource;

    public SAXAdapter(XMLReader parser, InputSource inputSource)
    {
        _parser = new org.apache.xerces.parsers.SAXParser();
        _parser.setContentHandler(this);
        
        this.inputSource = inputSource;
    }
    
    /*******************************************************************
     * Threading stuff
     */
    public void parse()
    {
      try {
        _parser.parse(inputSource);
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
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
