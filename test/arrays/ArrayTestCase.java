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

package test.arrays;

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.log4j.Category;

/** Test the personalInfo book sample code.
 */
public class ArrayTestCase extends TestCase {
    static Category category =
            Category.getInstance(ArrayTestCase.class.getName());

    public ArrayTestCase(String name) {
        super(name);
    }

    public void doTest () throws Exception {
        String[] args = {};
        Main.main(args);
    }

    public void testArrayService () throws Exception {
        try {
            category.info("Testing Arrays sample.");
            doTest();
            category.info("Test complete.");
        }
        catch( Exception e ) {
            if ( e instanceof AxisFault ) ((AxisFault)e).dump();
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
        
        // Multi-dimensional array tests
        test.arrays.PersonalInfoBook binding =
            new PersonalInfoBookService().getPersonalInfoBook();
        assertTrue("binding is null", binding != null);
        try {
            int[][][] one = new int[2][2][2];
            int[][][] two = new int[2][2][];
            two[0][0] = new int[1];
            two[1][0] = new int[2];
            two[0][1] = new int[3];
            two[1][1] = new int[4];

            String[][][] oneS = new String[2][2][2];
            String[][][] twoS = new String[2][2][];
            twoS[0][0] = new String[1];
            twoS[1][0] = new String[2];
            twoS[0][1] = new String[3];
            twoS[1][1] = new String[4];

            fill(one, oneS);
            fill(two, twoS);

            int[][][] one_rc;
            int[][][] two_rc;
            String[][][] oneS_rc;
            String[][][] twoS_rc;

            one_rc = binding.testArray3(one);
            assertTrue("testArray3 One Failed", verify(one, one_rc));

            two_rc = binding.testArray3(two);
            assertTrue("testArray3 Two Failed", verify(two, two_rc));

            oneS_rc = binding.testArray3S(oneS);
            assertTrue("testArray3S One Failed", verifyS(one, oneS_rc));

            twoS_rc = binding.testArray3S(twoS);
            assertTrue("testArray3S Two Failed", verifyS(two, twoS_rc));

        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }

    }

    public void fill(int[][][] array3, String[][][] array3S) {
        for (int i=0; i<array3.length; i++) {
            int[][] array2 = array3[i];
            for (int j=0; j<array2.length; j++) {
                int[] array = array2[j];
                for (int k=0; k<array.length; k++) {
                    array[k] = i + 10*j + 100*k;
                    array3S[i][j][k] = String.valueOf(array[k]);
                }
            }
        }
    }

    public boolean verify(int[][][] array3, int[][][] check) {
        for (int i=0; i<array3.length; i++) {
            int[][] array2 = array3[i];
            for (int j=0; j<array2.length; j++) {
                int[] array = array2[j];
                for (int k=0; k<array.length; k++) {
                    // The service will add 1000 to each valid index
                    if (array[k] != (check[i][j][k] - 1000))
                        return false;
                }
            }
        }
        return true;
    }

    public boolean verifyS(int[][][] array3, String[][][] check) {
        for (int i=0; i<array3.length; i++) {
            int[][] array2 = array3[i];
            for (int j=0; j<array2.length; j++) {
                int[] array = array2[j];
                for (int k=0; k<array.length; k++) {
                    // The service will add 1000 to each valid index
                    if (!String.valueOf(array[k]+1000).equals(check[i][j][k]))
                        return false;
                }
            }
        }
        return true;
    }
}


