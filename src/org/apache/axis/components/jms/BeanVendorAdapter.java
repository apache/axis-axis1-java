/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

 package org.apache.axis.components.jms;

import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.ClassUtils;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import java.util.HashMap;

/**
 * Uses ClassUtils.forName and reflection to configure ConnectionFactory.  Uses
 * the input sessions to create destinations.
 *
 * @author Jaime Meritt (jmeritt@sonicsoftware.com)
 */
public class BeanVendorAdapter extends JMSVendorAdapter
{
    protected final static String CONNECTION_FACTORY_CLASS =
                                        "transport.jms.ConnectionFactoryClass";

    public QueueConnectionFactory getQueueConnectionFactory(HashMap cfConfig)
        throws Exception
    {
        return (QueueConnectionFactory)getConnectionFactory(cfConfig);
    }

    public TopicConnectionFactory getTopicConnectionFactory(HashMap cfConfig)
        throws Exception
    {
        return (TopicConnectionFactory)getConnectionFactory(cfConfig);
    }

    private ConnectionFactory getConnectionFactory(HashMap cfConfig)
        throws Exception
    {
        String classname = (String)cfConfig.get(CONNECTION_FACTORY_CLASS);
        if(classname == null || classname.trim().length() == 0)
            throw new IllegalArgumentException("noCFClass");

        Class factoryClass = ClassUtils.forName(classname);
        ConnectionFactory factory = (ConnectionFactory)factoryClass.newInstance();
        callSetters(cfConfig, factoryClass, factory);
        return factory;
    }

    private void callSetters(HashMap cfConfig,
                             Class factoryClass,
                             ConnectionFactory factory)
      throws Exception
    {
        BeanPropertyDescriptor[] bpd = BeanUtils.getPd(factoryClass);
        for(int i = 0; i < bpd.length; i++)
        {
            BeanPropertyDescriptor thisBPD = bpd[i];
            String propName = thisBPD.getName();
            if(cfConfig.containsKey(propName))
            {
                Object value = cfConfig.get(propName);
                if(value == null)
                    continue;

                String validType = thisBPD.getType().getName();
                if(!value.getClass().getName().equals(validType))
                    throw new IllegalArgumentException("badType");
                if(!thisBPD.isWriteable())
                    throw new IllegalArgumentException("notWriteable");
                if(thisBPD.isIndexed())
                    throw new IllegalArgumentException("noIndexedSupport");
                thisBPD.set(factory, value);
            }
        }
    }
}