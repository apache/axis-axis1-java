/*
 * Copyright 2002,2004 The Apache Software Foundation.
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

package test.functional;

import java.util.Date;

/**
 * More Complex JavaBean used in serialisation tests.
 *
 * @author Patrick Martin
 */
public class NestedBean {
    private Date _startDate;
    private String _testString;
    private SimpleBean _simpleBean;
    private SimpleBean[] _simpleBeanList;

    public SimpleBean getSimpleBean() {
        return _simpleBean;
    }

    public void setSimpleBean(SimpleBean simpleBean) {
        this._simpleBean = simpleBean;
    }

    public SimpleBean[] getSimpleBeanList() {
        return _simpleBeanList;
    }

    public void setSimpleBeanList(SimpleBean[] simpleBeanList) {
        this._simpleBeanList = simpleBeanList;
    }

    public Date getStartDate() {
        return _startDate;
    }

    public void setStartDate(Date startDate) {
        this._startDate = startDate;
    }

    public String getTestString() {
        return _testString;
    }

    public void setTestString(String testString) {
        this._testString = testString;
    }
}
