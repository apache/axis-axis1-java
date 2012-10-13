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
