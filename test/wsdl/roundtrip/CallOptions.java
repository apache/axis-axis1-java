/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package test.wsdl.roundtrip;

import java.util.Calendar;

/**
 * The CallOptions class is just a class to determine how
 * Java2WSDL will generate WSDL for user defined classes.
 *
 * @version   1.00  06 Feb 2002
 * @author    Brent Ulbricht
 */
public class CallOptions {

    private double callPrice = 103.30;
    public Calendar callDate;

    public static void main(String[] args) {

    } // main

} // CallOptions
