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

import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.gen.NoopGenerator;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaBindingWriter;

public class JavaBindingWriterEx extends JavaBindingWriter {
    public JavaBindingWriterEx(Emitter emitter, Binding binding, SymbolTable symbolTable) {
        super(emitter, binding, symbolTable);
    }

    protected Generator getJavaStubWriter(Emitter emitter, BindingEntry bEntry, SymbolTable st) {
        if (((EmitterEx)emitter).isClientSide()) {
            return new JavaStubWriterEx(emitter, bEntry, st);
        } else {
            return new NoopGenerator();
        }
    }

    protected Generator getJavaImplWriter(Emitter emitter, BindingEntry bEntry, SymbolTable st) {
        if (((EmitterEx)emitter).isGenerateImplementation()) {
            return super.getJavaImplWriter(emitter, bEntry, st);
        } else {
            return new NoopGenerator();
        }
    }
}
