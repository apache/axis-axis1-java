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

package org.apache.axis.holders;

import org.apache.axis.types.NormalizedString;

import javax.xml.rpc.holders.Holder;

/**
 * Class NormalizedStringHolder
 *
 */
public final class NormalizedStringHolder implements Holder {

    /** Field _value */
    public NormalizedString value;

    /**
     * Constructor NormalizedStringHolder
     */
    public NormalizedStringHolder() {
    }

    /**
     * Constructor NormalizedStringHolder
     *
     * @param value
     */
    public NormalizedStringHolder(NormalizedString value) {
        this.value = value;
    }
}

