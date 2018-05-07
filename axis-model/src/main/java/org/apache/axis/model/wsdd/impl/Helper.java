/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.model.wsdd.impl;

import java.util.Iterator;

import org.apache.axis.model.wsdd.Deployment;
import org.apache.axis.model.wsdd.GlobalConfiguration;
import org.apache.axis.model.wsdd.Parameter;
import org.apache.axis.model.wsdd.Parameterizable;
import org.apache.axis.model.wsdd.WSDDFactory;

final class Helper {
    private Helper() {}

    static void merge(Deployment that, Deployment other) {
        // TODO: very naive implementation; need more fine grained merging
        GlobalConfiguration otherGlobalConfiguration = other.getGlobalConfiguration();
        if (otherGlobalConfiguration != null) {
            that.setGlobalConfiguration(otherGlobalConfiguration);
        }
        that.getHandlers().addAll(other.getHandlers());
        that.getTransports().addAll(other.getTransports());
        that.getServices().addAll(other.getServices());
        that.getTypeMappings().addAll(other.getTypeMappings());
        that.getBeanMappings().addAll(other.getBeanMappings());
        that.getArrayMappings().addAll(other.getArrayMappings());
    }

    static void setParameter(Parameterizable that, String name, String value) {
        for (Iterator it = that.getParameters().iterator(); it.hasNext(); ) {
            Parameter param = (Parameter)it.next();
            if (name.equals(param.getName())) {
                param.setValue(value);
                return;
            }
        }
        Parameter param = WSDDFactory.INSTANCE.createParameter();
        param.setName(name);
        param.setValue(value);
        that.getParameters().add(param);
    }
}
