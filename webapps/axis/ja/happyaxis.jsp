<html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.io.InputStream,
                 java.io.IOException,
                 javax.xml.parsers.SAXParser,
                 java.lang.reflect.*,
                 javax.xml.parsers.SAXParserFactory"
   session="false"
 %>
 
 <%
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
%>
<head>
<title>Axis Happiness Page</title>
</head>
<body bgcolor='#ffffff'>
<%!

    /*
     * Happiness tests for axis. These look at the classpath and warn if things
     * are missing. Normally addng this much code in a JSP page is mad
     * but here we want to validate JSP compilation too, and have a drop-in
     * page for easy re-use
     * @author Steve 'configuration problems' Loughran
     * @author dims
     * @author Brian Ewins
     */


    /**
     * Get a string providing install information.
     * TODO: make this platform aware and give specific hints
     */
    public String getInstallHints(HttpServletRequest request) {

        String hint=
            "<B><I>注意:</I></B> Tomcat 4.x と Java1.4 上では、CATALINA_HOME/common/lib に、"
            +"java.* もしくは javax.* パッケージを含むライブラリを配置する必要があるかもしれません。"
            +"<br>たとえば jaxrpc.jar と saaj.jar は、2つのそのようなライブラリです。";
          
        return hint;
    }

    /**
     * test for a class existing
     * @param classname
     * @return class iff present
     */
    Class classExists(String classname) {
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * test for resource on the classpath
     * @param resource
     * @return true iff present
     */
    boolean resourceExists(String resource) {
        boolean found;
        InputStream instream=this.getClass().getResourceAsStream(resource);
        found=instream!=null;
        if(instream!=null) {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
        return found;
    }

    /**
     * probe for a class, print an error message is missing
     * @param out stream to print stuff
     * @param category text like "warning" or "error"
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @return the number of missing classes
     * @throws IOException
     */
    int probeClass(JspWriter out,
                   String category,
                   String classname,
                   String jarFile,
                   String description,
                   String errorText,
                   String homePage) throws IOException {
        try {
            Class clazz = classExists(classname);
            if(clazz == null)  {
               String url="";
               if(homePage!=null) {
                  url="<br>  <a href="+homePage+">"+homePage+"</a>を参照してください。";
               }
               
               out.write("<p>"+category+":<b>"+jarFile+"</b>ファイルが提供する"
                   +classname+"クラスが見つかりません。<br>"
                   +errorText
                   +url
                   +"<p>");
                                     
               return 1;
            } else {
               String location = getLocation(out, clazz);
               if(location == null) {
                  out.write(description + " (" + classname + ") が見つかりました。<br>");
               }
               else {
                  out.write(location + "に、" + description + " (" + classname + ") が見つかりました。<br>");
               }
               return 0;
            }
        } catch(NoClassDefFoundError ncdfe) {
            String url="";
            if(homePage!=null) {
                url="<br>  <a href="+homePage+">"+homePage+"</a> を参照してください。";
            }
            out.write("<p>"+category+":<b>"+jarFile+"</b>ファイルが提供する"
                    +classname+"クラスの依存関係が解決できません。<br>"
                    +errorText
                    +url
                    +"<br>根本的な原因: "+ncdfe.getMessage()
                    +"<br>このエラーは次のような場合に発生する可能性があります。"
                    +"'共通の' クラスパスに"+classname+" が設定されているにもかかわらず、"
                    +" activation.jar のような依存するライブラリがwebappのクラスパス"
                    +"だけにしか設定されていないような場合。"
                    +"<p>");
            return 1;
        }
    }

    /**
     * get the location of a class
     * @param out
     * @param clazz
     * @return the jar file or path where a class was found
     */

    String getLocation(JspWriter out,
                       Class clazz) {
        try {
            java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            String location = url.toString();
            if(location.startsWith("jar")) {
                url = ((java.net.JarURLConnection)url.openConnection()).getJarFileURL();
                location = url.toString();
            }

            if(location.startsWith("file")) {
                java.io.File file = new java.io.File(url.getFile());
                return file.getAbsolutePath();
            } else {
                return url.toString();
            }
        } catch (Throwable t){
        }
        return "不明な場所";
    }

    /**
     * a class we need if a class is missing
     * @param out stream to print stuff
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @throws IOException when needed
     * @return the number of missing libraries (0 or 1)
     */
    int needClass(JspWriter out,
                   String classname,
                   String jarFile,
                   String description,
                   String errorText,
                   String homePage) throws IOException {
        return probeClass(out,
                "<b>エラー</b>",
                classname,
                jarFile,
                description,
                errorText,
                homePage);
    }

    /**
     * print warning message if a class is missing
     * @param out stream to print stuff
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @throws IOException when needed
     * @return the number of missing libraries (0 or 1)
     */
    int wantClass(JspWriter out,
                   String classname,
                   String jarFile,
                   String description,
                   String errorText,
                   String homePage) throws IOException {
        return probeClass(out,
                "<b>警告</b>",
                classname,
                jarFile,
                description,
                errorText,
                homePage);
    }

    /**
     * probe for a resource existing,
     * @param out
     * @param resource
     * @param errorText
     * @throws Exception
     */
    int wantResource(JspWriter out,
                      String resource,
                      String errorText) throws Exception {
        if(!resourceExists(resource)) {
            out.write("<p><b>警告</b>: リソース "+resource+"を見つけることができません。"
                        +"<br>"
                        +errorText);
            return 0;
        } else {
            out.write(resource+"が見つかりました。<br>");
            return 1;
        }
    }


    /**
     *  get servlet version string
     *
     */

    public String getServletVersion() {
        ServletContext context=getServletConfig().getServletContext();
        int major = context.getMajorVersion();
        int minor = context.getMinorVersion();
        return Integer.toString(major) + '.' + Integer.toString(minor);
    }



    /**
     * what parser are we using.
     * @return the classname of the parser
     */
    private String getParserName() {
        SAXParser saxParser = getSAXParser();
        if (saxParser == null) {
            return "XML Parserを生成することができません。";
        }

        // check to what is in the classname
        String saxParserName = saxParser.getClass().getName();
        return saxParserName;
    }

    /**
     * Create a JAXP SAXParser
     * @return parser or null for trouble
     */
    private SAXParser getSAXParser() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        if (saxParserFactory == null) {
            return null;
        }
        SAXParser saxParser = null;
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (Exception e) {
        }
        return saxParser;
    }

    /**
     * get the location of the parser
     * @return path or null for trouble in tracking it down
     */

    private String getParserLocation(JspWriter out) {
        SAXParser saxParser = getSAXParser();
        if (saxParser == null) {
            return null;
        }
        String location = getLocation(out,saxParser.getClass());
        return location;
    }

    /**
     * Check if class implements specified interface.
     * @param Class clazz
     * @param String interface name
     * @return boolean
     */
    private boolean implementsInterface(Class clazz, String interfaceName) {
        if (clazz == null) {
            return false;
        }
        Class[] interfaces = clazz.getInterfaces();
        if (interfaces.length != 0) {
            for (int i = 0; i < interfaces.length; i++) {
                if (interfaces[i].getName().equals(interfaceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    %>
<html><head><title>Axis Happiness Page</title></head>
<body>
<h1>Axis Happiness Page</h1>
<h2>webappの構成に関する調査</h2>
<p>
<h3>必須コンポーネント</h3>
<%
    int needed=0,wanted=0;

    /**
     * the essentials, without these Axis is not going to work
     */

    // need to check if the available version of SAAJ API meets requirements
    String className = "javax.xml.soap.SOAPPart";
    String interfaceName = "org.w3c.dom.Document";
    Class clazz = classExists(className);
    if (clazz == null || implementsInterface(clazz, interfaceName)) {
        needed = needClass(out, "javax.xml.soap.SOAPMessage",
        	"saaj.jar",
            "SAAJ API",
            "おそらくAxisは動きません。",
            "http://ws.apache.org/axis/jp/");
    } else {
        String location = getLocation(out, clazz);
        out.write("<b>エラー:</b> "+location+"に適切でないバージョンのSAAJ APIが見つかりました。" +
        "Axisのsaaj.jarを、CLASSPATHに設定されている"+location+ "よりも前方に設定してください。<br />" +
        "おそらくAxisは動きません。より詳細な情報は<a href=\"http://ws.apache.org/axis/jp/java/install.html\">Axis installation instructions</a>を参照してください。<br />");
    }

    needed+=needClass(out, "javax.xml.rpc.Service",
            "jaxrpc.jar",
            "JAX-RPC API",
            "おそらくAxisは動きません。",
            "http://ws.apache.org/axis/jp/");

    needed+=needClass(out, "org.apache.axis.transport.http.AxisServlet",
            "axis.jar",
            "Apache-Axis",
            "おそらくAxisは動きません。",
            "http://ws.apache.org/axis/jp/");

    needed+=needClass(out, "org.apache.commons.discovery.Resource",
            "commons-discovery.jar",
            "Jakarta-Commons Discovery",
            "おそらくAxisは動きません。",
            "http://jakarta.apache.org/commons/discovery/");

    needed+=needClass(out, "org.apache.commons.logging.Log",
            "commons-logging.jar",
            "Jakarta-Commons Logging",
            "おそらくAxisは動きません。",
            "http://jakarta.apache.org/commons/logging/");

    needed+=needClass(out, "org.apache.log4j.Layout",
            "log4j-1.2.8.jar",
            "Log4j",
            "Axisが動かない可能性があります。",
            "http://jakarta.apache.org/log4j");

    //should we search for a javax.wsdl file here, to hint that it needs
    //to go into an approved directory? because we dont seem to need to do that.
    needed+=needClass(out, "com.ibm.wsdl.factory.WSDLFactoryImpl",
            "wsdl4j.jar",
            "IBM's WSDL4Java",
            "おそらくAxisは動きません。",
            null);

    needed+=needClass(out, "javax.xml.parsers.SAXParserFactory",
            "xerces.jar",
            "JAXP implementation",
            "おそらくAxisは動きません。",
            "http://ws.apache.org/xerces-j/");

    needed+=needClass(out,"javax.activation.DataHandler",
            "activation.jar",
            "Activation API",
            "おそらくAxisは動きません。",
            "http://java.sun.com/products/javabeans/glasgow/jaf.html");
%>
<h3>オプショナル･コンポーネント</h3>
<%
    /*
     * now the stuff we can live without
     */
    wanted+=wantClass(out,"javax.mail.internet.MimeMessage",
            "mail.jar",
            "Mail API",
            "おそらくAttachmentsは機能しません。",
            "http://java.sun.com/products/javamail/");

    wanted+=wantClass(out,"org.apache.xml.security.Init",
            "xmlsec.jar",
            "XML Security API",
            "XML Securityはサポートされません。",
            "http://ws.apache.org/security/");

    wanted += wantClass(out, "javax.net.ssl.SSLSocketFactory",
            "jsse.jar or java1.4+ runtime",
            "Java Secure Socket Extension",
            "httpsはサポートされません。",
            "http://java.sun.com/products/jsse/");
    /*
     * resources on the classpath path
     */
    /* broken; this is a file, not a resource
    wantResource(out,"/server-config.wsdd",
            "サーバー構成ファイルがありません。"
            +"作成するためにAdminClientを実行してください。");
    */
    /* add more libraries here */

    out.write("<h3>");
    //is everythng we need here
    if(needed==0) {
       //yes, be happy
        out.write("<i>axisのコア･ライブラリは存在しています。 </i>");
    } else {
        //no, be very unhappy
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.write("<i>"
                +"axisのコア･ライブラリ"
                +(needed==1?"が":"が")
                +needed+"つ欠けています。</i>");
    }
    //now look at wanted stuff
    if(wanted>0) {
        out.write("<i>"
                +"axisのオプショナル･ライブラリ"
                +(wanted==1?"が":"が")
                +wanted+"つ欠けています。 </i>");
    } else {
        out.write("オプショナル･コンポーネントは存在しています。");
    }
    out.write("</h3>");
    //hint if anything is missing
    if(needed>0 || wanted>0 ) {
        out.write(getInstallHints(request));
    }

    %>
    <p>
    <B><I>注意:</I></B> ページに全ての調査結果が表示されたとしても、
    チェックできない構成オプションも多いため、あなたのWebサービスが正常に機能する保障はありません。
    これらのテストは<i>必要</i>なものですが、<i>十分</i>なものではありません。
    <hr>

    <h2>アプリケーション･サーバーに関する調査</h2>
    <%
        String servletVersion=getServletVersion();
        String xmlParser=getParserName();
        String xmlParserLocation = getParserLocation(out);

    %>
    <table>
        <tr><td>サーブレットのバージョン</td><td><%= servletVersion %></td></tr>
        <tr><td>XMLパーサー</td><td><%= xmlParser %></td></tr>
        <tr><td>XMLパーサーの場所</td><td><%= xmlParserLocation %></td></tr>
    </table>
<% if(xmlParser.indexOf("crimson")>=0) { %>
    <p>
    <b> Axisで使用するXMLパーサーには Crimson ではなく、 
        <a href="http://ws.apache.org/xerces2-j/">Xerces 2</a> を推奨しています。
    </p>
<%    } %>

    <h2>システム･プロパティに関する調査</h2>
<%
    /**
     * Dump the system properties
     */
    java.util.Enumeration e=null;
    try {
        e= System.getProperties().propertyNames();
    } catch (SecurityException se) {
    }
    if(e!=null) {
        out.write("<pre>");
        for (;e.hasMoreElements();) {
            String key = (String) e.nextElement();
            out.write(key + "=" + System.getProperty(key)+"\n");
        }
        out.write("</pre><p>");
    } else {
        out.write("システム･プロパティにアクセスできません。<p>");
    }
%>
    <hr>
    プラットフォーム: <%= getServletConfig().getServletContext().getServerInfo()  %>
</body>
</html>

