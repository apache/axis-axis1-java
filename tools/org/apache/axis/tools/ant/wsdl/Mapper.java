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


package org.apache.axis.tools.ant.wsdl;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

import java.util.HashMap;

/**
 * interface that namespace mappers are expected to implement
 */

public interface Mapper {
    /**
     * execute the mapping
     * @param owner owner object
     * @param map map to map to
     * @param packageIsKey if the package is to be the key for the map
     * @throws BuildException in case of emergency
     */
    void execute(ProjectComponent owner, HashMap map, boolean packageIsKey) throws BuildException;
}
