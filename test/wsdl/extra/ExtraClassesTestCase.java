/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package test.wsdl.extra;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * This tests the file generation of only the items that are referenced in WSDL
 * This should extend FileGenTestCase, but we have a dependancy problem as
 * "extra" comes before "filegen".  Oh well.
 * 
 */
public class ExtraClassesTestCase  extends junit.framework.TestCase {
    public ExtraClassesTestCase(String name) {
        super(name);
    }

    /**
     * List of files which should be generated.
     */
    protected Set shouldExist() {
        HashSet set = new HashSet();
        set.add("Extra.java");  // this is the important one
        set.add("MyService.java");
        set.add("MyServiceService.java");
        set.add("MyServiceServiceLocator.java");
        set.add("MyServiceSoapBindingStub.java");
        set.add("MyService.wsdl");
        set.add("ExtraClassesTestCase.java");
        return set;
    } // shouldExist

    /**
     * List of files which may or may not be generated.
     */
    protected Set mayExist() {
        HashSet set = new HashSet();
        return set;
    }

    /**
     * The directory containing the files that should exist.
     */
    protected String rootDir() {
        return "build" + File.separator + "work" + File.separator +
                "test" + File.separator + "wsdl" + File.separator +
                "extra";
    } // rootDir

        public void testFileGen() throws IOException {
        String rootDir = rootDir();
        Set shouldExist = shouldExist();
        Set mayExist = mayExist();

        // open up the output directory and check what files exist.
        File outputDir = new File(rootDir);
        
        String[] files = outputDir.list();

        Vector shouldNotExist = new Vector();

        for (int i = 0; i < files.length; ++i) {
            if (shouldExist.contains(files[i])) {
                shouldExist.remove(files[i]);
            } 
            else if (mayExist.contains(files[i])) {
                mayExist.remove(files[i]);
            }
            else {
                shouldNotExist.add(files[i]);
            }
        }

        if (shouldExist.size() > 0) {
            fail("The following files should exist but do not:  " + shouldExist);
        }

        if (shouldNotExist.size() > 0) {
            fail("The following files should NOT exist, but do:  " + shouldNotExist);
        }
    }
} // class AllOptionTestCase
