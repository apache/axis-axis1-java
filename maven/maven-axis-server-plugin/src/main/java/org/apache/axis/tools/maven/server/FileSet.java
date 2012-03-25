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
package org.apache.axis.tools.maven.server;

import java.io.File;

import org.codehaus.plexus.util.DirectoryScanner;

public class FileSet {
    private File directory;
    private String[] includes;
    private String[] excludes;
    
    public File getDirectory() {
        return directory;
    }
    
    public void setDirectory(File directory) {
        this.directory = directory;
    }
    
    public String[] getIncludes() {
        return includes;
    }
    
    public void setIncludes(String[] includes) {
        this.includes = includes;
    }
    
    public String[] getExcludes() {
        return excludes;
    }
    
    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }
    
    public DirectoryScanner createScanner() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(directory);
        scanner.setIncludes(includes);
        scanner.setExcludes(excludes);
        return scanner;
    }
}
