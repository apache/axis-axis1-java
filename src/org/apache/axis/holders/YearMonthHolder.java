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

import org.apache.axis.types.YearMonth;

import javax.xml.rpc.holders.Holder;

/**
 * Class YearMonthHolder
 *
 */
public final class YearMonthHolder implements Holder {

    /** Field _value */
    public YearMonth value;

    /**
     * Constructor YearMonthHolder
     */
    public YearMonthHolder() {
    }

    /**
     * Constructor YearMonthHolder
     *
     * @param value
     */
    public YearMonthHolder(YearMonth value) {
        this.value = value;
    }
}

