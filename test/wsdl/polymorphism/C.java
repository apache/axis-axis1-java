/*
 * Copyright 2002-2004 The Apache Software Foundation.
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

/**
 * C is a class which extends A, but HAS NO TYPE MAPPING associated with it
 * directly.
 *
 * We use this to make sure that such a class can still be serialized as an
 * A without any trouble.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
package test.wsdl.polymorphism;

public class C extends A {
    private float floatField = 0.5F;

    public float getFloatField() {
        return floatField;
    }

    public void setFloatField(float floatField) {
        this.floatField = floatField;
    }
}
