package org.apache.axis.message;

import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.axis.message.events.*;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;

/** This is an example specific header class which someone might
 * implement.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class DebugHeader extends SOAPHeader
{
    static class DebugHeaderFactory implements ElementFactory
    {
        public MessageElement createElement(String namespace, String localName,
                                        Attributes attributes, DeserializationContext context)
        {
            return new DebugHeader(namespace, localName, attributes, context);
        }
    }

    public static DebugHeaderFactory getFactory()
    {
        return new DebugHeaderFactory();
    }
    
    class SAXHandler extends DefaultHandler
    {
        public void characters(char [] chars, int start, int end)
        {
            // Parse this as an int.  (do we need to build up a string,
            // and only do the parse at the end?)
            debugLevel = Integer.parseInt(new String(chars, start, end));
            System.out.println("[DebugHeader] debugLevel is " + debugLevel);
        }
    }
    
    private int debugLevel;
    public int getDebugLevel()
    {
        return debugLevel;
    }
    public void setDebugLevel(int level)
    {
        debugLevel = level;
    }
    
    public DebugHeader(int debugLevel)
    {
        this.name = "Debug";
        this.debugLevel = debugLevel;
    }
    
    public DebugHeader(String namespace, String localPart,
                       Attributes attributes, DeserializationContext context)
    {
        super(namespace, localPart, attributes, context);
    }

    // Override the default (recording) implementation because we
    // want to parse this header ourselves as it goes by.
    
    public ContentHandler getContentHandler()
    { return new SAXHandler(); }
    
    public void output(SerializationContext context)
        throws IOException
    {
        context.startElement(new QName(this.getNamespaceURI(), this.getName()), null);
        context.writeString(new Integer(debugLevel).toString());
        context.endElement();
    }
}
