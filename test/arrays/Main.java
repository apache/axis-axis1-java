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
 * 4. The names "SOAP" and "Apache Software Foundation" must
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2000, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package test.arrays;

import org.apache.axis.utils.Options;

import java.net.URL;
import java.util.Vector;



/**
 * This class shows is a modification of the AddressBook class to 
 * verify arrays. 
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class Main {
    static String name = "Joe Geek";
    static String[] movies = new String[] { "Star Trek", "A.I." };
    static String[] hobbies= new String[] { "programming", "reading about programming" };
    static String[] pets   = new String[] { "Byte", "Nibbles" };
    static int[]    id     = new int[]    { 0, 0, 7 };
    static int id2         = 123;
    static String[] foods  = new String[] { "Cheeze Whiz", "Jolt Cola" };
    static String[] games  = new String[] { "Doom", "Axis & Allies" };
    static byte[]   nickName = new byte[] { 'g', 'e', 'e', 'k' };
    static PersonalInfo pi = new PersonalInfo(name,movies,hobbies,pets, id, id2,
                                              //foods,games,
                                              nickName);
    
    private static void printPersonalInfo (PersonalInfo pi) {
        if (pi == null) {
            System.err.println ("\t[PERSONAL INFO NOT FOUND!]");
            return;
        }
        Object[] fm =pi.getFavoriteMovies();
        System.err.print("\tFavorite Movies=");
        for(int i=0; i<fm.length; i++)
            System.err.print(" \"" + (String) fm[i] + "\"");
        System.err.println("");

        Object[] h =pi.getHobbies();
        System.err.print ("\tHobbies=");
        for(int i=0; i<h.length; i++)
            System.err.print(" \"" + (String) h[i] + "\"");
        System.err.println("");

        System.err.print ("\tPets=");
        for(int i=0; i<pets.length; i++)
            System.err.print(" \"" + (String) pets[i] + "\"");
        System.err.println("");

        System.err.print ("\tId=");
        for(int i=0; i<id.length; i++)
            System.err.print(" \"" + id[i] + "\"");
        System.err.println("");

        System.err.print ("\tId2="+id2);
        System.err.println("");


        /*
        Object[] foods =pi.getFoods();
        System.err.print ("\tFavorite Foods=");
        for(int i=0; i<foods.length; i++)
            System.err.print(" \"" + (String) foods[i] + "\"");
        System.err.println("");

        Object[] games =pi.getGames();
        System.err.print ("\tFavorite Games=");
        for(int i=0; i<games.length; i++)
            System.err.print(" \"" + (String) games[i] + "\"");
        System.err.println("");
        */

        byte[] nb =pi.getNickName();
        System.err.print ("\tNickName=");
        for(int i=0; i<nb.length; i++)
            System.err.print(nb[i]);
        System.err.println("");
    }
    
    private static Object doit (PersonalInfoBook pib) throws Exception {
        System.err.println (">> Storing info for '" + name + "'");
        pib.addEntry (name, pi);
        System.err.println (">> Querying info for '" + name + "'");
        PersonalInfo resp = pib.getPersonalInfoFromName (name);

        // Get just the pets to test return of an array.
        pets = pib.getPetsFromName (name);
        // Get just the id to test return of an int array.
        id = pib.getIDFromName (name);
        // Get just id2 to test return of int.
        id2 = pib.getID2FromName (name);
        System.err.println (">> Response is:");
        printPersonalInfo (resp);
        pib.addEntry (name, pi);
        return resp;
    }
    
    public static void main (String[] args) throws Exception {
        Options opts = new Options(args);
        
        PersonalInfoBookService service = new PersonalInfoBookService();
        opts.setDefaultURL( service.getPersonalInfoBookAddress() );
        URL serviceURL = new URL(opts.getURL());

        PersonalInfoBook pib2 = null;
        if (serviceURL == null) {
            pib2 = service.getPersonalInfoBook();
        }
        else {
            pib2 = service.getPersonalInfoBook(serviceURL);
        }
        ((PersonalInfoBookSOAPBindingStub) pib2).setMaintainSession (true);
        Object ret = doit (pib2);
        if (ret == null) {
            throw new Exception("session test expected non-null response, got "+ret);
        }
    }
}
