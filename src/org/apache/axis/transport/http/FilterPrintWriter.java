/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

package org.apache.axis.transport.http;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * simple wrapper around PrintWriter class. It creates the PrintWriter
 * object on demand, thus allowing to have a ResponseWriter class for
 * structural reasons, while actually creating the writer on demand.
 * This solves the problem of having to have a PrintWriter object available and being forced
 * to set the contentType of the response object after creation.
 * 
 * @author Davanum Srinivas (dims@yahoo.com)
 */

public class FilterPrintWriter extends PrintWriter {

    private PrintWriter _writer = null;
    private HttpServletResponse _response = null;
    private static OutputStream _sink = new NullOutputStream();

    public FilterPrintWriter(HttpServletResponse aResponse) {
        super(_sink);
        _response = aResponse;
    }

    private PrintWriter getPrintWriter() {
        if (_writer == null) {
            try {
                _writer = _response.getWriter();
            } catch (IOException e) {
                throw new RuntimeException(e.toString());
            }
        }
        return _writer;
    }

    public void write(int i) {
        getPrintWriter().write(i);
    }

    public void write(char[] chars) {
        getPrintWriter().write(chars);
    }

    public void write(char[] chars, int i, int i1) {
        getPrintWriter().write(chars, i, i1);
    }

    public void write(String string) {
        getPrintWriter().write(string);
    }

    public void write(String string, int i, int i1) {
        getPrintWriter().write(string, i, i1);
    }

    public void flush() {
        getPrintWriter().flush();
    }

    public void close() {
        getPrintWriter().close();
    }

    public boolean checkError() {
        return getPrintWriter().checkError();
    }

    public void print(boolean b) {
        getPrintWriter().print(b);
    }

    public void print(char c) {
        getPrintWriter().print(c);
    }

    public void print(int i) {
        getPrintWriter().print(i);
    }

    public void print(long l) {
        getPrintWriter().print(l);
    }

    public void print(float v) {
        getPrintWriter().print(v);
    }

    public void print(double v) {
        getPrintWriter().print(v);
    }

    public void print(char[] chars) {
        getPrintWriter().print(chars);
    }

    public void print(String string) {
        getPrintWriter().print(string);
    }

    public void print(Object object) {
        getPrintWriter().print(object);
    }

    public void println() {
        getPrintWriter().println();
    }

    public void println(boolean b) {
        getPrintWriter().println(b);
    }

    public void println(char c) {
        getPrintWriter().println(c);
    }

    public void println(int i) {
        getPrintWriter().println(i);
    }

    public void println(long l) {
        getPrintWriter().println(l);
    }

    public void println(float v) {
        getPrintWriter().println(v);
    }

    public void println(double v) {
        getPrintWriter().println(v);
    }

    public void println(char[] chars) {
        getPrintWriter().println(chars);
    }

    public void println(String string) {
        getPrintWriter().println(string);
    }

    public void println(Object object) {
        getPrintWriter().println(object);
    }

    static public class NullOutputStream extends OutputStream {
        public void write(int b) {
            // no code -- no output
        }
    }
}
