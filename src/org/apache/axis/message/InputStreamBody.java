/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.message;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.IOUtils;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.io.InputStream;


public class InputStreamBody extends SOAPBodyElement
{
    protected static Log log =
        LogFactory.getLog(InputStreamBody.class.getName());

    protected InputStream inputStream;
    
    public InputStreamBody(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }
    
    public void outputImpl(SerializationContext context) throws IOException
    {
        try {
            byte[]  buf = new byte[ inputStream.available() ];
            IOUtils.readFully(inputStream,buf);
            String contents = new String(buf);
            context.writeString(contents);
        }
        catch( IOException ex ) {
            throw ex;
        }
        catch( Exception e ) {
            log.error(Messages.getMessage("exception00"), e);
        }        
    }
}
