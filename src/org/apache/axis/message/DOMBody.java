package org.apache.axis.message;

import java.io.*;
import java.util.*;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;
import org.xml.sax.helpers.AttributesImpl;
import org.w3c.dom.*;

public class DOMBody extends SOAPBodyElement
{
    protected Element element;
    
    public DOMBody(Element element)
    {
        this.element = element;
    }
    
    public void output(SerializationContext context) throws IOException
    {
        outputElement(element, context);
    }
    
    void outputElement(Element el, SerializationContext context)
        throws IOException
    {
        AttributesImpl attributes = null;
        NamedNodeMap attrMap = el.getAttributes();
        
        if (attrMap.getLength() > 0) {
            attributes = new AttributesImpl();
            for (int i = 0; i < attrMap.getLength(); i++) {
            }
        }
        
        QName qName = new QName(el.getNamespaceURI(), el.getTagName());
        
        context.startElement(qName, attributes);
        
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                outputElement((Element)child, context);
            } else if (child instanceof Text) {
                context.writeString(((Text)child).getData());
            }
        }
        
        context.endElement();
    }
}
