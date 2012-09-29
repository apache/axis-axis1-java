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
package org.apache.axis.tools.maven.wsdl2java;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Service;

import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.gen.NoopGenerator;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaDefinitionWriter;
import org.apache.axis.wsdl.toJava.JavaGeneratorFactory;

public class JavaGeneratorFactoryEx extends JavaGeneratorFactory {
    public JavaGeneratorFactoryEx(Emitter emitter) {
        super(emitter);
    }

    protected void addDefinitionGenerators() {
        addGenerator(Definition.class, JavaDefinitionWriter.class);
        if (((EmitterEx)emitter).getDeployWsdd() != null) {
            addGenerator(Definition.class, JavaDeployWriterEx.class);
        }
        if (((EmitterEx)emitter).getUndeployWsdd() != null) {
            addGenerator(Definition.class, JavaUndeployWriterEx.class);
        }
    }

    public Generator getGenerator(Service service, SymbolTable symbolTable) {
        if (((EmitterEx)emitter).isClientSide() && include(service.getQName())) {
            Generator writer = new JavaServiceWriterEx(emitter, service, symbolTable);
            ServiceEntry sEntry = symbolTable.getServiceEntry(service.getQName());
            serviceWriters.addStuff(writer, sEntry, symbolTable);
            return serviceWriters;
        } else {
            return new NoopGenerator();
        }
    }
    
    public Generator getGenerator(Binding binding, SymbolTable symbolTable) {
        if (include(binding.getQName())) {
            Generator writer = new JavaBindingWriterEx(emitter, binding, symbolTable);
            BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
            bindingWriters.addStuff(writer, bEntry, symbolTable);
            return bindingWriters;
        } else {
            return new NoopGenerator();
        }
    }
}
