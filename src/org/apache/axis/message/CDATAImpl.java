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

/**
 * 
 * @author Heejune Ahn (cityboy@tmax.co.kr)
 */

public class CDATAImpl extends org.apache.axis.message.Text
    implements org.w3c.dom.CDATASection
{
    public CDATAImpl(String text)
    {
        super(text);
    }
    public boolean isComment()
    {
        return false;
    }
    static final String cdataUC = "<![CDATA[";
    static final String cdataLC = "<![cdata[";

}
