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

/**
 * This tests the file generation of only the items that are referenced in WSDL
 * 
 * @author Tom Jordahl (tomj@macromedia.com)
 */ 
package test.filegen;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;


public class FileGenTestCase extends junit.framework.TestCase {
    public FileGenTestCase(String name) {
        super(name);
    }

    // List of files which should be generated
    private static String[] shouldExist= new String[] {
        "PortTypeSoap.java",
        "ReferenceService.java",
        "ReferenceSoapBindingStub.java",
        "OpFault.java"
    };
    
    // List of files which should NOT be generated
    private static String[] shouldNotExist= new String[] {
        "InvalidTickerFaultMessage.java",
        "PortTypeNotSoap.java",
        "ReferenceHttpGetStub.java"
        // Add these when we don't emit unreferenced types 
/*
        "Address.java",
        "AddressHolder.java",
        "StateType.java",
        "StateTypeHolder.java"
*/
    };

    public void testFileGen() throws IOException {
        String rootDir = "build"+ File.separator + "work" + File.separator + 
                "test" + File.separator + "filegen";
        // open up the output directory and check what files exist.
        File outputDir = new File(rootDir);
        
        String[] files = outputDir.list();
        
        for (int i=0; i < shouldExist.length; i++) {
            File f = new File(rootDir, shouldExist[i]);
            //System.out.println(shouldExist[i] + " : " +  f.exists());
            assertTrue("File does not exist (and it should): " + shouldExist[i], f.exists()); 
        }
        
        for (int i=0; i < shouldNotExist.length; i++) {
            File f = new File(rootDir, shouldNotExist[i]);
            //System.out.println(shouldNotExist[i] + " : " +  f.exists());
            assertTrue("File exist (and it should NOT): " + shouldNotExist[i], !f.exists()); 
        }
    }
}

