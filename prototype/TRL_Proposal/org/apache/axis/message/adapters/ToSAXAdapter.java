package org.apache.axis.message.adapters;

import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.message.SOAPDocument;

final public class ToSAXAdapter {
    private final SAXParserFactory factory;
    public ToSAXAdapter(SAXParserFactory factory) {
        this.factory = factory;
    }

    public void convert(SOAPDocument message, DefaultHandler handler)
        throws SAXException, ParserConfigurationException
    {
        Reader in = null;
        try {
            in = new StringReader(message.toXML());
            factory.newSAXParser().parse(new InputSource(in), handler);
        } catch (IOException e) {
            throw new UnknownError(e.getMessage());
        } finally {
            if (in != null)
                try { in.close(); } catch (IOException e) { }
        }
    }
}

