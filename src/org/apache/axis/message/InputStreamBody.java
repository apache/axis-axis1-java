package org.apache.axis.message;

import org.apache.axis.encoding.SerializationContext;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamBody extends SOAPBodyElement
{
    protected InputStream inputStream;
    
    public InputStreamBody(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }
    
    public void outputImpl(SerializationContext context) throws IOException
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
