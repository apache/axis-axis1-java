/*
 * Copyright 2001, 2002,2004 The Apache Software Foundation.
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

 package org.apache.axis.components.jms;

import java.util.HashMap;

import javax.jms.ConnectionFactory;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;

import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.ClassUtils;

/**
 * Uses ClassUtils.forName and reflection to configure ConnectionFactory.  Uses
 * the input sessions to create destinations.
 *
 * @author Jaime Meritt (jmeritt@sonicsoftware.com)
 */
public abstract class BeanVendorAdapter extends JMSVendorAdapter
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