package org.apache.axis.message;

import java.io.*;
import java.util.*;
import org.apache.axis.encoding.*;
import org.xml.sax.*;

public class InputStreamBody extends SOAPBodyElement
{
    protected InputStream inputStream;
    
    public InputStreamBody(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }
    
    public void output(SerializationContext context) throws IOException
    {
        try {
            byte[]  buf = new byte[ inputStream.available() ];
            inputStream.read( buf );
            String contents = new String(buf);
            context.writeString(contents);
        }
        catch( IOException ex ) {
            throw ex;
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
        }        
    }
}
