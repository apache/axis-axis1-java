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

import org.apache.tools.ant.ProjectComponent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *a mappingset is a set of mappings
 */
public class MappingSet implements Mapper {

    List mappings=new LinkedList();

    /**
     * add a new mapping
     * @param mapping
     */
    public void addMapping(NamespaceMapping mapping) {
        mappings.add(mapping);
    }

    /**
     * add a mappingset inside this one
     * @param mappingset
     */
    public void addMappingSet(MappingSet mappingset) {
        mappings.add(mappingset);
    }

    /**
     * execute by mapping everything iteratively and recursively
     * @param owner owner task
     * @param map map to map into
     * @param packageIsKey if the package is to be the key for the map
     */
    public void execute(ProjectComponent owner, HashMap map, boolean packageIsKey) {
        Iterator it=mappings.iterator();
        while (it.hasNext()) {
            Mapper mapper = (Mapper) it.next();
            mapper.execute(owner,map, packageIsKey);
        }
    }
}
