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
package javax.xml.messaging;

/**
 * A special case of the <code>Endpoint</code> class used for simple applications that want to communicate directly
 * with another SOAP-based application in a point-to-point fashion instead of going through a messaging provider.
 * <P>
 * A <code>URLEndpoint</code> object contains a URL, which is used to make connections to the remote party.
 * A standalone client can pass a <code>URLEndpoint</code> object to the <code>SOAPConnection</code> method <code>call</code> to
 * send a message synchronously.
 */
public class URLEndpoint extends Endpoint {

    /**
     *  Constructs a new <code>URLEndpoint</code> object using the given URL.
     *  @param  url  a <code>String</code> giving the URL to use in constructing the new <code>URLEndpoint</code> object
     */
    public URLEndpoint(String url) {
        super(url);
    }

    /**
     *  Gets the URL associated with this <code>URLEndpoint</code> object.
     *  @return  a <code>String</code> giving the URL associated with this <code>URLEndpoint</code> object
     */
    public String getURL() {
        return super.id;
    }
}
