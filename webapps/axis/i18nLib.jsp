<%@ page contentType="text/html; charset=utf-8" import="java.util.*" %>
<%
/*
 * Copyright 2005 The Apache Software Foundation.
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
%>

<%!
    /*
     * A library file to produce i18n web applications. This can be easily
     * reused from your jsp(s) - just include and call any methods.
     * @author toshi
     */

    // private variable
    HttpServletRequest _req = null;

    /**
     * Set a HttpServletRequest to a private variable.
     * @param request HttpServletRequest
     */
    void setRequest(HttpServletRequest request) {
        _req = request;
    }

    /**
     * Get a ResourceBundle object.
     * @return a ResourceBundle object
     */
    ResourceBundle getRB() {
        String strLocale = _req.getParameter("locale");
        ResourceBundle objRb = null;
        Locale objLcl = null;

        if (strLocale!=null) {
            objLcl=new Locale(strLocale,"");
        } else {
            objLcl=_req.getLocale();
        }

        Locale.setDefault(objLcl);
        objRb = ResourceBundle.getBundle("i18n",objLcl);

        return objRb;
    }

    /**
     * Get a list of locale choice
     * @return a list of supported locales
     */
    String getLocaleChoice() {
        String choice = getRB().getString("locales");
        StringBuffer buf = new StringBuffer();
        
        buf.append("<div align=\"right\">\n");
        buf.append(getRB().getString("language"));
        buf.append(": ");

        StringTokenizer st = new StringTokenizer(choice);
        String locale = null;
        while (st.hasMoreTokens()) {
            locale = st.nextToken();
            buf.append("[<a href=\"?locale="+ locale +"\">"+ locale +"</a>] ");
        }
        buf.append("\n</div>\n");

        return buf.toString();
    }

    /**
     * Get a message from i18n.properties with several arguments.
     * @param key The resource key
     * @return The formatted message
     */
    String getMessage(String key) {
        return getMessage(key, null, null, null, null, null);
    }

    /**
     * Get a message from i18n.properties with several arguments.
     * @param key The resource key
     * @param arg0 The argument to place in variable {0}
     * @return The formatted message
     */
    String getMessage(String key, String arg0) {
        return getMessage(key, arg0, null, null, null, null);
    }

    /**
     * Get a message from i18n.properties with several arguments.
     * @param key The resource key
     * @param arg0 The argument to place in variable {0}
     * @param arg1 The argument to place in variable {1}
     * @return The formatted message
     */
    String getMessage(String key, String arg0, String arg1) {
        return getMessage(key, arg0, arg1, null, null, null);
    }

    /**
     * Get a message from i18n.properties with several arguments.
     * @param key The resource key
     * @param arg0 The argument to place in variable {0}
     * @param arg1 The argument to place in variable {1}
     * @param arg2 The argument to place in variable {2}
     * @return The formatted message
     */
    String getMessage(String key, String arg0, String arg1, String arg2) {
        return getMessage(key, arg0, arg1, arg2, null, null);
    }

    /**
     * Get a message from i18n.properties with several arguments.
     * @param key The resource key
     * @param arg0 The argument to place in variable {0}
     * @param arg1 The argument to place in variable {1}
     * @param arg2 The argument to place in variable {2}
     * @param arg3 The argument to place in variable {3}
     * @return The formatted message
     */
    String getMessage(String key, String arg0, String arg1,
                      String arg2, String arg3) {
        return getMessage(key, arg0, arg1, arg2, arg3, null);
    }

    /**
     * Get a message from i18n.properties with several arguments.
     * @param key The resource key
     * @param arg0 The argument to place in variable {0}
     * @param arg1 The argument to place in variable {1}
     * @param arg2 The argument to place in variable {2}
     * @param arg3 The argument to place in variable {3}
     * @param arg4 The argument to place in variable {4}
     * @return The formatted message
     */
    String getMessage(String key, String arg0, String arg1,
                      String arg2, String arg3, String arg4) {
        String strPattern = getRB().getString(key);

        String [] params = { arg0, arg1, arg2, arg3, arg4 };
        for (int i=0; i<5; i++) {
            if (params[i]!=null) params[i]=params[i].replaceAll("\\\\","\\\\\\\\");
            if (params[i]!=null) params[i]=params[i].replaceAll("%20"," ");
        }

        if (arg0!=null) strPattern = strPattern.replaceAll("\\{0\\}",params[0]);
        if (arg1!=null) strPattern = strPattern.replaceAll("\\{1\\}",params[1]);
        if (arg2!=null) strPattern = strPattern.replaceAll("\\{2\\}",params[2]);
        if (arg3!=null) strPattern = strPattern.replaceAll("\\{3\\}",params[3]);
        if (arg4!=null) strPattern = strPattern.replaceAll("\\{4\\}",params[4]);

        return strPattern;
    }
%>