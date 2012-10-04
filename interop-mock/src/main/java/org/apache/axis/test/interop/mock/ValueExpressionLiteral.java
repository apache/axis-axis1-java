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
package org.apache.axis.test.interop.mock;

import javax.el.ELContext;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;

import org.apache.commons.lang.ObjectUtils;

public final class ValueExpressionLiteral extends ValueExpression {
    private static final long serialVersionUID = -3847213439349942695L;
    
    private String value;

    public ValueExpressionLiteral(String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public Object getValue(ELContext context) {
        return this.value;
    }

    public void setValue(ELContext context, Object value) {
        throw new PropertyNotWritableException();
    }

    public boolean isReadOnly(ELContext context) {
        return true;
    }
   
    public Class<?> getType(ELContext context) {
        return String.class;
    }

    public Class<?> getExpectedType() {
        return String.class;
    }

    public String getExpressionString() {
        return value;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ValueExpressionLiteral && equals((ValueExpressionLiteral)obj));
    }

    public boolean equals(ValueExpressionLiteral ve) {
        return ve != null && ObjectUtils.equals(value, ve.value);
    }
    
    public int hashCode() {
        return value.hashCode();
    }
       
    public boolean isLiteralText() {
        return true;
    }
}
